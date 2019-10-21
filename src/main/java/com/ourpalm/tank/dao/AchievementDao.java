package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.RoleAchievement;

public interface AchievementDao {

	RoleAchievement get(int roleId);
	
	void save(int roleId, RoleAchievement ach);
	
}
