package com.ourpalm.tank.vo;

import com.ourpalm.tank.message.BATTLE_MSG.INCOME_TYPE;

public class IncomeInfo {
	private INCOME_TYPE type;// 收益类型
	private long incomeTime;// 收益时间
	private int iron;// 收益银币
	private int exp;// 收益经验
	private int honor;// 收益荣誉

	public INCOME_TYPE getType() {
		return type;
	}

	public void setType(INCOME_TYPE type) {
		this.type = type;
	}

	public long getIncomeTime() {
		return incomeTime;
	}

	public void setIncomeTime(long incomeTime) {
		this.incomeTime = incomeTime;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

}
