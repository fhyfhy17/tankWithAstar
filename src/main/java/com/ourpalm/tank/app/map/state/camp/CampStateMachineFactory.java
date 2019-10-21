package com.ourpalm.tank.app.map.state.camp;

import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.vo.CampaignMapInstance;

/**
 * 战役比赛规则状态机工厂
 * 
 * @author wangkun
 *
 */
public class CampStateMachineFactory {

	public static StateMachine createStateMachine(CampaignMapInstance mapInstance, int roleId){
		int passType = mapInstance.getMapTemplate().getPassType();
		switch(passType){
			case 1 : return new CampOccupyMachine(mapInstance, roleId);
			case 2 : return new CampKillMachine(mapInstance, roleId);
			default : return null;
		}
	}
}
