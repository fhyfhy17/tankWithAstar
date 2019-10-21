package com.ourpalm.tank.action;

import java.util.Arrays;
import java.util.Set;

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
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_PAGE_MSG;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_PAGE_VALUE
)
public class FriendListAction implements Action<Object> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int roleId = connect.getRoleId();	
		Set<Integer> friends = GameContext.getFriendApp().getAllFriend(roleId);
		
		STC_FRIEND_PAGE_MSG.Builder builder = STC_FRIEND_PAGE_MSG.newBuilder();
		for(int fId : friends) {
			RoleAccount friend = GameContext.getUserApp().getRoleAccount(fId);
			FriendInfo info = GameContext.getFriendApp().buildFriendInfo(roleId, friend);
			builder.addFriends(info);
		}
		int max = GameContext.getFriendApp().getFriendMax();
		builder.setFriendMax(max);
		
		if(logger.isDebugEnabled()){
			logger.debug("{}, 获取好友列表={}", roleId, Arrays.toString(friends.toArray()));
		}
		return builder.build();
	}
	
	

}
