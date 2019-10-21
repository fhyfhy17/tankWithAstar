package com.ourpalm.tank.type;

public enum ActivityLogicEnum {

	Daily_Battle_Score(101),	//战力比拼
	Daily_Free_Double(102),		//免费双倍经验
	Daily_Army_Title(103),		//军衔冲级
	Daily_Online_Time(104),		//在线时长
	Daily_Month_Card(105),		//月卡
	Hall_Red_Packet(106),		//大厅，红包活动
	Hall_Daily_Grab(107),		//大厅，每日必抢
	Total_LOGIN(108);		//累计登录
	
	private int type;
	
	private ActivityLogicEnum(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
}
