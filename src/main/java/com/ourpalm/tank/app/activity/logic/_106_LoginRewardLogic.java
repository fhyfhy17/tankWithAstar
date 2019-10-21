package com.ourpalm.tank.app.activity.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.ActivityDailyInfo;
import com.ourpalm.tank.message.ACTIVITY_MSG.DailyConditionItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardState;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template._106_LoginRewardTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class _106_LoginRewardLogic extends DailyActivityLogicAdapter {

	private List<_106_LoginRewardTemplate> itemTemplates = new ArrayList<>();
	
	@Override
	public void start() {
		loadItemTemplates();
	}
	
	private void loadItemTemplates() {
		String sourceFile = XlsSheetType.DailyActivity106Item.getXlsFileName();
		String sheetName = XlsSheetType.DailyActivity106Item.getSheetName();
		try {
			List<_106_LoginRewardTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, _106_LoginRewardTemplate.class);
			for(_106_LoginRewardTemplate template : list) {
				template.init();
				itemTemplates.add(template);
			}
			Collections.sort(list);
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
		
		ActivityDailyInfo info = init(roleId, template.getId());
		
		//跨天
		long curTime = System.currentTimeMillis();
		if(!DateUtil.isSameDay(info.getLastLoginTime(), curTime)) {
			info.setLastLoginTime(System.currentTimeMillis());
			
			int nextItemId = info.getRemainId() + 1;
			_106_LoginRewardTemplate nextItemTemplate = null;
			for(_106_LoginRewardTemplate item : itemTemplates) {
				if(item.getId() == nextItemId) {
					nextItemTemplate = item;
					break;
				}
			}
			if(nextItemTemplate != null) {
				info.putItemState(nextItemId, RewardState.DRAW);
				info.setRemainId(nextItemId);
				
				prompt(roleId);
			}
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
			
			_106_LoginRewardTemplate firstItemTemplate = itemTemplates.get(0);
			info.putItemState(firstItemTemplate.getId(), RewardState.DRAW);
			info.setRemainId(firstItemTemplate.getId());
			info.setLastLoginTime(System.currentTimeMillis());
			saveInfo(roleId, info);
			
			prompt(roleId);
		}
		return info;
	}
	

	public Result doDraw(int roleId, int itemId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return Result.newFailure("活动已结束");
		}
		
		_106_LoginRewardTemplate itemTemplate = null;
		for(_106_LoginRewardTemplate item : itemTemplates) {
			if(item.getId() == itemId) {
				itemTemplate = item;
				break;
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
		GameContext.getGoodsApp().addGoods(roleId, itemTemplate.getGoodsMap(), StringUtil.buildLogOrigin(template.getName(), "领取日常活动累计登录奖励"));
		GameContext.getUserAttrApp().changeAttribute(roleId, itemTemplate.getAttrList(), OutputType.dailyLoginInc.type(), StringUtil.buildLogOrigin(template.getName(), "领取日常活动累计登录奖励"));
		
		if(logger.isDebugEnabled()) {
			logger.debug("role: {} 领取累计登录奖励动id: {} 奖励id：{} ", roleId, template.getId(), itemId);
		}
		return Result.newSuccess();
	}
	
	public List<DailyConditionItem> getItemBuilder(int roleId) {
		List<DailyConditionItem> result = new ArrayList<>();
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null)
			return null;
		
		for(_106_LoginRewardTemplate item : itemTemplates) {
			DailyConditionItem.Builder itemBuilder = DailyConditionItem.newBuilder();
			itemBuilder.setId(item.getId());
			itemBuilder.setName(item.getName());
			itemBuilder.setDesc(item.getDesc());
			itemBuilder.addAllRewards(buildRewardItem(item.getAttrList()));
			itemBuilder.addAllRewards(buildRewardItem(item.getGoodsMap()));
			itemBuilder.setToPage(item.getToPage());
			itemBuilder.setState(info.getItemState(item.getId()));
			
			if(itemBuilder.getState() == RewardState.INIT){
				itemBuilder.setState(RewardState.WAIT);
			}
			result.add(itemBuilder.build());
		}
		return result;
	}
	
	private ActivityDailyInfo getInfo(int roleId) {
		ActivityDailyInfo info = activityDao.getDailyInfo(roleId, ActivityLogicEnum.LoginReward.logicId);
		return info;
	}
	
	private void saveInfo(int roleId, ActivityDailyInfo info) {
		activityDao.saveDailyInfo(roleId, ActivityLogicEnum.LoginReward.logicId, info);
	}
}
