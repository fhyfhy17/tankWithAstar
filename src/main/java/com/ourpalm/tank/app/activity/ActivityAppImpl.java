package com.ourpalm.tank.app.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.activity.logic.ActivityLogic;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic.ActivityLogicFactory;
import com.ourpalm.tank.app.activity.logic._101_FightingCompetitionLogic;
import com.ourpalm.tank.app.activity.logic._102_FreeDoubleExpLogic;
import com.ourpalm.tank.app.activity.logic._103_ArmyTitleLevelLogic;
import com.ourpalm.tank.app.activity.logic._104_OnlineTimeLogic;
import com.ourpalm.tank.app.activity.monthlogin.ActivityMonthLoginApp;
import com.ourpalm.tank.app.activity.sevenlogin.ActivitySevenLoginApp;
import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.template.ActivityNoticeTemplate;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;

public class ActivityAppImpl implements ActivityApp {
	private static final Logger logger = LogCore.runtime;
	private Map<Integer, ActivityLogic> logicMap = new HashMap<>();
	
	private ActivityDao activityDao;
	
	private ActivitySevenLoginApp sevenLoginApp;
	private ActivityMonthLoginApp monthLoginApp;
	
	private List<ActivityNoticeTemplate> noticeList = new ArrayList<>();
	
	@Override
	public void start() {
		loadDailyActivityTemplates();
		loadHallActivityTemplates();
		logicStart();
		
		loadActivityNoticeTemplate();
		
		sevenLoginApp.start();
		monthLoginApp.start();
	}
	
	private void loadActivityNoticeTemplate() {
		String sourceFile = XlsSheetType.DailyActivityNotice.getXlsFileName();
		String sheetName = XlsSheetType.DailyActivityNotice.getSheetName();
		try {
			noticeList = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityNoticeTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	private void loadDailyActivityTemplates() {
		String sourceFile = XlsSheetType.DailyActivity.getXlsFileName();
		String sheetName = XlsSheetType.DailyActivity.getSheetName();
		try {
			List<ActivityTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityTemplate.class);
			for(ActivityTemplate template : list) {
				template.init();
				
				initLogic(template);
			}
			
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	private void loadHallActivityTemplates() {
		String sourceFile = XlsSheetType.HallActivity.getXlsFileName();
		String sheetName = XlsSheetType.HallActivity.getSheetName();
		try {
			List<ActivityTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityTemplate.class);
			for(ActivityTemplate template : list) {
				template.init();
				
				initLogic(template);
			}
			
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	private void logicStart() {
		for(ActivityLogic logic : logicMap.values()) {
			logic.start();
		}
	}
	
	/**
	 * 创建Logic
	 * @param template
	 */
	private void initLogic(ActivityTemplate template) {
		int logicId = template.getLogicId();
		
		if(!logicMap.containsKey(logicId)) {
			ActivityLogic logic = ActivityLogicFactory.createLogic(logicId);
			if(logic == null) {
				logger.info("活动逻辑不存在：{}", logicId);
				return;
			}
			logic.setActivityDao(activityDao);
			logicMap.put(logicId, logic);
		}
		
		if(template.getType() == 0)
			return;
		
		//添加配置
		logicMap.get(logicId).addActivityTemplate(template);
	}
	

	@Override
	public void stop() {
		
	}

	@Override
	public void login(int roleId, boolean online, String serviceId) {
		for(ActivityLogic logic : logicMap.values()) {
			logic.login(roleId, online, serviceId);
		}
	}

	@Override
	public void offline(int roleId, boolean offline) {
		for(ActivityLogic logic : logicMap.values()) {
			logic.offline(roleId, offline);
		}
	}

	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
		for(ActivityLogic logic : logicMap.values()) {
			logic.refreshAM0(roleId, b, serviceId);
		}
	}

	@Override
	public void refreshServerAM0() {
		for(ActivityLogic logic : logicMap.values()) {
			logic.refreshServerAM0();
		}
	}

	@Override
	public void recharge(int roleId, PayType type, int actualRmb) {
		for(ActivityLogic logic : logicMap.values()) {
			logic.recharge(roleId, type, actualRmb);
		}
	}
	
	@Override
	public void battleScore(int roleId, int battleScore) {
		_101_FightingCompetitionLogic logic = getActivityLogic(ActivityLogicEnum.FightingCompetition);
		if(logic == null)
			return;
		
		logic.fightingCompetition(roleId, battleScore);
	}
	
	@Override
	public int freeDoubleExp(int roleId) {
		_102_FreeDoubleExpLogic logic = getActivityLogic(ActivityLogicEnum.FreeDoubleExp);
		if(logic == null)
			return 1;
		
		return logic.freeDoubleExp(roleId);
	}

	@Override
	public void armyTitleLevel(int roleId, int armyTitleId) {
		_103_ArmyTitleLevelLogic logic = getActivityLogic(ActivityLogicEnum.ArmyTitleLevel);
		if(logic == null)
			return;
		logic.armyTitleLevel(roleId, armyTitleId);
	}

	@Override
	public void onlineTime(int roleId) {
		_104_OnlineTimeLogic logic = getActivityLogic(ActivityLogicEnum.OnlineTime);
		if(logic == null)
			return;
		
		logic.onlineTime(roleId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public  <T> T getActivityLogic(ActivityLogicEnum logicEnum) {
		int logicId = logicEnum.logicId;
		
		if(!logicMap.containsKey(logicId)) {
			return null;
		}
		return (T)logicMap.get(logicId);
	}

	@Override
	public Collection<ActivityLogic> getActivityLogic() {
		return logicMap.values();
	}

	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}
	
	public void setSevenLoginApp(ActivitySevenLoginApp sevenLoginApp) {
		this.sevenLoginApp = sevenLoginApp;
	}
	public void setMonthLoginApp(ActivityMonthLoginApp monthLoginApp) {
		this.monthLoginApp = monthLoginApp;
	}

	@Override
	public ActivitySevenLoginApp getSevenLoginApp() {
		return sevenLoginApp;
	}

	@Override
	public ActivityMonthLoginApp getMonthLoginApp() {
		return monthLoginApp;
	}

	@Override
	public void createUser(int roleId) {
		sevenLoginApp.create(roleId);
	}
	
	@Override
	public List<ActivityNoticeTemplate> getNoticeTemplates() {
		return noticeList;
	}
}
