package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class BattleWeakTemplate implements KeySupport<Integer>{
	
	private int count;
	private float weakValue;

	@Override
	public Integer getKey() {
		return count;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public float getWeakValue() {
		return weakValue;
	}

	public void setWeakValue(float weakValue) {
		this.weakValue = weakValue;
	}

	
}
