package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.app.GameContext;

public class UserInitTemplate {

	private int gold;
	private int iron;
	private int titleId;
	private int tankId1;
	private int tankId2;
	private int tankId3;
	private int tankId4;
	private int goldToIronRate;
	private int diamondToGoldRate;		//钻石换金币比率
	private float toTankExpGoldRate;	//坦克经验转换花费金币的比率	
	private float toTankExpIronRate;	//坦克经验转换花费银币的比率
	private int teachGold;		//完成新手引导金币奖励
	private int quest1;
	private int quest2;
	private int quest3;
	private int diamonds;
	private int park;
	private int initPart1;
	private int initPart2;
	private int initPart3;
	private int initPart4;
	private int initPart5;
	private int initPart6;
	private int initPart7;
	
	private List<Integer> initTankList = new ArrayList<>();
	private List<Integer> initQuestList = new ArrayList<>();
	
	public void init(){
		this.buildInitTank(tankId1);
		this.buildInitTank(tankId2);
		this.buildInitTank(tankId3);
		this.buildInitTank(tankId4);
		
		buildInitQuest(quest1);
		buildInitQuest(quest2);
		buildInitQuest(quest3);
	}
	
	private void buildInitQuest(int questId) {
		if(questId <= 0)
			return;
		
		MasterQuestTemplate t = GameContext.getMasterQuestApp().getMasterQuestTemplate(questId);
		if(t == null)
			throw new NullPointerException("主线任务ID不存在questId = " + questId);
		this.initQuestList.add(questId);
	}
	
	private void buildInitTank(int tankId){
		if(tankId <= 0){
			return ;
		}
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		if(template == null){
			throw new NullPointerException("坦克ID不存在 tankId = " + tankId);
		}
		this.initTankList.add(tankId);
	}

	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public int getTitleId() {
		return titleId;
	}
	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}
	public int getTankId1() {
		return tankId1;
	}
	public void setTankId1(int tankId1) {
		this.tankId1 = tankId1;
	}
	public int getTankId2() {
		return tankId2;
	}
	public void setTankId2(int tankId2) {
		this.tankId2 = tankId2;
	}
	public int getTankId3() {
		return tankId3;
	}
	public void setTankId3(int tankId3) {
		this.tankId3 = tankId3;
	}
	public int getTankId4() {
		return tankId4;
	}
	public void setTankId4(int tankId4) {
		this.tankId4 = tankId4;
	}
	public List<Integer> getInitTankList() {
		return initTankList;
	}
	public int getIron() {
		return iron;
	}
	public void setIron(int iron) {
		this.iron = iron;
	}
	public int getGoldToIronRate() {
		return goldToIronRate;
	}
	public void setGoldToIronRate(int goldToIronRate) {
		this.goldToIronRate = goldToIronRate;
	}
	public int getTeachGold() {
		return teachGold;
	}
	public void setTeachGold(int teachGold) {
		this.teachGold = teachGold;
	}

	public int getQuest1() {
		return quest1;
	}

	public void setQuest1(int quest1) {
		this.quest1 = quest1;
	}

	public int getQuest2() {
		return quest2;
	}

	public void setQuest2(int quest2) {
		this.quest2 = quest2;
	}

	public int getQuest3() {
		return quest3;
	}

	public void setQuest3(int quest3) {
		this.quest3 = quest3;
	}

	public List<Integer> getInitQuestList() {
		return initQuestList;
	}

	public float getToTankExpGoldRate() {
		return toTankExpGoldRate;
	}

	public void setToTankExpGoldRate(float toTankExpGoldRate) {
		this.toTankExpGoldRate = toTankExpGoldRate;
	}

	public float getToTankExpIronRate() {
		return toTankExpIronRate;
	}

	public void setToTankExpIronRate(float toTankExpIronRate) {
		this.toTankExpIronRate = toTankExpIronRate;
	}

	public int getDiamondToGoldRate() {
		return diamondToGoldRate;
	}

	public void setDiamondToGoldRate(int diamondToGoldRate) {
		this.diamondToGoldRate = diamondToGoldRate;
	}

	public int getDiamonds() {
		return diamonds;
	}

	public void setDiamonds(int diamonds) {
		this.diamonds = diamonds;
	}

	public int getPark() {
		return park;
	}

	public void setPark(int park) {
		this.park = park;
	}

	public int getInitPart1() {
		return initPart1;
	}

	public void setInitPart1(int initPart1) {
		this.initPart1 = initPart1;
	}

	public int getInitPart2() {
		return initPart2;
	}

	public void setInitPart2(int initPart2) {
		this.initPart2 = initPart2;
	}

	public int getInitPart3() {
		return initPart3;
	}

	public void setInitPart3(int initPart3) {
		this.initPart3 = initPart3;
	}

	public int getInitPart4() {
		return initPart4;
	}

	public void setInitPart4(int initPart4) {
		this.initPart4 = initPart4;
	}

	public int getInitPart5() {
		return initPart5;
	}

	public void setInitPart5(int initPart5) {
		this.initPart5 = initPart5;
	}

	public int getInitPart6() {
		return initPart6;
	}

	public void setInitPart6(int initPart6) {
		this.initPart6 = initPart6;
	}

	public int getInitPart7() {
		return initPart7;
	}

	public void setInitPart7(int initPart7) {
		this.initPart7 = initPart7;
	}
	
}
