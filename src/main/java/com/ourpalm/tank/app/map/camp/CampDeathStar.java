package com.ourpalm.tank.app.map.camp;

import com.ourpalm.tank.vo.AbstractInstance;


/**
 * 死亡小于多少时，获得一星
 * 
 * @author wangkun
 *
 */
public class CampDeathStar implements CampStar{
	
	private int deathCount ;
	
	public CampDeathStar(int deathCount){
		this.deathCount = deathCount;
	}

	@Override
	public boolean condition(AbstractInstance tank) {
		return tank.getDeathCount() < deathCount;
	}

}
