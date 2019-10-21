package com.ourpalm.tank.app.map.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.app.map.astar.AStar;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.app.map.astar.Point;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_MOVE_MSG;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 逃跑AI
 * 
 * @author fhy
 *
 */
public class EscapeAi extends Ai {
	// 不可行走BUFF效果ID
	private final static int STOP_EFFECT_ID = 2;
	private final static int WAIT_MAX_COUNT = 12;
	//AI坦克转向的时候，避免瞬间转向，则对转向加入了等待时间，该定义为每一帧转动的角度
	private final static int STOP_TURN_ANGLE = 20;
	
	//AI坦克转向的时候，可以不停顿直接行走的最小角度
	private final static int MIN_TURN_ANGLE = 45;
	
	private AStar astar; // 寻路对象
	private int waitCount; // 网格被占时，等待次数
	private Node targetNode; // 所前往的目标节点
	private double angle = -1;			//坦克车身角度
	private double targetAngle = -1;	//前进目标坦克角度
	
	public EscapeAi(AbstractInstance self) {
		super(self);
		this.astar = new AStar(grid, false);
		this.loopCount = new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME);
	}

	@Override
	public void init() {
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
		// 死亡状态，将等待复活
		if (self.isDeath()) {
			return;
		}
		
		SearchRoadRet ret = this.searchRoad();
		// 寻路
		if( ret == SearchRoadRet.noFind || ret == SearchRoadRet.end ){
			// 如果是找不到路，就放弃逃跑AI了，如果不放弃，可能会出现AI一直卡住不切换问题
			self.setEscape(true);
			self.moveAiSwitch();
			return;
		}
		// 逃跑
		this.escape();
	}

	@Override
	public void clearRoads() {
		if (roads != null) {
			roads.clear();
		}
		this.pointLvl = self.getAiPathPoint().randomPointType();
		// 坦克死亡复活,设置点为复活点
		this.x = self.getX();
		this.y = self.getY();
		this.z = self.getZ();
		
		//释放格子
		if(targetNode != null){
			this.targetNode.setTankOccupy(false);
		}
		
		this.targetNode = null;
		this.angle = self.getAngle();
		this.targetAngle= self.getAngle();
	}

	@Override
	public AiType getType() {
		return AiType.escape;
	}

	// 寻找路径
	private SearchRoadRet searchRoad() {
		if (!Util.isEmpty(roads)) {
			return SearchRoadRet.noEnd;
		}
		//System.out.println(self.getTeam().toString() + " " + self.getRoleName() + "  开始逃跑寻路-----");

		// 寻找到达路径
		Node targetNode = grid.getNodeByWoldPoint(self.getBirthLocation().getX(), self.getBirthLocation().getZ());
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());

		if( targetNode == selfNode ){
			return SearchRoadRet.end;
		}
		
		astar.setStartNode(selfNode);
		astar.setEndNode(targetNode);
		
		Map<Integer, Set<Integer>> ignoreNode = new HashMap<>();
		List<Node> beforeNodes = grid.getNearByNodes(self.getBirthLocation().getX(), self.getBirthLocation().getZ());
		for(Node node : beforeNodes){
			if( !ignoreNode.containsKey(node.getX())){
				Set<Integer> set = new HashSet<>();
				ignoreNode.put(node.getX(), set);
			}
			
			ignoreNode.get(node.getX()).add(node.getY());
		}
		
		if (astar.findPath(grid)) {
			this.roads.clear();
			this.roads.addAll(astar.getPath());
			// 删除第一个自身点
			if (!Util.isEmpty(this.roads)) {
				this.roads.pop();
			} else {
				return SearchRoadRet.noFind;
			}
		}
		return SearchRoadRet.find;
	}

	// 逃跑
	private void escape() {

		if (Util.isEmpty(roads)) {
			return;
		}
		// 设置逃跑状态
		self.setEscape(true);

		// 判断是否有阻碍行走类的BUFF效果
		for (IBuff buff : self.getBuffers()) {
			if (buff.getEffectId() == STOP_EFFECT_ID) {
				return;
			}
		}
		// 设置目标点
		if (targetNode == null) {
			// 去第一个坐标点
			Node targetNode = roads.peek();

			// 判断目标格子已被占据
			if (targetNode.isTankOccupy()) {
				// 换条路走
				if (this.waitCount >= WAIT_MAX_COUNT) {
					this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
					this.roads.clear();
					this.waitCount = 0;
				}
				this.waitCount += 1;
				return;// 等一会
			}
			roads.pop();
			this.targetNode = targetNode;
			targetNode.setTankOccupy(true);
		}

		// 判断是否有目标点
		// 计算一次循环所移动的距离
		float way = self.get(AttrType.speed) * AIThreadHandler.AI_LOOP_TIME / 1000f;
		// 判断是否到达目标点
		float range = Util.range(x, z, targetNode.getPx(), targetNode.getPz());
		//System.out.println("range=" + range);
		if (range <= 0) {
			// 设置目标点
			targetNode.setTankOccupy(false);
			this.targetNode = null;

			// 设置坦克位置
			self.setX(this.x);
			self.setY(this.y);
			self.setZ(this.z);

			return;
		}

		// 计算坦克这次心跳时间内所需到达的位置
		float x = this.x + ((targetNode.getPx() - this.x) * (way / range));
		float z = this.z + ((targetNode.getPz() - this.z) * (way / range));
		float y = (float) this.readHightMapData.getHight(x, z);

		if( !this.calculateTrunAngle( this.x, this.z, x, z )){
			return;
		}
		
		// 之前格子释放
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		selfNode.setTankOccupy(false);
		
		// 设置坦克位置
		self.setX(this.x);
		self.setY(this.y);
		self.setZ(this.z);

		// 当前格子占住
		selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		selfNode.setTankOccupy(true);

		// 通知客户端
		STC_AI_MOVE_MSG moveMsg = STC_AI_MOVE_MSG.newBuilder().setId(self.getId()).setPosition(Coordinate3D.newBuilder().setPx(x).setPy(y).setPz(z)).setTarget(0).build();
		mapInstance.brodcastMsg(moveMsg);

		// 设置未来目标点
		this.x = x;
		this.y = y;
		this.z = z;

		// 判断路是否走到尽头
		if (Util.isEmpty(roads)) {
			this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
			self.moveAiSwitch();
		}
		// 设置目标点
		if (range < way) {
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
}
