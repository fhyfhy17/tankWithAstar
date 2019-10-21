package com.ourpalm.tank.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.QuestDao;
import com.ourpalm.tank.domain.MasterQuest;
import com.ourpalm.tank.domain.Quest;
import com.ourpalm.tank.domain.RoleActive;


public class QuestDaoImpl extends AbstractJedisDao implements QuestDao {
	
	private final static String KEY = "ROLE_QUEST_";
	private final static String MASTER_KEY = "ROLE_MASTER_QUEST_";
	private final static String ACTIVE_KEY = "ROLE_ACTIVE_";
	
	
	@Override
	public List<Quest> getAll(Integer roleId) {
		Map<String, String> jsonMap = getClient().hgetAll(KEY + roleId);
		if (Util.isEmpty(jsonMap)) {
			return new ArrayList<>();
		}

		List<Quest> result = new ArrayList<>();
		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			result.add(JSON.parseObject(entry.getValue(), Quest.class));
		}
		return result;
	}


	@Override
	public void save(Integer roleId, Quest quest) {
		getClient().hset(KEY + roleId, quest.getId() + "", JSON.toJSONString(quest));
	}

	
	
	@Override
	public void saveAll(Integer roleId, List<Quest> quests){
		if(Util.isEmpty(quests)){
			return ;
		}
		Map<String, String> saveMap = new HashMap<>();
		for(Quest quest : quests){
			saveMap.put(quest.getId() + "" , JSON.toJSONString(quest));
		}
		client.hmSet(KEY + roleId, saveMap);
	}
	
	
	
	@Override
	public void delete(Integer roleId, Integer questId){
		getClient().hdel(KEY + roleId, questId.toString());
	}

	@Override
	public List<MasterQuest> getAllMaster(Integer roleId) {
		Map<String, String> jsonMap = getClient().hgetAll(MASTER_KEY + roleId);
		if (Util.isEmpty(jsonMap)) {
			return null;
		}

		List<MasterQuest> result = new ArrayList<>();
		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			result.add(JSON.parseObject(entry.getValue(), MasterQuest.class));
		}
		return result;
	}

	@Override
	public void saveAllMaster(Integer roleId, List<MasterQuest> quests) {
		if (Util.isEmpty(quests)) {
			return;
		}

		Map<String, String> saveMap = new HashMap<>();
		for (MasterQuest quest : quests) {
			saveMap.put(quest.getId() + "", JSON.toJSONString(quest));
		}
		getClient().hmSet(MASTER_KEY + roleId, saveMap);
	}

	@Override
	public void saveMaster(Integer roleId, MasterQuest quest) {
		getClient().hset(MASTER_KEY + roleId, quest.getId() + "", JSON.toJSONString(quest));
	}

	@Override
	public void deleteMaster(Integer roleId, Integer questId) {
		getClient().hdel(MASTER_KEY + roleId, questId.toString());
	}
	
	//活跃点
	@Override
	public RoleActive getRoleActive(Integer roleId){
		String jsonStr = this.client.get(ACTIVE_KEY + roleId);
		if(Util.isEmpty(jsonStr)){
			RoleActive active = new RoleActive();
			active.setRoleId(roleId);
			return active;
		}
		return JSON.parseObject(jsonStr, RoleActive.class);
	}
		
	//保存活跃点
	@Override
	public void saveRoleActive(RoleActive roleActive){
		this.client.set(ACTIVE_KEY + roleActive.getRoleId(), JSON.toJSONString(roleActive));
	}

	
}
