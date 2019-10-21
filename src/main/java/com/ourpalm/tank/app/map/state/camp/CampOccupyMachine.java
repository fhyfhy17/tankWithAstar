package com.ourpalm.tank.app.map.state.camp;

import com.ourpalm.core.util.Cat;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_HOLD_FLAG_MSG;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.vo.CampaignMapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 战役占领据点状态机
 * 
 * @author wangkun
 *
 */
public class CampOccupyMachine extends AbstractCampMachine implements StateMachine{

	private int roleId;			//玩家角色ID
	private float x;			//占据点坐标
	private float z; 
	private int radius;			//占据半径
	private int occupyTime;		//占据时间
	
	private boolean hadOccupy = false;			//当前是否占据
	private int currOccupyTime;					//当前占据时间
	
	public CampOccupyMachine(CampaignMapInstance mapInstance, int roleId){
		super(mapInstance);
		this.mapTemplate = mapInstance.getMapTemplate();
		this.mapInstance = mapInstance;
		this.roleId = roleId;
		
		//初始化占据点坐标
		String[] points = mapTemplate.getPassParam1().split(Cat.comma);
		this.x = Float.valueOf(points[0]);
		this.z = Float.valueOf(points[2]);
		this.radius = Integer.parseInt(mapTemplate.getPassParam2());
		this.occupyTime = Integer.parseInt(mapTemplate.getPassParam3());
		
		logger.debug("创建战役占据点获胜状态机, 占据点 x = {}, z = {}, radius = {}", this.x, this.z, this.radius);
	}
	
	@Override
	public void update() {
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
			logger.warn("战役坦克实例不存在");
			return ;
		}
		//玩家杀进程退出,进入其他战场情况处理
		if(mapInstance.getTank(tank.getId()) == null){
			this.release(tank);
		}
		
		//判断比赛结束时间
		try{
			this.checkOverTime(tank);
		}catch(Exception e){
			logger.error("", e);
		}
		
		//占据点判断
		try{
			this.occupyPoint(tank);
		}catch(Exception e){
			logger.error("", e);
		}
		
		//比赛奖励
		try{
			this.reward(tank);
		}catch(Exception e){
			logger.error("", e);
		}
		
		//释放资源
		try{
			this.release(tank);
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	/** 占据点判断 */
	private void occupyPoint(AbstractInstance tank){
		//比赛结束返回
		if(this.winTeam != null){
			return ;
		}
		boolean currOccupy = this.inRadius(tank);
		//占领
		if(currOccupy){
			currOccupyTime += 1;
			//之前状态改变,发送消息
			if(this.hadOccupy != currOccupy){
				//占领消息
				STC_HOLD_FLAG_MSG redHoldMsg = STC_HOLD_FLAG_MSG.newBuilder()
						.setCurrTime(currOccupyTime)
						.setEndTime(this.occupyTime)
						.setTeamId(tank.getTeam().getNumber())
						.build();
				mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_HOLD_FLAG_VALUE, redHoldMsg.toByteArray());
				logger.debug("广播占领据点消息....");
			}
			//设置当前状态
			this.hadOccupy = currOccupy;
			logger.debug("占领据点.... currOccupyTime = {}", currOccupyTime);
			
		} else {
			//没有占领据点
			currOccupyTime = 0;
			//之前状态改变,发送消息
			if(this.hadOccupy != currOccupy){
				mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
						BATTLE_MSG.CMD_ID.STC_HOLD_STOP_VALUE, null);
				logger.debug("发送中止消息....");
			}
			//设置当前状态
			this.hadOccupy = currOccupy;
		}
		
		if(currOccupyTime >= occupyTime){
			this.winTeam = tank.getTeam();
			logger.debug("占领据点获胜... ");
		}
	}
	
	
	
	//是否在占据地点范围内
	private boolean inRadius(AbstractInstance tank){
		if(tank.isDeath()){
			return false;
		}
		float tankX = tank.getX();
		float tankZ = tank.getZ();
		float x = Math.abs(tankX - this.x);
		float z = Math.abs(tankZ - this.z);
		
		int range = (int)Math.sqrt(x*x  + z*z);
		if(this.radius > range){
			return true;
		}
		return false;
	}
	

	@Override
	public void notify(NotifyState state, int tankInstanceId) {
		//死亡限制逻辑，超过限制死亡数比赛结束
		if(state == NotifyState.death){
			AbstractInstance tank = GameContext.getTankApp().getInstance(tankInstanceId);
			if(tank.isRobot()){
				//建筑死亡不消失
				this.brodcastDeathMsg(tank.getId(), tank.getBodyType() == BodyType.build);
				return ;
			}
			this.playerDeath(tank);
		}
	}

	@Override
	public boolean hadOver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void forceClose() {
		// TODO Auto-generated method stub
		
	}
}
