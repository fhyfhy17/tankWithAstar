package com.ourpalm.tank.vo.tank;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.ai.Ai;
import com.ourpalm.tank.app.map.ai.AiType;
import com.ourpalm.tank.app.map.state.BeginStateMachine;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

public class NewPlayer3TankInstance extends NewPlayerTankInstance {

	public NewPlayer3TankInstance(int instanceId) {
		super(instanceId);
		// TODO Auto-generated constructor stub
	}
	
	// 第三关AI需求：
	// 己方不占旗的逻辑，可以通过配表，来实现不往旗子方向走
	// 敌方有概率向旗子方向移动（每辆坦克都进行几率配置）
	// 敌我AI支持逃跑，逃跑逻辑优先于残血
	// 敌方坦克如果残血了，会直接开到玩家面前（支持边靠拢边开火）
	// 敌方坦克如果在旗子内的话，会停留并攻击可攻击的己方坦克
	// 敌方坦克如果占旗时间过长，则会自己退出旗子范围内（支持边离开边开火）
	// 战旗的时候，不触发逃跑
	
	@Override
	public void aiTypeSwitch() {
		// 注意：
		// 以下各个逻辑分支，需要立即调用 return，避免选择优先级被代替
		// 每个逻辑分支判断条件不能带，判断是否当前状态是否 不等于要切换的状态，在 aiTypeSwitch（  type ） 函数里面会自动过滤
		/*
			比如：
			// 判断是否可以逃跑
			if( !isEscape && currAi.getType() != AiType.escape) {
				aiTypeSwitch(AiType.escape);
				return;	
			} 
			
			这样会导致该AI被pass，应该写成
			
			// 判断是否可以逃跑
			if( !isEscape ) {
				aiTypeSwitch(AiType.escape);
				return;	
			} 
			
			aiTypeSwitch(AiType.escape); 函数里面会自己做过滤
			
		 */
		
		Ai currAi = this.getCurrAi();
		// 是否空AI
		if (currAi == null) {
			this.moveAiSwitch();
			return;
		}
		
		// 如果当前是死亡AI，则停止一切判断
		if( getCurrAi().getType() == AiType.death ){
			return;
		}
		
		// 是否触发逃跑逻辑 或者 自杀逻辑
		if ( get(AttrType.hp) < get(AttrType.n_hpMax) * 1.0 * SysConfig.get(13) / 100) {
			
			// 判断是否可以逃跑
			if( this.thinkCanEscape() ) {
				aiTypeSwitch(AiType.escape);
				return;	
			} 
			
			// 暂时 自杀逻辑优先级高于逃跑逻辑
			List<AbstractInstance> list = new ArrayList<AbstractInstance>();
			if( this.thinkNeedMoveToEnemy(list) ){
				aiTypeSwitch(AiType.fireAndMoveToEnemy, list.get(0));
				this.setMoveToEnemyLastTime((int)(System.currentTimeMillis()/1000));
				return;
			}
			
		}

		if ( getCurrAi().getType() == AiType.move || 
				getCurrAi().getType() == AiType.fireAndMoveToEnemy || 
				getCurrAi().getType() == AiType.moveToFlag ) {
			
			// 己方不占旗
			if( this.getTeam().getNumber() == TEAM.BLUE_VALUE && this.thinkNeedKeepAwayFromFlag() ){
				aiTypeSwitch(AiType.fireAndMoveOutFlag);
				return;
			}
			
			// 这些行走模式，如果坦克已经在圈子里面，则放弃该AI，改为停留在圈子里面，该模式高于其他模式
			if ( this.thinkNeedStayInFlag() ) {
				aiTypeSwitch(AiType.stayAndFire);
				return;
			}
			
			// 判断是否要强制瞬间向旗子进攻前进
			MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
			if( mapInstance.getPreseasonTemplate().getForceFireToFlag() == 1){
				int r = (int)(1+Math.random()*(100));
				if( this.thinkNeedFireToFlag() ) {
					// 如果攻击前进的目标是在旗子内的话
					if( getCurrAi().getType() == AiType.fireAndMoveToEnemy ){
						if( getCurrAi().getTarget().isInFlag() ){
							aiTypeSwitch(AiType.fireAndMoveToEnemy);
							return;
						}
					}
					
					if( ( this.getTeam().getNumber() == TEAM.RED_VALUE && r <= mapInstance.getPreseasonTemplate().getEnemyFireToFlagList().get(this.getMemberId()-1)) ||
						( this.getTeam().getNumber() == TEAM.BLUE_VALUE && r <= mapInstance.getPreseasonTemplate().getOurFireToFlagList().get(this.getMemberId()-1)) ){
						this.aiTypeSwitch(AiType.FireToFlag);
						return;
					}
				}
			}
		}
		
		// 如果是在旗子内
		if( getCurrAi().getType() == AiType.stayAndFire ) {
			if( this.thinkNeedFireOutFlag() ){
				aiTypeSwitch(AiType.fireAndMoveOutFlag);
				return;
			}
		}
	}
}
