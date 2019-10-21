package com.ourpalm.tank.vo;

import java.util.Map;

import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public interface IComponent {

	Map<AttrType, Float> getAttr();
	Map<AttrType, Float> getNewAttr();
}
