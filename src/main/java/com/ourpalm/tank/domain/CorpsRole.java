package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.Map;

/**
 *	军团成员信息
 */
public class CorpsRole {

	private int roleId;				// 角色id
	private int job;				// 职位
	private int corpsId;			// 军团id
	private int contribution;		// 军团贡献值
	private boolean donateCool;		// 是否处于冷却状态
	private long techDonateTime;	// 军团科技捐赠时间(未来的时间)
	private int weekContribution;	// 周贡献值
	private long enterTime;			// 进入军团时间
	private long shopSysFlushTime; 	// 商城系统刷新时间(记录未来时间)
	private int shopFlushCount;		// 商城刷新次数
	private long shopFlushTime;		// 商城刷新时间
	private long saluteTime;		// 敬礼时间

	//商城物品(KEY=商品ID)
	private Map<Integer, ShopItem> shopMap = new HashMap<>();

	//活跃度，记录8天的活跃值
	//1970年1月1日到此时的天数作为KEY
	private Map<Integer, Integer> activeMap = new HashMap<>();

	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getJob() {
		return job;
	}
	public void setJob(int job) {
		this.job = job;
	}
	public int getCorpsId() {
		return corpsId;
	}
	public void setCorpsId(int corpsId) {
		this.corpsId = corpsId;
	}
	public int getContribution() {
		return contribution;
	}
	public void setContribution(int contribution) {
		this.contribution = contribution;
	}
	public Map<Integer, Integer> getActiveMap() {
		return activeMap;
	}
	public void setActiveMap(Map<Integer, Integer> activeMap) {
		this.activeMap = activeMap;
	}
	public long getTechDonateTime() {
		return techDonateTime;
	}
	public void setTechDonateTime(long techDonateTime) {
		this.techDonateTime = techDonateTime;
	}
	public int getWeekContribution() {
		return weekContribution;
	}
	public void setWeekContribution(int weekContribution) {
		this.weekContribution = weekContribution;
	}

	/** 捐赠倒计时间 单位:秒 */
	public int donateCountdownTime(){
		int countdownTime = (int)((this.techDonateTime - System.currentTimeMillis()) / 1000);
		if(countdownTime <= 0){
			return 0;
		}
		return countdownTime;
	}

	/** 返回系统刷新倒计时(单位:秒) */
	public int shopSysCountdownTime(){
		int countdownTime = (int)(this.shopSysFlushTime - System.currentTimeMillis());
		if(countdownTime <= 0){
			return 0;
		}
		return countdownTime / 1000;
	}

	public long getEnterTime() {
		return enterTime;
	}
	public void setEnterTime(long enterTime) {
		this.enterTime = enterTime;
	}
	public int getShopFlushCount() {
		return shopFlushCount;
	}
	public void setShopFlushCount(int shopFlushCount) {
		this.shopFlushCount = shopFlushCount;
	}
	public long getShopFlushTime() {
		return shopFlushTime;
	}
	public void setShopFlushTime(long shopFlushTime) {
		this.shopFlushTime = shopFlushTime;
	}
	public long getSaluteTime() {
		return saluteTime;
	}
	public void setSaluteTime(long saluteTime) {
		this.saluteTime = saluteTime;
	}
	public Map<Integer, ShopItem> getShopMap() {
		return shopMap;
	}
	public void setShopMap(Map<Integer, ShopItem> shopMap) {
		this.shopMap = shopMap;
	}
	public boolean isDonateCool() {
		return donateCool;
	}
	public void setDonateCool(boolean donateCool) {
		this.donateCool = donateCool;
	}
	public long getShopSysFlushTime() {
		return shopSysFlushTime;
	}
	public void setShopSysFlushTime(long shopSysFlushTime) {
		this.shopSysFlushTime = shopSysFlushTime;
	}
}
