package com.ourpalm.tank.dao.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleCampDao;
import com.ourpalm.tank.domain.RoleCamp;

public class RoleCampDaoImpl extends AbstractJedisDao implements RoleCampDao{
	
	private static final String KEY = "ROLE_CAMP_";
	private static final String BOX_KEY = "ROLE_CAMP_BOX_";

	@Override
	public void save(RoleCamp roleCamp) {
		this.client.hset(KEY + roleCamp.getRoleId(), roleCamp.getId()+"", JSON.toJSONString(roleCamp));
	}

	@Override
	public RoleCamp get(int roleId, int id) {
		return JSON.parseObject(this.client.hget(KEY + roleId, id + ""), RoleCamp.class);
	}

	@Override
	public List<RoleCamp> getMuch(int roleId, List<String> ids){
		List<String> list = this.client.hmGet(KEY + roleId, ids.toArray(new String[ids.size()]));
		if(Util.isEmpty(list)){
			return null;
		}
		List<RoleCamp> campList = new ArrayList<>();
		for(String jsonStr : list){
			if(Util.isEmpty(jsonStr)){
				continue;
			}
			campList.add(JSON.parseObject(jsonStr, RoleCamp.class));
		}
		return campList;
	}

	
	private String campBoxMapKey(int warId, int hard){
		return warId + Cat.underline + hard;
	}
	
	@Override
	public boolean hadCampBoxExist(int roleId, int warId, int hard) {
		String key = BOX_KEY + roleId;
		if(client.exists(key)){
			return client.sismember(BOX_KEY + roleId, campBoxMapKey(warId, hard));
		}
		return false;
	}

	@Override
	public void saveDrawCampBox(int roleId, int warId, int hard) {
		client.sadd(BOX_KEY + roleId, campBoxMapKey(warId, hard));
	}
	
	
	
}
