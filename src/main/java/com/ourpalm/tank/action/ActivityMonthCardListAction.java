package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._105_MonthCardLogic;
import com.ourpalm.tank.domain.ActivityMonthCardInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.MonthCardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardState;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_MONTH_CARD_LIST_MSG;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template.PayTemplate;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_MONTH_CARD_LIST_VALUE
)
public class ActivityMonthCardListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		
		_105_MonthCardLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.MonthCard);
		if(logic == null)
			return null;
		
		ActivityTemplate template = logic.getActivityTemplate();
		if(template == null) {
			return null;
		}
		
		PayTemplate monthCardTemplate = GameContext.getPayApp().getMonthCard();
		PayTemplate ownCardTemplate = GameContext.getPayApp().getOwnCard();
		if(monthCardTemplate == null || ownCardTemplate == null)
			return null;
		
		ActivityMonthCardInfo info = logic.getInfo(connect.getRoleId());
		if(info == null)
			return null;
		
		//月卡
		MonthCardItem.Builder monthCardBuilder = MonthCardItem.newBuilder();
		monthCardBuilder.setId(monthCardTemplate.getId());
		monthCardBuilder.setDesc(monthCardTemplate.getDesc());
		monthCardBuilder.setName(monthCardTemplate.getName());
		monthCardBuilder.setRmb(monthCardTemplate.getRmb());
		monthCardBuilder.setRemainTime(info.getMonthCardDay());
		if(info.monthCardIsExpire()) {
			monthCardBuilder.setState(RewardState.DRAW);	//可以购买
		} else {
			monthCardBuilder.setState(RewardState.FINISH);	//不可购买
		}
		
		//永久卡
		MonthCardItem.Builder ownCardBuilder = MonthCardItem.newBuilder();
		ownCardBuilder.setId(ownCardTemplate.getId());
		ownCardBuilder.setDesc(ownCardTemplate.getDesc());
		ownCardBuilder.setName(ownCardTemplate.getName());
		ownCardBuilder.setRmb(ownCardTemplate.getRmb());
		ownCardBuilder.setRemainTime(info.getYearCardDay());
		if(info.yearCardIsExpire()) {
			ownCardBuilder.setState(RewardState.DRAW);
		} else {
			ownCardBuilder.setState(RewardState.FINISH);
		}
		
		STC_MONTH_CARD_LIST_MSG.Builder builder = STC_MONTH_CARD_LIST_MSG.newBuilder();
		builder.setMonthCard(monthCardBuilder);
		builder.setOwnCard(ownCardBuilder);
		return builder.build();
	}
	
	

}
