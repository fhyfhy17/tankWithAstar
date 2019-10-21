package com.ourpalm.tank.template;

import com.ourpalm.tank.util.peshe.GoodsPeshe;

public class GoodsBoxTankGroup {

	private int group;
	private int tankId;
	private int num;
	private int weight;
	
	public GoodsPeshe build() {
		GoodsPeshe peshe = new GoodsPeshe();
		peshe.setGoodsId(tankId);
		peshe.setNum(num);
		peshe.setGon(weight);
		return peshe;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
