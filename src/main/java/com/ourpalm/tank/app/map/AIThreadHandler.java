package com.ourpalm.tank.app.map;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.vo.AITankViewDeal;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class AIThreadHandler extends Thread {
	public static int AI_LOOP_TIME = 250;
	private Logger logger = LogCore.runtime;

	// 需执行的坦克实例ID
	private final ConcurrentHashMap<Integer, Integer> mapInstanceIds = new ConcurrentHashMap<>();
	private String id;
	private volatile boolean running = false;
	private int interval = AI_LOOP_TIME; // 间隔时间250毫秒

	public AIThreadHandler(String id) {
		this.id = id;
	}

	public void startup() {
		// 正在运行
		if (running) {
			return;
		}

		// 开始启动
		running = true;

		start();
	}

	public void add(int mapInstanceId) {
		this.mapInstanceIds.put(mapInstanceId, mapInstanceId);
	}

	@Override
	public void run() {
		while (running) {
			long startTime = System.currentTimeMillis();

			// 执行心跳
			pulse();

			// 统计时长
			long finishTime = System.currentTimeMillis();
			long timeRunning = finishTime - startTime;
			if (timeRunning > 250) {
				LogCore.system.info("执行器 id={}, 本次心跳时长较长,达到 time={}", id, timeRunning);
			}
			try {
				if (timeRunning < interval) {
					Thread.sleep(interval - timeRunning);
				} else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
		}
	}



	private void pulse() {

		try {
			for (int mapInstanceId : mapInstanceIds.values()) {
				MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
				if (mapInstance == null) {
					mapInstanceIds.remove(mapInstanceId);
					continue;
				}

				// 地图没有玩家将不执行
				if (!mapInstance.hadPlayer() || mapInstance.getStateMachine().hadOver()) {
					continue;
				}

				// 执行AI
				try {
					for (AbstractInstance tank : mapInstance.getAllTank()) {
						if (!tank.isRobot() || !tank.isAiRunning()) {
							continue;
						}
						if (tank.isUsingFSM()) {
							// 执行状态机
							tank.aiTypeSwitch();
							if(tank.getCurrAi()!=null && tank.getCurrAi().isInited()){
								tank.getCurrAi().rotateTurrent();
								tank.getCurrAi().update();
							}
						} else {
							// 遍历行为树
							tank.performBehavior();
						}
						// 视野更新
						AITankViewDeal.INSTANCE.update(mapInstance, tank, System.currentTimeMillis());
					}
				} catch (Exception e) {
					logger.error("AI异常...", e);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
