package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_BLUE_GET_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_BLUE_GET_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_BLUE_GET_VALUE)
public class BlueGetAction implements Action<CTS_BLUE_GET_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_BLUE_GET_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;

		int type = reqMsg.getType();
		int levelGiftId = reqMsg.getLevelGiftId();
		STC_BLUE_GET_MSG.Builder builder = STC_BLUE_GET_MSG.newBuilder();
		Result r = GameContext.getBlueApp().getGift(roleId, type, levelGiftId,builder);

		
		builder.setSuccess(r.getResult() == (byte) 1 ? true : false);
		builder.setInfo(r.getInfo());
		return builder.build();
	}

}
