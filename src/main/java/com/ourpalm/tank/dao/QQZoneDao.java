package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.QQZoneInfo;

public interface QQZoneDao {

	QQZoneInfo getQQZoneInfo(int roleId);

	void insert(QQZoneInfo qQZoneInfo);

	void update(QQZoneInfo qQZoneInfo);

}
