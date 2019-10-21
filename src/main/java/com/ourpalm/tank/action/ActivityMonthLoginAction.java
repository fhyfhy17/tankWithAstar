package com.ourpalm.tank.action;

import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.monthlogin.ActivityMonthLoginApp;
import com.ourpalm.tank.domain.ActivityMonthLoginInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.MonthLoginDay;
import com.ourpalm.tank.message.ACTIVITY_MSG.MonthLoginMonth;
import com.ourpalm.tank.message.ACTIVITY_MSG.MonthLoginPointDay;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_ACTIVITY_MONTHLOGIN_MSG;
import com.ourpalm.tank.template.ActivityMonthLoginPointRewardTemplate;
import com.ourpalm.tank.template.ActivityMonthLoginRewardTemplate;
import com.ourpalm.tank.vo.RewardInfo;

@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_ACTIVITY_MONTHLOGIN_VALUE
)
public class ActivityMonthLoginAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		ActivityMonthLoginApp app = GameContext.getActivityApp().getMonthLoginApp();
		ActivityMonthLoginInfo info = app.getPageList(connect.getRoleId());
		//计算可补签的次数
		info.reCalcFillSignCount();
		
		//判断今日是否可签到
		boolean todaySigned = DateUtil.isSameDay(info.getLastSignTime(), System.currentTimeMillis());
		
		//补通奖励
		List<ActivityMonthLoginRewardTemplate> dayList = app.getRewardTemplates(info.getMonth());
		MonthLoginMonth.Builder monthBuilder = MonthLoginMonth.newBuilder();
		monthBuilder.setMonth(info.getMonth());
		for(ActivityMonthLoginRewardTemplate t : dayList) {
			RewardItem.Builder itemBuilder = RewardItem.newBuilder();
			itemBuilder.setId(t.getRewardId());
			itemBuilder.setCount(t.getCount());
			itemBuilder.setType(RewardType.valueOf(t.getType()));
			
			MonthLoginDay.Builder dayBuilder = MonthLoginDay.newBuilder();
			dayBuilder.setDay(t.getDay());
			dayBuilder.setHadDraw(false);
			dayBuilder.setCanDraw(false);
			dayBuilder.setVipLevel(t.getVipLevel());
			dayBuilder.addReward(itemBuilder);
			
			if(!todaySigned) {
				if(info.nextSignDay() == t.getDay())
					dayBuilder.setCanDraw(true);
			} else {
				if(info.getFillSignCount() > 0 && info.nextSignDay() == t.getDay())
					dayBuilder.setCanDraw(true);
			}
			
			if(t.getDay() < info.nextSignDay()) {
				dayBuilder.setHadDraw(true);
				dayBuilder.setCanDraw(false);
			}
			
			monthBuilder.addDays(dayBuilder);
		}
		

		
		STC_ACTIVITY_MONTHLOGIN_MSG.Builder builder = STC_ACTIVITY_MONTHLOGIN_MSG.newBuilder();
		builder.setCanSign(!todaySigned);
		builder.setSignCount(info.getSignCount());
		builder.setFillSignCost(app.getFillSignCost(info.getTodayFillSignCount()));
		builder.setCanFillSign(info.getFillSignCount() > 0);
		builder.setMonths(monthBuilder);
		//计算节点奖励
		List<ActivityMonthLoginPointRewardTemplate> pointDayList = app.getPointRewardTemplates(info.getMonth());
		for(ActivityMonthLoginPointRewardTemplate t : pointDayList) {
			MonthLoginPointDay.Builder everyDayItemBuilder = MonthLoginPointDay.newBuilder();
			RewardItem.Builder itemBuilder = RewardItem.newBuilder();
			itemBuilder.setId(t.getReward());
			itemBuilder.setCount(t.getCount());
			itemBuilder.setType(RewardType.valueOf(t.getRewardInfo().getType()));
			everyDayItemBuilder.addRewards(itemBuilder);
			everyDayItemBuilder.setDay(t.getDay());
			if(info.getHasReceive().contains(t.getDay())){
				everyDayItemBuilder.setCanDraw(2);
			}else if(info.getSignCount()>t.getDay()){
				everyDayItemBuilder.setCanDraw(1);
			}else{
				everyDayItemBuilder.setCanDraw(0);
			}
			builder.addEveryDayItem(everyDayItemBuilder);
		}
		
		return builder.build();
		
	}
	
	

}
