package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 组队收益模板
 * 
 * @author fhy
 *
 */
public class TeamIncomeAddTemplate implements KeySupport<Integer> {
	private int id;
	private int type;// 1好友 2军团
	private int corpLevel;// 军团等级
	private int income;// 加成百分比

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCorpLevel() {
		return corpLevel;
	}

	public void setCorpLevel(int corpLevel) {
		this.corpLevel = corpLevel;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

}
