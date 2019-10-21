package com.ourpalm.tank.domain;

import com.ourpalm.core.util.DateUtil;

public class ActivityMonthCardInfo {

	private int activityId;				//活动id
	
	private boolean monthCardBuying;	//订单提交中
	private boolean yearCardBuying;		//订单提交中
	
	private long yearCardExpireTime;		//永久卡效天数365天 （过期时间）
	private long monthCardExpireTime;	 //月卡有效天数30天 （过期时间）
	
	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public boolean monthCardBuying() {
		return monthCardBuying;
	}

	public boolean getMonthCardBuying() {
		return monthCardBuying;
	}
	
	public void setMonthCardBuying(boolean monthCardBuying) {
		this.monthCardBuying = monthCardBuying;
	}
	
	public boolean getYearCardBuying() {
		return yearCardBuying;
	}
	
	public boolean yearCardBuying() {
		return yearCardBuying;
	}

	public void setYearCardBuying(boolean yearCardBuying) {
		this.yearCardBuying = yearCardBuying;
	}

	public long getYearCardExpireTime() {
		return yearCardExpireTime;
	}

	public void setYearCardExpireTime(long yearCardExpireTime) {
		this.yearCardExpireTime = yearCardExpireTime;
	}

	public long getMonthCardExpireTime() {
		return monthCardExpireTime;
	}

	public void setMonthCardExpireTime(long monthCardExpireTime) {
		this.monthCardExpireTime = monthCardExpireTime;
	}

	public boolean monthCardIsExpire() {
		long curTime = System.currentTimeMillis();
		return curTime >= monthCardExpireTime;
	}
	
	public boolean yearCardIsExpire() {
		long curTime = System.currentTimeMillis();
		return curTime >= yearCardExpireTime;
	}
	
	public void monthCardBuy() {
		monthCardExpireTime = DateUtil.timeAddDay(System.currentTimeMillis(), 30);
	}
	
	public void yearCardBuy() {
		yearCardExpireTime = DateUtil.timeAddDay(System.currentTimeMillis(), 365);
	}

	public int getMonthCardDay() {
		if(monthCardIsExpire()) {
			return 0;
		}
		return DateUtil.betweenDates(System.currentTimeMillis(), monthCardExpireTime);
	}

	public int getYearCardDay() {
		if(yearCardIsExpire()) {
			return 0;
		}
		return DateUtil.betweenDates(System.currentTimeMillis(), yearCardExpireTime);
	}

}
