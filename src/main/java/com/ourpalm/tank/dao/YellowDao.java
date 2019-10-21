package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.YellowInfo;

public interface YellowDao {

	YellowInfo getYellowInfo(int roleId);

	void insert(YellowInfo yellowInfo);

	void update(YellowInfo yellowInfo);

	boolean isYellow(int roleId);

}
