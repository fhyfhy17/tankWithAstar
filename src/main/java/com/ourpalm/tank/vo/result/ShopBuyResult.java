package com.ourpalm.tank.vo.result;

public class ShopBuyResult extends Result{

	private int moneyType; 	//所花费的货币类型
	private int money;		//花费钱数目 
	private String goodsName = "";	//所购买的道具名称
	private int goodsNum;		//所购买的物品数量
	
	
	public int getMoneyType() {
		return moneyType;
	}
	public void setMoneyType(int moneyType) {
		this.moneyType = moneyType;
	}
	public int getMoney() {
		return money;
	}
	public void setMoney(int money) {
		this.money = money;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}
}
