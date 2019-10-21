package com.ourpalm.tank.app.map;

import static com.ourpalm.tank.app.GameContext.getMapApp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.astar.AStar;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.app.map.astar.Point;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_MOVE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.template.TankAiTestTemplate;
import com.ourpalm.tank.type.AIPointType;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class RoadManager {

	protected static final Logger logger = LogCore.runtime;

	// 不可行走BUFF效果ID
	private final static int STOP_EFFECT_ID = 2;
	private final static int WAIT_MAX_COUNT = 12;
	// AI坦克转向的时候，避免瞬间转向，则对转向加入了等待时间，该定义为每一帧转动的角度
	private final static int STOP_TURN_ANGLE = 20;
	// AI坦克转向的时候，可以不停顿直接行走的最小角度
	private final static int MIN_TURN_ANGLE = 45;

	private AStar astar; // 寻路对象
	private int waitCount; // 网格被占时，等待次数
	private int indexNode = 0; // AI 指定路径的当前路径节点下标值
	private double angle = -1; // 坦克车身角度
	private double targetAngle = -1; // 前进目标坦克角度

	protected AbstractInstance self; // 自身坦克
	protected MapInstance mapInstance; // 所在地图实例
	protected Grid grid; // 地图网格信息
	protected ReadHightMapData readHightMapData; // 地图高度数据

	protected Stack<Node> roads = new Stack<>(); // 道路
	protected float x;
	protected float y;
	protected float z;
	protected int pointLvl; // 点级别
	protected Node next;

	protected LoopCount loopCount = new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME);

	protected SearchStrategy strategy = SearchStrategy.DEFAULT;

	/**
	 * 寻找下一个目标点的策略
	 *
	 * @author zhix
	 *
	 */
	public enum SearchStrategy {

		DEFAULT {

			@Override
			public Node getTargetNode(RoadManager rm) {
				// 寻找所属队伍的点
				int teamId = rm.self.getTeam().getNumber();
				List<Point> points = GameContext.getMapApp().getAIPoint(rm.mapInstance.getMapId(), rm.pointLvl, teamId);
				if (Util.isEmpty(points)) {
					rm.pointLvl = rm.self.getAiPathPoint().randomPointType();
					logger.error("!!!!!!! 点类型{}, 没有配置 队伍{}, 类型的行径点集合...", rm.pointLvl, teamId);
					return null;
				}

				int index = RandomUtil.randomInt(points.size());
				Point point = points.get(index);

				return rm.grid.getNode((int) point.getX(), (int) point.getY());
			}

			@Override
			public Node getTargetNodeWithDefaultStrategy(RoadManager rm) {
				return getTargetNode(rm);
			}
		},

		ESCAPE {

			@Override
			public Node getTargetNode(RoadManager rm) {
				Location birth = rm.self.getBirthLocation();
				return rm.grid.getNodeByWoldPoint(birth.getX(), birth.getZ());
			}

			@Override
			public boolean disableAttack() {
				return true;
			}
		},

		ESCAPE_UNTIL_INVISIBLE {

			@Override
			public Node getTargetNode(RoadManager rm) {
				AbstractInstance self = rm.self;
				AbstractInstance attacker = self.getAttacker();

				if (attacker == null || attacker.isDeath() || !attacker.view(self)) {
					if (attacker != null) {
						attacker.setFireTarget(null);
					}
					self.setAttacker(null);
					return getTargetNodeWithDefaultStrategy(rm);
				}

				return ESCAPE.getTargetNode(rm);
			}
		},

		PURSUE {

			@Override
			public Node getTargetNode(RoadManager rm) {
				AbstractInstance self = rm.self;
				AbstractInstance pursueTarget = self.getFireTarget();
				if (pursueTarget == null || pursueTarget.isDeath() || !rm.self.view(pursueTarget)) {
					self.setFireTarget(null);
					return getTargetNodeWithDefaultStrategy(rm);
				}
				return rm.grid.getNodeByWoldPoint(pursueTarget.getX(), pursueTarget.getZ());
			}

		},

		/** 远离战场策略 */
		STAY_AWAY_FROM_BATTLEFIELD {

			@Override
			public Node getTargetNode(RoadManager rm) {
				AbstractInstance self = rm.self;
				// 确定方向
				// 获得视野范围内的所有敌人坐标
				// 计算向量
				// 寻找离自身最近并在射程内的目标
				MapInstance map = getMapApp().getMapInstance(self.getMapInstanceId());
				if (map == null) {
					return DEFAULT.getTargetNode(rm);
				}

				Set<AbstractInstance> enemy = new HashSet<>();
				for(AbstractInstance tank : map.getAllTank()) {

					if (tank.isDeath()
							|| tank.getId() == self.getId()
							|| tank.getTeam() == self.getTeam()
							|| !self.viewSelf(tank)) {
						continue;
					}

					enemy.add(tank);
				}

				if (Util.isEmpty(enemy)) {
					rm.strategy = SearchStrategy.DEFAULT;
					return DEFAULT.getTargetNode(rm);
				}

				int n = enemy.size();
				float x = 0;
				float y = 0;
				for (AbstractInstance tank : enemy) {
					x += tank.getX();
					y += tank.getZ();
				}
				x = n * self.getX() - x;
				y = n * self.getZ() - y;

				Node selfNode = rm.grid.getNodeByWoldPoint(self.getX(), self.getZ());
				Node next = null;
				int i = 0;

				do {
					next = rm.grid.getNodeWithDirection(selfNode, x, y);
					if (i > 10) break;
					i++;
				} while (next == null || !next.isWalkable());

				return next == null ? getTargetNodeWithDefaultStrategy(rm) : next;
			}
		},

		/** 向外圈移动策略 */
		OUTER_SPACE_PREFERRED {

			@Override
			public Node getTargetNode(RoadManager rm) {
				// 寻找所属队伍的点
				int teamId = rm.self.getTeam().getNumber();
				rm.pointLvl = AIPointType.outside.type;
				List<Point> points = GameContext.getMapApp().getAIPoint(rm.mapInstance.getMapId(), rm.pointLvl, teamId);
				if (Util.isEmpty(points)) {
					rm.pointLvl = rm.self.getAiPathPoint().randomPointType();
					logger.error("!!!!!!! 点类型{}, 没有配置 队伍{}, 类型的行径点集合...", rm.pointLvl, teamId);
					return getTargetNodeWithDefaultStrategy(rm);
				}

				int index = RandomUtil.randomInt(points.size());
				Point point = points.get(index);

				return rm.grid.getNode((int) point.getX(), (int) point.getY());
			}
		},

		/** 点灯策略 */
		SCOUT {

			@Override
			public Node getTargetNode(RoadManager rm) {

				// middle out 为点灯路径
				int teamId = rm.self.getTeam().getNumber();
				rm.pointLvl = AIPointType.outside_middle.type;

				List<Point> points = GameContext.getMapApp().getAIPoint(rm.mapInstance.getMapId(), rm.pointLvl, teamId);
				if (Util.isEmpty(points)) {
					rm.pointLvl = rm.self.getAiPathPoint().randomPointType();
					logger.error("!!!!!!! 点类型{}, 没有配置 队伍{}, 类型的行径点集合...", rm.pointLvl, teamId);
					return getTargetNodeWithDefaultStrategy(rm);
				}

				int index = RandomUtil.randomInt(points.size());
				Point point = points.get(index);

				return rm.grid.getNode((int) point.getX(), (int) point.getY());
			}
		}

		;

		public abstract Node getTargetNode(RoadManager rm);

		public boolean disableAttack() {
			return false;
		}

		public Node getTargetNodeWithDefaultStrategy(RoadManager rm) {
			rm.strategy = DEFAULT;
			return rm.strategy.getTargetNode(rm);
		}
	}

	public enum SearchRoadRet {

		find,   // 找到
		noFind, // 找不到路
		end,    // 已经走到目标点，无需再找
		noEnd,  // 路还没走完
		;
	}

	public RoadManager(AbstractInstance self) {
		this.self = self;
	}

	public void initialize() {
		mapInstance = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
		// 复制一份网格信息
		this.grid = (Grid) mapInstance.getGrid().clone();
		this.readHightMapData = mapInstance.getReadHightMapData();
		this.astar = new AStar(grid, false);
		clearRoads();
	}

	public void clearRoads() {

		if (roads != null) {
			roads.clear();
		}

		pointLvl = self.getAiPathPoint().randomPointType();
		// 坦克死亡复活,设置点为复活点
		this.x = self.getX();
		this.y = self.getY();
		this.z = self.getZ();

		// 释放格子
		if (next != null) {
			next.setTankOccupy(false);
		}

		next = null;
		indexNode = 0;
		angle = self.getAngle();
		targetAngle = self.getAngle();
	}

	public boolean walk() {

		if (Util.isEmpty(roads)) {
			return false;
		}

		// 判断是否有阻碍行走类的BUFF效果
		for (IBuff buff : self.getBuffers()) {
			if (buff.getEffectId() == STOP_EFFECT_ID) {
				return false;
			}
		}

		// 设置目标点
		if (next == null) {
			// 去第一个坐标点
			Node node = roads.peek();

			// System.out.println("第一个坐标点 X="+targetNode.getPx() + "
			// Z="+targetNode.getPz());

			// 判断目标格子已被占据
			if (node.isTankOccupy()) {
				if (!GameContext.isUseAiPath()) {
					// 换条路走
					if (this.waitCount >= WAIT_MAX_COUNT) {
						this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
						this.roads.clear();
						this.waitCount = 0;
					}
					this.waitCount += 1;
				}
				return false;// 等一会
			}

			roads.pop();

			next = node;
			next.setTankOccupy(true);
		}

		// 判断是否有目标点
		// 计算一次循环所移动的距离
		float way = self.get(AttrType.n_speed) / 10 * AIThreadHandler.AI_LOOP_TIME / 1000f;

		// 判断是否到达目标点
		float range = Util.range(x, z, next.getPx(), next.getPz());
		if (range <= 0) {
			// 设置目标点
			next.setTankOccupy(false);
			next = null;

			// 设置坦克位置
			self.setX(this.x);
			self.setY(this.y);
			self.setZ(this.z);

			return next != null;
		}

		// 计算坦克这次心跳时间内所需到达的位置
		float x = this.x + ((next.getPx() - this.x) * (way / range));
		float z = this.z + ((next.getPz() - this.z) * (way / range));
		float y = (float) this.readHightMapData.getHight(x, z);

		if (!this.calculateTrunAngle(this.x, this.z, x, z)) {
			return false;
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
		syncCoordinate(null);

		// 设置未来目标点
		this.x = x;
		this.y = y;
		this.z = z;

		// 判断路是否走到尽头
		if (Util.isEmpty(roads)) {
			this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
		}

		// 设置目标点
		if (range < way) {
			next.setTankOccupy(false);
			next = null;
		}

		return next != null;
	}

	// 计算旋转角度，返回 false 表示不需要旋转直接前进
	private boolean calculateTrunAngle(float beginX, float beginY, float endX, float endY) {

		// 计算坦克车身角度
		float ax = endX - beginX;
		float az = endY - beginY;
		this.targetAngle = Math.toDegrees(Math.atan2(az, ax));
		if (this.targetAngle < 0) {
			this.targetAngle = 360 - Math.abs(this.targetAngle);
		}
		if (this.angle == -1) {
			this.angle = targetAngle;
		}

		boolean needTurned = false;
		@SuppressWarnings("unused")
		double oldAngle = this.angle;
		// 计算原地转向车身逻辑
		double includedAngle = this.targetAngle - this.angle;
		if (includedAngle >= MIN_TURN_ANGLE && includedAngle < 180) {
			this.angle = this.angle + STOP_TURN_ANGLE;
			needTurned = true;
		} else if (includedAngle >= 180 && includedAngle <= (360 - MIN_TURN_ANGLE)) {
			this.angle = this.angle - STOP_TURN_ANGLE;
			needTurned = true;
		} else if (includedAngle <= -MIN_TURN_ANGLE && includedAngle > -180) {
			this.angle = this.angle - STOP_TURN_ANGLE;
			needTurned = true;
		} else if (includedAngle <= -180 && includedAngle >= -(360 - MIN_TURN_ANGLE)) {
			this.angle = this.angle + STOP_TURN_ANGLE;
			needTurned = true;
		}

		if (this.angle > 360) {
			this.angle = this.angle - 360;
		}
		if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}

		if (needTurned) {
			// if (self.getMemberId() == 2 &&
			// self.getTeam().toString().equals("BLUE") ){
			// logger.error(" BLUE {} old [ x {} y {} ], new [ x {} y {} ], a [
			// x {} y {} ], oldAngle {} this.angle {},this.targetAngle {} ",
			// self.getRoleName(), this.x, this.z, x, z, ax, az, oldAngle,
			// this.angle, this.targetAngle );
			// }
			return false;
		}

		this.angle = this.targetAngle;
		return true;
	}

	// 寻找路径
	public void searchRoad() {

		if (!Util.isEmpty(roads)) {
//			if (strategy == SearchStrategy.ESCAPE) {
//				self.setEscape(false);
//				this.strategy = SearchStrategy.DEFAULT;
//				System.out.println(self.getId() + " STOP ESCAPING");
//			}
			if (strategy == SearchStrategy.PURSUE && Math.random() < 0.5) {
				clearRoads();
			}
			return;
		}

		// 根据策略的不同寻找到达路径
		Node targetNode = strategy.getTargetNode(this);
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());

		if (targetNode == null) {
			return;
		}

		if (targetNode == selfNode && strategy == SearchStrategy.ESCAPE) {
			this.strategy = SearchStrategy.DEFAULT;
			System.out.println(self.getId() + " STOP ESCAPING");
		}

		// System.out.println("目标点-----X="+targetNode.getPx() + "
		// Z="+targetNode.getPz() + " 自身点 X="+selfNode.getPx() + "
		// Z="+selfNode.getPz());

		astar.setStartNode(selfNode);
		astar.setEndNode(targetNode);

		if (astar.findPath(grid)) {
			roads.clear();
			roads.addAll(astar.getPath());
			// 删除第一个自身点
			if (!Util.isEmpty(roads)) {
				roads.pop();
			}
		}
	}

	// 寻找路径
	public void searchRoadEx() {

		if (!Util.isEmpty(roads)) {
			return;
		}

		int teamId = self.getTeam().getNumber();

		TankAiTestTemplate temp = GameContext.getTankApp().getTankAiTestTemplate(teamId, self.getMemberId());
		List<Point> points = temp.getPoints();

		if (Util.isEmpty(points)) {
			logger.error("!!!!!!! 点类型{}, 没有配置 队伍{}, 类型的行径点集合...", teamId, self.getMemberId());
			return;
		}

		@SuppressWarnings("unused")
		int oldIndexNode = this.indexNode;
		if ((this.indexNode + 1) > points.size()) {
			if (this.indexNode - 2 < 0) {
				return;
			}
			this.indexNode = this.indexNode - 2;
		}

		Point point = points.get(this.indexNode);

		// 寻找到达路径
		Node targetNode = grid.getNodeByWoldPoint((int) point.getX(), (int) point.getY());
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());

		// System.out.println("目标点-----X="+targetNode.getPx() + "
		// Z="+targetNode.getPz() + " 自身点 X="+selfNode.getPx() + "
		// Z="+selfNode.getPz());
		// logger.info("!!!!!!! index {} 点[{},{}] 走到另外一个 index {} 点[{},{} ...",
		// oldIndexNode, selfNode.getX(), selfNode.getY(), this.indexNode,
		// targetNode.getX(), targetNode.getY());

		astar.setStartNode(selfNode);
		astar.setEndNode(targetNode);

		if (astar.findPath(grid)) {
			roads.clear();
			roads.addAll(astar.getPath());
			// 删除第一个自身点
			if (!Util.isEmpty(this.roads)) {
				roads.pop();
				indexNode++;
			}
		}
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public Stack<Node> getCurrentRoads() {
		return roads;
	}

	public boolean roadsEmpty() {
		return Util.isEmpty(roads);
	}

	public boolean reachNextNode() {
		return (next == null) || (Util.range(x, z, next.getPx(), next.getPz()) <= 0);
	}

	public Node nextNode() {
		return next;
	}

	public Grid getAStarGrid() {
		return grid;
	}

	public void syncCoordinate(AbstractInstance target) {
		syncCoordinate(target, x, y, z);
	}

	public void syncCoordinate(AbstractInstance target, float x, float y, float z) {
		//
		STC_AI_MOVE_MSG moveMsg = STC_AI_MOVE_MSG
				.newBuilder()
				.setId(self.getId())
				.setPosition(Coordinate3D.newBuilder().setPx(x).setPy(y).setPz(z))
				.setTarget(target != null ? target.getId() : 0).build();
		mapInstance.brodcastMsg(moveMsg);
	}

	public void rotateTurrent(AbstractInstance target, float x, float y, float z) {
		
		if (target == null) {
			return;
		}

		int timeOfPulse = AIThreadHandler.AI_LOOP_TIME;
		// 一个心跳内能转动的角度
		double pulseAngle = self.get(AttrType.n_turrentSpeep) * timeOfPulse / 1000.0;

		// 自己与目标角度
		float tz = target.getZ();
		float tx = target.getX();
		double angle = Math.atan2(tz - z, tx - x) * (-180) / Math.PI;
		if (angle < 0) {
			angle = angle + 360;
		}
		// 炮塔角度
		double gunAngle = self.getGunAngle();
		// 角度差
		double _angle = Math.abs(self.getGunAngle() - angle);

		if (gunAngle > angle) {
			// 确定方向
			if (_angle < 360 - _angle) {
				// 逆时针转动炮塔，减小角度
				self.setGunAngle(pulseAngle < _angle ? gunAngle - pulseAngle : angle);
			} else {
				// 顺时针转动炮塔，增加角度
				self.setGunAngle(pulseAngle < 360 - _angle ? gunAngle + pulseAngle : angle);
			}
		} else {
			// 确定方向
			if (_angle < 360 - _angle) {
				// 顺时针转动炮塔，增加角度
				self.setGunAngle(pulseAngle < _angle ? gunAngle + pulseAngle : angle);
			} else {
				// 逆时针转动炮塔，减小角度
				self.setGunAngle(pulseAngle < 360 - _angle ? gunAngle - pulseAngle : angle);
			}
		}
	}

	public SearchStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(SearchStrategy strategy) {
		this.strategy = strategy;
	}
}
