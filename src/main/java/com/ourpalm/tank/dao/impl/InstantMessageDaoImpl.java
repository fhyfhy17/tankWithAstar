package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.tank.dao.InstantMessageDao;
import com.ourpalm.tank.domain.InstantMessageInfo;

public class InstantMessageDaoImpl extends AbstractJedisDao implements InstantMessageDao {
	private static final String Key = "Role_Instant_Message_Key_";

	@Override
	public void save(int roleId, InstantMessageInfo info) {
		if(info == null)
			return;
		
		getClient().set(Key + roleId, JSON.toJSONString(info));
	}

	@Override
	public InstantMessageInfo get(int roleId) {
		String json = getClient().get(Key + roleId);
		if(json == null)
			return null;
		
		return JSON.parseObject(json, InstantMessageInfo.class);
	}

	@Override
	public void remove(int roleId) {
		getClient().del(Key + roleId);
	}



}
