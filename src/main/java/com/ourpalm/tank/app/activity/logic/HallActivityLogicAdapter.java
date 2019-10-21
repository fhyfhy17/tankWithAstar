package com.ourpalm.tank.app.activity.logic;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyConditionItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyPictureItem;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;

public abstract class HallActivityLogicAdapter implements ActivityLogic {
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
	
	@Override
	public void start() {
		
	}
	
	@Override
	public int getLogicType() {
		return 2;
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
