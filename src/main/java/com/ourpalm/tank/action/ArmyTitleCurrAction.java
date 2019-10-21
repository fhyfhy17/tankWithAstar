package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ARMY_TITLE_MSG;
import com.ourpalm.tank.message.ARMY_TITLE_MSG.STC_CURR_TITLE_MSG;

@Command(
	type = ARMY_TITLE_MSG.CMD_TYPE.CMD_TYPE_TITLE_VALUE, 
	id = ARMY_TITLE_MSG.CMD_ID.CTS_CURR_TITLE_VALUE
)
public class ArmyTitleCurrAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		
		int currTitleId = role.getCurrTitleId();
		return STC_CURR_TITLE_MSG.newBuilder()
				.setTitleId(currTitleId)
				.setIsMax(currTitleId >= role.getMaxTitleId())
				.build();
	}

}
