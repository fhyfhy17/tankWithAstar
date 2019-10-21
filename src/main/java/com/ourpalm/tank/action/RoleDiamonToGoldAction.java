package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_DIAMOND_TO_GOLD_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_DIAMOND_TO_GOLD_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, 
	id = ROLE_MSG.CMD_ID.CTS_DIAMOND_TO_GOLD_VALUE
)
public class RoleDiamonToGoldAction implements Action<CTS_DIAMOND_TO_GOLD_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_DIAMOND_TO_GOLD_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int diamonds = reqMsg.getDiamonds();
		if(diamonds <= 0)
			return null;
		
		Result result = GameContext.getUserAttrApp().diamondTranslateToGold(connect.getRoleId(), diamonds);
		STC_DIAMOND_TO_GOLD_MSG.Builder builder = STC_DIAMOND_TO_GOLD_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}
}


