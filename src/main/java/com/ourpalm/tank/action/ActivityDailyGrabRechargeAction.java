package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._202_DailyGrabLogic;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_DAILY_GRAB_RECHARGE_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_DAILY_GRAB_RECHARGE_VALUE
)
public class ActivityDailyGrabRechargeAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		_202_DailyGrabLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.DailyGrab);
		if(logic == null) {
			return null;
		}
		
		Result result = logic.order(connect.getRoleId());
		STC_DAILY_GRAB_RECHARGE_MSG.Builder builder = STC_DAILY_GRAB_RECHARGE_MSG.newBuilder();
		if(result.isSuccess()) {
			String param = GameContext.getPayApp().getPayParam(connect.getRoleId());
			builder.setParam(param);
		}
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		return builder.build();
		
	}
	
	

}
