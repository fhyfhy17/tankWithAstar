package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class BattleRewardBaseTempalte implements KeySupport<Integer>{

	private int type;			// 1：银币  2：坦克经验  3：荣誉
	private float baseValue;	//基础值
	private float killValue;	//击杀系数
	private float helpValue;	//助攻系数
	private float aidValue;		//协作系数
	private float deathValue;	//死亡系数
	private float winValue;		//获胜系数
	private float loseValue;	//失败系数
	private float mvpValue;		//mvp系数
	private float rankValue;	//军衔排行系数
	private float battleValue;	//对战模式系数

	
	@Override
	public Integer getKey() {
		return type;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public float getBaseValue() {
		return baseValue;
	}


	public void setBaseValue(float baseValue) {
		this.baseValue = baseValue;
	}


	public float getKillValue() {
		return killValue;
	}


	public void setKillValue(float killValue) {
		this.killValue = killValue;
	}


	public float getHelpValue() {
		return helpValue;
	}


	public void setHelpValue(float helpValue) {
		this.helpValue = helpValue;
	}


	public float getAidValue() {
		return aidValue;
	}


	public void setAidValue(float aidValue) {
		this.aidValue = aidValue;
	}


	public float getDeathValue() {
		return deathValue;
	}


	public void setDeathValue(float deathValue) {
		this.deathValue = deathValue;
	}


	public float getWinValue() {
		return winValue;
	}


	public void setWinValue(float winValue) {
		this.winValue = winValue;
	}


	public float getLoseValue() {
		return loseValue;
	}


	public void setLoseValue(float loseValue) {
		this.loseValue = loseValue;
	}


	public float getMvpValue() {
		return mvpValue;
	}


	public void setMvpValue(float mvpValue) {
		this.mvpValue = mvpValue;
	}


	public float getRankValue() {
		return rankValue;
	}


	public void setRankValue(float rankValue) {
		this.rankValue = rankValue;
	}


	public float getBattleValue() {
		return battleValue;
	}


	public void setBattleValue(float battleValue) {
		this.battleValue = battleValue;
	}
	
}
