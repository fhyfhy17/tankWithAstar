package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_VIP_DRAW_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_VIP_DRAW_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
		type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
		id = ROLE_MSG.CMD_ID.CTS_VIP_DRAW_VALUE
	)
public class VipDrawAction implements Action<CTS_VIP_DRAW_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_VIP_DRAW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int vipLevel = reqMsg.getLevel();
		
		Result result = GameContext.getVipApp().draw(connect.getRoleId(), vipLevel);
		
		return STC_VIP_DRAW_MSG.newBuilder().setSuccess(result.isSuccess()).setInfo(result.getInfo()).build();
	}

}
