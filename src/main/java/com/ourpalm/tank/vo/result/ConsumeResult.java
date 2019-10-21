package com.ourpalm.tank.vo.result;

import java.util.HashMap;
import java.util.Map;

public class ConsumeResult {

	private int gold; //需要消耗的金币
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	
	
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public Map<Integer, Integer> getGoodsMap() {
		return goodsMap;
	}
	public void putGoods(int goodsId, int num){
		if(goodsId <= 0 || num <= 0){
			return ;
		}
		if(goodsMap.containsKey(goodsId)){
			int count = goodsMap.get(goodsId) + num;
			goodsMap.put(goodsId, count);
			return ;
		}
		goodsMap.put(goodsId, num);
	}
}
