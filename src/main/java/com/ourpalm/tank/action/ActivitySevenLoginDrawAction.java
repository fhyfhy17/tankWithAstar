package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_SEVENLOGIN_DRAW_MSG;
import com.ourpalm.tank.vo.result.ValueResult;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_SEVENLOGIN_DRAW_VALUE
)
public class ActivitySevenLoginDrawAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		ValueResult<Integer> result = GameContext.getActivityApp().getSevenLoginApp().draw(connect.getRoleId());
		
		STC_ACTIVITY_SEVENLOGIN_DRAW_MSG.Builder builder = STC_ACTIVITY_SEVENLOGIN_DRAW_MSG.newBuilder();
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		builder.setDay(result.getValue());
		return builder.build();
	}
	
	

}
