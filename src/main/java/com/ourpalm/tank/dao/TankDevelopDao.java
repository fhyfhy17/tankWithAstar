package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.TankDevelop;

/**
 * 坦克研发 dao
 * 
 * @author fhy
 *
 */
public interface TankDevelopDao {
	void save(TankDevelop tankDevelop);

	TankDevelop getDevelop(int roleId);
}
