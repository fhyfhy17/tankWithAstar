package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.BlueExpireInfo;
import com.ourpalm.tank.domain.BlueInfo;
import com.ourpalm.tank.domain.LuckyWheelInfo;

public interface LuckyWheelDao {

	LuckyWheelInfo getLuckyWheelInfo(int roleId);

	void insert(LuckyWheelInfo luckyWheelInfo);

	void update(LuckyWheelInfo luckyWheelInfo);

}
