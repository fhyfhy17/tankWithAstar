package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.BlueExpireInfo;
import com.ourpalm.tank.domain.BlueInfo;

public interface BlueDao {

	BlueInfo getBlueInfo(int roleId);

	void insert(BlueInfo blueInfo);

	void update(BlueInfo blueInfo);

	boolean isBlue(int roleId);
	
	BlueExpireInfo getBlueExpireInfo(int roleId);
	
}
