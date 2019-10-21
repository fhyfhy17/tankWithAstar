package com.ourpalm.tank.app.activity.monthlogin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.domain.ActivityMonthLoginInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.ActivityMonthLoginPointRewardTemplate;
import com.ourpalm.tank.template.ActivityMonthLoginRewardTemplate;
import com.ourpalm.tank.template.ActivityMonthLoginSignCostTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.RewardInfo;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.ValueResult;

public class ActivityMonthLoginAppImpl implements ActivityMonthLoginApp {
	private static final Logger logger = LogCore.runtime;
	
	
	private ActivityDao activityDao;
	
	private Map<Integer, List<ActivityMonthLoginRewardTemplate>> monthRewardTemplateMap = new HashMap<>();
	private Map<Integer, List<ActivityMonthLoginPointRewardTemplate>> monthPointRewardTemplateMap = new HashMap<>();
	
	private Map<Integer, Integer> monthFillSignCostTemplate = new HashMap<>();
	
	@Override
	public void start() {
		loadMonthLoginTemplate();
		loadMonthRewardTemplate();
		loadMonthPointRewardTemplate();
	}

	@Override
	public void stop() {
	}
	
	private void loadMonthLoginTemplate() {
		String sourceFile = XlsSheetType.HallMonthLoginSignCost.getXlsFileName();
		String sheetName = XlsSheetType.HallMonthLoginSignCost.getSheetName();
		try {
			List<ActivityMonthLoginSignCostTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityMonthLoginSignCostTemplate.class);
			for(ActivityMonthLoginSignCostTemplate t : list) {
				monthFillSignCostTemplate.put(t.getFillSignCount(), t.getFillSignCost());
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	private void loadMonthRewardTemplate() {
		String sourceFile = XlsSheetType.HallMonthLoginReward.getXlsFileName();
		String sheetName = XlsSheetType.HallMonthLoginReward.getSheetName();
		try {
			List<ActivityMonthLoginRewardTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityMonthLoginRewardTemplate.class);
			for(ActivityMonthLoginRewardTemplate t : list) {
				t.init();
				
				if(monthRewardTemplateMap.containsKey(t.getMonth())) {
					monthRewardTemplateMap.get(t.getMonth()).add(t);
				} else {
					List<ActivityMonthLoginRewardTemplate> dayList = new ArrayList<>(31);
					dayList.add(t);
					monthRewardTemplateMap.put(t.getMonth(), dayList);
				}
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	private void loadMonthPointRewardTemplate() {
		String sourceFile = XlsSheetType.HallMonthLoginPointReward.getXlsFileName();
		String sheetName = XlsSheetType.HallMonthLoginPointReward.getSheetName();
		try {
			List<ActivityMonthLoginPointRewardTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityMonthLoginPointRewardTemplate.class);
			Collections.sort(list);
			for(ActivityMonthLoginPointRewardTemplate t : list) {
				t.init();
				if(monthPointRewardTemplateMap.containsKey(t.getMonth())) {
					monthPointRewardTemplateMap.get(t.getMonth()).add(t);
				} else {
					List<ActivityMonthLoginPointRewardTemplate> dayList = new ArrayList<>();
					dayList.add(t);
					monthPointRewardTemplateMap.put(t.getMonth(), dayList);
				}
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	

	@Override
	public ActivityMonthLoginInfo getPageList(int roleId) {
		ActivityMonthLoginInfo info = activityDao.getMonthLoginInfo(roleId);
		//换月，则初始化
		int currentMonth = DateUtil.getCurrentMonth();
		if(info == null || info.getMonth() != currentMonth) {	
			info = new ActivityMonthLoginInfo(currentMonth);
			
			int pointDay = nextPointDay(currentMonth, info.getPointDay());
			info.setPointDay(pointDay);
			info.setPointCanDraw(false);
			info.getHasReceive().clear();
			activityDao.saveMonthLoginInfo(roleId, info);
			logger.debug("role: {}, new month: {}, pointDay: {}", roleId, currentMonth, pointDay);
		}
		return info;
	}
	
	public int getFillSignCost(int todayFillSignCount) {
		todayFillSignCount++;
		int cost = 0;
		if(monthFillSignCostTemplate.containsKey(todayFillSignCount))
			cost = monthFillSignCostTemplate.get(todayFillSignCount);
		else 
			cost = monthFillSignCostTemplate.get(-1);
		
		return cost;
	}

	/**
	 * 每日只可签到一次
	 */
	@Override
	public ValueResult<ActivityMonthLoginInfo> sign(int roleId) {
		ValueResult<ActivityMonthLoginInfo> result = new ValueResult<ActivityMonthLoginInfo>();
		ActivityMonthLoginInfo info = activityDao.getMonthLoginInfo(roleId);
		if(info == null) {
			result.failure("获取签到数据出错");
			return result;
		}
		
		int currentMonth = DateUtil.getCurrentMonth();
		if(currentMonth != info.getMonth()) {
			result.failure("本月已结束");
			return result;
		}
		
		boolean todaySigned = DateUtil.isSameDay(info.getLastSignTime(), System.currentTimeMillis());
		if(todaySigned) {
			result.failure("今日已签到过了");
			return result;
		}
		List<ActivityMonthLoginRewardTemplate> dayList = monthRewardTemplateMap.get(currentMonth);
		for (ActivityMonthLoginRewardTemplate t : dayList) {
			if(t.getType()==1){
				RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
				Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(roleId);
				if (role.getPark() < allTankList.size() + 1) {
					result.failure("车位不足，请先购买车位");
					return result;
				}
			}
		}
		//累加签到次数
		info.increaseSignCount();
		info.setLastSignTime(System.currentTimeMillis());
		
		//判断到达节点奖励
		if(info.getSignCount() == info.getPointDay()){
			info.setPointCanDraw(true);
		}
		
		//计算补签天
		info.reCalcFillSignCount();
		info.setTodayFillSignCount(0);	//每日初始化今日补签数
		
		activityDao.saveMonthLoginInfo(roleId, info);
		
		logger.debug("role: {}, 签到month: {}, 签到数：{}, 补签数:{}, pointDay:{}, canDraw:{}", roleId, currentMonth, info.getSignCount(), info.getFillSignCount(), info.getPointDay(), info.isPointCanDraw());
		
		//发奖
		for(ActivityMonthLoginRewardTemplate t : dayList) {
			if(t.getDay() == info.getSignCount()) {
				this.reward(roleId, new RewardInfo(t.getRewardId(), t.getCount(), t.getType()), OutputType.monthLoginSignInc);
			}
		}
		
		result.setValue(info);
		result.success();
		return result;
	}

	@Override
	public ValueResult<ActivityMonthLoginInfo> fillSign(int roleId) {
		ValueResult<ActivityMonthLoginInfo> result = new ValueResult<ActivityMonthLoginInfo>();
		ActivityMonthLoginInfo info = activityDao.getMonthLoginInfo(roleId);
		if(info == null) {
			LogCore.runtime.error("角色:{}, 签到失败，获取签到数据出错", roleId);
			result.failure("获取签到数据出错");
			return result;
		}
		
		int currentMonth = DateUtil.getCurrentMonth();
		if(currentMonth != info.getMonth()) {
			result.failure("本月已结束");
			return result;
		}
		
		int fillCount = info.getFillSignCount();
		if(fillCount <= 0) {
			result.failure("没有补签机会");
			return result;
		}
		
		int cost = getFillSignCost(info.getTodayFillSignCount());
		boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, cost), OutputType.activityMonthLoginRetroactiveDec);
		if(!suc) {
			result.failure("金币不够");
			return result;
		}
		
		List<ActivityMonthLoginRewardTemplate> dayList = monthRewardTemplateMap.get(currentMonth);
		for (ActivityMonthLoginRewardTemplate t : dayList) {
			if(t.getType()==1){
				RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
				Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(roleId);
				if (role.getPark() < allTankList.size() + 1) {
					result.failure("车位不足，请先购买车位");
					return result;
				}
			}
		}
		
		info.increaseSignCount();
		info.setLastSignTime(System.currentTimeMillis());
		info.decreaseFillSignCount();
		
		//判断到达节点奖励
		if(info.getSignCount() == info.getPointDay())
			info.setPointCanDraw(true);
		
		activityDao.saveMonthLoginInfo(roleId, info);
		
		logger.debug("role: {}, 补签month: {}, 签到数：{}, 补签数:{}, pointDay:{}, canDraw:{}", roleId, currentMonth, info.getSignCount(), info.getFillSignCount(), info.getPointDay(), info.isPointCanDraw());
		
		//发奖
	
		for(ActivityMonthLoginRewardTemplate t : dayList) {
			if(t.getDay() == info.getSignCount()) {
				this.reward(roleId, new RewardInfo(t.getRewardId(), t.getCount(), t.getType()), OutputType.monthLoginSignInc);
				break;
			}
		}
		
		result.setValue(info);
		result.success();
		return result;
	}

	@Override
	public Result drawPoint(int roleId,int day) {
		ActivityMonthLoginInfo info = activityDao.getMonthLoginInfo(roleId);
		if(info == null) {
			LogCore.runtime.error("角色:{}, 签到失败，获取签到数据出错", roleId);
			return Result.newFailure("获取签到数据出错");
		}
		
		int currentMonth = DateUtil.getCurrentMonth();
		if(currentMonth != info.getMonth()) {
			return Result.newFailure("本月已结束");
		}
		
//		if(!info.isPointCanDraw()) {
//			return Result.newFailure("奖励未激活");
//		}
		List<ActivityMonthLoginPointRewardTemplate> pointDayList = monthPointRewardTemplateMap.get(currentMonth);
		boolean isinTemplates = false;
		for (ActivityMonthLoginPointRewardTemplate activityMonthLoginPointRewardTemplate : pointDayList) {
			if (day == activityMonthLoginPointRewardTemplate.getDay()) {
				isinTemplates = true;
			}
		}
		if (!isinTemplates) {
			return Result.newFailure("非法数据");
		}
		if (info.getSignCount() < day) {
			return Result.newFailure("奖励未激活");
		}
		if(info.getHasReceive().contains(day)){
			return Result.newFailure("该日奖励已领取过，不可重复领取");
		}
		int pointDay = info.getPointDay();
		info.setPointCanDraw(false);
		int nextPointDay = this.nextPointDay(currentMonth, pointDay);

		if (nextPointDay != pointDay) {
			info.setPointDay(nextPointDay);
			if (info.getSignCount() >= nextPointDay)
				info.setPointCanDraw(true);
		}
		info.getHasReceive().add(day);
		activityDao.saveMonthLoginInfo(roleId, info);
		
		logger.debug("role: {}, 节点奖励month: {}, 签到数：{}, 补签数:{}, beforePointDay:{}, newPointDay:{}, newPointCanDraw:{}", roleId, currentMonth,
				info.getSignCount(), info.getFillSignCount(), pointDay, info.getPointDay(), info.isPointCanDraw());
		
		//发奖
		
		for(ActivityMonthLoginPointRewardTemplate t : pointDayList) {
			if(t.getDay() == pointDay) {
				this.reward(roleId, t.getRewardInfo(), OutputType.monthLoginAddUpInc);
			}
		}
		
		return Result.newSuccess();
	}
	
	private void reward(int roleId, RewardInfo info, OutputType origin) {
		this.reward(roleId, info, origin.type(), origin.getInfo());
	}
	
	private void reward(int roleId, RewardInfo info, int originId, String origin) {
		switch(info.getType()) {
			case 1:
				for(int i = 0, l = info.getCount(); i < l; i++) {
					RoleTank tank = GameContext.getTankApp().tankAdd(roleId, info.getId(), origin);
					GameContext.getTankApp().tankPush(tank);
				}
				break;
			case 2:
				for(int i = 0, l = info.getCount(); i < l; i++)
					GameContext.getMemberApp().addMember(roleId, info.getId(), origin);
				break;
			case 3:
				GameContext.getGoodsApp().addGoods(roleId, info.getId(), info.getCount(), origin);
				break;
			case 4:
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.add, info.getCount()), originId, origin);
				break;
			case 5:
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, info.getCount()), originId, origin);
				break;
			case 6:
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.honor, Operation.add, info.getCount()), originId, origin);
				break;
		}
	}
	
	private int nextPointDay(int month, int curDay) {
		List<ActivityMonthLoginPointRewardTemplate> pointDayList = monthPointRewardTemplateMap.get(month);
		for(ActivityMonthLoginPointRewardTemplate t : pointDayList) {
			if(t.getDay() > curDay)
				return t.getDay();
		}
		return curDay;
	}
	
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	@Override
	public List<ActivityMonthLoginRewardTemplate> getRewardTemplates(int month) {
		return monthRewardTemplateMap.get(month);
	}

	@Override
	public List<ActivityMonthLoginPointRewardTemplate> getPointRewardTemplates(int month) {
		return monthPointRewardTemplateMap.get(month);
	}

}
