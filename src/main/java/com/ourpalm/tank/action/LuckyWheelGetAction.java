package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_LuckyWheel_GET_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_LuckyWheel_GET_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE, id = ACTIVITY_MSG.CMD_ID.CTS_LuckyWheel_GET_VALUE)
public class LuckyWheelGetAction implements Action<CTS_LuckyWheel_GET_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_LuckyWheel_GET_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;

		String type = reqMsg.getType();
		STC_LuckyWheel_GET_MSG.Builder builder = STC_LuckyWheel_GET_MSG.newBuilder();
		Result r = GameContext.getLuckyWheelApp().getGift(roleId, type, builder);
		builder.setSuccess(r.getResult() == (byte) 1 ? true : false);
		builder.setInfo(r.getInfo());

		return builder.build();
	}

}
