package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.RoleFriend;

public interface FriendDao {
	
	RoleFriend get(int roleId);
	
	void save(int roleId, RoleFriend friend);
	
	boolean hadInit(int roleId);
	
}
