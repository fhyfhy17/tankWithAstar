package com.ourpalm.tank.template;

import com.ourpalm.tank.message.MEMBER_MSG.LotteryType;

public class MemberLotteryConfig {

	private int diamondsFreeCount;		//钻石免费次数
	private int singleDiamonds;			//钻石单抽价格
	private int multiDiamonds;			//钻石五连抽价格
	private int diamondsFloorCount;		//钻石抽取保底次数
	
	private int ironFreeCount;	//银币免费次数
	private int sigalIron;		//银币单抽价格
	private int multiIron;		//银币五连抽价格
	private int ironFloorCount;	//银币抽取保底次数
	
	private int ironRewardId;	//银币抽获得经验卡
	private int diamondsRewardId;	//金币抽获得经验卡
	
	private int floorAptitudeMax;
	private int ironFloorAptitudeMax;
	
	
	public int getRewardId(LotteryType type){
		switch(type){
		case diamondsOnce : 
		case diamondsMulti : return this.diamondsRewardId;
		case ironOnce : 
		case ironMulti : return this.ironRewardId;
		default : return this.ironRewardId;
		}
	}


	public int getDiamondsFreeCount() {
		return diamondsFreeCount;
	}


	public void setDiamondsFreeCount(int diamondsFreeCount) {
		this.diamondsFreeCount = diamondsFreeCount;
	}


	public int getSingleDiamonds() {
		return singleDiamonds;
	}


	public void setSingleDiamonds(int singleDiamonds) {
		this.singleDiamonds = singleDiamonds;
	}


	public int getMultiDiamonds() {
		return multiDiamonds;
	}


	public void setMultiDiamonds(int multiDiamonds) {
		this.multiDiamonds = multiDiamonds;
	}


	public int getDiamondsFloorCount() {
		return diamondsFloorCount;
	}


	public void setDiamondsFloorCount(int diamondsFloorCount) {
		this.diamondsFloorCount = diamondsFloorCount;
	}


	public int getIronFreeCount() {
		return ironFreeCount;
	}


	public void setIronFreeCount(int ironFreeCount) {
		this.ironFreeCount = ironFreeCount;
	}


	public int getSigalIron() {
		return sigalIron;
	}


	public void setSigalIron(int sigalIron) {
		this.sigalIron = sigalIron;
	}


	public int getMultiIron() {
		return multiIron;
	}


	public void setMultiIron(int multiIron) {
		this.multiIron = multiIron;
	}


	public int getIronFloorCount() {
		return ironFloorCount;
	}


	public void setIronFloorCount(int ironFloorCount) {
		this.ironFloorCount = ironFloorCount;
	}


	public int getIronRewardId() {
		return ironRewardId;
	}


	public void setIronRewardId(int ironRewardId) {
		this.ironRewardId = ironRewardId;
	}


	public int getDiamondsRewardId() {
		return diamondsRewardId;
	}


	public void setDiamondsRewardId(int diamondsRewardId) {
		this.diamondsRewardId = diamondsRewardId;
	}


	public int getFloorAptitudeMax() {
		return floorAptitudeMax;
	}


	public void setFloorAptitudeMax(int floorAptitudeMax) {
		this.floorAptitudeMax = floorAptitudeMax;
	}


	public int getIronFloorAptitudeMax() {
		return ironFloorAptitudeMax;
	}


	public void setIronFloorAptitudeMax(int ironFloorAptitudeMax) {
		this.ironFloorAptitudeMax = ironFloorAptitudeMax;
	}
}
