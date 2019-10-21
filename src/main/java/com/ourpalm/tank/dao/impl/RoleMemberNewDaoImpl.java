package com.ourpalm.tank.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleMemberNewDao;
import com.ourpalm.tank.domain.RoleMemberNew;

public class RoleMemberNewDaoImpl extends AbstractJedisDao implements RoleMemberNewDao {
	private final static String KEY = "ROLE_MEMBER_NEW_";

	@Override
	public Map<String, RoleMemberNew> getAll(int roleId) {
		Map<String, String> map = getClient().hgetAll(KEY + roleId);
		if (Util.isEmpty(map)) {
			return new HashMap<>();
		}

		Map<String, RoleMemberNew> result = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			RoleMemberNew member = JSON.parseObject(entry.getValue(), RoleMemberNew.class);
			result.put(entry.getKey(), member);
		}
		return result;
	}

	@Override
	public RoleMemberNew get(int roleId, String uniqueId) {
		if ("".equals(uniqueId)) {
			return null;
		}

		String jsonStr = client.hget(KEY + roleId, String.valueOf(uniqueId));
		if (Util.isEmpty(jsonStr)) {
			return null;
		}

		return JSON.parseObject(jsonStr, RoleMemberNew.class);
	}

	@Override
	public void save(RoleMemberNew member) {
		getClient().hset(KEY + member.getRoleId(), String.valueOf(member.getUniqueId()), JSON.toJSONString(member));
	}

	@Override
	public void delete(int roleId, Collection<String> memberList) {
		if (Util.isEmpty(memberList)) {
			return;
		}

		String[] ids = new String[memberList.size()];
		int i = 0;
		for (String id : memberList) {
			ids[i++] = id;
		}

		getClient().hdel(KEY + roleId, ids);
	}

}
