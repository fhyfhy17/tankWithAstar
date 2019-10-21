package com.ourpalm.tank.dao.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleTankDao;
import com.ourpalm.tank.domain.RoleTank;

public class RoleTankDaoImp extends AbstractJedisDao implements RoleTankDao {
	private final static String KEY = "ROLE_TANK_";
	
	@Override
	public Map<Integer, RoleTank> getAll(int roleId) {
		Map<String, String> map = getClient().hgetAll(KEY + roleId);
		if(Util.isEmpty(map)){
			return new HashMap<Integer, RoleTank>(1);
		}
		
		Map<Integer, RoleTank> result = new HashMap<Integer, RoleTank>();
		for(Map.Entry<String, String> entry : map.entrySet()){
			result.put(Integer.valueOf(entry.getKey()), JSON.parseObject(entry.getValue(), RoleTank.class));
		}
		return result;
	}

	public RoleTank getRoleTank(Integer roleId, Integer tankId){
		String jsonStr = getClient().hget(KEY + roleId, tankId.toString());
		if(Util.isEmpty(jsonStr)){
			return null;
		}
		
		return JSON.parseObject(jsonStr, RoleTank.class);
	}
	
	@Override
	public void insert(RoleTank roleTank) {
		update(roleTank);
	}

	@Override
	public void update(RoleTank roleTank) {
		getClient().hset(KEY + roleTank.getRoleId(), roleTank.getTankId() + "", JSON.toJSONString(roleTank));
	}

	@Override
	public void delete(Integer roleId, Integer tankId) {
		getClient().hdel(KEY + roleId, tankId.toString());
	}
	
	@Override
	public boolean exist(Integer roleId, Integer tankId){
		return this.client.hexiste(KEY + roleId, tankId.toString());
	}
}
