package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, 
	id = ROLE_MSG.CMD_ID.CTS_HEARTBEAT_VALUE
)
public class RoleHeartbeatAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		connect.setHearbeatTime(System.currentTimeMillis());
		
//		connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_HEARTBEAT_VALUE, null);
		
		return null;
	}

}
