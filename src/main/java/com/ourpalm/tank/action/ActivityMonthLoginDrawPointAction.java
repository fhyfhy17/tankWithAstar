package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_ACTIVITY_MONTHLOGIN_POINT_DRAW_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_MONTHLOGIN_POINT_DRAW_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_MONTHLOGIN_POINT_DRAW_VALUE
)
public class ActivityMonthLoginDrawPointAction implements Action<CTS_ACTIVITY_MONTHLOGIN_POINT_DRAW_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_ACTIVITY_MONTHLOGIN_POINT_DRAW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int day =reqMsg.getDay();
		Result r = GameContext.getActivityApp().getMonthLoginApp().drawPoint(connect.getRoleId(), day);
		
		
		STC_ACTIVITY_MONTHLOGIN_POINT_DRAW_MSG.Builder builder = STC_ACTIVITY_MONTHLOGIN_POINT_DRAW_MSG.newBuilder();
		builder.setInfo(r.getInfo());
		builder.setSuccess(r.isSuccess());
		return builder.build();
		
	}
	
	

}
