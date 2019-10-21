package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * partNew partDraw 配件 开槽表
 * 
 * @author fhy
 *
 */
public class PartDrawTemplate implements KeySupport<Integer> {

	private int id;// id
	private int rate;// 概率
	private int itemId;// 物品ID
	private int type;// 类型 1 物品 2 银币 3 槽位
	private int num;// 数量

	public void init() {
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

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
