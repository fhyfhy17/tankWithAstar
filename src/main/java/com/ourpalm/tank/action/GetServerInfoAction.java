package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_GET_GAMESERVER_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_GET_GAMESERVER_INFO_MSG;

/**
 * 前端拿服务器信息
 * 
 * @author Administrator
 *
 */
@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_GET_GAMESERVER_INFO_VALUE)
public class GetServerInfoAction implements Action<CTS_GET_GAMESERVER_INFO_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_GET_GAMESERVER_INFO_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;

		String nodeName = connect.getNodeName();
		int ioId = connect.getIoId();
		STC_GET_GAMESERVER_INFO_MSG.Builder builder = STC_GET_GAMESERVER_INFO_MSG.newBuilder();
		builder.setNodeName(nodeName);
		builder.setIoId(ioId);
		builder.setPrepareUrl(GameContext.getTxPayPrepareUrl());

		return builder.build();
	}

}
