package com.ourpalm.tank.domain;

/**
 * 幸运转盘
 * 
 * @author Administrator
 *
 */
public class LuckyWheelInfo {
	private int roleId;
	private int count;// 已免费领取次数
	private int diamondCount;// 已钻石领取次数
	private long lastFreeTime;// 上次免费领取时间

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getDiamondCount() {
		return diamondCount;
	}

	public void setDiamondCount(int diamondCount) {
		this.diamondCount = diamondCount;
	}

	public long getLastFreeTime() {
		return lastFreeTime;
	}

	public void setLastFreeTime(long lastFreeTime) {
		this.lastFreeTime = lastFreeTime;
	}

}
