package com.ourpalm.tank.type;

public enum PayType {

	shop(1),			//充值商城
	day(2),				//每日秒杀
	month_card(3),		//月卡
	forever_card(4),	//年卡
	first(5),			//首充活动
	;
	
	public int type;
	PayType(int type){
		this.type = type;
	}
	
	public static PayType valueOf(int type){
		for(PayType payType : values()){
			if(payType.type == type){
				return payType;
			}
		}
		return null;
	}
}
