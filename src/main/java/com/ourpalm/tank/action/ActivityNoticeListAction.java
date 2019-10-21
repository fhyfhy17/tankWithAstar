package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.ActivityNoticeItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_NOTICE_LIST_MSG;
import com.ourpalm.tank.template.ActivityNoticeTemplate;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_NOTICE_LIST_VALUE
)
public class ActivityNoticeListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		if(account == null)
			return null;	
		
		List<ActivityNoticeTemplate> list = GameContext.getActivityApp().getNoticeTemplates();
		
		STC_ACTIVITY_NOTICE_LIST_MSG.Builder builder = STC_ACTIVITY_NOTICE_LIST_MSG.newBuilder();
		for(ActivityNoticeTemplate t : list) {
			//为空则表示全渠道
			if(!Util.isEmpty(t.getServiceId()) && !account.getServiceId().equals(t.getServiceId()))
				continue;
			
			ActivityNoticeItem.Builder itemBuilder = ActivityNoticeItem.newBuilder();
			itemBuilder.setName(t.getName());
			itemBuilder.setTitle(t.getTitle());
			itemBuilder.setDesc(t.getDesc());
			itemBuilder.setToPage(t.getToPage());
			
			builder.addNotices(itemBuilder);
		}
		
		return builder.build();
	}
	
}
