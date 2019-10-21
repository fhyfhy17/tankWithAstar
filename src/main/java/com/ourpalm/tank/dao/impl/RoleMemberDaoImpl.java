package com.ourpalm.tank.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleMemberDao;
import com.ourpalm.tank.domain.RoleMember;

public class RoleMemberDaoImpl extends AbstractJedisDao implements RoleMemberDao {
	private final static String KEY = "ROLE_MEMBER_";
	
	@Override
	public Map<String, RoleMember> getAll(int roleId) {
		Map<String, String> map = getClient().hgetAll(KEY + roleId);
		if (Util.isEmpty(map)) {
			return new HashMap<>();
		}
		
		Map<String, RoleMember> result = new HashMap<>();
		for(Map.Entry<String, String> entry : map.entrySet()) {
			RoleMember member = JSON.parseObject(entry.getValue(), RoleMember.class);
			result.put(entry.getKey(), member);
		}
		return result;
	}

	
	@Override
	public Map<String, RoleMember> get(int roleId, Collection<String> ids) {
		if(Util.isEmpty(ids)){
			return new HashMap<>();
		}
		List<String> list = getClient().hmGet(KEY + roleId, ids.toArray(new String[ids.size()]));
		if (Util.isEmpty(list)) {
			return new HashMap<>();
		}
		
		Map<String, RoleMember> result = new HashMap<>();
		for(String jsonStr : list) {
			if (!Util.isEmpty(jsonStr)) {
				RoleMember member = JSON.parseObject(jsonStr, RoleMember.class);
				result.put(String.valueOf(member.getTemplateId()), member);
			}
		}
		return result;
	}
	
	
	
	@Override
	public RoleMember get(int roleId, int templateId){
		if(templateId <= 0){
			return null;
		}
		
		String jsonStr = client.hget(KEY + roleId, String.valueOf(templateId));
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		
		return JSON.parseObject(jsonStr, RoleMember.class);
	}
	

		
	@Override
	public void save(RoleMember member) {
		getClient().hset(KEY + member.getRoleId(), String.valueOf(member.getTemplateId()), JSON.toJSONString(member));
	}

	@Override
	public void delete(int roleId, Collection<RoleMember> memberList) {
		if (Util.isEmpty(memberList)) {
			return;
		}
		
		String[] ids = new String[memberList.size()];
		int i = 0;
		for(RoleMember roleMember : memberList) {
			ids[i++] = String.valueOf(roleMember.getTemplateId());
		}
			
		getClient().hdel(KEY + roleId, ids);
	}

}
