package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.util.peshe.GoodsPeshe;

public class VipShopItemTemplate implements KeySupport<Integer>{
	private int id;
	private int group;
	private int goodsId;
	private int count;
	private int price1;
	private int price2;
	private int moneyType1;
	private int moneyType2;
	private int weight;

	@Override
	public Integer getKey(){
		return this.id;
	}
	
	public GoodsPeshe buildPeshe() {
		GoodsPeshe peshe = new GoodsPeshe();
		peshe.setGon(weight);
		peshe.setGoodsId(id);
		peshe.setNum(count);
		return peshe;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getPrice1() {
		return price1;
	}

	public void setPrice1(int price1) {
		this.price1 = price1;
	}

	public int getPrice2() {
		return price2;
	}

	public void setPrice2(int price2) {
		this.price2 = price2;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getMoneyType1() {
		return moneyType1;
	}

	public void setMoneyType1(int moneyType1) {
		this.moneyType1 = moneyType1;
	}

	public int getMoneyType2() {
		return moneyType2;
	}

	public void setMoneyType2(int moneyType2) {
		this.moneyType2 = moneyType2;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
