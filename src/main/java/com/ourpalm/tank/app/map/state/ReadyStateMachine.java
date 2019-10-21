package com.ourpalm.tank.app.map.state;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_BEGIN_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_PLAYER_READY_MSG;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.SportMapInstance;

/**
 * 比赛准备阶段
 * @author wangkun
 *
 */
public class ReadyStateMachine implements StateMachine{
	private final static Logger logger = LogCore.runtime;
	private final static int READY_OUT_TIME = 30 * 1000; //超时30秒
	private SportMapInstance mapInstance;
	
	//保存已经进入准备阶段的坦克列表
	private Set<Integer> readSet = new HashSet<Integer>();
	
	private long readyBeginTime ;
	
	
	
	public ReadyStateMachine(SportMapInstance mapInstance){
		this.mapInstance = mapInstance;
	}
	
	
	@Override
	public void update() {
		this.readyLogic();
	}
	
	//准备逻辑
	private void readyLogic(){
		boolean ready = true;
		for(AbstractInstance tank : mapInstance.getAllTank()){
			if(tank.isRobot()){
				continue;
			}
			if(!this.readSet.contains(tank.getId())){
				ready = false;
				break;
			}
		}
		
		if(ready || hasOutTime()){
			MapTemplate template = mapInstance.getTemplate();
			STC_BEGIN_MSG beginMsg = STC_BEGIN_MSG.newBuilder()
					.setOverTime(template.getOverTime())
					.setReadyTime(template.getReadyTime())
					.build();
			
			//发送比赛开始消息
			mapInstance.brodcastMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_BEGIN_VALUE, beginMsg.toByteArray());
			
			//切换比赛开始状态机
			mapInstance.setStateMachine(StateMachineFactory.createBeginStateMachine(mapInstance));
			
			if(logger.isDebugEnabled()){
				logger.debug(" 地图实例={} 通知比赛开始...", mapInstance.getInstanceId());
			}
		}
	}
	
	
	
	//判断是否等待超时
	private boolean hasOutTime(){
		if(readyBeginTime == 0){
			return false;
		}
		return (System.currentTimeMillis() - readyBeginTime) > READY_OUT_TIME;
	}
	

	@Override
	public void notify(NotifyState state, int tankInstanceId) {
		if(state == NotifyState.enter){
			synchronized (this) {
				if(readyBeginTime == 0){
					readyBeginTime = System.currentTimeMillis();
				}
			
				this.readSet.add(tankInstanceId);
				logger.debug("准备阶段，收到已准备坦克实例= {}", tankInstanceId);
				
				//通知客户端已经准备好的玩家
				for(int instanceId : readSet){
					mapInstance.brodcastMsg(
							STC_PLAYER_READY_MSG.newBuilder()
							.setId(instanceId)
							.build());
				}
				
				//机器人已准备好
				for(AbstractInstance tank : mapInstance.getAllTank()){
					if(tank.isRobot()){
						mapInstance.brodcastMsg(
								STC_PLAYER_READY_MSG.newBuilder()
								.setId(tank.getId())
								.build());
					}
				}
			}
		}
	}


	@Override
	public boolean hadOver() {
		return false;
	}


	@Override
	public void forceClose() {
		// TODO Auto-generated method stub
		
	}
}
