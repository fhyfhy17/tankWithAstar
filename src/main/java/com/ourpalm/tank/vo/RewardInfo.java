package com.ourpalm.tank.vo;

public class RewardInfo {
	private int id;
	private int count;
	private int type;
	
	public RewardInfo(int id, int count, int type) {
		this.id = id;
		this.count = count;
		this.type = type;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
}
