package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.GTS_OFFLINE_MSG;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.GTS_OFFLINE_VALUE
)
public class GateWayOfflineAction implements Action<GTS_OFFLINE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, GTS_OFFLINE_MSG reqMsg) {
		int ioId = reqMsg.getIoId();
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(ioId);
		if(connect == null){
			return null;
		}
		LogCore.runtime.info("收到 roleId = {}, ioId = {} 下线请求，处理下线", connect.getRoleId(), ioId);
		
		GameContext.getOnlineCenter().offline(ioId, reqMsg.getOffline());
		return null;
	}


}
