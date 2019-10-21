package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

/**
 * partNew groove 配件 槽表
 * 
 * @author fhy
 *
 */
public class GrooveTemplate implements KeySupport<Integer> {

	private int tankLevel;// 坦克等级
	private int grooveNum;// 初始槽数量
	private int gold;// 开槽抽奖金币
	private int buyGold;// 直接购买金币

	public void init() {
	}

	@Override
	public Integer getKey() {
		return tankLevel;
	}

	public int getTankLevel() {
		return tankLevel;
	}

	public void setTankLevel(int tankLevel) {
		this.tankLevel = tankLevel;
	}

	public int getGrooveNum() {
		return grooveNum;
	}

	public void setGrooveNum(int grooveNum) {
		this.grooveNum = grooveNum;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getBuyGold() {
		return buyGold;
	}

	public void setBuyGold(int buyGold) {
		this.buyGold = buyGold;
	}

}
