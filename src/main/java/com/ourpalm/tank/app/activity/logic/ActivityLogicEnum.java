package com.ourpalm.tank.app.activity.logic;

public enum ActivityLogicEnum {

	FightingCompetition(101),
	FreeDoubleExp(102),
	ArmyTitleLevel(103),
	OnlineTime(104),
	MonthCard(105),
	LoginReward(106),
	bindTank1Account(107),
	RedPacket(201),
	DailyGrab(202),
	FirstPay(203),
	KeepOnline(204),
	;
	
	public final int logicId;
	private ActivityLogicEnum(int logicId) {
		this.logicId = logicId;
	}
	
	public static ActivityLogicEnum getLogic(int logicId) {
		for(ActivityLogicEnum logic : ActivityLogicEnum.values()) {
			if(logic.logicId == logicId)
				return logic;
		}
		return null;
	}
}
