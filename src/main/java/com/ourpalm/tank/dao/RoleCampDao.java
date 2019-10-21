package com.ourpalm.tank.dao;

import java.util.List;

import com.ourpalm.tank.domain.RoleCamp;

public interface RoleCampDao {

	void save(RoleCamp roleCamp);
	
	RoleCamp get(int roleId, int id);
	
	List<RoleCamp> getMuch(int roleId, List<String> ids);
	
	boolean hadCampBoxExist(int roleId, int warId, int hard);
	
	void saveDrawCampBox(int roleId, int warId, int hard);
}
