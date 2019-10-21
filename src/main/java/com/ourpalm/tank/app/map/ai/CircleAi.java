package com.ourpalm.tank.app.map.ai;

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ourpalm.core.service.ServerHolder;
import com.ourpalm.core.service.Service;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.app.map.MapAppImpl;
import com.ourpalm.tank.app.map.ReadHightMapData;
import com.ourpalm.tank.app.map.astar.AStar;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_MOVE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.PointXZ;

/**
 * 绕圈射击AI
 * 
 * @author fhy
 *
 */
public class CircleAi extends Ai {
	// 不可行走BUFF效果ID
	private final static int STOP_EFFECT_ID = 2;
	private final static int WAIT_MAX_COUNT = 12;
	private AStar astar; // 寻路对象
	private int waitCount; // 网格被占时，等待次数
	private Node targetNode; // 所前往的目标节点
	private boolean reached = true;// 到没到达最终点

	public CircleAi(AbstractInstance self) {
		super(self);
		this.astar = new AStar(grid, false);
		this.loopCount = new LoopCount(2000, AIThreadHandler.AI_LOOP_TIME);

	}

	@Override
	public void reset(AbstractInstance target) {
		super.reset(target);
		this.x = self.getX();
		this.y = self.getY();
		this.z = self.getZ();
	}

	@Override
	public void update() {
		// 死亡状态，将等待复活
		if (self.isDeath()) {
			return;
		}
		System.out.println("开始绕圈");
		if (target == null) {
			// 如果 上一次是追击，继续追击，否则就移动
			if (self.getLastAiType() == AiType.pursue) {
				changeAI(AiType.pursue);
				return;
			}
			self.aiTypeSwitch(AiType.move);
			return;
		}

		if (target == null) {
			changeAI(AiType.move);
			return;
		}
		// 走路逻辑
		walk();
//		if (loopCount.isReachCycle()) {
			// 目标消失或者死亡就切移动
			if (target == null || target.isDeath()) {
				changeAI(AiType.move);
				return;
			}
			// 绕圈打点
			walkCircleNext();
			// 开火
			if (isFire(target)) {
				changeAI(AiType.fire);
			}
//		}
	}

	@Override
	public AiType getType() {
		return AiType.circle;
	}

	public boolean walkCircleNext() {
		float range = Util.range(self.getX(), self.getZ(), target.getX(), target.getZ());
		if (range > 50 || range < 8) {
			System.out.println("距离不对，脱离AI range=" + range + "，切为move");
			changeAI(AiType.move);
			return false;
		}

		if (this.reached == false) {
			return false;
		}

		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());
		// Node targetNode = grid.getNodeByWoldPoint(target.getX(),
		// target.getZ());
		// if (selfNode.equals(targetNode)) {
		// System.out.println("在同一个格子里，不执行狗屁绕圈");
		// changeAI();
		// return false;
		// }
		System.out.println("当前位置 = " + self.getX() + "_" + self.getZ());

		PointXZ pointXZ = getNewPoint(-45);

		System.out.println("要到的   " + pointXZ.getX() + "_" + pointXZ.getZ());
		Node newTargetNode = grid.getNodeByWoldPoint(pointXZ.getX(), pointXZ.getZ());

		astar.setStartNode(selfNode);
		astar.setEndNode(newTargetNode);
		// 坦克死亡复活,设置点为复活点
		this.x = self.getX();
		this.y = self.getY();
		this.z = self.getZ();

		if (astar.findPath(grid)) {
			this.roads.clear();
			this.roads.addAll(astar.getPath());
			// 删除第一个自身点
			if (!Util.isEmpty(this.roads)) {
				this.roads.pop();
			}
			System.out.println("A星打出来的点");
			for (Node node : roads) {
				System.out.println(node.getPx() + "  " + node.getPz());
			}
			if (roads.size() < 1) {
				System.out.println("绕圈打点打不出来，切换为move");
				changeAI(AiType.move);
				return false;
			}
		}

		return true;
	}

	public void changeAI(AiType aiType) {
		System.out.println("绕圈切换为  " + aiType.name());
		self.aiTypeSwitch(AiType.move);
	}

	public PointXZ getNewPoint(int angle) {
		float newMinusX = (float) Math.abs((self.getX() - target.getX()) * Math.cos(angle * 3.14 / 180) - (self.getZ() - target.getZ()) * Math.sin(angle * 3.14 / 180) + target.getX());
		float newMinusZ = (float) Math.abs((self.getX() - target.getX()) * Math.sin(angle * 3.14 / 180) + (self.getZ() - target.getZ()) * Math.cos(angle * 3.14 / 180) + target.getZ());
		PointXZ p = new PointXZ();
		p.setX(newMinusX);
		p.setZ(newMinusZ);
		return p;
	}

	// 行走
	private void walk() {
		if (Util.isEmpty(roads) && targetNode == null) {
			return;
		}
		// 判断是否有阻碍行走类的BUFF效果
		for (IBuff buff : self.getBuffers()) {
			if (buff.getEffectId() == STOP_EFFECT_ID) {
				return;
			}
		}
		reached = false;
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
		System.out.println(String.format("当前位置  x=%s,z=%s,y=%s", x, z, y));
		// 判断是否有目标点
		// 计算一次循环所移动的距离
		float way = self.get(AttrType.n_speed)/10 * AIThreadHandler.AI_LOOP_TIME / 1000f;

		// 判断是否到达目标点
		float range = Util.range(x, z, targetNode.getPx(), targetNode.getPz());

		System.out.println(String.format("目标点位置  x=%s,z=%s", targetNode.getPx(), targetNode.getPz()));
		System.out.println("range=" + range);
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
		System.out.println(String.format("心跳要达到的位置 x=%s,z=%s,y=%s", x, z, y));
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

		// 设置目标点
		if (range < way) {
			targetNode.setTankOccupy(false);
			this.targetNode = null;
			if (roads.isEmpty()) {
				reached = true;
			}
		}
	}

	public static void main(String[] args) {
		ReadHightMapData readHightMapData;
		CircleAi c = new CircleAi(null);
		ClassPathXmlApplicationContext context = null;
		try {
			String mainContextFilePath = "spring.conf.dir";
			try {
				java.util.ResourceBundle rb = ResourceBundle.getBundle("configure");
				mainContextFilePath = rb.getString("spring.conf.dir");
			} catch (Throwable e) {
			}
			// 多路径用;分隔
			String[] paths = mainContextFilePath.split(";");
			String[] load = new String[paths.length];
			int i = 0;
			for (String path : paths) {
				load[i] = path + "/*.xml";
				i++;
			}
			context = new ClassPathXmlApplicationContext(load);
		} catch (Exception e) {
			logger.error("", e);
		}

		MapAppImpl mapApp = null;
		ServerHolder holder = (ServerHolder) context.getBean("serverHolder");

		// 启动功能模块
		Map<String, Service> initMap = holder.getInitServiceMap();
		if (!Util.isEmpty(initMap)) {

			for (Iterator<Map.Entry<String, Service>> initIt = initMap.entrySet().iterator(); initIt.hasNext();) {
				Map.Entry<String, Service> entry = initIt.next();
				if (entry.getKey().equals("goodsApp") || entry.getKey().equals("mapApp")) {
					entry.getValue().start();
					if (entry.getKey().equals("mapApp")) {
						mapApp = (MapAppImpl) entry.getValue();
					}
					logger.info("init res: " + entry.getKey() + " success");
				}
			}
		}
		MapInstance mapInstance = mapApp.createSportMapInstance(5, WAR_TYPE.BATTLE_VALUE,1);
		// 复制一份网格信息
		// readHightMapData = mapInstance.getReadHightMapData();
		c.setGrid((Grid) mapInstance.getGrid().clone());
		// c.init();
		AStar astar = new AStar(c.getGrid(), false);
		// 当前位置 192.29102 297.70898
		// 要到的 197.59554 288.09827

		Node startNode = c.getGrid().getNodeByWoldPoint(192.29102f, 297.70898f);
		Node endNode = c.getGrid().getNodeByWoldPoint(197.59554f, 288.09827f);
		astar.setStartNode(startNode);
		astar.setEndNode(endNode);
		boolean flag = astar.findPath(c.getGrid());
		System.out.println(flag);
		if (flag) {
			astar.getPath();
			for (Node n : astar.getPath()) {
				System.out.println(n.getPx() + "_" + n.getPz());
			}
		}

	}
}
