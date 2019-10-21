package com.ourpalm.tank.domain;

public class ActivitySevenLoginInfo {
	private int day;			//第几天
	private boolean hadDraw;	//是否已领取
	private long drawTime;		//领取时间
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public boolean isHadDraw() {
		return hadDraw;
	}
	
	public boolean getHadDraw() {
		return hadDraw;
	}
	
	public void setHadDraw(boolean hadDraw) {
		this.hadDraw = hadDraw;
	}
	public long getDrawTime() {
		return drawTime;
	}
	public void setDrawTime(long drawTime) {
		this.drawTime = drawTime;
	}
	
	public void increaseDay() {
		this.day = this.day + 1;
		this.hadDraw = false;
		this.drawTime = 0;
	}
	
}
