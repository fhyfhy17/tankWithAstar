package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_LOGIN_MSG;

@Command(
		type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
		id = SERV_MSG.CMD_ID.STS_LOGIN_VALUE
)
public class SysRoleLoginAction implements Action<STS_LOGIN_MSG>{

	@Override
	public MessageLite execute(ActionContext context, STS_LOGIN_MSG reqMsg) {
		
		GameContext.getUserApp().loginInit(reqMsg.getRoleId(), reqMsg.getLogin());
		
		return null;
	}

}
