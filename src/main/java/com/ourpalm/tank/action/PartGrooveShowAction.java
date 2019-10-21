package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_OPEN_GROOVE_SHOW_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_OPEN_GROOVE_SHOW_MSG;

/**
 * 开槽信息
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_OPEN_GROOVE_SHOW_VALUE)
public class PartGrooveShowAction implements Action<CTS_OPEN_GROOVE_SHOW_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_OPEN_GROOVE_SHOW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		int tankId = reqMsg.getTankId();
		STC_OPEN_GROOVE_SHOW_MSG.Builder builder = STC_OPEN_GROOVE_SHOW_MSG.newBuilder();
		GameContext.getTankApp().openGrooveShow(builder, roleId, tankId);
		return builder.build();
	}

}
