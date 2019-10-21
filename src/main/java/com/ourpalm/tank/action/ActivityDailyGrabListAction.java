package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._202_DailyGrabLogic;
import com.ourpalm.tank.domain.ActivityDailyGrabInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_DAILY_GRAB_LIST_MSG;
import com.ourpalm.tank.template.ActivityDailyGrabTemplate;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template.PayTemplate;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_DAILY_GRAB_LIST_VALUE
)
public class ActivityDailyGrabListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		_202_DailyGrabLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.DailyGrab);
		if(logic == null) {
			return null;
		}
		
		
		ActivityTemplate template = logic.getActivityTemplate();
		if(template == null)
			return null;
		
		
		ActivityDailyGrabInfo info = logic.getInfo(connect.getRoleId());
		if(info == null)
			return null;
		
		ActivityDailyGrabTemplate infoTemplate = logic.getGrabTemplate();
		PayTemplate payTemplate = GameContext.getPayApp().getDayGoodsBox();
		
		STC_DAILY_GRAB_LIST_MSG.Builder builder = STC_DAILY_GRAB_LIST_MSG.newBuilder();
		builder.setGrabCountLimit(infoTemplate.getGrabMax());
		builder.setGrabCount(info.getTodayGrabCount());
		builder.setPacketCountLimit(infoTemplate.getPacketCount());
		builder.setPacketCount(logic.getServerGrabPacketCount());
		builder.setFreeCountLimit(infoTemplate.getGrabTotalMax());
		builder.setFreeCount(info.getTotalGrabCount());
		builder.setFreeRewardId(infoTemplate.getFreeReward());
		builder.setGrabRewardId(payTemplate.getGoodsId());
		builder.setPId(payTemplate.getId());
		builder.setRmb(payTemplate.getRmb());
		builder.setPDesc(payTemplate.getDesc());
		builder.setPName(payTemplate.getName());
		builder.setHadDraw(info.hadFreeReward());
		builder.setPayTimeDesc(infoTemplate.getGrabTimeDesc());
		builder.setCanPay(logic.canPay());
		
		
		int time =(int)((template.getEndTime() - System.currentTimeMillis())/1000);
		if(time < 0)
			time = 0;
		
		builder.setRemainTime(time);
		
		return builder.build();
		
	}
	
	

}
