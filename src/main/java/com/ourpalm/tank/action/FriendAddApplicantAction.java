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
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_APPLY_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_APPLY_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_PAGE_PUSH_MSG;
import com.ourpalm.tank.tip.Tips;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_APPLY_VALUE
)
public class FriendAddApplicantAction implements Action<CTS_FRIEND_APPLY_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_APPLY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId(); 	//申请者		
		int friendId = reqMsg.getRoleId();	//要添加的好友
		
		STC_FRIEND_APPLY_MSG.Builder builder = STC_FRIEND_APPLY_MSG.newBuilder();
		
		//TODO 做friend 是否开启--禁止任何人加我为好友。
		boolean forbid = false;
		builder.setRoleId(friendId);
		builder.setSuccess(false);
		if(forbid) {
			builder.setInfo(Tips.FRIEND_FORBID_ADD);
		} else if(GameContext.getFriendApp().isFriend(roleId, friendId)){
			builder.setInfo(Tips.FRIEND_IS_FRIEND);
		} else if(GameContext.getFriendApp().hadApplicant(roleId, friendId)){ 
			builder.setSuccess(true);
		} else {
			GameContext.getFriendApp().addFirendApplicant(roleId, friendId);
			builder.setSuccess(true);
			
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(friendId);
			if(_connect != null) {
				STC_FRIEND_PAGE_PUSH_MSG.Builder push = STC_FRIEND_PAGE_PUSH_MSG.newBuilder();
				push.setType(3);
				
				FriendInfo friendInfo = GameContext.getFriendApp().buildFriendInfo(friendId, GameContext.getUserApp().getRoleAccount(roleId));
				push.setFriendInfo(friendInfo);
				
				_connect.sendMsg(push.build());
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("role: {}, 向 {}, 发出好友申请 , opr={}", roleId, friendId, builder.getSuccess());
		}
		
		return builder.build();
	}

}
