package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.VipInfo;

public interface VipDao {

	void save(int roleId, VipInfo info);
	
	VipInfo get(int roleId);
}
