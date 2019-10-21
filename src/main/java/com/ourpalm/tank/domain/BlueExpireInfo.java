package com.ourpalm.tank.domain;

public class BlueExpireInfo {
	private int type;// 1普通蓝钻2年费蓝钻3豪华蓝钻4超级蓝钻
	private int day;// -3 到 3

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

}
