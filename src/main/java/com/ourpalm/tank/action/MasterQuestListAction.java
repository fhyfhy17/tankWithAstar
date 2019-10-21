package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.MasterQuest;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.QUEST_MSG;
import com.ourpalm.tank.message.QUEST_MSG.QuestItem;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.message.QUEST_MSG.STC_MASTER_QUEST_LIST_MSG;

@Command(
	type = QUEST_MSG.CMD_TYPE.CMD_TYPE_QUEST_VALUE, 
	id = QUEST_MSG.CMD_ID.CTS_MASTER_QUEST_LIST_VALUE
)
public class MasterQuestListAction implements Action<Object> {
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		List<MasterQuest> quests = GameContext.getMasterQuestApp().getRoleQuest(connect.getRoleId());
		if(quests == null)
			quests = new ArrayList<>();	
		
		Collections.sort(quests);

		STC_MASTER_QUEST_LIST_MSG.Builder builder = STC_MASTER_QUEST_LIST_MSG.newBuilder();
		for (MasterQuest quest : quests) {
			QuestItem.Builder item = QuestItem.newBuilder();
			 item.setId(quest.getId());
			 item.setProgress(quest.getProgress());
			 item.setState(QuestState.valueOf(quest.getState()));
			 item.setRemainCount(0);
			 builder.addItems(item);
		}
		
		return builder.build();
	}
	
}



