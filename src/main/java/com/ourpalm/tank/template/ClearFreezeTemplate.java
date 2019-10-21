package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * partNew clearFreeze 配件 清除强化冷却花费表
 * 
 * @author fhy
 *
 */
public class ClearFreezeTemplate implements KeySupport<Integer> {

	private int num; // 清除的次数
	private int gold; // 需要的金币

	public void init() {
	}

	@Override
	public Integer getKey() {
		return num;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

}
