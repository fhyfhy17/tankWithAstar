package com.ourpalm.tank.app.activity;

import java.util.Collection;
import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.app.activity.logic.ActivityLogic;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.monthlogin.ActivityMonthLoginApp;
import com.ourpalm.tank.app.activity.sevenlogin.ActivitySevenLoginApp;
import com.ourpalm.tank.template.ActivityNoticeTemplate;
import com.ourpalm.tank.type.PayType;

public interface ActivityApp extends Service{
	/**
	 * 
	 * @param roleId
	 * @param online	true是真登录，false是服务器内部登录
	 * @param serviceId
	 */
	public void login(int roleId, boolean online, String serviceId);
	/**
	 * 
	 * @param roleId
	 * @param offline true是下线，false不是下线
	 */
	public void offline(int roleId, boolean offline);
	/**
	 * 玩家在线时，0点刷新
	 * @param roleId
	 * @param b
	 */
	public void refreshAM0(int roleId, boolean b, String serviceId);
	
	/**
	 * 服务器内部，0点刷新 
	 */
	public void refreshServerAM0();
	
	/**
	 * 免费双倍
	 * @param roleId
	 * @return
	 */
	int freeDoubleExp(int roleId);
	/**
	 * 军衔等级
	 * @param roleId
	 * @param armyTitleId
	 */
	void armyTitleLevel(int roleId, int armyTitleId);
	/**
	 * 战力比拼
	 * @param roleId
	 * @param battleSocre 战斗力
	 */
	void battleScore(int roleId, int battleSocre);
	/**
	 * 处发在线时长
	 * @param roleId
	 */
	void onlineTime(int roleId);
	
    void createUser(int roleId);
	
	/**
	 * 玩家充值
	 * @param roleId
	 * @param type		充值商品类型
	 * @param actualRmb 本次金额
	 */
	void recharge(int roleId, PayType type, int actualRmb);
	
	<T> T getActivityLogic(ActivityLogicEnum logicEnum);
	
	Collection<ActivityLogic> getActivityLogic();
	
	public ActivitySevenLoginApp getSevenLoginApp();
	public ActivityMonthLoginApp getMonthLoginApp();
	public List<ActivityNoticeTemplate> getNoticeTemplates();
	
}
