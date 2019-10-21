package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.QQHallInfo;

public interface QQHallDao {

	QQHallInfo getQQHallInfo(int roleId);

	void insert(QQHallInfo qQHallInfo);

	void update(QQHallInfo qQHallInfo);

}
