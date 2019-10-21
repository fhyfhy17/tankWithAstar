package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.CTS_MEMBER_COMPOSITE_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.MemberItem;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEMBER_COMPOSITE_MSG;
import com.ourpalm.tank.vo.result.MemberCompositeResult;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE,
	id = MEMBER_MSG.CMD_ID.CTS_MEMBER_COMPOSITE_VALUE
)
public class MemberCompositeAction implements Action<CTS_MEMBER_COMPOSITE_MSG> {
	

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_COMPOSITE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		MemberCompositeResult result = GameContext.getMemberApp().composite(connect.getRoleId(), reqMsg.getGoodsId());
		
		STC_MEMBER_COMPOSITE_MSG.Builder builder = STC_MEMBER_COMPOSITE_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		
		MemberItem item = GameContext.getMemberApp().buildMemberItem(result.getMember());
		if(item != null){
			builder.setMember(item);
		}
		return builder.build();
	}

}
