package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.ContinueLoginInfo;

public interface ContinueLoginDao {

	ContinueLoginInfo getInfo(int roleId);
	
	void saveInfo(int roleId, ContinueLoginInfo info);
	
	String randomThreeDayCode();
	String randomSevenDayCode();
}
