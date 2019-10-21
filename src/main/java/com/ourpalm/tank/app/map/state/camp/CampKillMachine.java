package com.ourpalm.tank.app.map.state.camp;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.vo.CampaignMapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 击杀固定数目坦克完成比赛
 * 
 * @author wangkun
 *
 */
public class CampKillMachine extends AbstractCampMachine implements StateMachine{

	private int roleId;
	private int killCount;		//击杀数
	
	public CampKillMachine(CampaignMapInstance mapInstance, int roleId) {
		super(mapInstance);
		
		this.roleId = roleId;
		this.killCount = Integer.parseInt(mapTemplate.getPassParam1());
		
		logger.debug("初始化战役击杀状态机, killCount = {}", killCount);
	}

	@Override
	public void update() {
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
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
		
		try{
			//发放奖励
			this.reward(tank);
			
			//回收资源
			this.release(tank);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void notify(NotifyState state, int tankInstanceId) {
		if(state == NotifyState.death){
			if(mapInstance.getRedKillNum() >= killCount){
				winTeam = TEAM.RED;
			} else if(mapInstance.getBlueKillNum() >= killCount){
				winTeam = TEAM.BLUE;
			}
			
			//通知比赛结果，发放奖励
			AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
			if(tank == null){
				return ;
			}
			//玩家自身复制品可以复活
			if(tank.getBodyType() == BodyType.selfTank){
				//加入死亡列表
				mapInstance.putDeath(tank.getId());
				
				//设置坦克死亡时间
				tank.setDeathTime(System.currentTimeMillis());
				
				//死亡广播
				this.brodcastDeathMsg(tank.getId(), true);
				
				return ;
			}
			
			//其他机器人不允许复活
			if(tank.isRobot()){
				this.brodcastDeathMsg(tank.getId(), tank.getBodyType() == BodyType.build);
				return ;
			}
			
			//玩家死亡
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
