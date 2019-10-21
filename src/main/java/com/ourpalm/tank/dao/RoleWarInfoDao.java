package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.RoleWarInfo;

public interface RoleWarInfoDao {

	RoleWarInfo get(int roleId);
	
	void save(RoleWarInfo roleWarInfo);
}
