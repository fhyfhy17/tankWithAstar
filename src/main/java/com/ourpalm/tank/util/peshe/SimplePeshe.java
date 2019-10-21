package com.ourpalm.tank.util.peshe;

public class SimplePeshe extends Peshe {

	private int id;
	private int num;
	
	public SimplePeshe() {}
	
	public SimplePeshe(int id, int num) {
		this.id = id;
		this.num = num;
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
}
