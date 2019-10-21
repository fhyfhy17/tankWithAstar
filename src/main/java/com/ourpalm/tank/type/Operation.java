package com.ourpalm.tank.type;

public enum Operation {
	add(1),
	decrease(0),
	equal(2),
	;
	
	private byte type;
	Operation(int type){
		this.type = (byte)type;
	}
	
	public byte getType(){
		return this.type;
	}
}
