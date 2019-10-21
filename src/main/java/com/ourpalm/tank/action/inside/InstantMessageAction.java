package com.ourpalm.tank.action.inside;

import java.util.Collection;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.ROLE_MSG.ChatMessageType;
import com.ourpalm.tank.message.ROLE_MSG.STC_IMESSAGE_MSG;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_IMESSAGE_SEND_MSG;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.STS_IMESSAGE_SEND_VALUE
)
public class InstantMessageAction implements Action<STS_IMESSAGE_SEND_MSG>{

	@Override
	public MessageLite execute(ActionContext context, STS_IMESSAGE_SEND_MSG reqMsg) {
		
		String msg = reqMsg.getMsg();
		ChatMessageType type = ChatMessageType.valueOf(reqMsg.getType());
		
		switch(type) {
			case AllServer:
				sendAllServer(msg);
				break;
			case World:
				sendOwnServer(reqMsg.getAreaId(), msg);
				break;
			case System:
				sendSystem(msg);
				break;
		}
		
		return null;
	}
	
	private void sendAllServer(String msg) {
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.AllServer);
		
		Collection<RoleConnect> allConnect = GameContext.getOnlineCenter().getAllRoleConnect();
		for(RoleConnect connect : allConnect) {
			connect.sendMsg(builder.build());
		}
	}
	
	private void sendOwnServer(int areaId, String msg) {
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.World);
		
		Collection<RoleConnect> allConnect = GameContext.getOnlineCenter().getAllRoleConnect();
		for(RoleConnect connect : allConnect) {
			if(areaId == connect.getAreaId())
				connect.sendMsg(builder.build());
		}
	}
	
	private void sendSystem(String msg) {
		STC_IMESSAGE_MSG.Builder builder = STC_IMESSAGE_MSG.newBuilder();
		builder.setMsg(msg);
		builder.setType(ChatMessageType.System);
		
		Collection<RoleConnect> allConnect = GameContext.getOnlineCenter().getAllRoleConnect();
		for(RoleConnect connect : allConnect) {
			connect.sendMsg(builder.build());
		}
	}
	
	

}
