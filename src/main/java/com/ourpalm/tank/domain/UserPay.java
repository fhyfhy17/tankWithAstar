package com.ourpalm.tank.domain;

public class UserPay {

	private int roleId;			
	private boolean firstPay;	//是否已首充
	private int rmb;	//累积充值
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public boolean isFirstPay() {
		return firstPay;
	}
	public void setFirstPay(boolean firstPay) {
		this.firstPay = firstPay;
	}
	public int getRmb() {
		return rmb;
	}
	public void setRmb(int rmb) {
		this.rmb = rmb;
	}
	
}
