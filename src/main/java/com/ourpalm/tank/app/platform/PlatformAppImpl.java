package com.ourpalm.tank.app.platform;

import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.STP_CHAT_CONTROL_MSG;

public class PlatformAppImpl implements PlatformApp{


	@Override
	public void chatControl(ChatInfo chatInfo) {
		RemoteNode remoteNode = GameContext.getPlatformManagerApp().randomRemoteNode();
		if(remoteNode != null){
			STP_CHAT_CONTROL_MSG chatMsg = STP_CHAT_CONTROL_MSG.newBuilder()
					.setAreaId(chatInfo.getAreaId()+"")
					.setUserId(chatInfo.getUserId())
					.setRoleId(chatInfo.getRoleId())
					.setRoleName(chatInfo.getRoleName())
					.setChatChannel(chatInfo.getChatChannel())
					.setChatInfo(chatInfo.getChatInfo())
					.setChatTime(chatInfo.getChatTime())
					.build();
			
			Message message = new Message();
			message.setCmdType((byte)SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
			message.setCmdId((byte)SERV_MSG.CMD_ID.STP_CHAT_CONTROL_VALUE);
			message.setFromNode(GameContext.getLocalNodeName());
			message.setData(chatMsg.toByteArray());
			remoteNode.sendReqMsg(message);
		}
	}

}
