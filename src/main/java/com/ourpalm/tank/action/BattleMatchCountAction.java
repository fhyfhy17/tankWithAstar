package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_MATCH_COUNT_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_MATCH_COUNT_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_MATCH_COUNT_VALUE
)
public class BattleMatchCountAction implements Action<CTS_MATCH_COUNT_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_MATCH_COUNT_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		STC_MATCH_COUNT_MSG resp = STC_MATCH_COUNT_MSG.newBuilder()
				.setWarType(reqMsg.getWarType())
				.setCount(GameContext.getMatchApp().warMatchCount(reqMsg.getWarType().getNumber()))
				.build();
		
		return resp;
	}

}
