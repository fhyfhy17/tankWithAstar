package com.ourpalm.tank.template;

import com.ourpalm.tank.util.peshe.IronPheshe;

public class GoodsBoxIronGroup {

	private int group;
	private int iron;
	private int weight;
	
	public IronPheshe build() {
		return new IronPheshe(iron, weight);
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
