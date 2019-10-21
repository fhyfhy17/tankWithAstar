package com.ourpalm.tank.template;

public class ActivityKeepOnlineTemplate implements Comparable<ActivityKeepOnlineTemplate>{
	private int vipLevel;
	private int upValue;
	private int addValueBySec;
	
	public int getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	public int getUpValue() {
		return upValue;
	}
	public void setUpValue(int upValue) {
		this.upValue = upValue;
	}
	public int getAddValueBySec() {
		return addValueBySec;
	}
	public void setAddValueBySec(int addValueBySec) {
		this.addValueBySec = addValueBySec;
	}
	@Override
	public int compareTo(ActivityKeepOnlineTemplate o) {
		return this.vipLevel > o.getVipLevel() ? 1 : -1;
	}
	
}
