package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.RoleLottery;

public interface RoleLotteryDao {

	public RoleLottery get(int roleId);
	
	public void save(RoleLottery roleLottery);
	
	
}
