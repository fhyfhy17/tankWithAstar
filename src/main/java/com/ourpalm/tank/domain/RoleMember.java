package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.List;

public class RoleMember {

	private int roleId;
//	private String instanceId;
	private int templateId;
	private int level;
	private int exp;
	private int aptitude;
	private List<Integer> medals = new ArrayList<>();
	
	
	/** 已解锁的格子 */
	public int medalHoleCount(){
		return medals.size();
	}
	
	/** 放入勋章 */
	public void putMedals(List<Integer> medals) {
		//先判断之前所拥有的格子数
		int holeCount = medalHoleCount();
		int surplus = holeCount - medals.size();
		//占位置
		for(int i = 0; i < surplus; i++){
			medals.add(new Integer(0));
		}
		this.medals = medals;
	}
	
	/** 清空勋章 */
	public void clearMedals(){
		int holeCount = this.medalHoleCount();
		this.medals.clear();
		//占住格子
		for(int i = 0; i < holeCount; i++){
			this.medals.add(new Integer(0));
		}
	}
	
	
	/** 开格子 */
	public void openHole(){
		this.medals.add(0);
	}
	
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
//	public String getInstanceId() {
//		return instanceId;
//	}
//	public void setInstanceId(String instanceId) {
//		this.instanceId = instanceId;
//	}
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getAptitude() {
		return aptitude;
	}
	public void setAptitude(int aptitude) {
		this.aptitude = aptitude;
	}
	public List<Integer> getMedals() {
		return medals;
	}
	public void setMedals(List<Integer> medals) {
		this.medals = medals;
	}
}
