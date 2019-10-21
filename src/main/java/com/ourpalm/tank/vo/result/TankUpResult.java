package com.ourpalm.tank.vo.result;

public class TankUpResult extends Result{

	private int tankId;
	private int newPardId;		//新的配件ID
	private int location; 		//位置
	private int tankExp;		//坦克经验值
	
	
	public int getTankId() {
		return tankId;
	}
	public void setTankId(int tankId) {
		this.tankId = tankId;
	}
	public int getNewPardId() {
		return newPardId;
	}
	public void setNewPardId(int newPardId) {
		this.newPardId = newPardId;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getTankExp() {
		return tankExp;
	}
	public void setTankExp(int tankExp) {
		this.tankExp = tankExp;
	}
}
