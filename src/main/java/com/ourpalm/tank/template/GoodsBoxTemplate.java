package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class GoodsBoxTemplate implements KeySupport<Integer>{

	private int id;
	
	private int gold;
	//银币
	private int iron;
	//荣誉
	private int honor;
	
	private int randomGoldGroup;
	
	private int randomType;
	private int ranCount;
	private int randomGoodsGroup;
	private int goodsGroup;
	private int tankGroup;

	private int randomIronGroup;
	
	@Override
	public Integer getKey() {
		return this.id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getRandomGoldGroup() {
		return randomGoldGroup;
	}

	public void setRandomGoldGroup(int randomGoldGroup) {
		this.randomGoldGroup = randomGoldGroup;
	}

	public int getRandomType() {
		return randomType;
	}

	public void setRandomType(int randomType) {
		this.randomType = randomType;
	}

	public int getRanCount() {
		return ranCount;
	}

	public void setRanCount(int ranCount) {
		this.ranCount = ranCount;
	}

	public int getRandomGoodsGroup() {
		return randomGoodsGroup;
	}

	public void setRandomGoodsGroup(int randomGoodsGroup) {
		this.randomGoodsGroup = randomGoodsGroup;
	}

	public int getGoodsGroup() {
		return goodsGroup;
	}

	public void setGoodsGroup(int goodsGroup) {
		this.goodsGroup = goodsGroup;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	public int getRandomIronGroup() {
		return randomIronGroup;
	}

	public void setRandomIronGroup(int randomIronGroup) {
		this.randomIronGroup = randomIronGroup;
	}

	public int getTankGroup() {
		return tankGroup;
	}

	public void setTankGroup(int tankGroup) {
		this.tankGroup = tankGroup;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}
}
