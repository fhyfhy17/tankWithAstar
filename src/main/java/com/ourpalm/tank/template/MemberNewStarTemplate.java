package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * 新成员星级
 * 
 * @author fhy
 *
 */
public class MemberNewStarTemplate implements KeySupport<Integer> {
	private int id;
	private int nextId;
	private int qualilty;
	private int star;
	private int levelLimit;
	private int num;

	public void init() {
	}

	public int getQualilty() {
		return qualilty;
	}

	public void setQualilty(int qualilty) {
		this.qualilty = qualilty;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public Integer getKey() {
		return id;
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

}
