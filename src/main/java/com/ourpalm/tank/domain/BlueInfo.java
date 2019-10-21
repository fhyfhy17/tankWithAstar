package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.app.GameContext;

/**
 * 蓝钻礼包领取情况
 * 
 * @author Administrator
 *
 */
public class BlueInfo {
	private int roleId;
	private int newBieGift;// 0无1可领取2已领取
	private int everyDayGift;// 0无1可领取2已领取
	private int superGift;// 豪华礼包 //0无1可领取2已领取
	private int yearGift;// 年费礼包 //0无1可领取2已领取
	private List<Integer> hasLevelGift = new ArrayList<>();// 已经领取的升级礼包 <ID>
	private int blueLevel;// 蓝钻等级

	private int everyDayGiftLevel;// 已领取的每日礼包等级，如 蓝钻1级领取时为1，玩家升为2级还可以再领取一个2级的

	public int getNewBieGift() {
		return newBieGift;
	}

	public void setNewBieGift(int newBieGift) {
		this.newBieGift = newBieGift;
	}

	public int getEveryDayGift() {
		return everyDayGift;
	}

	public void setEveryDayGift(int everyDayGift) {
		this.everyDayGift = everyDayGift;
	}

	public int getSuperGift() {
		return superGift;
	}

	public void setSuperGift(int superGift) {
		this.superGift = superGift;
	}

	public int getYearGift() {
		return yearGift;
	}

	public void setYearGift(int yearGift) {
		this.yearGift = yearGift;
	}

	public List<Integer> getHasLevelGift() {
		return hasLevelGift;
	}

	public void setHasLevelGift(List<Integer> hasLevelGift) {
		this.hasLevelGift = hasLevelGift;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getBlueLevel() {
		return blueLevel;
	}

	public void setBlueLevel(int blueLevel) {
		this.blueLevel = blueLevel;
	}

	public int getEveryDayGiftLevel() {
		return everyDayGiftLevel;
	}

	public void setEveryDayGiftLevel(int everyDayGiftLevel) {
		this.everyDayGiftLevel = everyDayGiftLevel;
	}

}
