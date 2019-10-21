package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_TANK_DEVELOP_LIST_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_DEVELOP_LIST_MSG;

/**
 * 请求坦克研发树
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_TANK_DEVELOP_LIST_VALUE)
public class DeveLopListAction implements Action<CTS_TANK_DEVELOP_LIST_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_TANK_DEVELOP_LIST_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		if (account == null)
			return null;

		STC_TANK_DEVELOP_LIST_MSG.Builder builder = STC_TANK_DEVELOP_LIST_MSG.newBuilder();
		GameContext.getTankApp().getTankTree(connect.getRoleId(), builder);
		return builder.build();
	}
}
