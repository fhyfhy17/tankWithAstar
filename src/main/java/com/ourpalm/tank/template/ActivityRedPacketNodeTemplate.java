package com.ourpalm.tank.template;

public class ActivityRedPacketNodeTemplate implements Comparable<ActivityRedPacketNodeTemplate>{
	private int id;
	private int rmbLimit;
	private int drawCount;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRmbLimit() {
		return rmbLimit;
	}
	public void setRmbLimit(int rmbLimit) {
		this.rmbLimit = rmbLimit;
	}
	public int getDrawCount() {
		return drawCount;
	}
	public void setDrawCount(int drawCount) {
		this.drawCount = drawCount;
	}
	@Override
	public int compareTo(ActivityRedPacketNodeTemplate o) {
		return this.id > o.getId() ? 1 : -1;
	}
	
}
