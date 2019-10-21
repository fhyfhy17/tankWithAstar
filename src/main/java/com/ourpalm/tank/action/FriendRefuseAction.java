package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.FRIEND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_REFUSE_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_REFUSE_MSG;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_REFUSE_VALUE
)
public class FriendRefuseAction implements Action<CTS_FRIEND_REFUSE_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_REFUSE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int refusedId = reqMsg.getRoleId();
		int roleId = connect.getRoleId();	
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 拒绝添加好友列表={}", connect.getRoleId(), refusedId);
		}
		
		GameContext.getFriendApp().refuseFriend(roleId, refusedId);
		
		STC_FRIEND_REFUSE_MSG.Builder builder = STC_FRIEND_REFUSE_MSG.newBuilder();
		builder.setSuccess(true);
		builder.setRoleId(refusedId);
		return builder.build();
	}
	
	

}
