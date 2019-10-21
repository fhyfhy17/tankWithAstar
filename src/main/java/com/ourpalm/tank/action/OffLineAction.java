package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_OFFLINE_MSG;

/**
 * 下线协议
 * 
 * @author fhy
 *
 */
@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_OFFLINE_VALUE)
public class OffLineAction implements Action<CTS_OFFLINE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_OFFLINE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		GameContext.getOnlineCenter().offlineByRoleId(connect.getRoleId(), true);
		return null;
	}

}
