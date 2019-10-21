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
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_MONTH_CARD_BUY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_MONTH_CARD_BUY_MSG;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_MONTH_CARD_BUY_VALUE
)
public class ActivityMonthCardOrderAction implements Action<CTS_MONTH_CARD_BUY_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MONTH_CARD_BUY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int cardId = reqMsg.getCardId();
		
		_105_MonthCardLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.MonthCard);
		if(logic == null)
			return null;
		
		ActivityTemplate template = logic.getActivityTemplate();
		if(template == null) {
			return null;
		}
		
		
		Result result = logic.order(connect.getRoleId(), cardId);
		STC_MONTH_CARD_BUY_MSG.Builder builder = STC_MONTH_CARD_BUY_MSG.newBuilder();
		if(result.isSuccess()) {
			String param = GameContext.getPayApp().getPayParam(connect.getRoleId());
			builder.setParam(param);
			builder.setSuccess(true);
		} else {
			builder.setSuccess(false);
			builder.setInfo(result.getInfo());
		}
		
		return builder.build();
	}
	
	

}
