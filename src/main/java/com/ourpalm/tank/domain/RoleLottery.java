package com.ourpalm.tank.domain;

import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.member.MemberApp;

public class RoleLottery {

	private int roleId;
	private int diamondsFreeCount; // 当日钻石单抽免费次数
	private int diamondsFloorCount; // 钻石保底抽奖次数,触发保底清0
	private int diamondsCount; // 钻石单抽累计次数

	private int ironFreeCount; // 当日银币单抽免费次数
	private int ironCount = MemberApp.MULTI_COUNT; // 银币单抽累计次数
	private int ironFloorCount; // 银币保底抽奖次数,触发保底清0

	private boolean firstOneDiamonds = true; // 首次钻石单抽
	private boolean firstMultDiamonds = true; // 首次钻石五连抽

	private long lastFreeIronTime;// 上次免费银币抽奖时间
	private long lastFreeDiamondTime;// 上次免费钻石抽奖时间

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getDiamondsFreeCount() {
		return diamondsFreeCount;
	}

	public void setDiamondsFreeCount(int diamondsFreeCount) {
		this.diamondsFreeCount = diamondsFreeCount;
	}

	public int getDiamondsFloorCount() {
		return diamondsFloorCount;
	}

	public void setDiamondsFloorCount(int diamondsFloorCount) {
		this.diamondsFloorCount = diamondsFloorCount;
	}

	public int getDiamondsCount() {
		return diamondsCount;
	}

	public void setDiamondsCount(int diamondsCount) {
		this.diamondsCount = diamondsCount;
	}

	public int getIronFreeCount() {
		return ironFreeCount;
	}

	public void setIronFreeCount(int ironFreeCount) {
		this.ironFreeCount = ironFreeCount;
	}

	public int getIronCount() {
		return ironCount;
	}

	public void setIronCount(int ironCount) {
		this.ironCount = ironCount;
	}

	public int getIronFloorCount() {
		return ironFloorCount;
	}

	public void setIronFloorCount(int ironFloorCount) {
		this.ironFloorCount = ironFloorCount;
	}

	public boolean isFirstOneDiamonds() {
		return firstOneDiamonds;
	}

	public void setFirstOneDiamonds(boolean firstOneDiamonds) {
		this.firstOneDiamonds = firstOneDiamonds;
	}

	public boolean isFirstMultDiamonds() {
		return firstMultDiamonds;
	}

	public void setFirstMultDiamonds(boolean firstMultDiamonds) {
		this.firstMultDiamonds = firstMultDiamonds;
	}

	public long getLastFreeIronTime() {
		return lastFreeIronTime;
	}

	public void setLastFreeIronTime(long lastFreeIronTime) {
		this.lastFreeIronTime = lastFreeIronTime;
	}

	public long getLastFreeDiamondTime() {
		return lastFreeDiamondTime;
	}

	public void setLastFreeDiamondTime(long lastFreeDiamondTime) {
		this.lastFreeDiamondTime = lastFreeDiamondTime;
	}

}
