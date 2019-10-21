package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_MATCH_CORPS_FRI_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_MATCH_CORPS_FRI_VALUE
)
public class BattleMatchCorpsRoleAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		
		return STC_MATCH_CORPS_FRI_MSG.newBuilder()
				.addAllCorpsRoles(GameContext.getMatchApp().getCorpsRoleItems(connect.getRoleId()))
				.build();
		
	}

}
