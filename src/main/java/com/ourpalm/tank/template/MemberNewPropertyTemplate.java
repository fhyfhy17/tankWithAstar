package com.ourpalm.tank.template;

/**
 * 新成员属性
 * 
 * @author fhy
 *
 */
public class MemberNewPropertyTemplate extends AbstractAttribute {
	private int id;
	private int nextId;
	private int quality;
	private int level;
	private int exp;
	private int battleNum;

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getBattleNum() {
		return battleNum;
	}

	public void setBattleNum(int battleNum) {
		this.battleNum = battleNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNextId() {
		return nextId;
	}

	public void setNextId(int nextId) {
		this.nextId = nextId;
	}

	@Override
	public Integer getKey() {
		return this.id;
	}
}
