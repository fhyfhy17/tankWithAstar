package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.QUEST_MSG;

@Command(
	type = QUEST_MSG.CMD_TYPE.CMD_TYPE_QUEST_VALUE, 
	id = QUEST_MSG.CMD_ID.CTS_QUEST_LIST_VALUE
)
public class QuestListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		return GameContext.getQuestApp().getQuestList(connect.getRoleId());
	}
}


