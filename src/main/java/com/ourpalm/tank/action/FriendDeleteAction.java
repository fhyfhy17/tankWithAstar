package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.FRIEND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_DELETE_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_DELETE_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_PAGE_PUSH_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_DELETE_VALUE
)
public class FriendDeleteAction implements Action<CTS_FRIEND_DELETE_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_DELETE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int friendId = reqMsg.getRoleId();
		int roleId = connect.getRoleId();
		
		Result result = GameContext.getFriendApp().deleteFriend(roleId, friendId);
		if(result.isSuccess()) {			
			RoleConnect friendConnect = GameContext.getUserApp().getRoleConnect(friendId);	//向对方推送
			if(friendConnect != null) {
				RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
				FriendInfo roleInfo = GameContext.getFriendApp().buildFriendInfo(friendId, role);
				STC_FRIEND_PAGE_PUSH_MSG.Builder pushMsg = STC_FRIEND_PAGE_PUSH_MSG.newBuilder();
				pushMsg.setFriendInfo(roleInfo);
				pushMsg.setType(2);		//1 add ; 2 delete
				friendConnect.sendMsg(FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, FRIEND_MSG.CMD_ID.STC_FRIEND_PAGE_PUSH_VALUE, pushMsg.build().toByteArray());
			}
		}
	
		logger.debug("roleId = {}, 删除好友 {}， opr={}", roleId, friendId, result.isSuccess());

		STC_FRIEND_DELETE_MSG.Builder builder = STC_FRIEND_DELETE_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		builder.setRoleId(friendId);
		return builder.build();
	}

}
