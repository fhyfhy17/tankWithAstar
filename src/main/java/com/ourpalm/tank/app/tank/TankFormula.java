package com.ourpalm.tank.app.tank;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class TankFormula {
	
	private static final Map<AttrType, AttrType> attrAdditions = new HashMap<>();
	
	static {
		attrAdditions.put(AttrType.maxHp, AttrType.maxHp_add_rat);
		attrAdditions.put(AttrType.fdef, AttrType.fdef_add_rat);
		attrAdditions.put(AttrType.idef, AttrType.idef_add_rat);
		attrAdditions.put(AttrType.bdef, AttrType.bdef_add_rat);
		attrAdditions.put(AttrType.atk, AttrType.atk_add_rat);
		attrAdditions.put(AttrType.chuanjia, AttrType.chuanjia_add_rat);
		attrAdditions.put(AttrType.speed, AttrType.speed_add_rat);
	}

	
	/** 计算属性百分比加成 */
	public static Map<AttrType, Float> calcAttr(Map<AttrType, Float> attrMap){
		if(Util.isEmpty(attrMap)){
			return attrMap;
		}
		for(Map.Entry<AttrType, AttrType> entry : attrAdditions.entrySet()){
			Float value = attrMap.get(entry.getKey());
			Float addRate = attrMap.remove(entry.getValue());
			if (value == null || addRate == null) {
				continue;
			}
			attrMap.put(entry.getKey(), value * ( 1 + addRate));
		}
		return attrMap;
	}
}
