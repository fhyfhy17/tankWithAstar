package com.ourpalm.tank.template;


public class BuildTemplate extends AbstractAttribute{

	private String name;
	private float atkDir;		//攻击角度
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getAtkDir() {
		return atkDir;
	}
	public void setAtkDir(float atkDir) {
		this.atkDir = atkDir;
	}
}
