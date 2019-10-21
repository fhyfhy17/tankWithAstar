package com.ourpalm.tank.util.peshe;

public class IronPheshe extends Peshe {
	
	private int count;
	
	public IronPheshe(int count, int gon){
		this.count = count;
		this.gon = gon;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
