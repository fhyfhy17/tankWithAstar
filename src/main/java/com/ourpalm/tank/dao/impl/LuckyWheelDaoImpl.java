package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.LuckyWheelDao;
import com.ourpalm.tank.domain.LuckyWheelInfo;

public class LuckyWheelDaoImpl extends AbstractJedisDao implements LuckyWheelDao {
	private final static String KEY = "LUCKY_WHEEL_";

	@Override
	public LuckyWheelInfo getLuckyWheelInfo(int roleId) {
		String jsonStr = getClient().get(KEY + roleId);
		LuckyWheelInfo luckyWheelInfo = null;
		if (Util.isEmpty(jsonStr)) {
			luckyWheelInfo = new LuckyWheelInfo();
			luckyWheelInfo.setRoleId(roleId);
			update(luckyWheelInfo);
			jsonStr = getClient().get(KEY + roleId);
		} else {
			luckyWheelInfo = JSONObject.parseObject(jsonStr, LuckyWheelInfo.class);
		}

		return luckyWheelInfo;
	}

	@Override
	public void insert(LuckyWheelInfo luckyWheelInfo) {
		update(luckyWheelInfo);

	}

	@Override
	public void update(LuckyWheelInfo luckyWheelInfo) {
		getClient().set(KEY + luckyWheelInfo.getRoleId(), JSON.toJSONString(luckyWheelInfo));
	}

}
