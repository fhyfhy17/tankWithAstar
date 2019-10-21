package com.ourpalm.tank.app.activity.logic;

import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.domain.ActivityDailyInfo;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyPictureItem;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template._102_FreeDoubleTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;

public class _102_FreeDoubleExpLogic extends DailyActivityLogicAdapter {

	private _102_FreeDoubleTemplate itemTemplate;
	
	@Override
	public void start() {
		loadItemTemplates();
	}
	
	
	
	@Override
	public void login(int roleId, boolean online, String serviceId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return;
		}
		
		ActivityDailyInfo info = init(roleId, template.getId());
		
		if(!DateUtil.isSameDay(info.getLastLoginTime(), System.currentTimeMillis())) {
			info.setLastLoginTime(System.currentTimeMillis());
			info.setDoubleExpCount(0);
			saveInfo(roleId, info);
		}
	}
	
	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
		login(roleId, b, serviceId);
	}
	
	private ActivityDailyInfo init(int roleId, int activityId) {
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null || info.getActivityId() != activityId) {
			info = new ActivityDailyInfo();
			info.setActivityId(activityId);
			saveInfo(roleId, info);
		}
		return info;
	}



	private void loadItemTemplates() {
		String sourceFile = XlsSheetType.DailyActivity102Item.getXlsFileName();
		String sheetName = XlsSheetType.DailyActivity102Item.getSheetName();
		try {
			List<_102_FreeDoubleTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, _102_FreeDoubleTemplate.class);
			itemTemplate = list.get(0);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	
	public int freeDoubleExp(int roleId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return 1;
		}
		
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null) {
			return 1;
		}
		
		if(!DateUtil.isSameDay(info.getLastLoginTime(), System.currentTimeMillis())) {
			info.setLastLoginTime(System.currentTimeMillis());
			info.setDoubleExpCount(0);
		}
		
		
		
		if(info.getDoubleExpCount() >= itemTemplate.getDayFreeCount())
			return 1;
		
		info.setDoubleExpCount(info.getDoubleExpCount() + 1);
		saveInfo(roleId, info);
		
		if(logger.isDebugEnabled()) {
			logger.debug("role: {} 处发免费经验-第：{} 次", roleId, info.getDoubleExpCount());
		}
		return itemTemplate.getTimes();
	}
	
	public DailyPictureItem getItemPictureBuilder(int roleId) {
		DailyPictureItem.Builder pBuilder = DailyPictureItem.newBuilder();
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null)
			return null;
		
		pBuilder.setPictureId(itemTemplate.getPictureId());
		pBuilder.setToPage(itemTemplate.getToPage());
		
		String tip = info.getDoubleExpCount() + "/" + itemTemplate.getDayFreeCount();
		pBuilder.setDesc(itemTemplate.getDesc()+ " " + tip);
		
		return pBuilder.build();
	}

	
	private ActivityDailyInfo getInfo(int roleId) {
		ActivityDailyInfo info = activityDao.getDailyInfo(roleId, ActivityLogicEnum.FreeDoubleExp.logicId);
		return info;
	}

	private void saveInfo(int roleId, ActivityDailyInfo info) {
		activityDao.saveDailyInfo(roleId, ActivityLogicEnum.FreeDoubleExp.logicId, info);
	}

}
