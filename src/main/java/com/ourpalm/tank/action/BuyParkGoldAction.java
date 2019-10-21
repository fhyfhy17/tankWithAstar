package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_BUYPARK_GOLD_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_BUYPARK_GOLD_MSG;
import com.ourpalm.tank.util.SysConfig;

@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_BUYPARK_GOLD_VALUE)
public class BuyParkGoldAction implements Action<CTS_BUYPARK_GOLD_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_BUYPARK_GOLD_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		STC_BUYPARK_GOLD_MSG.Builder builder = STC_BUYPARK_GOLD_MSG.newBuilder();
		builder.setGold(SysConfig.get(18));
		return builder.build();
	}

}
