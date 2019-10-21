package com.ourpalm.tank.app.map.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.app.map.astar.AStar;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.app.map.astar.Point;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AI_MOVE_MSG;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.template.GridTemplate;
import com.ourpalm.tank.template.MapDataTemplate;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;

public class TestAIPath extends Ai {
	// 不可行走BUFF效果ID
	private final static int STOP_EFFECT_ID = 2;
	private final static int WAIT_MAX_COUNT = 12;

	private AStar astar; // 寻路对象
	private int waitCount; // 网格被占时，等待次数
	private Node targetNode; // 所前往的目标节点

	public TestAIPath(AbstractInstance self) {
		super(self);
		this.astar = new AStar(grid, false);
		this.loopCount = new LoopCount(1000, AIThreadHandler.AI_LOOP_TIME);
	}

	public TestAIPath() {
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

		// 寻找行走路径
		if (!GameContext.isUseAiPath()) {
			this.searchRoad();
		}

		// 没找到路径返回
		if (Util.isEmpty(roads)) {
			return;
		}

		// 行走
		this.walk();

		if (this.loopCount.isReachCycle()) {

		}
	}

	@Override
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
		if (targetNode != null) {
			this.targetNode.setTankOccupy(false);
		}

		this.target = null;
		this.targetNode = null;
	}

	@Override
	public AiType getType() {
		return AiType.move;
	}

	public static void main(String[] args) {

		TestAIPath t = new TestAIPath();
		Grid grid = t.loadMapDataTemplate();
		// 寻找到达路径
//		int targetX = 200;
//		int targetY = 158;
//		int selfX = 197;
//		int selfY = 151;
		
		int targetX = 123;
		int targetY = 123;
		int selfX = 888;
		int selfY = 888;
		Node targetNode = grid.getNodeByWoldPoint(targetX, targetY);
		Node selfNode = grid.getNodeByWoldPoint(selfX, selfY);

		// System.out.println("目标点-----X="+targetNode.getPx() + "
		// Z="+targetNode.getPz() + " 自身点 X="+selfNode.getPx() + "
		// Z="+selfNode.getPz());

		t.astar.setStartNode(selfNode);
		t.astar.setEndNode(targetNode);
		long start = System.currentTimeMillis();
		if (t.astar.findPath(grid)) {
			t.roads.clear();
			t.roads.addAll(t.astar.getPath());
			for (Node node : t.roads) {
				System.out.println(node.getPx());
			}
			System.err.println("节点数量：" + t.roads.size());
			// 删除第一个自身点
			if (!Util.isEmpty(t.roads)) {
				t.roads.pop();
			}
		}
		long now = System.currentTimeMillis();
		System.err.println("总时间为：" + (now - start));
	}

	/** 加载地图网格数据 */
	private Grid loadMapDataTemplate() {
		String fileName = "";
		try {
			File file = new File("D:\\workspace\\Tank_Server\\trunk\\tank\\conf\\mapdata\\TieLuZhengDuoZhanMap.json");
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader br = new BufferedReader(read);
			StringBuffer strBuf = new StringBuffer();
			String str = null;
			while ((str = br.readLine()) != null) {
				if (Util.isEmpty(str)) {
					continue;
				}
				strBuf.append(str);
			}
			br.close();
			MapDataTemplate template = JSON.parseObject(strBuf.toString(), MapDataTemplate.class);
			Grid grid = new Grid();
			grid.setCellSize(template.getCellSize());
			grid.setCols(template.getCols());
			grid.setDeviationX(template.getDeviationX());
			grid.setDeviationZ(template.getDeviationZ());
			grid.setRows(template.getRows());

			Map<Integer, List<Node>> nodeMap = new HashMap<>();
			for (GridTemplate gridTmp : template.getNodeList()) {
				int x = gridTmp.getX();
				Node node = new Node(gridTmp.getIndex(), x, gridTmp.getZ());
				node.setPx(gridTmp.getPx());
				node.setPy(gridTmp.getPy());
				node.setPz(gridTmp.getPz());
				node.setWalkable(gridTmp.getWalk() == 1);

				List<Node> list = nodeMap.get(x);
				if (list == null) {
					list = new ArrayList<>();
					nodeMap.put(x, list);
				}
				list.add(node);
			}
			grid.setNodeMap(nodeMap);
			return grid;
		} catch (Exception e) {
			LogCore.startup.error("加载地图网格数据异常...fileName = " + fileName, e);
		}
		return grid;
	}

	// 寻找路径
	private void searchRoad() {
		if (!Util.isEmpty(roads)) {
			return;
		}
		// System.out.println(self.getTeam().toString() + " " +
		// self.getRoleName() + " 开始寻找路-----");
		// 寻找所属队伍的点
		List<Point> points = GameContext.getMapApp().getAIPoint(mapInstance.getMapId(), pointLvl, teamId);
		if (Util.isEmpty(points)) {
			pointLvl = self.getAiPathPoint().randomPointType();
			logger.error("!!!!!!! 点类型{}, 没有配置 队伍{}, 类型的行径点集合...", pointLvl, teamId);
			return;
		}

		int index = RandomUtil.randomInt(points.size());
		Point point = points.get(index);

		// 寻找到达路径
		Node targetNode = grid.getNode((int) point.getX(), (int) point.getY());
		Node selfNode = grid.getNodeByWoldPoint(self.getX(), self.getZ());

		// System.out.println("目标点-----X="+targetNode.getPx() + "
		// Z="+targetNode.getPz() + " 自身点 X="+selfNode.getPx() + "
		// Z="+selfNode.getPz());

		astar.setStartNode(selfNode);
		astar.setEndNode(targetNode);

		if (astar.findPath(grid)) {
			this.roads.clear();
			this.roads.addAll(astar.getPath());
			// 删除第一个自身点
			if (!Util.isEmpty(this.roads)) {
				this.roads.pop();
			}
		}
	}

	// 行走
	private void walk() {
		if (Util.isEmpty(roads)) {
			return;
		}
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

			// System.out.println("第一个坐标点 X="+targetNode.getPx() + "
			// Z="+targetNode.getPz());

			// 判断目标格子已被占据
			if (targetNode.isTankOccupy()) {
				if (!GameContext.isUseAiPath()) {
					// 换条路走
					if (this.waitCount >= WAIT_MAX_COUNT) {
						this.pointLvl = GameContext.getRoleArmyTitleApp().nextAiPointType(this.pointLvl);
						this.roads.clear();
						this.waitCount = 0;
					}
					this.waitCount += 1;
				}
				return;// 等一会
			}
			roads.pop();
			this.targetNode = targetNode;
			targetNode.setTankOccupy(true);
		}

		// 判断是否有目标点
		// 计算一次循环所移动的距离
		float way = self.get(AttrType.n_speed) /10 * AIThreadHandler.AI_LOOP_TIME / 1000f;

		// 判断是否到达目标点
		float range = Util.range(x, z, targetNode.getPx(), targetNode.getPz());
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
		}

		// 设置目标点
		if (range < way) {
			targetNode.setTankOccupy(false);
			this.targetNode = null;
		}
	}

}
