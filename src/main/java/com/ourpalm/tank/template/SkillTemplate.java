package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class SkillTemplate implements KeySupport<Integer>{
	
	private int id;
	private int type;
	private int coolTime;
	private float rat;
	private int logic;
	private float param;		
	private int addBuff;
	private int addBuff2;
	private int delBuff1;
	private int delBuff2;
	
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
	public int getCoolTime() {
		return coolTime;
	}
	public void setCoolTime(int coolTime) {
		this.coolTime = coolTime;
	}
	public float getRat() {
		return rat;
	}
	public void setRat(float rat) {
		this.rat = rat;
	}
	public float getParam() {
		return param;
	}
	public void setParam(float param) {
		this.param = param;
	}
	public int getAddBuff() {
		return addBuff;
	}
	public void setAddBuff(int addBuff) {
		this.addBuff = addBuff;
	}
	public int getDelBuff1() {
		return delBuff1;
	}
	public void setDelBuff1(int delBuff1) {
		this.delBuff1 = delBuff1;
	}
	public int getDelBuff2() {
		return delBuff2;
	}
	public void setDelBuff2(int delBuff2) {
		this.delBuff2 = delBuff2;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLogic() {
		return logic;
	}
	public void setLogic(int logic) {
		this.logic = logic;
	}
	public int getAddBuff2() {
		return addBuff2;
	}
	public void setAddBuff2(int addBuff2) {
		this.addBuff2 = addBuff2;
	}
}
