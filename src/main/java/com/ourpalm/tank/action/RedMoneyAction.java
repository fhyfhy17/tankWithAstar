package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_REDMONEY_GET_INFO_MSG;

/**
 * 红包查询
 * 
 * @author fhy
 *
 */
@Command(type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE, id = ACTIVITY_MSG.CMD_ID.CTS_REDMONEY_GET_INFO_VALUE)
public class RedMoneyAction implements Action<CTS_REDMONEY_GET_INFO_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_REDMONEY_GET_INFO_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = reqMsg.getRoleId();
		String uniqueId = reqMsg.getUnqueId();
		GameContext.getRedMoneyApp().get(roleId, uniqueId);
		return null;

	}

}
