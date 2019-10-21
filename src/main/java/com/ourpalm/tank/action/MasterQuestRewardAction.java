package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.QUEST_MSG;
import com.ourpalm.tank.message.QUEST_MSG.CTS_MASTER_QUEST_DRAW_MSG;
import com.ourpalm.tank.message.QUEST_MSG.STC_MASTER_QUEST_DRAW_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = QUEST_MSG.CMD_TYPE.CMD_TYPE_QUEST_VALUE, 
	id = QUEST_MSG.CMD_ID.CTS_MASTER_QUEST_DRAW_VALUE
)
public class MasterQuestRewardAction implements Action<CTS_MASTER_QUEST_DRAW_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MASTER_QUEST_DRAW_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		Result result = GameContext.getMasterQuestApp().drawReward(connect.getRoleId(), reqMsg.getId());
		
		STC_MASTER_QUEST_DRAW_MSG.Builder builder = STC_MASTER_QUEST_DRAW_MSG.newBuilder();
		builder.setSuccess(result.isSuccess()).setInfo(result.getInfo());
		return builder.build();
	}

}
