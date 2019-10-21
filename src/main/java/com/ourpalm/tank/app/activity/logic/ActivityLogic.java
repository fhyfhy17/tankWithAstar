package com.ourpalm.tank.app.activity.logic;

import java.util.List;

import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyConditionItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyPictureItem;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;

public interface ActivityLogic {

	void addActivityTemplate(ActivityTemplate template);
	
	void start();
	public void login(int roleId, boolean online, String serviceId);
	public void offline(int roleId, boolean offline);
	public void refreshAM0(int roleId, boolean b, String serviceId);
	public void refreshServerAM0();
	void recharge(int roleId, PayType type, int actualRmb);
	
	ActivityTemplate getActivityTemplate();
	
	int getLogicType();
	
	public List<DailyConditionItem> getItemBuilder(int roleId);
	
	public DailyPictureItem getItemPictureBuilder(int roleId);
	
	void setActivityDao(ActivityDao activityDao);
}
