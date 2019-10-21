package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_SEVENLOGIN_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.SevenLoginDay;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_SEVENLOGIN_VALUE
)
public class ActivitySevenLoginAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		List<SevenLoginDay> list = GameContext.getActivityApp().getSevenLoginApp().getPageList(connect.getRoleId());
		
		STC_ACTIVITY_SEVENLOGIN_MSG.Builder builder = STC_ACTIVITY_SEVENLOGIN_MSG.newBuilder();
		builder.addAllDays(list);
		return builder.build();
	}
	
	

}
