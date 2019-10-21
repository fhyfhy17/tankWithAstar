package com.ourpalm.tank.app.qqHall;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.dao.QQHallDao;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQHall_GET_MSG;
import com.ourpalm.tank.vo.result.Result;

public interface QQHallApp extends Service {

	Result getGift(int roleId, int type, int levelId,	STC_QQHall_GET_MSG.Builder builder);

	void refresh(int roleId);

	void push(int roleId);

	void login(int roleId, boolean isfirst);
	
	QQHallDao getQQHallDao();
}
