package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleLotteryDao;
import com.ourpalm.tank.domain.RoleLottery;

public class RoleLotteryDaoImpl extends AbstractJedisDao implements RoleLotteryDao {
	private final static String KEY = "ROLE_LOTTERY_";
	
	@Override
	public RoleLottery get(int roleId) {
		String jsonStr = getClient().get(KEY + roleId); 
		if (Util.isEmpty(jsonStr)) {
			return null;
		}
		
		return JSON.parseObject(jsonStr, RoleLottery.class);
	}

	@Override
	public void save(RoleLottery roleLottery) {
		getClient().set(KEY + roleLottery.getRoleId(), JSON.toJSONString(roleLottery));
	}

}
