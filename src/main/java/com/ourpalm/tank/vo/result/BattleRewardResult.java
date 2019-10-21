package com.ourpalm.tank.vo.result;

public class BattleRewardResult {

	private int iron;		//本次获得银币数
	private int dayIron;	//今日累计获得银币数
	private int dayMaxIron; //今日获得银币上限
	
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getDayIron() {
		return dayIron;
	}
	public void setDayIron(int dayIron) {
		this.dayIron = dayIron;
	}
	public int getDayMaxIron() {
		return dayMaxIron;
	}
	public void setDayMaxIron(int dayMaxIron) {
		this.dayMaxIron = dayMaxIron;
	}
}
