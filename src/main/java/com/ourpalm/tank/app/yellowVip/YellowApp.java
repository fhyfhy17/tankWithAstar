package com.ourpalm.tank.app.yellowVip;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.dao.YellowDao;
import com.ourpalm.tank.message.ROLE_MSG.STC_YELLOW_GET_MSG;
import com.ourpalm.tank.template.YellowGiftTemplate;
import com.ourpalm.tank.vo.result.Result;

public interface YellowApp extends Service {

	Result getGift(int roleId, int type, int levelId, STC_YELLOW_GET_MSG.Builder builder);

	void refreshYellow(int roleId, boolean isNextDay);

	void login(int roleId);

	YellowDao getYellowDao();

	void pushYellow(int roleId);

}
