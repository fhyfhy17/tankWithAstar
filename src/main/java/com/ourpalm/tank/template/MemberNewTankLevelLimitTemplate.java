package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 新成员 坦克等级限制
 * 
 * @author fhy
 *
 */
public class MemberNewTankLevelLimitTemplate implements KeySupport<Integer> {
	private int level;
	private int propertyPercent;
	private int combineSwitch;

	public void init() {
	}

	@Override
	public Integer getKey() {
		return level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPropertyPercent() {
		return propertyPercent;
	}

	public void setPropertyPercent(int propertyPercent) {
		this.propertyPercent = propertyPercent;
	}

	public int getCombineSwitch() {
		return combineSwitch;
	}

	public void setCombineSwitch(int combineSwitch) {
		this.combineSwitch = combineSwitch;
	}

}
