package com.ourpalm.tank.type;

public enum BuffRepeat {

	repeat(1), //可叠加
	no_repeat(0), //不可叠加
	;
	
	private int type;
	
	BuffRepeat(int type){
		this.type = type;
	}
	
	public int getType(){
		return this.type;
	}
	
	public static BuffRepeat valueOf(int type){
		switch(type){
			case 1 : return repeat;
			case 0 : return no_repeat;
			default : return null;
		}
	}
}
