package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.message.ACHIEVEMENT_MSG.AchievementState;

public class RoleAchievement {
	
	//保存完成状态的achievement
	private Map<Integer, Integer> stateMap;		//key achievementId,  value AchievementState
	
	private Map<Integer, Integer> valueMap;		//key groupId,  value 数量
	
	public RoleAchievement() {
		stateMap = new HashMap<>();
		valueMap = new HashMap<>();
	}

	public Map<Integer, Integer> getStateMap() {
		return stateMap;
	}

	public void setStateMap(Map<Integer, Integer> stateMap) {
		this.stateMap = stateMap;
	}

	public Map<Integer, Integer> getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map<Integer, Integer> valueMap) {
		this.valueMap = valueMap;
	}
	
	public int getAchievementValue(int groupId, int limit) {
		return Math.min(getValue(groupId), limit);
	}
	
	public void putValue(int groupId, int v) {
		valueMap.put(groupId, v);
	}
	
	public void putState(int achId, int state) {
		stateMap.put(achId, state);
	}
	
	public int getState(int achId) {
		return stateMap.containsKey(achId) ? stateMap.get(achId) : AchievementState.accept_VALUE;
	}
	
	public int getValue(int groupId) {
		return valueMap.containsKey(groupId) ? valueMap.get(groupId) : 0;
	}
}
