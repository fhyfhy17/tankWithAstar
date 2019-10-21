package com.ourpalm.tank.domain;

public class HallFirstPayInfo {
	private int activityId;		//活动id
	private boolean hadReward;	//已经领取奖励
	private boolean canDraw;	//是可以领奖
	private int createTime;	//活动创建时间
	
	public int getActivityId() {
		return activityId;
	}
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}
	
	public boolean getHadReward() {
		return hadReward;
	}
	public boolean getCanDraw() {
		return canDraw;
	}
	public boolean hadReward() {
		return hadReward;
	}
	public void setHadReward(boolean hadReward) {
		this.hadReward = hadReward;
	}
	public boolean canDraw() {
		return canDraw;
	}
	public void setCanDraw(boolean canDraw) {
		this.canDraw = canDraw;
	}
	public int getCreateTime() {
		return createTime;
	}
	public void setCreateTime(int createTime) {
		this.createTime = createTime;
	}
}
