package com.ourpalm.tank.app.activity.logic;

public class ActivityLogicFactory {

	
	public static ActivityLogic createLogic(int logicId) {
		switch(logicId) {
			//日常活动
			case 101: return new _101_FightingCompetitionLogic();
			case 102: return new _102_FreeDoubleExpLogic();
			case 103: return new _103_ArmyTitleLevelLogic();
			case 104: return new _104_OnlineTimeLogic();
			case 105: return new _105_MonthCardLogic();
			case 106: return new _106_LoginRewardLogic();
			case 107: return new _107_BindTank1AccountLogic();
			
			//大厅
			case 201: return new _201_RedPacketLogic();
			case 202: return new _202_DailyGrabLogic();
			case 203: return new _203_FirstPayLogic();
			case 204: return new _204_KeepOnlineLogic();
			default: return null;
		}
	}
}
