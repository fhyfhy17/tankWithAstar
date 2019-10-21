package com.ourpalm.tank.template;

import java.util.Map;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.vo.IComponent;

/**
 * 新成员
 * 
 * @author fhy
 *
 */
public class MemberNewTemplate implements KeySupport<Integer> {
	private int id;
	private String name;
	private int quality;
	private int initProperty;

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

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getInitProperty() {
		return initProperty;
	}

	public void setInitProperty(int initProperty) {
		this.initProperty = initProperty;
	}

}
