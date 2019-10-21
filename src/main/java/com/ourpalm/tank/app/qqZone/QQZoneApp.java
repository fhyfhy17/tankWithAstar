package com.ourpalm.tank.app.qqZone;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.dao.QQHallDao;
import com.ourpalm.tank.dao.QQZoneDao;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQHall_GET_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQZone_GET_MSG;
import com.ourpalm.tank.vo.result.Result;

public interface QQZoneApp extends Service {

	Result getGift(int roleId, int type, int levelId,	STC_QQZone_GET_MSG.Builder builder);

	void refresh(int roleId);

	void push(int roleId);

	void login(int roleId, boolean isfirst);
	
	QQZoneDao getQQZoneDao();
}
