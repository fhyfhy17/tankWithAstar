package com.ourpalm.tank.domain;

public class ContinueLoginInfo {

	private long lastLoginTime;	//上次登录时间
	
	private int day;	//连续几天;
	
	private boolean hadThreeCode;	//已经获取3天激活码
	private boolean hadSevenCode;	//已经获取7天激活码

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public void increaseDay() {
		this.day ++;
	}

	public boolean isHadThreeCode() {
		return hadThreeCode;
	}

	public void setHadThreeCode(boolean hadThreeCode) {
		this.hadThreeCode = hadThreeCode;
	}

	public boolean isHadSevenCode() {
		return hadSevenCode;
	}

	public void setHadSevenCode(boolean hadSevenCode) {
		this.hadSevenCode = hadSevenCode;
	}
	
}
