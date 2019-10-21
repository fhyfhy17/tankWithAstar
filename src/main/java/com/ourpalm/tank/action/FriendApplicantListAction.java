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
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_APPLYPAGE_MSG;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_APPLYPAGE_VALUE
)
public class FriendApplicantListAction implements Action<Object> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		STC_FRIEND_APPLYPAGE_MSG.Builder builder = STC_FRIEND_APPLYPAGE_MSG.newBuilder();
		
		int roleId = connect.getRoleId();	
		Set<Integer> applicantList = GameContext.getFriendApp().getAllApplicant(roleId);
		for(int applicantId : applicantList) {
			RoleAccount applicant = GameContext.getUserApp().getRoleAccount(applicantId);
			if(applicant == null)
				continue;
			
			FriendInfo info = GameContext.getFriendApp().buildFriendInfo(roleId, applicant);
			builder.addFriends(info);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("{}, 获取好友申请列表 ={}", connect.getRoleId(), Arrays.toString(applicantList.toArray()));
		}
		
		return builder.build();
	}

}
