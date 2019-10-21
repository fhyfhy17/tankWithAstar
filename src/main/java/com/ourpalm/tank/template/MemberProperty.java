package com.ourpalm.tank.template;

import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class MemberProperty extends AbstractAttribute{

	private int level;

	public float getAttrValue(AttrType attrType) {
		Float value = this.property.get(attrType);
		return value == null ? 0 : value;
	}
	@Override
	public Integer getKey() {
		return this.level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
