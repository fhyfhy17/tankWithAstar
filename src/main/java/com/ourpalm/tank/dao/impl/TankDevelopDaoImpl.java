package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.TankDevelopDao;
import com.ourpalm.tank.domain.TankDevelop;

public class TankDevelopDaoImpl extends AbstractJedisDao implements TankDevelopDao {
	private final static String KEY = "ROLE_TANK_DEVELOP_";

	@Override
	public void save(TankDevelop tankDevelop) {
		getClient().set(KEY + tankDevelop.getRoleId(), JSON.toJSONString(tankDevelop));
	}

	@Override
	public TankDevelop getDevelop(int roleId) {
		String s = getClient().get(KEY + roleId);
		if (Util.isEmpty(s)) {
			TankDevelop tankDevelop = new TankDevelop();
			return tankDevelop;
		}
		return JSONObject.parseObject(s, TankDevelop.class);
	}
}
