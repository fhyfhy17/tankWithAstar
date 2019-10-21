package com.ourpalm.tank.app.blueVip;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.dao.BlueDao;
import com.ourpalm.tank.message.ROLE_MSG.STC_BLUE_GET_MSG;
import com.ourpalm.tank.template.BlueGiftTemplate;
import com.ourpalm.tank.vo.result.Result;

public interface BlueApp extends Service {

	void query(String openId, String openKey);

	Result getGift(int roleId, int type, int levelId, STC_BLUE_GET_MSG.Builder builder);

	void refreshBlue(int roleId, boolean isNextDay);

	void login(int roleId);

	BlueDao getBlueDao();

	void pushBlue(int roleId);

	BlueGiftTemplate getBlueGiftTemplate(int templateId);

	BlueGiftTemplate getBlueRenewGiftTemplate();
}
