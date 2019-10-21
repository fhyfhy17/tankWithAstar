package com.ourpalm.tank.template;

import com.ourpalm.tank.util.peshe.GoodsPeshe;

public class GoodsBoxGoodsGroup {

	private int group;
	private int goodsId;
	private int count;
	private int weight;
	
	public GoodsPeshe build() {
		GoodsPeshe peshe = new GoodsPeshe();
		peshe.setGoodsId(goodsId);
		peshe.setNum(count);
		peshe.setGon(weight);
		return peshe;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
