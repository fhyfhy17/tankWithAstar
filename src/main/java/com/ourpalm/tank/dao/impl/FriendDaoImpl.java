package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.tank.dao.FriendDao;
import com.ourpalm.tank.domain.RoleFriend;

public class FriendDaoImpl extends AbstractJedisDao implements FriendDao{
	private static final String FRIEND_KEY = "Role_FRIEND_KEY_";			
	
	
	@Override
	public RoleFriend get(int roleId) {
		return JSON.parseObject(client.get(FRIEND_KEY + roleId), RoleFriend.class);
	}

	@Override
	public void save(int roleId, RoleFriend friend) {
		client.set(FRIEND_KEY + roleId, JSON.toJSONString(friend));
	}
	
	public boolean hadInit(int roleId) {
		return client.exists(FRIEND_KEY + roleId);
	}

}
