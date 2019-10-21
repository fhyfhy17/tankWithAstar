package com.ourpalm.tank.type;

public enum BodyType {

	tank(1),		//坦克
	build(2),		//建筑
	fire(3),		//火炮
	airplane(4), 	//飞机
	selfTank(5),	//自身坦克复制
	;
	
	private int type;
	
	BodyType(int type){
		this.type = type;
	}
	
	public int getType(){
		return this.type;
	}
	
	public static BodyType valueOf(int type){
		switch(type){
		case 1 : return tank;
		case 2 : return build;
		case 3 : return fire;
		case 4 : return airplane;
		case 5 : return selfTank;
		default : return tank;
		}
	}
}
