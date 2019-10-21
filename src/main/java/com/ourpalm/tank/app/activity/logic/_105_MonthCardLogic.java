package com.ourpalm.tank.app.activity.logic;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.ActivityMonthCardInfo;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.vo.result.Result;

public class _105_MonthCardLogic extends DailyActivityLogicAdapter{

	@Override
	public void start() {
	}
	
	@Override
	public void login(int roleId, boolean online, String serviceId) {
		ActivityMonthCardInfo info = getInfo(roleId);
		if(info == null) {
			info = new ActivityMonthCardInfo();
		}
		info.setMonthCardBuying(false);
		info.setYearCardBuying(false);
		saveInfo(roleId, info);
	}
	
	public Result order(int roleId, int cardId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return Result.newFailure("活动已结束");
		}
		
		ActivityMonthCardInfo info = getInfo(roleId);
		if(info == null) {
			return Result.newFailure("月卡信息不存在");
		}
		
		PayType cardType = GameContext.getPayApp().getPayTypeById(cardId);
		switch(cardType) {
			case month_card:
				if(!info.monthCardIsExpire()) {
					return Result.newFailure("月卡有效期内，不能重复购买");
				}
				
				if(info.monthCardBuying()) {
					return Result.newFailure("购买月卡订单不能重复提交");
				}
				
				info.setMonthCardBuying(true);
				break;
			case forever_card:
				if(!info.yearCardIsExpire()) {
					return Result.newFailure("年卡有效期内，不能重复购买");
				}
				
				if(info.yearCardBuying()) {
					return Result.newFailure("购买年卡订单不能重复提交");
				}
				
				info.setYearCardBuying(true);
				break;
			default:
				break;
		}

		info.setActivityId(template.getId());
		saveInfo(roleId, info);
		
		return Result.newSuccess();
	}
	
	public void orderFail(int roleId, int cardId) {
		ActivityMonthCardInfo info = getInfo(roleId);
		if(info == null) {
			return;
		}
		
		PayType cardType = GameContext.getPayApp().getPayTypeById(cardId);
		switch(cardType) {
			case month_card:
				info.setMonthCardBuying(false);
				break;
			case forever_card:
				info.setYearCardBuying(false);
				break;
			default:
				break;
		}
		saveInfo(roleId, info);
	}

	@Override
	public void recharge(int roleId, PayType type, int actualRmb) {
		if(type != PayType.month_card && type != PayType.forever_card){
			return ;
		}
		
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return;
		}
		
		ActivityMonthCardInfo info = getInfo(roleId);
		if(info == null) {
			return;
		}
		
		switch(type) {
			case month_card:
				if(info.monthCardIsExpire()) {
					info.monthCardBuy();
					GameContext.getQuestTriggerApp().roleMonthCard(roleId);
				}
				break;
			case forever_card:
				if(info.yearCardIsExpire()) {
					info.yearCardBuy();
					GameContext.getQuestTriggerApp().roleYearCard(roleId);
				}
				break;
			default:
				break;
		}
		
		saveInfo(roleId, info);
		
		prompt(roleId);
	}
	
	public ActivityMonthCardInfo getInfo(int roleId) {
		ActivityMonthCardInfo info = activityDao.getMonthCardInfo(roleId, ActivityLogicEnum.MonthCard.logicId);
		return info;
	}

	private void saveInfo(int roleId, ActivityMonthCardInfo info) {
		activityDao.saveMonthCardInfo(roleId, ActivityLogicEnum.MonthCard.logicId, info);
	}
}
