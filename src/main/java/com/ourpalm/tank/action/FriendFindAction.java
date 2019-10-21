package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.FRIEND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_FIND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.FriendInfo;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_FIND_MSG;
import com.ourpalm.tank.tip.Tips;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_FIND_VALUE
)
public class FriendFindAction implements Action<CTS_FRIEND_FIND_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_FIND_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		String keyWord = reqMsg.getKeyword();
		int roleId = connect.getRoleId();	
		
		if(Util.isEmpty(keyWord)) {	//前端验证
			return STC_FRIEND_FIND_MSG.newBuilder().setSuccess(false).setInfo("请输入查找内容").build();
		}
		
		STC_FRIEND_FIND_MSG.Builder builder = STC_FRIEND_FIND_MSG.newBuilder();
		builder.setSuccess(false);
		
		if(StringUtil.isInteger(keyWord)) {
			RoleAccount role = GameContext.getFriendApp().findFriendById(Integer.parseInt(keyWord));
			if(role != null && role.getRoleId() != roleId) {
				FriendInfo info = GameContext.getFriendApp().buildFriendInfo(roleId, role);
				builder.addFriend(info);
				builder.setSuccess(true);
			}
		}
	
		RoleAccount role = GameContext.getFriendApp().findFriendByName(keyWord);
		if(role != null && role.getRoleId() != roleId) {
			FriendInfo info = GameContext.getFriendApp().buildFriendInfo(roleId, role);
			builder.addFriend(info);
			builder.setSuccess(true);
		}
		
		if(!builder.getSuccess()) {
			builder.setInfo(Tips.FRIEND_NOT_FIND_ROLE_BYNAME);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 查找好友 {}, opr={}", roleId, keyWord, builder.getSuccess());
		}
		return builder.build();
	}

}
