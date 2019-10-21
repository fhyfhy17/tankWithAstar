package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.vo.RewardInfo;

public class ActivityMonthLoginPointRewardTemplate implements Comparable<ActivityMonthLoginPointRewardTemplate>{
	private int month;
	private int day;
	
	private int reward;
	private int count;

	
	RewardInfo rewardInfo;
	
	public void init() {
		rewardInfo = new RewardInfo(reward, count, 3);
	}
	


	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}



	public int getReward() {
		return reward;
	}



	public void setReward(int reward) {
		this.reward = reward;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}



	public RewardInfo getRewardInfo() {
		return rewardInfo;
	}



	public void setRewardInfo(RewardInfo rewardInfo) {
		this.rewardInfo = rewardInfo;
	}



	@Override
	public int compareTo(ActivityMonthLoginPointRewardTemplate o) {
		if(this.month != o.getMonth()) {
			return this.month > o.month ? 1 : -1;
		}
		return this.day > o.day ? 1 : -1;
	}
	
}
