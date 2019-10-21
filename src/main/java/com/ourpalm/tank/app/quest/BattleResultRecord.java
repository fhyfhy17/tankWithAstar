package com.ourpalm.tank.app.quest;

import java.util.HashSet;
import java.util.Set;

import com.ourpalm.tank.vo.AbstractInstance;

public class BattleResultRecord {
	/** 燃烧弹的技能ID */
	private final static Set<Integer> burnSkillSet = new HashSet<>();
	private final static Set<Integer> burnBuffSet = new HashSet<>();
	static {
		burnSkillSet.add(104);
		burnSkillSet.add(116);
		burnBuffSet.add(111);
		burnBuffSet.add(116);
	}
	
	private int warType;
	private boolean win;				//是否赢了
	private boolean flagWin;			//是否是占旗获得胜利
	private boolean mvp;				//是否获得MVP
	private int deadKillMaxCount;		//复仇同时炸死敌人
	private int fireBulletKillCount;	//使用燃烧弹击杀敌人
	private int killOcccpyFlagCount;	//击杀正在占旗的敌人
	private int damagePercent;			//输出伤害占全队总伤害的百分比
	private int helpDamagePercent;		//协助伤害占全队总协助伤害的百分比
	private int receiveDamagePercent;	//接受伤害占全队总伤害的百分比
	private AbstractInstance tank;		//所使用的坦克对象
	
	/** 死于燃烧弹统计数目 */
	public void burnFireBulletKillCount(int buffId){
		if(burnBuffSet.contains(buffId)){
			fireBulletKillCount += 1;
		}
	}
	
	
	/** 死于燃烧弹统计数目 */
	public void burnFireBulletKillCountBySkill(int skillId){
		if(burnSkillSet.contains(skillId)){
			fireBulletKillCount += 1;
		}
	}

	public int getWarType() {
		return warType;
	}
	public void setWarType(int warType) {
		this.warType = warType;
	}
	public boolean isWin() {
		return win;
	}
	public void setWin(boolean win) {
		this.win = win;
	}
	public boolean isFlagWin() {
		return flagWin;
	}
	public void setFlagWin(boolean flagWin) {
		this.flagWin = flagWin;
	}
	public boolean isMvp() {
		return mvp;
	}
	public void setMvp(boolean mvp) {
		this.mvp = mvp;
	}
	public int getDeadKillMaxCount() {
		return deadKillMaxCount;
	}
	public void setDeadKillMaxCount(int deadKillMaxCount) {
		this.deadKillMaxCount = deadKillMaxCount;
	}
	public int getFireBulletKillCount() {
		return fireBulletKillCount;
	}
	public void setFireBulletKillCount(int fireBulletKillCount) {
		this.fireBulletKillCount = fireBulletKillCount;
	}
	public int getKillOcccpyFlagCount() {
		return killOcccpyFlagCount;
	}
	public void setKillOcccpyFlagCount(int killOcccpyFlagCount) {
		this.killOcccpyFlagCount = killOcccpyFlagCount;
	}
	public AbstractInstance getTank() {
		return tank;
	}
	public void setTank(AbstractInstance tank) {
		this.tank = tank;
	}
	public int getDamagePercent() {
		return damagePercent;
	}
	public void setDamagePercent(int damagePercent) {
		this.damagePercent = damagePercent;
	}
	public int getHelpDamagePercent() {
		return helpDamagePercent;
	}
	public void setHelpDamagePercent(int helpDamagePercent) {
		this.helpDamagePercent = helpDamagePercent;
	}
	public int getReceiveDamagePercent() {
		return receiveDamagePercent;
	}
	public void setReceiveDamagePercent(int receiveDamagePercent) {
		this.receiveDamagePercent = receiveDamagePercent;
	}
}
