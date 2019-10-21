package com.ourpalm.tank.domain;

/**
 * 活跃点
 * @author wangkun
 *
 */
public class RoleActive {

	private int roleId;
	private int dayActive;			//今日活跃点
	private int weekActive;			//本周活跃点
	private long lastLoginTime;		//上次登录时间(用于跨周判断)
	
	private int dayRewardsIndex;	//今日领取奖励索引
	private int weekRewardsIndex;	//本周领取奖励索引
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getDayActive() {
		return dayActive;
	}
	public void setDayActive(int dayActive) {
		this.dayActive = dayActive;
	}
	public int getWeekActive() {
		return weekActive;
	}
	public void setWeekActive(int weekActive) {
		this.weekActive = weekActive;
	}
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public int getDayRewardsIndex() {
		return dayRewardsIndex;
	}
	public void setDayRewardsIndex(int dayRewardsIndex) {
		this.dayRewardsIndex = dayRewardsIndex;
	}
	public int getWeekRewardsIndex() {
		return weekRewardsIndex;
	}
	public void setWeekRewardsIndex(int weekRewardsIndex) {
		this.weekRewardsIndex = weekRewardsIndex;
	}
}
