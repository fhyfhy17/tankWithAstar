package com.ourpalm.tank.app.redMoney;

import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_REDMONEY_RECEIVE_MSG;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.result.Result;

public interface RedMoneyApp extends Service {

	Result getGift(int roleId, String uniqueId, STC_REDMONEY_RECEIVE_MSG.Builder builder);

	void get(int roleId, String uniqueId);

	boolean saveRedMoney(String uniqueId, List<AbstractInstance> roleId);

	void updateByRobotSys(String uniqueId, int roleId);
}
