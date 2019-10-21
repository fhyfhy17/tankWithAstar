package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_GOLD_TO_IRON_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_GOLD_TO_IRON_MSG;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, 
	id = ROLE_MSG.CMD_ID.CTS_GOLD_TO_IRON_VALUE
)
public class RoleGoldToIronAction implements Action<CTS_GOLD_TO_IRON_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_GOLD_TO_IRON_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int translateGold = reqMsg.getGold();
		if(translateGold <= 0)
			return null;
		
		UserInitTemplate template = GameContext.getUserApp().getUserInitTemplate();	
		int rate = template.getGoldToIronRate();
		Result result = GameContext.getUserAttrApp().goldTranslateToIron(connect.getRoleId(), translateGold, rate);
		
		STC_GOLD_TO_IRON_MSG.Builder builder = STC_GOLD_TO_IRON_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}
}


