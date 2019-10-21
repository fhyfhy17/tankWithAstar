package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._101_FightingCompetitionLogic;
import com.ourpalm.tank.app.activity.logic._103_ArmyTitleLevelLogic;
import com.ourpalm.tank.app.activity.logic._104_OnlineTimeLogic;
import com.ourpalm.tank.app.activity.logic._106_LoginRewardLogic;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_ACTIVITY_DAILY_TAB_DRAW_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_DAILY_TAB_DRAW_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_DAILY_TAB_DRAW_VALUE
)
public class ActivityDailyDrawAction implements Action<CTS_ACTIVITY_DAILY_TAB_DRAW_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_ACTIVITY_DAILY_TAB_DRAW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		int itemId = reqMsg.getItemId();
		
		ActivityLogicEnum logicEnum = ActivityLogicEnum.getLogic(reqMsg.getActivityId());
		Result result = Result.newFailure("活动不存在");
		switch(logicEnum) {
			case FightingCompetition:
				_101_FightingCompetitionLogic logic101 = GameContext.getActivityApp().getActivityLogic(logicEnum);
				if(logic101 != null) {
					result = logic101.doDraw(roleId, itemId);
				}
				break;
			case ArmyTitleLevel:
				_103_ArmyTitleLevelLogic logic103 = GameContext.getActivityApp().getActivityLogic(logicEnum);
				if(logic103 != null) {
					result = logic103.doDraw(roleId, itemId);
				}
				break;
			case OnlineTime:
				_104_OnlineTimeLogic logic104 = GameContext.getActivityApp().getActivityLogic(logicEnum);
				if(logic104 != null) {
					result = logic104.doDraw(roleId, itemId);
				}
				break;
			case LoginReward:
				_106_LoginRewardLogic logic106 = GameContext.getActivityApp().getActivityLogic(logicEnum);
				if(logic106 != null) {
					result = logic106.doDraw(roleId, itemId);
				}
				break;
			default:
				break;
		}
		
		STC_ACTIVITY_DAILY_TAB_DRAW_MSG.Builder builder = STC_ACTIVITY_DAILY_TAB_DRAW_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}
	
}
