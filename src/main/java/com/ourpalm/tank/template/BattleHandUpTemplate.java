package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class BattleHandUpTemplate implements KeySupport<Integer>{

	private int count;
	private float handupValue;
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public float getHandupValue() {
		return handupValue;
	}
	public void setHandupValue(float handupValue) {
		this.handupValue = handupValue;
	}
	
	@Override
	public Integer getKey() {
		return count;
	}
}
