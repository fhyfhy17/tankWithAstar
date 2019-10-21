package com.ourpalm.tank.action;

import java.util.Collection;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogic;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.ActivityItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyConditionItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyPictureItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_DAILY_LIST_MSG;
import com.ourpalm.tank.template.ActivityTemplate;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_DAILY_LIST_VALUE
)
public class ActivityDailyListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null)
			return null;
		
		STC_ACTIVITY_DAILY_LIST_MSG.Builder builder = STC_ACTIVITY_DAILY_LIST_MSG.newBuilder();
		
		Collection<ActivityLogic> logicList = GameContext.getActivityApp().getActivityLogic();
		for(ActivityLogic logic : logicList) {
			ActivityTemplate template = logic.getActivityTemplate();
			if(template == null)
				continue;
			
			//不是同一渠道
			String serviceId = template.getServiceId();
			if(!Util.isEmpty(serviceId) && !account.getServiceId().equals(serviceId)) {
				continue;
			}
			
			//过滤大厅活动
			if(logic.getLogicType() != 1) {
				continue;
			}
			
			ActivityItem.Builder activityBuilder = ActivityItem.newBuilder();
			activityBuilder.setId(template.getLogicId());
			activityBuilder.setName(template.getName());
			activityBuilder.setTime(template.getTimeDesc());
			activityBuilder.setDesc(template.getDesc());
			activityBuilder.setType(template.getType());
			activityBuilder.setFlag(template.getFlag());
			activityBuilder.setSort(template.getSort());
			
			List<DailyConditionItem> conditionItem = logic.getItemBuilder(roleId);
			if(conditionItem != null) {
				activityBuilder.addAllItems(conditionItem);
				activityBuilder.setType(1);
			}
			DailyPictureItem pictureItem = logic.getItemPictureBuilder(roleId);
			if(pictureItem != null) {
				activityBuilder.setPictureItem(pictureItem);
				activityBuilder.setType(2);
			}
			
			if(template.getType() == 2) {
				activityBuilder.setType(5);
			}
			
			if(template.getLogicId() == ActivityLogicEnum.MonthCard.logicId) {
				activityBuilder.setType(3);
			}
			
			builder.addActivitys(activityBuilder);
		}
		
		return builder.build();
	}
	
}
