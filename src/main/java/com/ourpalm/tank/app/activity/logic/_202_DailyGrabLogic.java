package com.ourpalm.tank.app.activity.logic;

import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.ActivityDailyGrabInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.ActivityDailyGrabTemplate;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class _202_DailyGrabLogic extends HallActivityLogicAdapter {


	private ActivityDailyGrabTemplate infoTemplate; 
	
	@Override
	public void start() {
		loadTemplate();
		
		int serverPacketCount = activityDao.getDailyGrabServerPacket();
		if(serverPacketCount <= 0) {
			activityDao.saveDailyGrabServerPacket(infoTemplate.getPacketCount());
		}
	}
	
	private void loadTemplate() {
		String sourceFile = XlsSheetType.HallDailyGrab.getXlsFileName();
		String sheetName = XlsSheetType.HallDailyGrab.getSheetName();
		try {
			List<ActivityDailyGrabTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityDailyGrabTemplate.class);
			if(!Util.isEmpty(list)) {
				infoTemplate = list.get(0); 
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public void login(int roleId, boolean online, String serviceId) {
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return;
		}
		
		ActivityDailyGrabInfo info = init(roleId, template.getId());
		
		if(!DateUtil.isSameDay(info.getLastGrabCount(), System.currentTimeMillis())) {
			info.setTodayGrabCount(0);
			saveInfo(roleId, info);
		}
		
		if(info.getTodayGrabCount() < infoTemplate.getGrabMax()) {
			prompt(roleId);
		}
		
		if(!info.hadFreeReward() && info.getTotalGrabCount() >= infoTemplate.getGrabTotalMax()) {
			prompt(roleId);
		}
	}
	
	private ActivityDailyGrabInfo init(int roleId, int activityId) {
		ActivityDailyGrabInfo info = getInfo(roleId);
		if(info == null || info.getActivityId() != activityId) {
			info = new ActivityDailyGrabInfo();
			info.setActivityId(activityId);
			saveInfo(roleId, info);
		}
		return info;
	}

	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return;
		}
		
		ActivityDailyGrabInfo info = init(roleId, template.getId());
		if(info == null) {
			return;
		}
		
		info.setTodayGrabCount(0);
		saveInfo(roleId, info);
		
		prompt(roleId);
	}

	@Override
	public void recharge(int roleId, PayType type, int actualRmb) {
		if(type != PayType.day)
			return;
		
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return;
		}
		
		ActivityDailyGrabInfo info = init(roleId, template.getId());
		if(info == null) {
			return;
		}
		
		int todayGrabCount = info.getTodayGrabCount();
		if(todayGrabCount >= infoTemplate.getGrabMax()) {
			return;
		}
		
		info.setTodayGrabCount(todayGrabCount + 1); 
		
		int totalGrabCount = info.getTotalGrabCount();
		if(totalGrabCount < infoTemplate.getGrabTotalMax()) {
			totalGrabCount += 1;
			info.setTotalGrabCount(totalGrabCount);
		}
		
		//提示领取免费礼包
		if(!info.hadFreeReward() && infoTemplate.getGrabTotalMax() <= totalGrabCount) {
			prompt(roleId);
		}
		
		info.setLastGrabCount(System.currentTimeMillis());
		
		int serverPacketCount = activityDao.getDailyGrabServerPacket();
		if(serverPacketCount > 0) {
			activityDao.saveDailyGrabServerPacket(serverPacketCount -1);
		}
		
		saveInfo(roleId, info);
	}
	
	public Result order(int roleId) {
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return Result.newFailure("活动已结束");
		}
		
		ActivityDailyGrabInfo info = init(roleId, template.getId());
		if(info == null) {
			return Result.newFailure("活动信息不存在");
		}
		
		if(!canPay()) {
			return Result.newFailure("未到购买时间");
		}
		
		if(info.getTodayGrabCount() >= infoTemplate.getGrabMax()) {
			return Result.newFailure("已到达今日秒杀上限");
		}
		
		if(activityDao.getDailyGrabServerPacket() <= 0)
			return Result.newFailure("达到今日礼包购买上限");
		
		return Result.newSuccess();
	}
	
	public Result reward(int roleId) {
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return Result.newFailure("活动已结束");
		}
		
		ActivityDailyGrabInfo info = init(roleId, template.getId());
		if(info == null) {
			return Result.newFailure("活动信息不存在");
		}
		
		if(info.hadFreeReward()) {
			return Result.newFailure("奖励已领取");
		}
		
		if(info.getTotalGrabCount() < infoTemplate.getGrabTotalMax()) {
			return Result.newFailure("奖励不可领取");
		}
		
		info.setHadFreeReward(true);
		saveInfo(roleId, info);
		
		int rewardId = infoTemplate.getFreeReward();	
		GameContext.getGoodsApp().addGoods(roleId, rewardId, 1, "领取每日秒杀奖励");
		return Result.newSuccess();
	}
	
	
	@Override
	public void refreshServerAM0() {
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return;
		}
		
		activityDao.saveDailyGrabServerPacket(infoTemplate.getPacketCount());
	}
	
	public boolean canPay() {
		long curTime = System.currentTimeMillis();
		int curHour = DateUtil.getTimeHour(curTime);
		
		return curHour >= infoTemplate.getGrabBeginHour() && curHour <= infoTemplate.getGrabEndHour();
	}

	public ActivityDailyGrabInfo getInfo(int roleId) {
		return activityDao.getDailyGrabInfo(roleId, ActivityLogicEnum.DailyGrab.logicId);
	}
	
	private void saveInfo(int roleId, ActivityDailyGrabInfo info) {
		activityDao.saveDailyGrabInfo(roleId, ActivityLogicEnum.DailyGrab.logicId, info);
	}
	

	private void prompt(int roleId) {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			
			connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, STC_PROMPT_MSG.
					newBuilder().
					setPrompt(PROMPT.DAILY_GRAB).build().toByteArray());
		}
	}

	public ActivityDailyGrabTemplate getGrabTemplate() {
		return infoTemplate;
	}

	public int getServerGrabPacketCount() {
		return activityDao.getDailyGrabServerPacket();
	}

	public void orderFail(int roleId) {
	}
	
}
