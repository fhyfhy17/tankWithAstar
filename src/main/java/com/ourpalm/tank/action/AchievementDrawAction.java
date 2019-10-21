package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.CTS_ACHIEVEMENT_DRAW_MSG;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.STC_ACHIEVEMENT_DRAW_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = ACHIEVEMENT_MSG.CMD_TYPE.CMD_TYPE_ACHIEVEMENT_VALUE, 
	id = ACHIEVEMENT_MSG.CMD_ID.CTS_ACHIEVEMENT_DRAW_VALUE
)
public class AchievementDrawAction implements Action<CTS_ACHIEVEMENT_DRAW_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_ACHIEVEMENT_DRAW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());              
		if (connect == null) {
			return null;
		}
		
		Result result = GameContext.getAchievementApp().drawReward(connect.getRoleId(), reqMsg.getId());
		return STC_ACHIEVEMENT_DRAW_MSG
				.newBuilder()
				.setSuccess(result.isSuccess())
				.setInfo(result.getInfo())
				.setId(reqMsg.getId())
				.build();
	}
		
}