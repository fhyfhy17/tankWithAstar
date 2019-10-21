package com.ourpalm.tank.app.activity.monthlogin;

import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.ActivityMonthLoginInfo;
import com.ourpalm.tank.template.ActivityMonthLoginPointRewardTemplate;
import com.ourpalm.tank.template.ActivityMonthLoginRewardTemplate;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.ValueResult;

public interface ActivityMonthLoginApp extends Service{

	/**
	 * 今日签到
	 * @param roleId
	 * @return
	 */
	public ValueResult<ActivityMonthLoginInfo> sign(int roleId);
	
	/**
	 * 补签
	 * @param roleId
	 * @return
	 */
	public ValueResult<ActivityMonthLoginInfo> fillSign(int roleId);
	
	/**
	 * 获取月界面
	 * @param roleId
	 * @return
	 */
	ActivityMonthLoginInfo getPageList(int roleId);
	
	List<ActivityMonthLoginRewardTemplate> getRewardTemplates(int month);
	
	List<ActivityMonthLoginPointRewardTemplate> getPointRewardTemplates(int month);
	
	int getFillSignCost(int todayFillSignCount);
	
	/**
	 * 领取节点奖励
	 * @param roleId
	 * @param month
	 * @param day 哪天的节点
	 * @return
	 */
	public Result drawPoint(int roleId, int day);
}
