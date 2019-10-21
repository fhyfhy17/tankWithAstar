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
import com.ourpalm.tank.template._104_OnlineTimeTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class _104_OnlineTimeLogic extends DailyActivityLogicAdapter {

	private List<_104_OnlineTimeTemplate> itemTemplates = new ArrayList<>();
	
	@Override
	public void start() {
		loadItemTemplates();
	}
	
	private void loadItemTemplates() {
		String sourceFile = XlsSheetType.DailyActivity104Item.getXlsFileName();
		String sheetName = XlsSheetType.DailyActivity104Item.getSheetName();
		try {
			List<_104_OnlineTimeTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, _104_OnlineTimeTemplate.class);
			for(_104_OnlineTimeTemplate template : list) {
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
		if(online) {
			ActivityTemplate template = getActivityTemplate();
			if(!isValid(template)) {
				return;
			}
			
			ActivityDailyInfo info = init(roleId, template.getId());
			
			//跨天
			long curTime = System.currentTimeMillis();
			if(!DateUtil.isSameDay(info.getLastLoginTime(), curTime)) {
				_104_OnlineTimeTemplate firstItemTemplate = itemTemplates.get(0);
				int firstItemId = firstItemTemplate.getId();
				info.getItemMap().clear();
				info.putItemState(firstItemId, RewardState.INIT);
				info.setRemainId(firstItemId);
				info.setRemainTime(firstItemTemplate.getSecond());
			}
			//设置登录时间
			info.setLastLoginTime(System.currentTimeMillis());
			saveInfo(roleId, info);
		}
	}
	
	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
		login(roleId, b, serviceId);
	}
	
	@Override
	public void offline(int roleId, boolean offline) {
		if(offline) {
			ActivityTemplate template = getActivityTemplate();
			if(!isValid(template)) {
				return;
			}
			
			ActivityDailyInfo info = getInfo(roleId);
			if(info == null) {
				return;
			}
			//计算倒计时
			if(info.getRemainTime() > 0) {
				int remainTime = onlineTime(roleId);
				info.setRemainTime(remainTime);
				saveInfo(roleId, info);
			}
		}
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
		
		_104_OnlineTimeTemplate itemTemplate = null;
		_104_OnlineTimeTemplate nextItemTemplate = null;
		for(_104_OnlineTimeTemplate item : itemTemplates) {
			if(item.getId() == itemId) {
				itemTemplate = item;
			}
			
			if(item.getId() == itemId + 1) {
				nextItemTemplate = item;
			}
		}
		if(itemTemplate == null)
			return Result.newFailure("奖励不存在");
		
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null) {
			return Result.newFailure("活动已经结束");
		}
		
		int curT = (int)(System.currentTimeMillis() / 1000);
		int lastLoginTime = (int)(info.getLastLoginTime() / 1000);
		
		int remainTime = info.getRemainTime() - (curT - lastLoginTime);
		remainTime = remainTime < 0 ? 0 : remainTime;
		
		if(remainTime == 0 && info.getItemState(info.getRemainId()) == RewardState.INIT) {
			info.putItemState(info.getRemainId(), RewardState.DRAW);
		}
		
		RewardState state = info.getItemState(itemId);
		if(state != RewardState.DRAW) {
			return Result.newFailure("奖励已领取");
		}
		
		info.putItemState(itemId, RewardState.FINISH);
		info.setRemainTime(0);
		info.setRemainId(0);
		
		if(nextItemTemplate != null) {
			int nextItemId = nextItemTemplate.getId();
			info.putItemState(nextItemId, RewardState.INIT);
			info.setRemainId(nextItemId);
			info.setRemainTime(nextItemTemplate.getSecond());
			//重设置登录时间，并开始计算
			info.setLastLoginTime(System.currentTimeMillis());
		}
		
		saveInfo(roleId, info);
		
		// 奖励
		GameContext.getGoodsApp().addGoods(roleId, itemTemplate.getGoodsMap(), StringUtil.buildLogOrigin(template.getName(), "领取日常活动在线时长奖励"));
		GameContext.getUserAttrApp().changeAttribute(roleId, itemTemplate.getAttrList(), OutputType.dailyOnlineRewardInc.type(), StringUtil.buildLogOrigin(template.getName(), "领取日常活动在线时长奖励"));
		
		if(logger.isDebugEnabled()) {
			logger.debug("role: {} 领取在线时长奖励动id: {} 奖励id：{} ", roleId, template.getId(), itemId);
		}
		return Result.newSuccess();
	}

	public int onlineTime(int roleId) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return 0;
		}
		
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null) {
			return 0;
		}
		
		int curT = (int)(System.currentTimeMillis() / 1000);
		int lastLoginTime = (int)(info.getLastLoginTime() / 1000);
		
		int remainTime = info.getRemainTime() - (curT - lastLoginTime);
		remainTime = remainTime < 0 ? 0 : remainTime;
		if(remainTime == 0 && info.getItemState(info.getRemainId()) == RewardState.INIT) {
			info.putItemState(info.getRemainId(), RewardState.DRAW);
			saveInfo(roleId, info);
			prompt(roleId);
		}
		
		return remainTime;
	}
	
	public List<DailyConditionItem> getItemBuilder(int roleId) {
		List<DailyConditionItem> result = new ArrayList<>();
		ActivityDailyInfo info = getInfo(roleId);
		if(info == null)
			return null;
		
		for(_104_OnlineTimeTemplate item : itemTemplates) {
			DailyConditionItem.Builder itemBuilder = DailyConditionItem.newBuilder();
			itemBuilder.setId(item.getId());
			itemBuilder.setName(item.getName());
			itemBuilder.setDesc(item.getDesc());
			itemBuilder.addAllRewards(buildRewardItem(item.getAttrList()));
			itemBuilder.addAllRewards(buildRewardItem(item.getGoodsMap()));
			itemBuilder.setToPage(item.getToPage());
			itemBuilder.setState(info.getItemState(item.getId()));
			itemBuilder.setTotalTimes(itemTemplates.size());
			
			if(item.getId() == info.getRemainId()) {
				if(itemBuilder.getState() == RewardState.INIT) {
					int remainTime = onlineTime(roleId);
					itemBuilder.setRemainTime(remainTime);
					if(remainTime == 0) {
						itemBuilder.setState(RewardState.DRAW);
					}
				}
			}
			
			if(itemBuilder.getState() == RewardState.INIT){
				itemBuilder.setState(RewardState.WAIT);
			}
			
			result.add(itemBuilder.build());
		}
		return result;
	}
	

	private ActivityDailyInfo getInfo(int roleId) {
		ActivityDailyInfo info = activityDao.getDailyInfo(roleId, ActivityLogicEnum.OnlineTime.logicId);
		return info;
	}
	private void saveInfo(int roleId, ActivityDailyInfo info) {
		activityDao.saveDailyInfo(roleId, ActivityLogicEnum.OnlineTime.logicId, info);
	}
}
