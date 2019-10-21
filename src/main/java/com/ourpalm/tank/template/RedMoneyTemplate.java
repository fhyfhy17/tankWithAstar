package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 战后胜利 红包
 * 
 * @author Administrator
 *
 */
public class RedMoneyTemplate implements KeySupport<Integer> {

	private int id;
	private int rate;
	private int iron;
	private int gold;
	private int dia;

	public void init() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	@Override
	public Integer getKey() {
		return id;
	}

}
