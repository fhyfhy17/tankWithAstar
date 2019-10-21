package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.app.tank.AttrBuffer;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.vo.AbstractInstance;

public class BuffTemplate  {

	private int id;
	private String name;
	private int type;			//1.间隔持续效果，到时消失  2.一次性效果加成，到时消失  3.地图buff  4.时间到触发buff
	private int repeat;			//是否可叠加 1:可叠加
	private int time;			//持续时间
	private int interval; 		//时间间隔
	private int effectId;		//特效ID
	private int change;			//变化类型 1：数值  2：百分比
	private int radius;			//地雷伤害半径
	
	private int attrType1;
	private float value1;
	private int reAttrType1;
	private int attrType2;
	private float value2;
	private int reAttrType2;
	private int attrType3;
	private float value3;
	private int reAttrType3;
	
	
	private Map<AttrType, Float> buildAttrMap(AbstractInstance tank, int attrType, float value, int reAttrType) {
		Map<AttrType, Float> attrMap = new HashMap<>();
		AttrType attr = AttrType.valueOf(attrType);
		if (attr == null) {
			return attrMap;
		}
		//改变值
		if (change == 1) {
			attrMap.put(attr, value);
			return attrMap;
		}

		// 百分比使用参照属性
		AttrType reAttr = AttrType.valueOf(reAttrType);
		if (reAttr == null) {
			return attrMap;
		}
		Float tankValue = tank.get(reAttr);
		if (tankValue == null) {
			return attrMap;
		}
		value = tankValue * value;
		attrMap.put(attr, value);
		
		return attrMap;
	}
	
	
	public Map<AttrType, Float> getAttr(AbstractInstance tank){
		AttrBuffer attr = new AttrBuffer();
		attr.append(this.buildAttrMap(tank, attrType1, value1, reAttrType1));
		attr.append(this.buildAttrMap(tank, attrType2, value2, reAttrType2));
		attr.append(this.buildAttrMap(tank, attrType3, value3, reAttrType3));
		
		return attr.getAttrMap();
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
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public int getEffectId() {
		return effectId;
	}
	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getChange() {
		return change;
	}
	public void setChange(int change) {
		this.change = change;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public int getAttrType1() {
		return attrType1;
	}
	public void setAttrType1(int attrType1) {
		this.attrType1 = attrType1;
	}
	public float getValue1() {
		return value1;
	}
	public void setValue1(float value1) {
		this.value1 = value1;
	}
	public int getReAttrType1() {
		return reAttrType1;
	}
	public void setReAttrType1(int reAttrType1) {
		this.reAttrType1 = reAttrType1;
	}
	public int getAttrType2() {
		return attrType2;
	}
	public void setAttrType2(int attrType2) {
		this.attrType2 = attrType2;
	}
	public float getValue2() {
		return value2;
	}
	public void setValue2(float value2) {
		this.value2 = value2;
	}
	public int getReAttrType2() {
		return reAttrType2;
	}
	public void setReAttrType2(int reAttrType2) {
		this.reAttrType2 = reAttrType2;
	}
	public int getAttrType3() {
		return attrType3;
	}
	public void setAttrType3(int attrType3) {
		this.attrType3 = attrType3;
	}
	public float getValue3() {
		return value3;
	}
	public void setValue3(float value3) {
		this.value3 = value3;
	}
	public int getReAttrType3() {
		return reAttrType3;
	}
	public void setReAttrType3(int reAttrType3) {
		this.reAttrType3 = reAttrType3;
	}
}
