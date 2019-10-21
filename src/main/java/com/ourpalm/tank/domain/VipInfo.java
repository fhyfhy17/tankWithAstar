package com.ourpalm.tank.domain;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.tank.message.ROLE_MSG.RewardState;

public class VipInfo {

	/**
	 * key : vipLevel
	 * value: RewardState Number value
	 */
	private Map<Integer, Integer> hadDraw = new HashMap<>();
	
	//上次领奖时间
	private long lastDrawTime;

	public Map<Integer, Integer> getHadDraw() {
		return hadDraw;
	}

	public void setHadDraw(Map<Integer, Integer> hadDraw) {
		this.hadDraw = hadDraw;
	}
	
	public int getState(int level) {
		if(this.hadDraw.containsKey(level))
			return this.hadDraw.get(level);
		return RewardState.ACCEPT_VALUE;
	}

	public long getLastDrawTime() {
		return lastDrawTime;
	}

	public void setLastDrawTime(long lastDrawTime) {
		this.lastDrawTime = lastDrawTime;
	}
	
}
