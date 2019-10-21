package com.ourpalm.tank.app.map.camp;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 持续时间内完成比赛
 * 
 * @author wangkun
 *
 */
public class CampTimeStar implements CampStar{
	
	private int time; 	//时间内
	
	public CampTimeStar(int time){
		this.time = time;
	}

	@Override
	public boolean condition(AbstractInstance tank) {
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance == null){
			return false;
		}
		return mapInstance.durationTime() <= time;
	}

}
