package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_BUYPARK_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_BUYPARK_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_BUYPARK_VALUE)
public class BuyParkAction implements Action<CTS_BUYPARK_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_BUYPARK_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		STC_BUYPARK_MSG.Builder builder = STC_BUYPARK_MSG.newBuilder();
		Result result = GameContext.getTankApp().buyPark(builder, connect.getRoleId());
		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		builder.setInfo(result.getInfo());
		builder.setResult(result.isSuccess());
		builder.setPark(account.getPark());
		return builder.build();

	}

}
