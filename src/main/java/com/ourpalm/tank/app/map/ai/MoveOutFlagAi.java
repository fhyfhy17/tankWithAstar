package com.ourpalm.tank.app.map.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.app.map.ai.Ai.SearchRoadRet;
import com.ourpalm.tank.app.map.astar.AStar;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_FIRE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_MOVE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_EFFECT_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.SportMapInstance;
import com.ourpalm.tank.vo.TankPreseasonDeal;

public class MoveOutFlagAi extends Ai{
	//不可行走BUFF效果ID
	private final static int STOP_EFFECT_ID = 2;	
	private final static int WAIT_MAX_COUNT = 12;
	//AI坦克转向的时候，避免瞬间转向，则对转向加入了等待时间，该定义为每一帧转动的角度
	private final static int STOP_TURN_ANGLE = 20;
	
	//AI坦克转向的时候，可以不停顿直接行走的最小角度
	private final static int MIN_TURN_ANGLE = 45;
	
	private AStar astar;				//寻路对象
	private int waitCount;				//网格被占时，等待次数
	private Node targetNode;			//所前往的目标节点
	private double angle = -1;			//坦克车身角度
	private double targetAngle = -1;	//前进目标坦克角度
	private boolean isMoveEnd = false;	//是否走到终点
	
	public MoveOutFlagAi(AbstractInstance self){
		super(self);
		this.astar = new AStar(grid, false);
		this.loopCount = new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME);
	}
	
	@Override
	public void init(){
		super.init();
		this.teamId = self.getTeam().getNumber();
		clearRoads();
	}
	
	@Override
	public void reset(AbstractInstance target) {
		// 清除上一次保留数据
		this.clearRoads();
		super.reset(target);
	}
	
	@Override
	public void update() {
		//死亡状态，将等待复活
		if(self.isDeath()){
			return ;
		}
		
		if( this.isMoveEnd ){
			self.moveAiSwitch();
			return ;
		}
		
		//寻找行走路径
		SearchRoadRet ret = this.searchRoad();
		// 寻路
		if( ret == SearchRoadRet.noFind || ret == SearchRoadRet.end ){
			this.isMoveEnd = true;
			self.moveAiSwitch();
			return;
		}

		//没找到路径返回
		if(Util.isEmpty(roads)){
			return;
		}
		
		//行走
		this.walk();
		
		if(this.loopCount.isReachCycle()){
			
			//寻找目标
			this.searchEnemy();
			
			//移动炮管
			this.moveGun();
			
			//开始填弹
			// this.redloadShell();	
			
			this.fireLogic();
		}
	}
	
	//寻找离自己最近两个敌人中血量最少的敌人
	private void searchEnemy(){
		
		this.target = null;
		
		//寻找一个目标
		AbstractInstance firstTank = this.findNearEnemyOfFire(null);
		if(firstTank == null){
			return ;
		}
		
		if(isFire(firstTank)){
			this.target = firstTank;
		}
	}
	
	@Override
	public void clearRoads(){
		if(roads != null){
			roads.clear();
		}
		pointLvl = self.getAiPathPoint().randomPointType();
		//坦克死亡复活,设置点为复活点
		this.x = self.getX();
		this.y = self.getY();
		this.z = self.getZ();
		
		//释放格子
		if(targetNode != null){
			this.targetNode.setTankOccupy(false);
		}
		
		this.target = null;
		this.targetNode = null;
		this.angle = self.getAngle();
		this.targetAngle= self.getAngle();
		this.isMoveEnd = false;
	}

	@Override
	public AiType getType() {
		return AiType.fireAndMoveOutFlag;
	}

	//寻找路径
	private SearchRoadRet searchRoad(){
		if(!Util.isEmpty(roads)){
			return SearchRoadRet.noEnd;
		}
		
		//寻找所属队伍的点
		// 当坦克处于追击或者普通行走的时候，已经在圈子里面，则放弃该AI，改为停留在圈子里面
		if (mapInstance == null || !(mapInstance instanceof SportMapInstance) ) {
			return SearchRoadRet.noFind;
		}
		
		SportMapInstance mapInstance2 = (SportMapInstance) mapInstance;
		MapTemplate mapTem = mapInstance2.getTemplate();
		
		// 圆心多少米外某个点，但是这样会导致行走的时候，在旗内时间过长
//		int sX = (int)(Math.random()*(80));
//		float tX = 0;
//		float tY = 0;
//		tX = mapTem.getX() - 40 + sX;
//		if( sX <= 10 || sX >= 70 ){
//			int sY = (int)(Math.random()*(80));
//			tY = mapTem.getZ() - 40 + sY;
//		} else {
//			int sY = (int)(Math.random()*(20)) - 10;
//			if( sY <= 0 ){
//				tY = mapTem.getZ() - 40 - sY;
//			} else {
//				tY = mapTem.getZ() + 40 - sY;
//			}
//		}
		
		// 取站点和圆心方向，直线行走
		float range = Util.range(self.getX(), self.getZ(), mapTem.getX(), mapTem.getZ());
		
		float sX = self.getX() - mapTem.getX();
		float sY = self.getZ() - mapTem.getZ();
		
		float tX = sX * 40 / range + mapTem.getX();
		float tY = sY * 40 / range + mapTem.getZ();
		
		//System.out.println(self.getTeam().toString() + " " + self.getRoleName() + "  开始寻找路 离开旗子----- 旗子中心点坐标：["+mapTem.getX()+","+mapTem.getZ()+"] 目标点 ["+tX+","+tY+"]");
		
		//寻找到达路径
		Node targetNode = grid.getNodeByWoldPoint(tX, tY);
		if( !targetNode.isWalkable() ){
			tX = sX * 80 / range + mapTem.getX();
			tY = sY * 80 / range + mapTem.getZ();
			targetNode = grid.getNodeByWoldPoint(tX, tY);
			//System.out.println(self.getTeam().toString() + " " + self.getRoleName() + "开始寻找路 重新寻找 离开旗子----- 旗子中心点坐标：["+mapTem.getX()+","+mapTem.getZ()+"] 目标点 ["+tX+","+tY+"]");
		}
		
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		
		if( targetNode == selfNode ){
			return SearchRoadRet.end;
		}
//		System.out.println("目标点-----X="+targetNode.getPx() + "  Z="+targetNode.getPz() + "  自身点  X="+selfNode.getPx() + " Z="+selfNode.getPz());
		
		astar.setStartNode(selfNode);
		astar.setEndNode(targetNode);
		
		Map<Integer, Set<Integer>> ignoreNode = new HashMap<>();
		List<Node> beforeNodes = grid.getNearByNodes(mapTem.getX(), mapTem.getZ());
		for(Node node : beforeNodes){
			if( !ignoreNode.containsKey(node.getX())){
				Set<Integer> set = new HashSet<>();
				ignoreNode.put(node.getX(), set);
			}
			
			ignoreNode.get(node.getX()).add(node.getY());
		}
		
		if(astar.findPath(grid)){
			this.roads.clear();
			this.roads.addAll(astar.getPath());
			//删除第一个自身点
			if(!Util.isEmpty(this.roads)){
				this.roads.pop();
			} else {
				return SearchRoadRet.noFind;
			}
		}
		return SearchRoadRet.find;
	}
	
	//行走
	private void walk(){
		if(Util.isEmpty(roads)){
			return ;
		}
		//判断是否有阻碍行走类的BUFF效果
		for(IBuff buff : self.getBuffers()){
			if(buff.getEffectId() == STOP_EFFECT_ID){
				return ;
			}
		}
		
		//设置目标点
		if(targetNode == null){
			//去第一个坐标点
			Node targetNode = roads.peek();
			
//			System.out.println("第一个坐标点 X="+targetNode.getPx() + "  Z="+targetNode.getPz());
			
			//判断目标格子已被占据
			if(targetNode.isTankOccupy()){
				//换条路走
				if(this.waitCount >= WAIT_MAX_COUNT){
					this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
					this.roads.clear();
					this.waitCount = 0;
				}
				this.waitCount += 1;
				return ;//等一会
			}
			roads.pop();
			this.targetNode = targetNode;
			targetNode.setTankOccupy(true);
			
			if(Util.isEmpty(roads)){
				this.isMoveEnd = true;
				return ;
			}
		}
		
		//判断是否有目标点
		//计算一次循环所移动的距离
		float way = self.get(AttrType.n_speed)/10  * AIThreadHandler.AI_LOOP_TIME / 1000f;
		
		//判断是否到达目标点
		float range = Util.range(x, z, targetNode.getPx(), targetNode.getPz());
		if(range <= 0){
			//设置目标点
			targetNode.setTankOccupy(false);
			this.targetNode = null;
			
			//设置坦克位置
			self.setX(this.x);
			self.setY(this.y);
			self.setZ(this.z);
			
			return ;
		}

		//计算坦克这次心跳时间内所需到达的位置
		float x = this.x + ((targetNode.getPx() - this.x) * (way / range));
		float z = this.z + ((targetNode.getPz() - this.z) * (way / range));
		float y = (float)this.readHightMapData.getHight(x, z);
		
		if( !this.calculateTrunAngle( this.x, this.z, x, z )){
			return;
		}
		
		//之前格子释放
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		selfNode.setTankOccupy(false);
		
		//设置坦克位置
		self.setX(this.x);
		self.setY(this.y);
		self.setZ(this.z);
		
		//当前格子占住
		selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		selfNode.setTankOccupy(true);
		
		//通知客户端
		STC_AI_MOVE_MSG moveMsg = STC_AI_MOVE_MSG.newBuilder()
				.setId(self.getId())
				.setPosition(Coordinate3D.newBuilder()
						.setPx(x)
						.setPy(y)
						.setPz(z))
				.setTarget(0)
				.build();
		mapInstance.brodcastMsg(moveMsg);
		
		//设置未来目标点
		this.x = x;
		this.y = y;
		this.z = z;
		
		//判断路是否走到尽头
		if(Util.isEmpty(roads)){
			this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
		}
		
		//设置目标点
		if(range < way){
			targetNode.setTankOccupy(false);
			this.targetNode = null;
		}
	}
	
	// 计算旋转角度，返回  false 表示不需要旋转直接前进
	private boolean calculateTrunAngle( float beginX, float beginY, float endX, float endY ){
		
		// 计算坦克车身角度
		float ax = endX - beginX;
		float az = endY - beginY;
		this.targetAngle = Math.toDegrees(Math.atan2(az, ax));
		if( this.targetAngle < 0 ){
			this.targetAngle = 360 - Math.abs(this.targetAngle);
		}
		if( this.angle == -1 ){
			this.angle = targetAngle;
		}
		
		boolean needTurned = false;
		double oldAngle = this.angle;
		// 计算原地转向车身逻辑
		double includedAngle = this.targetAngle - this.angle;
		if( includedAngle >= MIN_TURN_ANGLE && includedAngle < 180 ){
			this.angle = this.angle + STOP_TURN_ANGLE;
			needTurned = true;
		} else if( includedAngle >= 180 && includedAngle <= (360 - MIN_TURN_ANGLE) ){
			this.angle = this.angle - STOP_TURN_ANGLE;
			needTurned = true;
		} else if( includedAngle <= -MIN_TURN_ANGLE && includedAngle > -180 ){
			this.angle = this.angle - STOP_TURN_ANGLE;
			needTurned = true;
		} else if( includedAngle <= -180 && includedAngle >= -(360 - MIN_TURN_ANGLE) ){
			this.angle = this.angle + STOP_TURN_ANGLE;
			needTurned = true;
		}
		
		if( this.angle > 360 ){
			this.angle = this.angle - 360;
		}
		if( this.angle < 0 ){
			this.angle = 360 + this.angle;
		}
		
		if( needTurned ){
//			if (self.getMemberId() == 2 && self.getTeam().toString().equals("BLUE") ){
//				logger.error(" BLUE {} old [ x {} y {} ], new [ x {} y {} ], a [ x {} y {} ], oldAngle {} this.angle {},this.targetAngle {} ", self.getRoleName(), this.x, this.z, x, z, ax, az, oldAngle, this.angle, this.targetAngle );
//			}
			return false;
		}
		
		this.angle = this.targetAngle;
		return true;
	}
	
	/** 是否命中目标 */
	private boolean hadHitTank(){
		float range = Util.range(self.getX(), self.getZ(), target.getX(), target.getZ());
		if(range <= 0){
			range = 0.1f;
		}
		double hitRat = Math.atan2(0.1, 5) / Math.atan2(self.get(AttrType.focal_radil_static), 5) * 60 / range;
		
		return Math.random() <= hitRat;
	}
	
	
	//普通开火
	private void fire(){
		this.brodcastFireMsg(FireType.fire, 0, 0, 0);
		
		HitParam param = new HitParam();
		param.setSource(self);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, self,target);
		mapInstance.putHitQueue(param);
	}
	
	//使用技能
	private void useSkill(){
		HitParam param = new HitParam();
		param.setSource(self);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));
		
		//判断是否可使用技能
		boolean hadUseKill = false;
		for(Skill skill : self.getAllSkill()){
			if(skill.isActive()){
				if(skill.finishCoolTime()){
					param.setFireType(FireType.skill);
					param.setItemId(skill.getId());
					hadUseKill = true;
				}
				break;
			}
		}
		//没触发使用技能，普通开炮
		if(!hadUseKill){
			this.fire();
			return ;
		}
		
		//使用技能广播
		this.brodcastFireMsg(FireType.skill, param.getItemId(), 0, 0);
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, self,target);
		//计算伤害
		mapInstance.putHitQueue(param);
	}
	
	//使用金币弹
	private void useGoldShell(){
		HitParam param = new HitParam();
		param.setSource(self);
		param.setTarget(target);
		param.setFireType(FireType.fire);
		param.setHadDodge(false);
		param.setHitPart(RandomUtil.randomInt(2));
		
		TankTemplate template = GameContext.getTankApp().getTankTemplate(self.getTemplateId());
		if(template == null){
			return ;
		}
		int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.valueOf(template.getShellType_i()));
		int count = self.getGoods(goodsId);
		if(count > 0){
			//道具不足，普通开火
			this.fire();
			return ;
		}
		self.putGoods(goodsId, count - 1);
		param.setFireType(FireType.goods);
		param.setItemId(goodsId);
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, self,target);
		//广播使用金币弹
		this.brodcastFireMsg(FireType.goods, goodsId, 0, 0);
		//计算伤害
		mapInstance.putHitQueue(param);
	}
	
	
	//瞎放一炮
	private void fireEmpty(){
		int[] deviations = {-1, 1};
		float offsetAngle = RandomUtil.randomInt(10);
		int index1 =  RandomUtil.randomInt(deviations.length);
		int index2 = RandomUtil.randomInt(deviations.length);
        float offsetDirX = deviations[index1] * offsetAngle / 80;
        float offsetDirY = deviations[index2] * offsetAngle / 90;
        this.brodcastFireMsg(FireType.fire, 0, offsetDirX, offsetDirY);
	}
	
	
	//广播开火消息
	private void brodcastFireMsg(FireType fireType, int stdItem, float skewX, float skewY){
		STC_AI_FIRE_MSG fireMsg = STC_AI_FIRE_MSG.newBuilder()
				.setSourceId(self.getId())
				.setTargetId(target.getId())
				.setFireType(fireType)
				.setHadDodge(false)
				.setStdItem(stdItem)
				.setSkewX(skewX)
				.setSkewY(skewY)
				.build();
		mapInstance.brodcastMsg(fireMsg);
		if( target.getId() == 0 && fireType == FireType.fire){
			self.setMissCount(self.getMissCount()+1);
		}
	}
	
	
	
	private void useGoods(){
		try{
			int rat = self.getAiAction().getGoodsRat();
			if(RandomUtil.randomInt(100) > rat || self.getBuffers().size() <= 0){
				return ;
			}
			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
			int count = self.getGoods(goodsId);
			if(count <= 0){
				return ;
			}
			self.putGoods(goodsId, count - 1);
			
			GoodsWarTemplate template = GameContext.getGoodsApp().getGoodsWarTemplate(goodsId);
			//广播使用效果
			STC_USE_WAR_EFFECT_MSG efffectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder()
					.setId(self.getId())
					.setGoodsId(goodsId)
					.build();
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
					BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE, efffectMsg.toByteArray());
			//删除buff
			for(String strId : template.getDelBuff().split(Cat.comma)){
				if(Util.isEmpty(strId)){
					continue;
				}
				int delBuffId = Integer.parseInt(strId);
				GameContext.getBuffApp().remove(self, delBuffId);
			}
			//添加buff
			GameContext.getBuffApp().putBuff(self, self, template.getAddBuff());
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}
	}
	
	private void fireLogic(){
		if(target == null){
			return ;
		}
		
		//判断开火冷却
		if(!self.hadAndSetFireCoolTime()){
			return ;
		}
		
		//使用战场道具
		this.useGoods();
		
		//判断是否命中
		if(hadHitTank()){
			//随机开火类型
			FireType fireType = self.getAiAction().randomFire();
			switch(fireType){
				case fire : this.fire(); return ;
				case goods : this.useGoldShell(); return;
				case skill : this.useSkill(); return ;
			default: break;
			}
			return ;
		}
		
		//空放一炮
		this.fireEmpty();
	}
	
	//移动炮塔
	private void moveGun(){
		if(target == null){
			return ;
		}
		
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		selfNode.setTankOccupy(false);
		
		self.setX(x);
		self.setY(y);
		self.setZ(z);
		
		selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		selfNode.setTankOccupy(true);
		
		//把炮台转向目标
		STC_AI_MOVE_MSG moveMsg = STC_AI_MOVE_MSG.newBuilder()
				.setId(self.getId())
				.setPosition(Coordinate3D.newBuilder()
						.setPx(x)
						.setPy(y)
						.setPz(z))
				.setTarget(target.getId())
				.build();
		mapInstance.brodcastMsg(moveMsg);
		
		//给转炮塔留时间
		self.setLastFireTime(System.currentTimeMillis());
	}
	
}
