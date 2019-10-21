package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.InstantMessageInfo;

public interface InstantMessageDao {

	void save(int roleId, InstantMessageInfo info);
	
	InstantMessageInfo get(int roleId);
	
	void remove(int roleId);
}
