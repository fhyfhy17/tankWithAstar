package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.CTS_MEMBER_OPEN_HOLE_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEMBER_OPEN_HOLE_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE, 
	id = MEMBER_MSG.CMD_ID.CTS_MEMBER_OPEN_HOLE_VALUE
)
public class MemberOpenHoleAction implements Action<CTS_MEMBER_OPEN_HOLE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_OPEN_HOLE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		Result result = GameContext.getMemberApp().memberOpenMedalHole(connect.getRoleId(), 
				Integer.parseInt(reqMsg.getInstanceId()), reqMsg.getHoleIndex());
		
		return STC_MEMBER_OPEN_HOLE_MSG.newBuilder()
				.setSuccess(result.isSuccess())
				.setInfo(result.getInfo())
				.setHoleIndex(reqMsg.getHoleIndex())
				.build();
	}

}
