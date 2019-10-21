package com.ourpalm.tank.app.activity.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.ActivityDailyInfo;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyConditionItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardState;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template._103_ArmyTitleLevelTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class _103_ArmyTitleLevelLogic extends DailyActivityLogicAdapter {

	private List<_103_ArmyTitleLevelTemplate> itemTemplates = new ArrayList<>();
	
	@Override
	public void start() {
		loadItemTemplates();
	}
	
	private void loadItemTemplates() {
		String sourceFile = XlsSheetType.DailyActivity103Item.getXlsFileName();
		String sheetName = XlsSheetType.DailyActivity103Item.getSheetName();
		try {
			List<_103_ArmyTitleLevelTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, _103_ArmyTitleLevelTemplate.class);
			for(_103_ArmyTitleLevelTemplate template : list) {
				template.init();
				itemTemplates.add(template);
			}
			Collections.sort(itemTemplates);
			
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	
	@Override
	public void login(int roleId, boolean online, String serviceId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return;
		}
		
		init(roleId, template.getId());
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
	
	public Result doDraw(int roleId, int itemId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return Result.newFailure("活动已结束");
		}
		
		_103_ArmyTitleLevelTemplate itemTemplate = null;
		for(_103_ArmyTitleLevelTemplate item : itemTemplates) {
			if(item.getId() == itemId) {
				itemTemplate = item;
			}
		}
		if(itemTemplate == null)
			return Result.newFailure("奖励不存在");
		
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null) {
			return Result.newFailure("奖励不可领取");
		}
		
		RewardState state = info.getItemState(itemId);
		if(state != RewardState.DRAW) {
			return Result.newFailure("奖励不可领取");
		}
		
		info.putItemState(itemId, RewardState.FINISH);
		saveInfo(roleId, info);
		
		// 奖励
		GameContext.getGoodsApp().addGoods(roleId, itemTemplate.getGoodsMap(), StringUtil.buildLogOrigin(template.getName(), "领取日常活动军衔冲级奖励"));
		GameContext.getUserAttrApp().changeAttribute(roleId, itemTemplate.getAttrList(), OutputType.dailyArmyTitleLevelInc.type(), StringUtil.buildLogOrigin(template.getName(), "领取日常活动军衔冲级奖励"));
		
		if(logger.isDebugEnabled()) {
			logger.debug("role: {} 领取军衔冲级活动id: {} 奖励id：{} ", roleId, template.getId(), itemId);
		}
		
		return Result.newSuccess();
	}

	public void armyTitleLevel(int roleId, int titleId) {
		if(Util.isEmpty(itemTemplates))
			return;
		
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return;
		}
		
		boolean t = false;
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null) {
			return;
		}
		
		for(_103_ArmyTitleLevelTemplate item : itemTemplates) {
			if(item.getArmyTitleId() <= titleId) {
				if(info.getItemState(item.getId()) == RewardState.INIT) {
					info.putItemState(item.getId(), RewardState.DRAW);
					t = true;
				}
			}
		}
		
		if(t) {
			saveInfo(roleId, info);
			prompt(roleId);
			if(logger.isDebugEnabled()) {
				logger.debug("role: {} 处发军衔奖励-军衔id：{}", roleId, titleId);
			}
		}
	}
	
	public List<DailyConditionItem> getItemBuilder(int roleId) {
		List<DailyConditionItem> result = new ArrayList<>();
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null)
			return null;
		
		for(_103_ArmyTitleLevelTemplate item : itemTemplates) {
			DailyConditionItem.Builder itemBuilder = DailyConditionItem.newBuilder();
			itemBuilder.setId(item.getId());
			itemBuilder.setName(item.getName());
			itemBuilder.setDesc(item.getDesc());
			itemBuilder.addAllRewards(buildRewardItem(item.getAttrList()));
			itemBuilder.addAllRewards(buildRewardItem(item.getGoodsMap()));
			itemBuilder.setState(info.getItemState(item.getId()));
			itemBuilder.setToPage(item.getToPage());
			
			result.add(itemBuilder.build());
		}
		return result;
	}
	
	private ActivityDailyInfo getInfo(int roleId) {
		ActivityDailyInfo info = activityDao.getDailyInfo(roleId, ActivityLogicEnum.ArmyTitleLevel.logicId);
		return info;
	}
	
	private void saveInfo(int roleId, ActivityDailyInfo info) {
		activityDao.saveDailyInfo(roleId, ActivityLogicEnum.ArmyTitleLevel.logicId, info);
	}
}
