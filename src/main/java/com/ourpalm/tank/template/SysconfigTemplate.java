package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class SysconfigTemplate implements KeySupport<Integer> {
	private int id;
	private int value;

	public void init() {
	}

	@Override
	public Integer getKey() {
		return id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
