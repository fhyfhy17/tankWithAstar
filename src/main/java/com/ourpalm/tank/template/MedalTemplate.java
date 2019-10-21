package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class MedalTemplate implements KeySupport<Integer> {

	private int id;
	private String name;
	private int type;
	private String desc;
	private int iron;
	private int level;
	private int nextId;
	
	private int property1;
	private float value1;
	private int property2;
	private float value2;
	private int property3;
	private float value3;
	private int property4;
	private float value4;
	private int property5;
	private float value5;
	private int property6;
	private float value6;
	private int property7;
	private float value7;
	private int property8;
	private float value8;
	
	private float strengthRat;		//战斗力加成比率
	private float matchScoreRat;	//匹配分加成比率
	
	private Map<AttrType, Float> attrMap = new HashMap<>();
	
	public void init(){
		this.put(property1, value1, attrMap);
		this.put(property2, value2, attrMap);
		this.put(property3, value3, attrMap);
		this.put(property4, value4, attrMap);
		this.put(property5, value5, attrMap);
		this.put(property6, value6, attrMap);
		this.put(property7, value7, attrMap);
		this.put(property8, value8, attrMap);
	}
	
	private void put(int property, float value, Map<AttrType, Float> map) {
		if (property <= 0 || value <= 0) {
			return;
		}
		AttrType type = AttrType.valueOf(property);
		map.put(type, value);
	}
	
	@Override
	public Integer getKey(){
		return this.id;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getNextId() {
		return nextId;
	}
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}

	public int getProperty1() {
		return property1;
	}

	public void setProperty1(int property1) {
		this.property1 = property1;
	}

	public float getValue1() {
		return value1;
	}

	public void setValue1(float value1) {
		this.value1 = value1;
	}

	public int getProperty2() {
		return property2;
	}

	public void setProperty2(int property2) {
		this.property2 = property2;
	}

	public float getValue2() {
		return value2;
	}

	public void setValue2(float value2) {
		this.value2 = value2;
	}

	public Map<AttrType, Float> getAttrMap() {
		return attrMap;
	}
	public int getProperty3() {
		return property3;
	}
	public void setProperty3(int property3) {
		this.property3 = property3;
	}
	public float getValue3() {
		return value3;
	}
	public void setValue3(float value3) {
		this.value3 = value3;
	}
	
	public float getStrengthRat() {
		return strengthRat;
	}

	public void setStrengthRat(float strengthRat) {
		this.strengthRat = strengthRat;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getProperty4() {
		return property4;
	}

	public void setProperty4(int property4) {
		this.property4 = property4;
	}

	public float getValue4() {
		return value4;
	}

	public void setValue4(float value4) {
		this.value4 = value4;
	}

	public int getProperty5() {
		return property5;
	}

	public void setProperty5(int property5) {
		this.property5 = property5;
	}

	public float getValue5() {
		return value5;
	}

	public void setValue5(float value5) {
		this.value5 = value5;
	}

	public int getProperty6() {
		return property6;
	}

	public void setProperty6(int property6) {
		this.property6 = property6;
	}

	public float getValue6() {
		return value6;
	}

	public void setValue6(float value6) {
		this.value6 = value6;
	}

	public int getProperty7() {
		return property7;
	}

	public void setProperty7(int property7) {
		this.property7 = property7;
	}

	public float getValue7() {
		return value7;
	}

	public void setValue7(float value7) {
		this.value7 = value7;
	}

	public int getProperty8() {
		return property8;
	}

	public void setProperty8(int property8) {
		this.property8 = property8;
	}

	public float getValue8() {
		return value8;
	}

	public void setValue8(float value8) {
		this.value8 = value8;
	}

	public float getMatchScoreRat() {
		return matchScoreRat;
	}

	public void setMatchScoreRat(float matchScoreRat) {
		this.matchScoreRat = matchScoreRat;
	}
}
