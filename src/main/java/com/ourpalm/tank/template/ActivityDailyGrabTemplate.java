package com.ourpalm.tank.template;

public class ActivityDailyGrabTemplate {

	private int grabMax;
	private int packetCount;
	private int grabTotalMax;
	private int freeReward;
	private int grabBeginHour;
	private int grabEndHour;
	private String grabTimeDesc;
	
	public int getGrabBeginHour() {
		return grabBeginHour;
	}
	public void setGrabBeginHour(int grabBeginHour) {
		this.grabBeginHour = grabBeginHour;
	}
	public int getGrabEndHour() {
		return grabEndHour;
	}
	public void setGrabEndHour(int grabEndHour) {
		this.grabEndHour = grabEndHour;
	}
	public String getGrabTimeDesc() {
		return grabTimeDesc;
	}
	public void setGrabTimeDesc(String grabTimeDesc) {
		this.grabTimeDesc = grabTimeDesc;
	}
	public int getGrabMax() {
		return grabMax;
	}
	public void setGrabMax(int grabMax) {
		this.grabMax = grabMax;
	}
	public int getPacketCount() {
		return packetCount;
	}
	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}
	public int getGrabTotalMax() {
		return grabTotalMax;
	}
	public void setGrabTotalMax(int grabTotalMax) {
		this.grabTotalMax = grabTotalMax;
	}
	public int getFreeReward() {
		return freeReward;
	}
	public void setFreeReward(int freeReward) {
		this.freeReward = freeReward;
	}
	
}
