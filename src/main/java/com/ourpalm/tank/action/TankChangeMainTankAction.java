package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_CHANGE_MAINTANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_CHANGE_MAINTANK_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_CHANGE_MAINTANK_VALUE
)
public class TankChangeMainTankAction implements Action<CTS_CHANGE_MAINTANK_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CHANGE_MAINTANK_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int roleId = connect.getRoleId();
		int tankId = reqMsg.getTankId();
		Result result = GameContext.getTankApp().changeMainTank(roleId, tankId);
		
		STC_CHANGE_MAINTANK_MSG.Builder builder = STC_CHANGE_MAINTANK_MSG.newBuilder();
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		return builder.build();
	}

}
