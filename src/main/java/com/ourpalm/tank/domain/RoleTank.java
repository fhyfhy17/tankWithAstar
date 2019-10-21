package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;

public class RoleTank {

	private int roleId;

	private int tankId;

	private int winCount; // 胜利场次
	private int lostCount; // 失败场次

	private int exp; // 经验值

	private int tired; // 疲劳

	Map<PART_INDEX, PartInfo> partNewMap = new HashMap<>();// 坦克新配件

	private int grooveNum;// 当前有的槽位数

	private long lastCallUpTime;// 上次战斗召唤时间
	private boolean callUp;// 战斗召唤状态

	/** 累计胜利场次 */
	public void accumuWinCount() {
		this.winCount += 1;
	}

	/** 累计失败场次 */
	public void accumuLostCount() {
		this.lostCount += 1;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getTankId() {
		return tankId;
	}

	public void setTankId(int tankId) {
		this.tankId = tankId;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public int getLostCount() {
		return lostCount;
	}

	public void setLostCount(int lostCount) {
		this.lostCount = lostCount;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getTired() {
		return tired;
	}

	public void setTired(int tired) {
		this.tired = tired;
	}

	public Map<PART_INDEX, PartInfo> getPartNewMap() {
		return partNewMap;
	}

	public void setPartNewMap(Map<PART_INDEX, PartInfo> partNewMap) {
		this.partNewMap = partNewMap;
	}

	public int getGrooveNum() {
		return grooveNum;
	}

	public void setGrooveNum(int grooveNum) {
		this.grooveNum = grooveNum;
	}

	public long getLastCallUpTime() {
		return lastCallUpTime;
	}

	public void setLastCallUpTime(long lastCallUpTime) {
		this.lastCallUpTime = lastCallUpTime;
	}

	public boolean isCallUp() {
		return callUp;
	}

	public void setCallUp(boolean callUp) {
		this.callUp = callUp;
	}

}
