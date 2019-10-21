package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class CorpsTechTemplate implements KeySupport<Integer>{

	private int techId;
	private int level;
	private int main;
	private int attrType;
	private float value;
	private int playerCount;
	private int levelShop;
	private int nextTechId;
	private int corpLevel;
	private int techMaxExp;
	private int devMaxTime;

	
	public int getTechId() {
		return techId;
	}


	public void setTechId(int techId) {
		this.techId = techId;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getMain() {
		return main;
	}


	public void setMain(int main) {
		this.main = main;
	}


	public int getAttrType() {
		return attrType;
	}


	public void setAttrType(int attrType) {
		this.attrType = attrType;
	}


	public float getValue() {
		return value;
	}


	public void setValue(float value) {
		this.value = value;
	}


	public int getPlayerCount() {
		return playerCount;
	}


	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}


	public int getLevelShop() {
		return levelShop;
	}


	public void setLevelShop(int levelShop) {
		this.levelShop = levelShop;
	}


	public int getNextTechId() {
		return nextTechId;
	}


	public void setNextTechId(int nextTechId) {
		this.nextTechId = nextTechId;
	}


	public int getCorpLevel() {
		return corpLevel;
	}


	public void setCorpLevel(int corpLevel) {
		this.corpLevel = corpLevel;
	}


	public int getTechMaxExp() {
		return techMaxExp;
	}


	public void setTechMaxExp(int techMaxExp) {
		this.techMaxExp = techMaxExp;
	}


	public int getDevMaxTime() {
		return devMaxTime;
	}


	public void setDevMaxTime(int devMaxTime) {
		this.devMaxTime = devMaxTime;
	}


	@Override
	public Integer getKey() {
		return techId;
	}

	
}
