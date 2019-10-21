package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;

public class TankAIPointTypeMoveTemplate implements KeySupport<String>{

	private int pointType;
	private int nextPointType;
	
	@Override
	public String getKey() {
		return String.valueOf(pointType);
	}
	public int getPointType() {
		return pointType;
	}
	public void setPointType(int pointType) {
		this.pointType = pointType;
	}
	public int getNextPointType() {
		return nextPointType;
	}
	public void setNextPointType(int nextPointType) {
		this.nextPointType = nextPointType;
	}
}
