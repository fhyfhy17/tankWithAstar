package com.ourpalm.tank.domain;

import java.util.HashSet;
import java.util.Set;

public class RolePay {

	private int roleId;
	private int rmb;					//累计充值金额
	
	//记录每个充值商品购买记录，集合中没有则代表首次购买
	private Set<Integer> proIds = new HashSet<>();	
	
	
	public void putProId(int pId){
		proIds.add(pId);
	}
	
	/** 是否首次购买 */
	public boolean hadFirst(int proId){
		return !proIds.contains(proId);
	}
	
	
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getRmb() {
		return rmb;
	}
	public void setRmb(int rmb) {
		this.rmb = rmb;
	}
	public Set<Integer> getProIds() {
		return proIds;
	}
	public void setProIds(Set<Integer> proIds) {
		this.proIds = proIds;
	}
}
