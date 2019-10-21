package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.RoleShop;


public interface ShopRecordDao {

	RoleShop get(int roleId);
	
	void save(int roleId, RoleShop roleShop);
	
}
