package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.platform.ChatInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_IMESSAGE_SEND_MSG;
import com.ourpalm.tank.message.ROLE_MSG.ChatMessageType;
import com.ourpalm.tank.message.ROLE_MSG.STC_IMESSAGE_SEND_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
	id = ROLE_MSG.CMD_ID.CTS_IMESSAGE_SEND_VALUE
)
public class InstantMessageSendAction implements Action<CTS_IMESSAGE_SEND_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_IMESSAGE_SEND_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null || account.hadTalkForbid()){
			STC_IMESSAGE_SEND_MSG.Builder builder = STC_IMESSAGE_SEND_MSG.newBuilder();
			builder.setInfo("您已被禁言,请联系GM");
			builder.setSuccess(false);
			builder.setKey(reqMsg.getKey());
			return builder.build();
		}
		
		Result result = GameContext.getImApp().checkMsg(reqMsg.getText());
		if(result.isSuccess()) {
			ChatMessageType type = reqMsg.getType();
			switch(type) {
				case AllServer:
					result = GameContext.getImApp().sendAllServer(roleId, reqMsg.getMsg());
					break;
				case World:
					result = GameContext.getImApp().sendOwnServer(roleId, connect.getAreaId(), reqMsg.getMsg());
					break;
				case Corps:
					result = GameContext.getImApp().sendCorps(roleId, reqMsg.getMsg());
					break;
				case Team:
					result = GameContext.getImApp().sendTeam(roleId, reqMsg.getMsg());
					break;
				case Whisper:
					result = GameContext.getImApp().sendWhisper(roleId, reqMsg.getReceiverId(), reqMsg.getMsg());
					break;
				default:
					result = Result.newFailure("错误消息类型");
					break;
			}
		}
		
		if(result.isSuccess()) {
			ChatInfo chatInfo = new ChatInfo();
			chatInfo.setAreaId(connect.getAreaId());
			chatInfo.setRoleId(roleId);
			chatInfo.setChatChannel(reqMsg.getType().getNumber());
			chatInfo.setChatTime(DateUtil.millsToString(System.currentTimeMillis()));
			chatInfo.setChatInfo(reqMsg.getText());
			chatInfo.setUserId(account.getUid());
			chatInfo.setRoleName(account.getRoleName());
			GameContext.getPlatformApp().chatControl(chatInfo);
		}
		
		STC_IMESSAGE_SEND_MSG.Builder builder = STC_IMESSAGE_SEND_MSG.newBuilder();
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		builder.setKey(reqMsg.getKey());
		return builder.build();
	}
	
	

}
