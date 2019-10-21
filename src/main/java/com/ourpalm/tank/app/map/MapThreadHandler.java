package com.ourpalm.tank.app.map;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.util.LoopCount;
import com.ourpalm.tank.vo.MapInstance;

public class MapThreadHandler extends Thread{

	private Logger logger = LogCore.runtime;

	//需执行的地图实例ID集合
	private final ConcurrentHashMap<Integer, Integer> mapInstanceIds = new ConcurrentHashMap<>();
	private String id;
	private volatile boolean running = false;
	private int interval = 20;
	private LoopCount mapLoop = new LoopCount(1000, interval); //一秒执行一次地图更新

	public MapThreadHandler(String id){
		this.id = id;
	}

	public void startup(){
		//正在运行
		if(running){
			return ;
		}

		//开始启动
		running = true;

		start();
	}


	public void add(int mapInstanceId){
		mapInstanceIds.put(mapInstanceId, mapInstanceId);
	}



	@Override
	public void run(){
		while(running){
			long startTime = System.currentTimeMillis();

			//执行心跳
			pulse();

			//统计时长
			long finishTime = System.currentTimeMillis();
			long timeRunning = finishTime - startTime;
			if(timeRunning > 1000){
				LogCore.system.warn("执行器 id={}, 本次心跳时长较长,达到 time={}", id, timeRunning);
			}
			try {
				if(timeRunning < interval) {
					Thread.sleep(interval - timeRunning);
				} else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
		}
	}




	private void pulse(){
		//地图更新
		if(mapLoop.isReachCycle()){
			this.mapUpdate();
		}

		//伤害战斗计算
		this.hitPulse();
	}

	//伤害战斗计算
	private void hitPulse(){
		for(int mapInstanceId : mapInstanceIds.values()){
			try{
				MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
				if(mapInstance == null){
					mapInstanceIds.remove(mapInstanceId);
					continue;
				}
				Queue<HitParam> hitQueue = mapInstance.getHitQueue();
				while(!hitQueue.isEmpty()){
					HitParam hitParam = hitQueue.poll();
					if (hitParam != null) {
						if (GameContext.getBattleNew() == 1) {
							GameContext.getBattleApp().hitTankNew(hitParam);
						} else {
							GameContext.getBattleApp().hitTank(hitParam);
						}
						
						if (hitParam.getCallBack() != null) {
							hitParam.getCallBack().call(hitParam.getSource(), hitParam.getTarget());
						}
					}
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}


	private void mapUpdate(){
		for(int mapInstanceId : mapInstanceIds.values()){
			try{
				MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
				if(mapInstance == null){
					mapInstanceIds.remove(mapInstanceId);
					continue;
				}
				mapInstance.update();
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
	
	public void mapForceClose(){
		for(int mapInstanceId : mapInstanceIds.values()){
			try{
				MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceId);
				if(mapInstance == null){
					mapInstanceIds.remove(mapInstanceId);
					continue;
				}
				mapInstance.forceClose();
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
}
