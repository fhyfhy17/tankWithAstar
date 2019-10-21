package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.util.peshe.GoodsPeshe;

public class ShopItemTemplate implements KeySupport<Integer>{
	private int id;
	private int group;
	private int goodsId;
	private int count;
	private int price;
	private int dis_price;
	private int discount;
	private int moneyType;
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
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getDis_price() {
		return dis_price;
	}
	public void setDis_price(int dis_price) {
		this.dis_price = dis_price;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(int moneyType) {
		this.moneyType = moneyType;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
