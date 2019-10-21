package com.ourpalm.tank.type;

public enum AIPointType {

	flag(1), 			//旗杆位置	
	flag_outside(2), 	//旗杆外围
	middle_inside(3),	//中部偏内
	middle(4),			//中部
	outside_middle(5),	//外围中部
	outside(6),			//外围
	up(7),				//最上面
	;
	
	public int type;
	
	AIPointType(int type){
		this.type = type;
	}
	
}
