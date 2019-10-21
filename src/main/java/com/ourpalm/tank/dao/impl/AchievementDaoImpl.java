package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.tank.dao.AchievementDao;
import com.ourpalm.tank.domain.RoleAchievement;

public class AchievementDaoImpl extends AbstractJedisDao implements AchievementDao {
	
	private final static String KEY = "ROLE_ACHIEVEMENT_";
	
	@Override
	public RoleAchievement get(int roleId) {
		return JSON.parseObject(getClient().get(KEY + roleId), RoleAchievement.class);
	}

	@Override
	public void save(int roleId, RoleAchievement ach) {
		if (ach == null) {
			return;
		}
		getClient().set(KEY + roleId, JSON.toJSONString(ach));
	}
}
