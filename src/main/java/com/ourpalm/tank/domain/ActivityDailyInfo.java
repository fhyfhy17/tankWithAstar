package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.message.ACTIVITY_MSG.RewardState;

public class ActivityDailyInfo {
	
	private int activityId;		//日常活动id
	private Map<Integer, Integer> itemMap = new HashMap<>();//活动条目id,奖励状态
	
	private long lastLoginTime;		//上次登录时间
	private int doubleExpCount;		//进行双倍经验活动次数
	private int remainTime;			//剩余时间(秒)
	private int remainId;			//上线时长活动当前条目id
	
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	public Map<Integer, Integer> getItemMap() {
		return itemMap;
	}
	public void setItemMap(Map<Integer, Integer> itemMap) {
		this.itemMap = itemMap;
	}
	
	public RewardState getItemState(int itemId) {
		if(itemMap.containsKey(itemId)) {
			return RewardState.valueOf(itemMap.get(itemId));
		}
		return RewardState.INIT;
	}
	
	public void putItemState(int itemId, RewardState state) {
		itemMap.put(itemId, state.getNumber());
	}
	public int getDoubleExpCount() {
		return doubleExpCount;
	}
	public void setDoubleExpCount(int doubleExpCount) {
		this.doubleExpCount = doubleExpCount;
	}
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public int getRemainTime() {
		return remainTime;
	}
	public void setRemainTime(int remainTime) {
		this.remainTime = remainTime;
	}
	
	public boolean hadItem(int itemId) {
		return itemMap.containsKey(itemId);
	}
	public int getRemainId() {
		return remainId;
	}
	public void setRemainId(int remainId) {
		this.remainId = remainId;
	}
	
}
