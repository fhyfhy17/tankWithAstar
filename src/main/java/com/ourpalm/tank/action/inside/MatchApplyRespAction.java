package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.MTS_MATCH_MSG;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.MTS_MATCH_VALUE
)
public class MatchApplyRespAction implements Action<MTS_MATCH_MSG>{

	@Override
	public MessageLite execute(ActionContext context, MTS_MATCH_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		LogCore.runtime.debug("收到匹配服务器申请返回, roleId = {}, ioId={}.", connect.getRoleId(), connect.getIoId());
		
		return null;
	}

}
