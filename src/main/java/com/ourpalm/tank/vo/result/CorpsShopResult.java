package com.ourpalm.tank.vo.result;

import java.util.Collection;

import com.ourpalm.tank.domain.ShopItem;

public class CorpsShopResult extends Result{

	//商品列表
	private Collection<ShopItem> shopList	;
	
	private int flushTime;	//剩余刷新时间(单位:秒)
	private int flushGold;	//刷新所需金币数
	
	
	public static CorpsShopResult newFailure(String info){
		CorpsShopResult result = new CorpsShopResult();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}
	

	public Collection<ShopItem> getShopList() {
		return shopList;
	}
	public void setShopList(Collection<ShopItem> shopList) {
		this.shopList = shopList;
	}
	public int getFlushTime() {
		return flushTime;
	}
	public void setFlushTime(int flushTime) {
		this.flushTime = flushTime;
	}
	public int getFlushGold() {
		return flushGold;
	}
	public void setFlushGold(int flushGold) {
		this.flushGold = flushGold;
	}
	
}
