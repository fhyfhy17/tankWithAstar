package com.ourpalm.tank.domain;

public class TechnologyInfo{
	
	private int techId;			//科技ID
	private long devOverTime;	//研发结束时间
	private int curExp;			//当前经验值
	
	public int getTechId() {
		return techId;
	}
	public void setTechId(int techId) {
		this.techId = techId;
	}
	public long getDevOverTime() {
		return devOverTime;
	}
	public void setDevOverTime(long devOverTime) {
		this.devOverTime = devOverTime;
	}
	public int getCurExp() {
		return curExp;
	}
	public void setCurExp(int curExp) {
		this.curExp = curExp;
	}
	
	/** 研发剩余时间(单位: 秒) */
	public int devOverplusTime(){
		int overTime = (int)((this.devOverTime - System.currentTimeMillis()) / 1000);
		if(overTime <= 0){
			return 0;
		}
		return overTime;
	}
}