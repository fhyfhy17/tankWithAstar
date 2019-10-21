package com.ourpalm.tank.action;

import java.util.List;

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
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_RECOMMEND_MSG;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_RECOMMEND_VALUE
)
public class FriendRecommendAction implements Action<Object> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		logger.debug("{}, 请求推荐好友", connect.getRoleId());
		
		List<RoleAccount> list = GameContext.getFriendApp().recommendFriend(connect.getRoleId(), connect.getAreaId());
		
		if(list == null)
			return null;
		
		STC_FRIEND_RECOMMEND_MSG.Builder builder = STC_FRIEND_RECOMMEND_MSG.newBuilder();
		for(RoleAccount role : list) {
			FriendInfo info = GameContext.getFriendApp().buildFriendInfo(connect.getRoleId(), role);
			builder.addFriends(info);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("{}, 请求推荐好友 数量 , {}", connect.getRoleId(), list.size());
		}
		
		return builder.build();
	}

}
