package com.ourpalm.tank.dao.impl;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleUseMemberDao;

public class RoleUseMemberDaoImpl extends AbstractJedisDao implements RoleUseMemberDao{
	private final static String KEY = "ROLE_USER_MEMBER_";
	
	@Override
	public Map<Integer, String> getAll(int roleId) {
		Map<String, String> jsonMap = getClient().hgetAll(KEY + roleId);
		if (Util.isEmpty(jsonMap)) {
			return new HashMap<>();
		}
		
		Map<Integer, String> result = new HashMap<>();
		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			Integer key = Integer.valueOf(entry.getKey());
			result.put(key, entry.getValue());
		}
		return result;
	}
	
	
	@Override
	public String get(int roleId, Integer type){
		return client.hget(KEY + roleId, type.toString());
	}
	

	@Override
	public void save(int roleId, Integer type, String instanceId) {
		getClient().hset(KEY + roleId, type.toString(), instanceId);
	}

}
