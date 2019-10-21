package com.ourpalm.tank.template;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;

public class ActivityMonthLoginRewardTemplate {
	private int month;
	private int day;
	private int type;
	private int rewardId;
	private int count;
	private int vipLevel;
	
	public void init() {
		Object o = true;
		switch(type) {
		case 1:
			o = GameContext.getTankApp().getTankTemplate(rewardId);
			break;
		case 2:
			o = GameContext.getMemberApp().getMemberTemplate(rewardId);
			break;
		case 3:
			o = GameContext.getGoodsApp().getGoodsBaseTemplate(rewardId);
			break;
		}
		if(o == null)
			LogCore.startup.error("月签到奖励表：{} 月， {} 天，奖励配置类型：{}， id: {} 不存在", month, day, type, rewardId);
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getRewardId() {
		return rewardId;
	}
	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}
	
}
