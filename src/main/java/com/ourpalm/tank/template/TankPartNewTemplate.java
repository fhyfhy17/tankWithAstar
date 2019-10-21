package com.ourpalm.tank.template;

public class TankPartNewTemplate extends AbstractAttribute {

	private int nextId;
	private int level;
	private int maxLevel;
	private int icon;
	private int battleNum;
	private String name;
	private int index_type;
	private String desc;
	public void init() {
		super.init();
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getId() {
		return id_i;
	}

	public void setId(int id) {
		this.id_i = id;
	}

	public int getNextId() {
		return nextId;
	}

	public void setNextId(int nextId) {
		this.nextId = nextId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getBattleNum() {
		return battleNum;
	}

	public void setBattleNum(int battleNum) {
		this.battleNum = battleNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex_type() {
		return index_type;
	}

	public void setIndex_type(int index_type) {
		this.index_type = index_type;
	}

}
