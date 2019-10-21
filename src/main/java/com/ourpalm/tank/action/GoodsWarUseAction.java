package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_USE_WAR_GOODS_MSG;

@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
	id = BATTLE_MSG.CMD_ID.CTS_USE_WAR_GOODS_VALUE
)
public class GoodsWarUseAction implements Action<CTS_USE_WAR_GOODS_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_USE_WAR_GOODS_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		GameContext.getGoodsApp().use(connect.getRoleId(), reqMsg.getId());
		
		return null;
	}

}
