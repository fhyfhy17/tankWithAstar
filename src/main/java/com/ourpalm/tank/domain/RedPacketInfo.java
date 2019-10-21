package com.ourpalm.tank.domain;

public class RedPacketInfo {
	
	private int activityId; 	//第几期活动
	private int rmb; //活动中充值rmb
	
	private int drawCount;	//剩余领取次数
	
	private int group;	//组Id
	
	private int usedCount; 	//已领取次数
	
	public RedPacketInfo() {
		this.group = -1;
	}

	public int getRmb() {
		return rmb;
	}

	public void setRmb(int rmb) {
		this.rmb = rmb;
	}

	public int getDrawCount() {
		return drawCount;
	}

	public void setDrawCount(int drawCount) {
		this.drawCount = drawCount;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public boolean canDraw() {
		return this.drawCount > 0;
	}
}
