package com.ourpalm.tank.vo.result;

import java.util.List;
import java.util.Map;

public class GoodsBoxOpenResult extends Result {
	private Map<Integer, Integer> goodsMap ;
	private int gold;
	private int iron;
	private int honor;
	private List<Integer> tankList ;
	
	
	public Map<Integer, Integer> getGoodsMap() {
		return goodsMap;
	}
	public void setGoodsMap(Map<Integer, Integer> goodsMap) {
		this.goodsMap = goodsMap;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getHonor() {
		return honor;
	}
	public void setHonor(int honor) {
		this.honor = honor;
	}
	public List<Integer> getTankList() {
		return tankList;
	}
	public void setTankList(List<Integer> tankList) {
		this.tankList = tankList;
	}
	
}
