package com.ourpalm.tank.action;

import java.util.Collections;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.Achievement;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.AchievementItem;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.AchievementState;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.STC_ACHIEVEMENT_LIST_MSG;

@Command(
	type = ACHIEVEMENT_MSG.CMD_TYPE.CMD_TYPE_ACHIEVEMENT_VALUE, 
	id = ACHIEVEMENT_MSG.CMD_ID.CTS_ACHIEVEMENT_LIST_VALUE
)
public class AchievementListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());              
		if (connect == null) {
			return null;
		}
		
		STC_ACHIEVEMENT_LIST_MSG.Builder builder = STC_ACHIEVEMENT_LIST_MSG.newBuilder();
		
		List<Achievement> achList = GameContext.getAchievementApp().getRoleAchievement(connect.getRoleId());
		Collections.sort(achList);
		for (Achievement achievement : achList) {
			AchievementItem.Builder item = AchievementItem.newBuilder();
			item.setId(achievement.getId());
			item.setFinishCount(achievement.getFinishCount());
			item.setState(AchievementState.valueOf(achievement.getState()));
			
			builder.addItem(item);
		}
		return builder.build();
	}
}