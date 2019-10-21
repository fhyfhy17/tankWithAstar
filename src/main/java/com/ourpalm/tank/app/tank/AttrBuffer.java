package com.ourpalm.tank.app.tank;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class AttrBuffer {

	private Map<AttrType, Float> attributeMap = new HashMap<>();	
	
	public AttrBuffer append(Map<AttrType, Float> attrMap){
		if(Util.isEmpty(attrMap)){
			return this;
		}
		for(Entry<AttrType, Float> entry : attrMap.entrySet()){
			Float value = attributeMap.get(entry.getKey());
			if(value == null){
				value = 0f;
			}
			value += entry.getValue();
			attributeMap.put(entry.getKey(), value);
		}
		return this;
	}
	
	
	public AttrBuffer append(AttrType attrType, float value){
		Float currValue = attributeMap.get(attrType);
		if(currValue == null){
			currValue = 0f;
		}
		currValue += value;
		attributeMap.put(attrType, currValue);
		
		return this;
	}
	
	
	public AttrBuffer append(AttrBuffer attrBuffer){
		if(attrBuffer == null || attrBuffer.isEmpty()){
			return this;
		}
		return this.append(attrBuffer.attributeMap);
	}
	
	
	public Map<AttrType, Float> getAttrMap(){
		return this.attributeMap;
	}
	
	
	public boolean isEmpty(){
		return this.attributeMap.isEmpty();
	}

	public void put(AttrType attrType, Float value) {
		this.attributeMap.put(attrType, value);
	}
	
	public Float getAttr(AttrType attrType){
		Float value = attributeMap.get(attrType);
		if(value == null){
			return 0f;
		}
		return value;
	}
	
}
