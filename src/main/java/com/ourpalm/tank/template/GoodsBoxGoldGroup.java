package com.ourpalm.tank.template;

import com.ourpalm.tank.util.peshe.MoneyPeshe;

public class GoodsBoxGoldGroup {

	private int group;
	private int gold;
	private int weight;
	
	public MoneyPeshe build() {
		return new MoneyPeshe(gold, weight);
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
