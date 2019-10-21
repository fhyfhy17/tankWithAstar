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
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_CONFIRM_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_CONFIRM_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_PAGE_PUSH_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_TIP_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_CONFIRM_VALUE
)
public class FriendConfirmAction implements Action<CTS_FRIEND_CONFIRM_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_CONFIRM_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int confirmedId = reqMsg.getRoleId();
		int roleId = connect.getRoleId();		
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 确认添加好友列表={}", roleId, confirmedId);
		}
		
		FriendInfo _info = null;
		Result result = GameContext.getFriendApp().confirmFriend(roleId, confirmedId);
		if(result.isSuccess()) {
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			RoleAccount friend = GameContext.getUserApp().getRoleAccount(confirmedId);
			_info = GameContext.getFriendApp().buildFriendInfo(roleId, friend);
			
			RoleConnect friendConnect = GameContext.getUserApp().getRoleConnect(confirmedId);	//向对方推送
			if(friendConnect != null) {
				STC_FRIEND_TIP_MSG.Builder tip = STC_FRIEND_TIP_MSG.newBuilder();
				tip.setInfo(String.format(Tips.FRIEND_CONFIRM_TIP, role.getRoleName()));
				friendConnect.sendMsg(FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, FRIEND_MSG.CMD_ID.STC_FRIEND_TIP_VALUE, tip.build().toByteArray());
				
				FriendInfo roleInfo = GameContext.getFriendApp().buildFriendInfo(confirmedId, role);
				STC_FRIEND_PAGE_PUSH_MSG.Builder pushMsg = STC_FRIEND_PAGE_PUSH_MSG.newBuilder();
				pushMsg.setFriendInfo(roleInfo);
				pushMsg.setType(1);		//1 add ; 2 delete
				friendConnect.sendMsg(FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, FRIEND_MSG.CMD_ID.STC_FRIEND_PAGE_PUSH_VALUE, pushMsg.build().toByteArray());
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 确认添加好友【成功】列表={}", roleId, confirmedId);
		}
		STC_FRIEND_CONFIRM_MSG.Builder builder = STC_FRIEND_CONFIRM_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		if(_info != null){
			builder.setFriendInfo(_info);
		}
		return builder.build();
	}
	
	

}
