package com.ourpalm.tank.action;

import java.util.Calendar;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ARMY_TITLE_MSG;
import com.ourpalm.tank.message.ARMY_TITLE_MSG.STC_TITLE_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_TIPS_MSG;
import com.ourpalm.tank.template.ArmyTitleTemplate;

@Command(
	type = ARMY_TITLE_MSG.CMD_TYPE.CMD_TYPE_TITLE_VALUE, 
	id = ARMY_TITLE_MSG.CMD_ID.CTS_TITLE_INFO_VALUE
)
public class ArmyTitleInfoAction implements Action<Object> {
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		int level = role.getLevel();
		if(level < 20){
			return STC_TIPS_MSG.newBuilder()
					.setTips("军衔排位赛需账号等级20级开放，加油吧指挥官")
					.build();
		}
		
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour == 0){
			hour = 24;
		}
		boolean isOpen = hour >= 8 && hour < 24;
		if(!isOpen){
			return STC_TIPS_MSG.newBuilder()
					.setTips("军衔排位赛将于每日8:00 - 00:00开放，敬请期待")
					.build();
		}
		
		String seasonCxt = GameContext.getRoleArmyTitleApp().getSeasonMatchContext();
		
		//判断是否已领取该奖项
		boolean hadDrawDay = DateUtil.isSameDay(role.getDayDrawRewardsTime(), System.currentTimeMillis());
		int lastSeasonMaxTitleId = role.getLastSeansonMaxTitleId();
		if(hadDrawDay){
			lastSeasonMaxTitleId = role.getSeasonMaxTitleId();
		}
		ArmyTitleTemplate template = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(lastSeasonMaxTitleId);
		
		STC_TITLE_INFO_MSG.Builder builder = STC_TITLE_INFO_MSG.newBuilder();
		builder.setContext(seasonCxt);
		builder.setCurrTitleId(role.getCurrTitleId());
		builder.setGold(template.getGold());
		builder.setHadDrawDay(hadDrawDay);
		builder.setHonor(template.getHonor());
		builder.setIron(template.getIron());
		builder.setSeasonMaxTitleId(template.getId_i());
		builder.setScore(role.getScore());
		
		ArmyTitleTemplate currTemplate = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(role.getCurrTitleId());
		ArmyTitleTemplate nextTemplate = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(currTemplate.getNextId());
		builder.setMaxScore(nextTemplate.getScore());
		
		return builder.build();
	}

}
