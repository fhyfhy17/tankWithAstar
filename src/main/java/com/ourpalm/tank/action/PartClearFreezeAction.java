package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_CLEAR_FREEZE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_CLEAR_FREEZE_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 消除冷却
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_CLEAR_FREEZE_VALUE)
public class PartClearFreezeAction implements Action<CTS_CLEAR_FREEZE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_CLEAR_FREEZE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();

		STC_CLEAR_FREEZE_MSG.Builder builder = STC_CLEAR_FREEZE_MSG.newBuilder();
		Result r = GameContext.getTankApp().clearFreeze(builder, roleId);
		builder.setResult(r.getResult() == (byte) 1 ? 1 : 0);
		builder.setInfo(r.getInfo());

		return builder.build();
	}

}
