package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ARMY_TITLE_MSG;


@Command(
	type = ARMY_TITLE_MSG.CMD_TYPE.CMD_TYPE_TITLE_VALUE, 
	id = ARMY_TITLE_MSG.CMD_ID.CTS_DRAW_DAY_REWARD_VALUE
)
public class ArmyTitleDrawRewardAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		return GameContext.getRoleArmyTitleApp().drawDayRaward(connect.getRoleId());
	}

}
