package com.ourpalm.tank.app.map.camp;

import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 战役评星条件
 * 
 * @author wangkun
 *
 */
public interface CampStar {

	boolean condition(AbstractInstance tank);
}
