package com.ourpalm.tank.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleShop {
	private int lastRefreshTime;			//普通商品上次刷新时间（秒）
	private int refreshCount;				//今日刷新次数
	private int refreshTime;				//今日首次刷新时间（秒）
	
	private Map<Integer, Integer> commonMap;	//普通商品1 是买了，0是没买
	
	private List<Integer> goodsIds;
	
	public RoleShop() {
		commonMap = new HashMap<>();
		goodsIds = new ArrayList<>();
	}

	public int getLastRefreshTime() {
		return lastRefreshTime;
	}

	public void setLastRefreshTime(int lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}
	
	public Map<Integer, Integer> getCommonMap() {
		return commonMap;
	}
	
	public void setCommonMap(Map<Integer, Integer> commonMap) {
		this.commonMap = commonMap;
	}

	public List<Integer> getGoodsIds() {
		return goodsIds;
	}

	public void setGoodsIds(List<Integer> goodsIds) {
		this.goodsIds = goodsIds;
	}

	public int getRefreshCount() {
		return refreshCount;
	}

	public void setRefreshCount(int refreshCount) {
		this.refreshCount = refreshCount;
	}
	
	public void increaseRefreshCount() {
		this.refreshCount++;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}
	
	public boolean hadBuy(int goodsId) {
		if(commonMap.containsKey(goodsId))
			return commonMap.get(goodsId) == 1 ? true : false;
		
		return false;
	}
	
	public void buy(int goodsId) {
		this.commonMap.put(goodsId, 1);
	}
	
	public void clearBuy() {
		this.commonMap.clear();
	}
}
