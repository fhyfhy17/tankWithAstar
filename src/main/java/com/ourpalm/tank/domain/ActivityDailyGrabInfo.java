package com.ourpalm.tank.domain;

public class ActivityDailyGrabInfo {
	private int activityId;			//活动id
	private int totalGrabCount;		//累计疯抢次数
	private int todayGrabCount;		//今日疯抢次数
	private long lastGrabCount;		//上次疯抢时间
	private boolean hadFreeReward;	//是否领取免费奖励
	
	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public boolean hadFreeReward() {
		return this.hadFreeReward;
	}
	
	public boolean getHadFreeReward() {
		return this.hadFreeReward;
	}

	public void setHadFreeReward(boolean hadFreeReward) {
		this.hadFreeReward = hadFreeReward;
	}

	public int getTotalGrabCount() {
		return totalGrabCount;
	}

	public void setTotalGrabCount(int totalGrabCount) {
		this.totalGrabCount = totalGrabCount;
	}

	public int getTodayGrabCount() {
		return todayGrabCount;
	}

	public void setTodayGrabCount(int todayGrabCount) {
		this.todayGrabCount = todayGrabCount;
	}

	public long getLastGrabCount() {
		return lastGrabCount;
	}

	public void setLastGrabCount(long lastGrabCount) {
		this.lastGrabCount = lastGrabCount;
	}
}
