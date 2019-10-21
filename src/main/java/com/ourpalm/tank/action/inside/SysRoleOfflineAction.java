package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_OFFLINE_MSG;

@Command(
		type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
		id = SERV_MSG.CMD_ID.STS_OFFLINE_VALUE
)
public class SysRoleOfflineAction implements Action<STS_OFFLINE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, STS_OFFLINE_MSG reqMsg) {
		
		GameContext.getOnlineCenter().offlineByRoleId(reqMsg.getRoleId(), reqMsg.getOffline());
		
		return null;
	}

}
