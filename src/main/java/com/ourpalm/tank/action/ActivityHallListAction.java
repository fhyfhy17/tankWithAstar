package com.ourpalm.tank.action;

import java.util.Collection;

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
import com.ourpalm.tank.message.ACTIVITY_MSG.ActivityType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_HALL_LIST_MSG;
import com.ourpalm.tank.template.ActivityTemplate;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_HALL_LIST_VALUE
)
public class ActivityHallListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		if(account == null)
			return null;
		
		STC_ACTIVITY_HALL_LIST_MSG.Builder builder = STC_ACTIVITY_HALL_LIST_MSG.newBuilder();
		
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
			if(logic.getLogicType() != 2) {
				continue;
			}
			
			if(template.getLogicId() == ActivityLogicEnum.RedPacket.logicId) {
				builder.addTypes(ActivityType.RedPacket);
			} else if(template.getLogicId() == ActivityLogicEnum.DailyGrab.logicId) {
				builder.addTypes(ActivityType.DailyGrab);
			}
			
		}
		return builder.build();
		
	}
	
	

}
