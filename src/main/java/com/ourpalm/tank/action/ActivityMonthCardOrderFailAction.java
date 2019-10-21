package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._105_MonthCardLogic;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_MONTH_CARD_BUY_FAIL_MSG;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_MONTH_CARD_BUY_FAIL_VALUE
)
public class ActivityMonthCardOrderFailAction implements Action<CTS_MONTH_CARD_BUY_FAIL_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MONTH_CARD_BUY_FAIL_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int cardId = reqMsg.getCardId();
		
		_105_MonthCardLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.MonthCard);
		if(logic == null)
			return null;
		
		logic.orderFail(connect.getRoleId(), cardId);
		return null;
	}

}
