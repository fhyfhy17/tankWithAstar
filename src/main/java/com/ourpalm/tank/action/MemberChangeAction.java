package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.CTS_MEMBER_CHANGE_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEMBER_CHANGE_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.UseMemberItem;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE,
	id = MEMBER_MSG.CMD_ID.CTS_MEMBER_CHANGE_VALUE
)
public class MemberChangeAction implements Action<CTS_MEMBER_CHANGE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_CHANGE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		UseMemberItem useMember = reqMsg.getNewUseMember();
		boolean dumpMedal = reqMsg.getDumpMedal(); //是否卸下所换下成员身上的勋章
		Result result = GameContext.getMemberApp().exchangeUseMember(connect.getRoleId(), dumpMedal, useMember.getType(), useMember.getInstanceId());
		
		STC_MEMBER_CHANGE_MSG.Builder builder = STC_MEMBER_CHANGE_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}

}
