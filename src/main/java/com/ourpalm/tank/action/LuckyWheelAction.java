package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_LuckyWheel_Info_MSG;

@Command(type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE, id = ACTIVITY_MSG.CMD_ID.CTS_LuckyWheel_Info_VALUE)
public class LuckyWheelAction implements Action<CTS_LuckyWheel_Info_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_LuckyWheel_Info_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;

		GameContext.getLuckyWheelApp().push(roleId);

		return null;
	}

}
