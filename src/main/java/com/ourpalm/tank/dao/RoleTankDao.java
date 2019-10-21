package com.ourpalm.tank.dao;

import java.util.Map;

import com.ourpalm.tank.domain.RoleTank;

public interface RoleTankDao {

	Map<Integer, RoleTank> getAll(int roleId);
	
	RoleTank getRoleTank(Integer roleId, Integer tankId);
	
	void insert(RoleTank roleTank);
	
	void update(RoleTank roleTank);
	
	void delete(Integer roleId, Integer tankId);
	
	boolean exist(Integer roleId, Integer tankId);
}
