package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.DateUtil;

public class ActivityMonthLoginInfo {

	private int month; 			//哪个月的数据
	private int signCount;		//签到次数
	private long lastSignTime; 	//上次签到时间
	private int pointDay; 		//节点奖励天   									//作废
	private boolean pointCanDraw;	//是否可领取节点奖励	        //作废
	private int todayFillSignCount; 	//今日补签次数（每日恢复0）,计算花费
	private int fillSignCount;	//剩余补签次数
	
	private List<Integer> hasReceive= new ArrayList<>();//已经领取的节点奖励
	
	public ActivityMonthLoginInfo() {}
	
	public ActivityMonthLoginInfo(int month) {
		this.month = month;
	}
	
	public int nextSignDay() {
		return this.signCount + 1;
	}
	
//	public void increaseFillSignCount(int count) {
//		this.fillSignCount += count;
//	}
	
	public void decreaseFillSignCount() {
		this.fillSignCount--;
		this.todayFillSignCount++;
	}
	
	public void increaseSignCount() {
		this.signCount++;
	}
	
	//计算可补签次数
	public void reCalcFillSignCount(){
		int fillSignCount = Math.max(DateUtil.getCurrentDayOfMonth() - this.getSignCount(), 0);
		this.setFillSignCount(fillSignCount);
	}
	
	
/***************************** setter/getter *******************************/
	public int getSignCount() {
		return signCount;
	}
	public void setSignCount(int signCount) {
		this.signCount = signCount;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public long getLastSignTime() {
		return lastSignTime;
	}
	public void setLastSignTime(long lastSignTime) {
		this.lastSignTime = lastSignTime;
	}
	
	public int getTodayFillSignCount() {
		return todayFillSignCount;
	}

	public void setTodayFillSignCount(int todayFillSignCount) {
		this.todayFillSignCount = todayFillSignCount;
	}

	public int getFillSignCount() {
		return fillSignCount;
	}

	public void setFillSignCount(int fillSignCount) {
		this.fillSignCount = fillSignCount;
	}

	public int getPointDay() {
		return pointDay;
	}
	public void setPointDay(int pointDay) {
		this.pointDay = pointDay;
	}
	public boolean isPointCanDraw() {
		return pointCanDraw;
	}
	
	public boolean getPointCanDraw() {
		return this.pointCanDraw;
	}
	
	public void setPointCanDraw(boolean pointCanDraw) {
		this.pointCanDraw = pointCanDraw;
	}

	public List<Integer> getHasReceive() {
		return hasReceive;
	}

	public void setHasReceive(List<Integer> hasReceive) {
		this.hasReceive = hasReceive;
	}
	
}
