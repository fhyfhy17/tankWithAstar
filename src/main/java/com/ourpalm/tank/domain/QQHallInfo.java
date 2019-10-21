package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * QQ大厅用户礼包领取情况
 * 
 * @author Administrator
 *
 */
public class QQHallInfo {
	private int isQQHall;
	private int roleId;
	private int newBieGift;// 0无1可领取2已领取
	private int everyDayGift;// 0无1可领取2已领取
	private List<Integer> hasLevelGift = new ArrayList<>();// 已经领取的升级礼包 <ID>

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

	public int getIsQQHall() {
		return isQQHall;
	}

	public void setIsQQHall(int isQQHall) {
		this.isQQHall = isQQHall;
	}

}
