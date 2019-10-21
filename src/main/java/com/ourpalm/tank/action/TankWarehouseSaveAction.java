package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_SAVE_WAREHOUSE_MSG;

@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_SAVE_WAREHOUSE_VALUE
)
public class TankWarehouseSaveAction implements Action<CTS_SAVE_WAREHOUSE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_SAVE_WAREHOUSE_MSG reqMsg) {
		return null;
	}
}
