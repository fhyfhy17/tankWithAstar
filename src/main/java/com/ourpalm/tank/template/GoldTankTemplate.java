package com.ourpalm.tank.template;

import com.ourpalm.tank.app.GameContext;

public class GoldTankTemplate {

	private int tankId;
	private int gold;
	private int diamond;
	
	public void init() {
		TankTemplate t = GameContext.getTankApp().getTankTemplate(tankId);
		if(t == null)
			throw new IllegalArgumentException("加载金币坦克表失败, 不存在：tankId: " + tankId);
	}
	
	
	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	
}
