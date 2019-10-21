package com.ourpalm.tank.app.map.state;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.STC_WAR_REPORT_MSG;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.SportMapInstance;

/**
 * 比赛结束状态
 * @author wangkun
 *
 */
public class EndStateMachine implements StateMachine{
	private static final long RELEASE_TIME = 2 * 60 * 1000; //2分钟后回收
	//战报信息
	private STC_WAR_REPORT_MSG reportMsg ;
	private SportMapInstance mapInstance;
	private long time;
	
	
	public EndStateMachine(STC_WAR_REPORT_MSG reportMsg, SportMapInstance mapInstance){
		this.reportMsg = reportMsg;
		this.mapInstance = mapInstance;
		this.time = System.currentTimeMillis();
	}


	@Override
	public void update() {
		this.release();
	}


	@Override
	public void notify(NotifyState state, int tankInstanceId) {
		if(state == NotifyState.enter){
			AbstractInstance tank = GameContext.getTankApp().getInstance(tankInstanceId);
			if(tank != null){
				tank.sendMsg(reportMsg);
			}
		}
	}
	
	
	//释放资源
	private void release(){
		if(System.currentTimeMillis() - time > RELEASE_TIME){
			//回收地图
			GameContext.getMapApp().removeMapInstance(mapInstance.getInstanceId());
			
			for(int id : mapInstance.getAllTanksId()){
				//回收坦克
				GameContext.getTankApp().removeTankInstance(id);
			}
		}
	}


	@Override
	public boolean hadOver() {
		return true;
	}


	@Override
	public void forceClose() {
	}
}
