package com.ourpalm.tank.app.map.camp;

import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 比赛中是否击杀巡逻坦克
 * 
 * @author wangkun
 *
 */
public class CampKillPatrolStar implements CampStar{
	
	private boolean result ;
	
	public CampKillPatrolStar(boolean result){
		this.result = result;
	}

	@Override
	public boolean condition(AbstractInstance tank) {
//		return tank.getQuestRecord().isKillPatrolTank() == result;
		return false;
	}

}
