package com.ourpalm.tank.template;

public class _102_FreeDoubleTemplate {
	private int id;
	private int logicId;
	private String pictureId;
	private int dayFreeCount;	//每日免费次数
	private int times;	//倍数
	private int toPage;
	private String desc;
	
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getLogicId() {
		return logicId;
	}
	public void setLogicId(int logicId) {
		this.logicId = logicId;
	}
	public int getDayFreeCount() {
		return dayFreeCount;
	}
	public void setDayFreeCount(int dayFreeCount) {
		this.dayFreeCount = dayFreeCount;
	}
	public String getPictureId() {
		return pictureId;
	}
	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getToPage() {
		return toPage;
	}
	public void setToPage(int toPage) {
		this.toPage = toPage;
	}
}
