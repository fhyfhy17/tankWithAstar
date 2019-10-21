package com.ourpalm.tank.vo.result;

public class ValueResult<T> extends Result {
	private T value;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
