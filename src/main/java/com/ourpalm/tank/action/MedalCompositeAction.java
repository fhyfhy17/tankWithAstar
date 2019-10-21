package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.CTS_MEDAL_COMPOSITE_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEDAL_COMPOSITE_MSG;
import com.ourpalm.tank.vo.result.MedalCompositeResult;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE,
	id = MEMBER_MSG.CMD_ID.CTS_MEDAL_COMPOSITE_VALUE
)
public class MedalCompositeAction implements Action<CTS_MEDAL_COMPOSITE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEDAL_COMPOSITE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		MedalCompositeResult result = GameContext.getMedalApp().composite(connect.getRoleId(), reqMsg.getMedalId(), reqMsg.getCount());
		
		STC_MEDAL_COMPOSITE_MSG.Builder builder = STC_MEDAL_COMPOSITE_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		builder.setMedalId(result.getNextMedalId());
		builder.setCount(result.getCount());
		
		return builder.build();
	}

}
