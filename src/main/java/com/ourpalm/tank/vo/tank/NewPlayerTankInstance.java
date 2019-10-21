package com.ourpalm.tank.vo.tank;

import java.util.List;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.ai.AiType;
import com.ourpalm.tank.app.map.state.BeginStateMachine;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.TankInstance;

public class NewPlayerTankInstance extends TankInstance {

	public NewPlayerTankInstance(int instanceId) {
		super(instanceId);
		// TODO Auto-generated constructor stub
	}

	@Override
	// 提供一种当前适合的开火AI
	public void FireAiSwitch( AbstractInstance target ){
		int r = (int)(1+Math.random()*(100));

		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		List<int[]> percentList = mapInstance.getPreseasonTemplate().getOurMoveAndFireList().get(this.getMemberId()-1);

		float range = Util.range(this.getX(), this.getZ(), target.getX(), target.getZ());
		for (int i = 0; i < percentList.size(); i++) {
			if( range <= percentList.get(i)[0] ){
				if( (int)(System.currentTimeMillis()/1000) - this.getMoveToEnemyLastTime() > 3 && r <= percentList.get(i)[1] ){
					this.aiTypeSwitch(AiType.fireAndMoveToEnemy);
					this.setMoveToEnemyLastTime((int)(System.currentTimeMillis()/1000));
				} else {
					this.aiTypeSwitch(AiType.fire);
				}
				return;
			}
		}
		this.aiTypeSwitch(AiType.fire);
	}

	@Override
	// 提供一种当前适合的行走AI
	public void moveAiSwitch(){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
		if( mapInstance.getWarType() != WAR_TYPE.PERISH_VALUE ){
			int r = (int)(1+Math.random()*(100));
//			if( this.getTeam().getNumber() == TEAM.RED_VALUE ){
//				logger.debug( "Preseason ID {} RED tank member id {} cur per {} moveToFlag per {} fireToFlag per {} ",
//						mapInstance.getPreseasonTemplate().getId(),
//						this.getMemberId(), r,
//						mapInstance.getPreseasonTemplate().getEnemyMoveToFlagList().get(this.getMemberId()-1),
//						mapInstance.getPreseasonTemplate().getEnemyFireToFlagList().get(this.getMemberId()-1));
//			} else {
//				logger.debug( "Preseason ID {} BLUE tank member id {} cur per {} moveToFlag per {} fireToFlag per {} ",
//						mapInstance.getPreseasonTemplate().getId(),
//						this.getMemberId(), r,
//						mapInstance.getPreseasonTemplate().getOurFireToFlagList().get(this.getMemberId()-1),
//						mapInstance.getPreseasonTemplate().getOurFireToFlagList().get(this.getMemberId()-1));
//			}

			// FireToFlag 比 moveToFlag 优先级高
			if( this.thinkNeedFireToFlag() && (
				( this.getTeam().getNumber() == TEAM.RED_VALUE && r <= mapInstance.getPreseasonTemplate().getEnemyFireToFlagList().get(this.getMemberId()-1)) ||
				( this.getTeam().getNumber() == TEAM.BLUE_VALUE && r <= mapInstance.getPreseasonTemplate().getOurFireToFlagList().get(this.getMemberId()-1)) )){

				this.aiTypeSwitch(AiType.FireToFlag);
			} else if( ( this.getTeam().getNumber() == TEAM.RED_VALUE && r <= mapInstance.getPreseasonTemplate().getEnemyMoveToFlagList().get(this.getMemberId()-1)) ||
				( this.getTeam().getNumber() == TEAM.BLUE_VALUE && r <= mapInstance.getPreseasonTemplate().getOurMoveToFlagList().get(this.getMemberId()-1))	){

				// 第一关的新手地图，蓝队不往旗子方向走
				this.aiTypeSwitch(AiType.moveToFlag);
			} else {
				this.aiTypeSwitch(AiType.move);
			}
		} else {
			this.aiTypeSwitch(AiType.move);
		}
	}

	// 判断是否能逃跑
	public boolean thinkCanEscape(){
		if( !isEscape || this.currAi.getType() == AiType.escape ){
			// 如果沒逃过，或者正在逃跑
			if( this.currAi.getType() != AiType.stayAndFire ){
				// 如果当前AI不是战旗，可以逃跑
				return true;
			} else {
				MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getMapInstanceId());
				if( mapInstance.getStateMachine() instanceof BeginStateMachine ) {

					BeginStateMachine stateMachine = (BeginStateMachine) mapInstance.getStateMachine();
					if( stateMachine.getCurrHold() != BeginStateMachine.NO_HOLE ){
						if(( this.getTeam().getNumber() == TEAM.BLUE_VALUE && (stateMachine.getBlueHoldTime() * 100 / stateMachine.getHoldTime() > mapInstance.getPreseasonTemplate().getCantEscapeInFlagTime() )) ||
							( this.getTeam().getNumber() == TEAM.RED_VALUE && (stateMachine.getRedHoldTime() * 100 / stateMachine.getHoldTime() > mapInstance.getPreseasonTemplate().getCantEscapeInFlagTime() ))){
							//System.out.println("time return false "+stateMachine.getRedHoldTime() * 100 / stateMachine.getHoldTime());
							return false;
						}
					}
					//System.out.println("time return true "+stateMachine.getRedHoldTime() * 100 / stateMachine.getHoldTime());
				}
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean isPreseason() {
		return true;
	}
}
