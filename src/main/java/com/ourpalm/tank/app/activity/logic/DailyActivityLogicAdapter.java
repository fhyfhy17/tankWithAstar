package com.ourpalm.tank.app.activity.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyConditionItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyPictureItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.vo.AttrUnit;

public abstract class DailyActivityLogicAdapter implements ActivityLogic {
	protected static final Logger logger = LogCore.runtime;
	
	private static final int INVALID_ACTIVITY_ID = -1;
	protected ActivityDao activityDao;
	protected List<ActivityTemplate> activityTemplate = new ArrayList<>();

	@Override
	public void login(int roleId, boolean online, String serviceId) {
			
	}

	@Override
	public void offline(int roleId, boolean offline) {

	}

	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {

	}

	@Override
	public void refreshServerAM0() {

	}

	@Override
	public void recharge(int roleId, PayType type, int actualRmb) {

	}

	@Override
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}
	
	@Override
	public ActivityTemplate getActivityTemplate() {
		for(ActivityTemplate template : activityTemplate) {
			if(template.isOpen()) {
				return template;
			}
		}
		return null;
	}
	
	protected boolean isValid(ActivityTemplate template) {
		return template != null && template.isOpen();
	}

	@Override
	public void addActivityTemplate(ActivityTemplate template) {
		activityTemplate.add(template);
	}
	
	
	protected int getValidActivityId() {
		ActivityTemplate template = getActivityTemplate();
		if(template != null) {
			return template.getId();
		}
		return INVALID_ACTIVITY_ID;
	}
	
	protected void prompt(int roleId) {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, STC_PROMPT_MSG.
					newBuilder().
					setPrompt(PROMPT.DAILY_ACTIVITY).build().toByteArray());
		}
	}

	@Override
	public void start() {
		
	}

	@Override
	public int getLogicType() {
		return 1;
	}
	
	protected List<RewardItem> buildRewardItem(List<AttrUnit> attrs) {
		List<RewardItem> list = new ArrayList<>();
		for(AttrUnit attr : attrs) {
			RewardItem.Builder builder = RewardItem.newBuilder();
			builder.setId(0);
			switch(attr.getType()) {
			case gold:
				builder.setCount(attr.getValue());
				builder.setType(RewardType.GOLD);
				break;
			case iron:
				builder.setCount(attr.getValue());
				builder.setType(RewardType.IRON);
				break;
			case exp:
				builder.setCount(attr.getValue());
				builder.setType(RewardType.EXP);
				break;
			case honor:
				builder.setCount(attr.getValue());
				builder.setType(RewardType.HONOR);
				break;
			default:
				continue;
			}
			list.add(builder.build());
		}
		return list;
	}

	protected List<RewardItem> buildRewardItem(Map<Integer, Integer> goodsMap) {
		List<RewardItem> list = new ArrayList<>();
		for(Integer id : goodsMap.keySet()) {
			RewardItem.Builder builder = RewardItem.newBuilder();
			builder.setId(id);
			builder.setCount(goodsMap.get(id));
			builder.setType(RewardType.GOODS);
			list.add(builder.build());
		}
		return list;
	}

	@Override
	public List<DailyConditionItem> getItemBuilder(int roleId) {
		return null;
	}

	@Override
	public DailyPictureItem getItemPictureBuilder(int roleId) {
		return null;
	}
	
	
}
