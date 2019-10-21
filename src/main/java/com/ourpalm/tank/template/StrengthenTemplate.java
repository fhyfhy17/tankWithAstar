package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * partNew strengthen 配件 强化表
 * 
 * @author fhy
 *
 */
public class StrengthenTemplate implements KeySupport<Integer> {

	private int level;// 强化等级
	private int iron;// 强化需要银币
	private int gold;// 保成强化需要金币
	private int strengthenSuccessRate;// 银币强化成功率

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

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getStrengthenSuccessRate() {
		return strengthenSuccessRate;
	}

	public void setStrengthenSuccessRate(int strengthenSuccessRate) {
		this.strengthenSuccessRate = strengthenSuccessRate;
	}

}
