package com.ourpalm.tank.dao;

import java.util.List;

import com.ourpalm.tank.domain.MasterQuest;
import com.ourpalm.tank.domain.Quest;
import com.ourpalm.tank.domain.RoleActive;

public interface QuestDao {

	List<Quest> getAll(Integer roleId);
	
	void save(Integer roleId, Quest quest);
	
	void saveAll(Integer roleId, List<Quest> quests);
	
	void delete(Integer roleId, Integer questId);
	
	List<MasterQuest> getAllMaster(Integer roleId);
	
	void saveAllMaster(Integer roleId, List<MasterQuest> quests);
	
	void saveMaster(Integer roleId, MasterQuest quest);
	
	void deleteMaster(Integer roleId, Integer questId);
	
	//活跃点
	RoleActive getRoleActive(Integer roleId);
	
	//保存活跃点
	void saveRoleActive(RoleActive roleActive);
}
