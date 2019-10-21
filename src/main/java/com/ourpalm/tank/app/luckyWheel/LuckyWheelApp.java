package com.ourpalm.tank.app.luckyWheel;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.dao.LuckyWheelDao;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_LuckyWheel_GET_MSG;
import com.ourpalm.tank.vo.result.Result;

public interface LuckyWheelApp extends Service {

	Result getGift(int roleId, String type, STC_LuckyWheel_GET_MSG.Builder builder);

	void refresh(int roleId, boolean isNextDay);

	void login(int roleId);

	LuckyWheelDao getLuckyWheelDao();

	void push(int roleId);

}
