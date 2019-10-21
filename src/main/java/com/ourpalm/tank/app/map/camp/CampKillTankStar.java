package com.ourpalm.tank.app.map.camp;

import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 击杀指定数量坦克获得评星
 * 
 * @author wangkun
 *
 */
public class CampKillTankStar implements CampStar{

	private int count;		//击杀数量
	
	public CampKillTankStar(int count){
		this.count = count;
	}
	
	@Override
	public boolean condition(AbstractInstance tank) {
		return tank.getKillCount() >= this.count;
	}

}
