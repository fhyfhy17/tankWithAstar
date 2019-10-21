package com.ourpalm.tank.template;

/**
 * 战斗收益表 增益表
 * 
 * @author fhy
 *
 */
public class ExtraIncomeTemplate {

	private int callUpTime;// 召唤冷却时间（小时）
	private int findLimit;// 发现敌人上限

	// 结算时计算
	private int goldIron;// 金币车银币收益
	private int goldExp;// 金币车经验收益
	private int treasureIron;// 珍藏车银币收益
	private int treasureExp;// 珍藏车经验收益
	private int rareIron;// 稀有车银币收益
	private int rareExp;// 稀有车经验收益
	private int weakIron;// 遭遇强敌银币收益
	private int weakExp;// 遭遇强敌经验收益
	private int callUpIron;// 战斗召唤银币收益
	private int callUpExp;// 战斗召唤经验收益
	
	private int Win5v5Iron;
	private int Win5v5Exp;
	private int Lose5v5Iron;
	private int Lose5v5Exp;
				


	public void init() {
	}

	public int getCallUpTime() {
		return callUpTime;
	}

	public void setCallUpTime(int callUpTime) {
		this.callUpTime = callUpTime;
	}

	public int getFindLimit() {
		return findLimit;
	}

	public void setFindLimit(int findLimit) {
		this.findLimit = findLimit;
	}

	public int getGoldIron() {
		return goldIron;
	}

	public void setGoldIron(int goldIron) {
		this.goldIron = goldIron;
	}

	public int getGoldExp() {
		return goldExp;
	}

	public void setGoldExp(int goldExp) {
		this.goldExp = goldExp;
	}

	public int getTreasureIron() {
		return treasureIron;
	}

	public void setTreasureIron(int treasureIron) {
		this.treasureIron = treasureIron;
	}

	public int getTreasureExp() {
		return treasureExp;
	}

	public void setTreasureExp(int treasureExp) {
		this.treasureExp = treasureExp;
	}

	public int getRareIron() {
		return rareIron;
	}

	public void setRareIron(int rareIron) {
		this.rareIron = rareIron;
	}

	public int getRareExp() {
		return rareExp;
	}

	public void setRareExp(int rareExp) {
		this.rareExp = rareExp;
	}

	public int getWeakIron() {
		return weakIron;
	}

	public void setWeakIron(int weakIron) {
		this.weakIron = weakIron;
	}

	public int getWeakExp() {
		return weakExp;
	}

	public void setWeakExp(int weakExp) {
		this.weakExp = weakExp;
	}

	public int getCallUpIron() {
		return callUpIron;
	}

	public void setCallUpIron(int callUpIron) {
		this.callUpIron = callUpIron;
	}

	public int getCallUpExp() {
		return callUpExp;
	}

	public void setCallUpExp(int callUpExp) {
		this.callUpExp = callUpExp;
	}

	public int getWin5v5Iron() {
		return Win5v5Iron;
	}

	public void setWin5v5Iron(int win5v5Iron) {
		Win5v5Iron = win5v5Iron;
	}

	public int getWin5v5Exp() {
		return Win5v5Exp;
	}

	public void setWin5v5Exp(int win5v5Exp) {
		Win5v5Exp = win5v5Exp;
	}

	public int getLose5v5Iron() {
		return Lose5v5Iron;
	}

	public void setLose5v5Iron(int lose5v5Iron) {
		Lose5v5Iron = lose5v5Iron;
	}

	public int getLose5v5Exp() {
		return Lose5v5Exp;
	}

	public void setLose5v5Exp(int lose5v5Exp) {
		Lose5v5Exp = lose5v5Exp;
	}

}
