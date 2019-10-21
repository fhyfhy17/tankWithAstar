package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 战斗收益表
 * 
 * @author fhy
 *
 */
public class IncomeTemplate implements KeySupport<Integer> {

	private int level;// 主坦克等级
	private int findIron;// 发现敌人银币
	private int findExp;// 发现敌人经验
	private float stabIron;// 击穿目标银币
	private float stabExp;// 击穿目标经验
	private float critIron;// 重创目标银币
	private float critExp;// 重创目标经验
	private int hitDieIron;// 击毁目标银币
	private int hitDieExp;// 击毁目标经验

	public void init() {
	}

	@Override
	public Integer getKey() {
		return level;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getFindIron() {
		return findIron;
	}

	public void setFindIron(int findIron) {
		this.findIron = findIron;
	}

	public int getFindExp() {
		return findExp;
	}

	public void setFindExp(int findExp) {
		this.findExp = findExp;
	}

	public float getStabIron() {
		return stabIron;
	}

	public void setStabIron(float stabIron) {
		this.stabIron = stabIron;
	}

	public float getStabExp() {
		return stabExp;
	}

	public void setStabExp(float stabExp) {
		this.stabExp = stabExp;
	}

	public float getCritIron() {
		return critIron;
	}

	public void setCritIron(float critIron) {
		this.critIron = critIron;
	}

	public float getCritExp() {
		return critExp;
	}

	public void setCritExp(float critExp) {
		this.critExp = critExp;
	}

	public int getHitDieIron() {
		return hitDieIron;
	}

	public void setHitDieIron(int hitDieIron) {
		this.hitDieIron = hitDieIron;
	}

	public int getHitDieExp() {
		return hitDieExp;
	}

	public void setHitDieExp(int hitDieExp) {
		this.hitDieExp = hitDieExp;
	}

}
