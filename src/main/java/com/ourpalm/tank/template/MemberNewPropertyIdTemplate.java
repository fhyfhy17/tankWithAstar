package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 新成员 ID属性对应表
 * 
 * @author fhy
 *
 */
public class MemberNewPropertyIdTemplate implements KeySupport<Integer> {
	private int id;
	private String property;
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
