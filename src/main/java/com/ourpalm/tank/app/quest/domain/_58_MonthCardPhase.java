package com.ourpalm.tank.app.quest.domain;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._105_MonthCardLogic;
import com.ourpalm.tank.domain.ActivityMonthCardInfo;

public class _58_MonthCardPhase extends QuestPhase{

	public _58_MonthCardPhase(int limit) {
		super(limit);
	}

	@Override
	public boolean initProgress() {
		_105_MonthCardLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.MonthCard);
		if(logic == null)
			return false;
		
		ActivityMonthCardInfo info = logic.getInfo(roleId);
		if(info == null)
			return false;
		
		if(info.monthCardIsExpire()) {
			return false;
		}
		
		this.roleMonthCard();
		return true;
	}

	@Override
	public boolean roleMonthCard() {
		this.progress += 1;
		return true;
	}

}
