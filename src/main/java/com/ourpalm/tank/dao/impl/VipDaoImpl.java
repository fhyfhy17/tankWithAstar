package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.tank.dao.VipDao;
import com.ourpalm.tank.domain.VipInfo;

public class VipDaoImpl extends AbstractJedisDao implements VipDao {
	private static final String VIP_KEY = "ROLE_VIP_KEY_";

	@Override
	public void save(int roleId, VipInfo info) {
		if(info == null)
			return;
		
		getClient().set(VIP_KEY + roleId, JSON.toJSONString(info));
	}

	@Override
	public VipInfo get(int roleId) {
		String json = getClient().get(VIP_KEY + roleId);
		if(json == null)
			return null;
		
		return JSON.parseObject(json, VipInfo.class);
	}

}
