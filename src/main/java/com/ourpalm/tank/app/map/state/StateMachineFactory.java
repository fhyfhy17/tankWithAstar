package com.ourpalm.tank.app.map.state;

import com.ourpalm.tank.message.BATTLE_MSG.STC_WAR_REPORT_MSG;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.vo.SportMapInstance;

public class StateMachineFactory {
	
	/** 创建准备阶段状态机 */
	public static StateMachine createReadyStateMachine(SportMapInstance mapInstance){
		return new ReadyStateMachine(mapInstance);
	}
	
	
	/** 创建比赛开始状态机 */
	public static StateMachine createBeginStateMachine(SportMapInstance mapInstance){
		switch(mapInstance.getWarType()){
			case WAR_TYPE.RANK_VALUE : return new RankBeginStateMachine(mapInstance);
			case WAR_TYPE.BATTLE_VALUE : return new BattleBeginStateMachine(mapInstance);
			case WAR_TYPE.PERISH_VALUE : return new PerishBeginStateMachine(mapInstance);
		}
		throw new NullPointerException("战场类型错误 warType = " + mapInstance.getWarType());
	}
	
	
	/** 创建比赛结束状态机 */
	public static StateMachine createEndStateMachine(SportMapInstance mapInstance, STC_WAR_REPORT_MSG reportMsg){
		return new EndStateMachine(reportMsg, mapInstance);
	}
	
}
