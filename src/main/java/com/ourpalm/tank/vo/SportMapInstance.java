package com.ourpalm.tank.vo;

import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.RoadManager.SearchStrategy;
import com.ourpalm.tank.app.map.ai.AiType;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.app.map.state.StateMachineFactory;
import com.ourpalm.tank.app.quest.BattleResultRecord;
import com.ourpalm.tank.constant.Constant;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_TANK_DIE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_TANK_RELIVE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.MapTemplate;


/**
 * 军衔排行赛地图实例
 * @author wangkun
 *
 */
public class SportMapInstance extends MapInstance{

	protected MapTemplate template; 	//地图配置
	protected long beginTime;			//比赛开始时间

	public SportMapInstance(MapTemplate template, int instanceId, int warType){
		super(instanceId);
		this.warType = warType;
		this.template = template;
		//准备比赛阶段
		this.stateMachine = StateMachineFactory.createReadyStateMachine(this);
	}


	/** 坦克死亡方法 */
	@Override
	public void death(AbstractInstance killer, AbstractInstance target){
		if(this.deathMap.containsKey(target.getId())){
			return ;
		}

		//比赛已结束
		if(this.stateMachine.hadOver()){
			return ;
		}

		// 计算被动技能是否允许忽略此次人头计算
		boolean hadKillCount = true;
//		for (Skill skill : target.getAllSkill()) {
//			if (skill.hadIngoreKillCountWhenDeath()) {
//				hadKillCount = false;
//				break;
//			}
//		}

		//统计击杀数目
		if (hadKillCount) {
			this.calcuKillCount(killer, target);
		}

		//累计协助次数和伤害(此方法需在助攻计算后触发)
		this.calcuAidKill(killer, target);

		//发送个人战绩
		this.notifyUserWarRecord(killer);
		this.notifyUserWarRecord(target);

		//任务统计
		this.deathQuest(killer, target);

		//累计和广播击杀比分
		if(hadKillCount){
			this.brodcastKillTipsWarScore(killer, target);
		}

		//广播死亡消息
		this.deathNotifyMsg(killer, target);

		//死亡逻辑
		this.death(target);
	}


	//任务击杀统计
	@Override
	protected void deathQuest(AbstractInstance killer, AbstractInstance target){
		final BattleResultRecord questRecord = killer.getQuestRecord();
		if(questRecord == null){
			return ;
		}

		//死亡时是否在旗帜范围内
		float x = Math.abs(target.getX() - template.getX());
		float z = Math.abs(target.getZ() - template.getZ());
		//统计击杀占旗任务
		int range = (int)Math.sqrt(x*x  + z*z);
		if(template.getRadius() >= range){
			questRecord.setKillOcccpyFlagCount(questRecord.getKillOcccpyFlagCount() + 1);
			logger.debug("死于旗内...... id = {}", target.getId());
		}
	}



	/** 广播死亡消息 */
	@Override
	public void deathNotifyMsg(AbstractInstance killer, AbstractInstance target){
		//广播死亡消息
		STC_TANK_DIE_MSG.Builder deathMsgBuilder = STC_TANK_DIE_MSG.newBuilder();
		deathMsgBuilder.setId(target.getId());
		deathMsgBuilder.setReliveTime(template.getReviveTime());
		deathMsgBuilder.setGold(0);
		deathMsgBuilder.setPayRelive(false); //不可花钱复活
		deathMsgBuilder.setFreeCount(0);
		deathMsgBuilder.setHasRelive(true);
		deathMsgBuilder.setAtkName(killer.getRoleName());
		deathMsgBuilder.setAtkTankId(killer.getTemplateId());
		deathMsgBuilder.setAtkId(killer.getId());

		this.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
				BATTLE_MSG.CMD_ID.STC_TANK_DIE_VALUE,
				deathMsgBuilder.build().toByteArray());
	}


	@Override
	public void death(AbstractInstance tank){
		//加入死亡列表
		this.deathMap.put(tank.getId(), tank.getId());

		//设置坦克死亡时间
		tank.setDeathTime(System.currentTimeMillis());

		//删除所有buff
		tank.clearBuff();

		for (Skill skill : tank.getAllSkill()) {
			skill.death();
		}

		//清空统计记录
		tank.clearCount();

		if( tank.isRobot() ){
			// 清理掉行为树中的状态
			tank.getBehaviorTree().initialize(tank);
			tank.setFireTarget(null);
			tank.setAttacker(null);
			tank.getRoadManager().setStrategy(SearchStrategy.DEFAULT);
			tank.aiTypeSwitch( AiType.death );
		}
	}



	/** 广播击杀和战绩 */
	private void brodcastKillTipsWarScore(AbstractInstance killer, AbstractInstance death){
		//击杀广播提示
		this.killTipsNotify(killer, death);

		//统计战场比分
		this.calcuWarScore(killer, death);

		//广播战绩表
		int overTime = this.template.getOverTime() + this.template.getReadyTime();
		overTime -= this.durationTime() ;
		if(overTime <= 0){
			overTime = 0;
		}
		this.brodcastWarScore(overTime);
	}

	/** 统计战场比分 */
	protected void calcuWarScore(AbstractInstance killer, AbstractInstance target){
		//统计队伍击杀数
		if(TEAM.BLUE == killer.getTeam()){
			this.blueKillNum += 1;
		}else{
			this.redKillNum += 1;
		}
	}


	@Override
	public void update(){

		//执行状态机,没有玩家还需要执行,状态机有回收地图机制
		try{
			this.stateMachine.update();
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}

		//执行地图逻辑
		this.mapLogic();
	}


	private void mapLogic(){
		//没有玩家返回，节省性能
		if(this.stateMachine.hadOver() || !this.hadPlayer()){
			return ;
		}

		//执行复活逻辑
		try{
			this.revive();
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}

		//处理地图buff
		try{
			this.runBuff();
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}

		//坦克状态
		try{
			this.runUpate();
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}
	}



	//坦克复活逻辑
	private void revive(){
		if(Util.isEmpty(deathMap)){
			return ;
		}
		long reviveTime = template.getReviveTime() * 1000;
		for(int deathInstanceId : this.deathMap.keySet()){
			AbstractInstance tank = this.tanks.get(deathInstanceId);
			if(tank == null){
				deathMap.remove(deathInstanceId);
				continue;
			}
			if((System.currentTimeMillis() - tank.getDeathTime()) > reviveTime){

				//删除所有buff
				tank.clearBuff();
				//清除逃跑状态
				tank.setEscape(false);
				//释放死亡所在格子为可行走
				this.setGridNode(tank.getX(), tank.getZ(), false);

				Location location = tank.getBirthLocation();
				//判断出生点是否被占
				if(this.birthPointOccupy(tank.getId(), location.getX(), location.getZ())){
					template.matchBirthLoaction(tank);
					continue;
				}

				//策划配置高度不精确
				float y = (float)readHightMapData.getHight(location.getX(), location.getZ());

				STC_TANK_RELIVE_MSG msg = STC_TANK_RELIVE_MSG.newBuilder()
						.setId(deathInstanceId)
						.setDir(location.getDir())
						.setPosition(Coordinate3D.newBuilder()
								.setPx(location.getX())
								.setPy(y)
								.setPz(location.getZ())
								.build())
						.build();

				tank.setX(location.getX());
				tank.setY(y);
				tank.setZ(location.getZ());
				tank.setAngle(location.getDir());
				tank.setGunAngle(location.getDir());
				//复活消息
				this.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
						BATTLE_MSG.CMD_ID.STC_TANK_RELIVE_VALUE, msg.toByteArray());

				//属性重算
				GameContext.getTankApp().reCalcAttr(tank);

				//添加无敌buff
				GameContext.getBuffApp().putBuff(tank, tank, template.getReviveBuffId());

				//队列中删除
				deathMap.remove(deathInstanceId);

				//重置AI行走路径
				if(tank.isRobot()){
					tank.moveAiSwitch();
					tank.getRoadManager().clearRoads();
				}
			}
		}
	}

	//设置网格占据
	private void setGridNode(float px, float py, boolean state){
		List<Node> nodes = grid.getNearByNodes(px, py);
		for(Node node : nodes){
			if(node != null){
				node.setTankOccupy(state);
			}
		}
	}



	/** 判断出生点是否被占用 */
	private boolean birthPointOccupy(int deathId, float x, float z){
		for(AbstractInstance tank : this.getAllTank()){
			if(tank.getId() == deathId){
				continue;
			}
			float range = Util.range(x, z, tank.getX(), tank.getZ());
			if(range < Constant.TANK_WIDTH){
				logger.debug("出生点被占...");
				return true;
			}
		}
		return false;
	}


	// 通知状态机
	@Override
	public void notify(NotifyState state, int roleId){
		if(state == NotifyState.enter){
			this.stateMachine.notify(state, roleId);
		}
		//比赛开始计时
		if(state == NotifyState.begin){
			if(beginTime == 0){
				beginTime = System.currentTimeMillis();
				//设置战斗记录
				this.updateBattleBeginTime();
			}
		}
	}

	//更新战斗记录的开始时间
	private void updateBattleBeginTime(){
		for(AbstractInstance tank : tanks.values()){
			if(tank.isRobot()){
				continue;
			}
			RoleBattle roleBattle = GameContext.getMatchApp().getLocalRoleBattle(tank.getRoleId());
			if(roleBattle == null){
				continue;
			}
			roleBattle.setCreateTime(System.currentTimeMillis());
			GameContext.getMatchApp().saveRoleBattle(roleBattle);
		}
	}


	/** 返回比赛开始了多长时间 */
	@Override
	public int durationTime(){
		return  (int)((System.currentTimeMillis() - this.beginTime) / 1000);
	}


	@Override
	public String getMapId() {
		return template.getMapId();
	}
	public MapTemplate getTemplate() {
		return template;
	}

	/** 获取天气 */
	@Override
	public int getWeather(){
		return template.getWeather();
	}

	@Override
	public void forceClose() {
		// TODO Auto-generated method stub
		this.stateMachine.forceClose();
	}


}