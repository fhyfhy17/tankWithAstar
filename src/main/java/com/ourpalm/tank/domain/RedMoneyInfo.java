package com.ourpalm.tank.domain;

/**
 * 红包
 * 
 * @author fhy
 *
 */
public class RedMoneyInfo {
	private int roleId;
	private int state;// 0不可领取1可领取2已领取
	private int dia;// 钻石
	private int gold;// 金币
	private int iron;// 银币
	private int tankId;// tankId
	private String name;// 名字

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	public int getTankId() {
		return tankId;
	}

	public void setTankId(int tankId) {
		this.tankId = tankId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
