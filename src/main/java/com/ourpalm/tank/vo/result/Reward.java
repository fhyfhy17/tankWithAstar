package com.ourpalm.tank.vo.result;

public class Reward {

	private int type;
	private int id;
	private int num;
	private boolean hadExist = false;		//成员是否已拥有
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public boolean isHadExist() {
		return hadExist;
	}
	public void setHadExist(boolean hadExist) {
		this.hadExist = hadExist;
	}
}
