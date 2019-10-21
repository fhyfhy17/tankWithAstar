package com.ourpalm.tank.app.activity.logic;

import java.util.Collection;
import java.util.List;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.HallFirstPayInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template.HallFirstPayRewardTemplate;
import com.ourpalm.tank.template.PayTemplate;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class _203_FirstPayLogic extends HallActivityLogicAdapter {


	private HallFirstPayRewardTemplate rewardTemplate;
	
	@Override
	public void start() {
		loadTemplate();
	}
	
	private void loadTemplate() {
		String sourceFile = XlsSheetType.HallFirstPayReward.getXlsFileName();
		String sheetName = XlsSheetType.HallFirstPayReward.getSheetName();
		try {
			List<HallFirstPayRewardTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, HallFirstPayRewardTemplate.class);
			if(!Util.isEmpty(list)) {
				rewardTemplate = list.get(0); 
				rewardTemplate.init();
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public void login(int roleId, boolean online, String serviceId) {
		ActivityTemplate template = getActivityTemplate();
		
		HallFirstPayInfo info = null;
		if(template != null) {
			info = init(roleId, template.getId());
		} else {
			info = getInfo(roleId);
		}
		
		if(info == null) {
			return;
		}
		
		if(info.hadReward()) {
			return;
		}
		
		if(info.canDraw()) {
			return;
		}
		
		if(getRemainTime(info.getCreateTime()) <= 0)  {
			info.setHadReward(true);
			saveInfo(roleId, info);
		}
	}
	
	public int getRemainTime(int createTime) {
		int endTime = createTime + rewardTemplate.getShowDay() * 24 * 60 * 60;
		int curTime = (int)(System.currentTimeMillis() / 1000);
		int remainTime = endTime - curTime;
		return remainTime > 0 ? remainTime : 0;
	}
	
	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
		login(roleId, b, serviceId);
	}

	private HallFirstPayInfo init(int roleId, int activityId) {
		HallFirstPayInfo info = getInfo(roleId);
		if(info == null || info.getActivityId() != activityId) {
			info = new HallFirstPayInfo();
			info.setActivityId(activityId);
			info.setCreateTime((int)(System.currentTimeMillis() / 1000));
			saveInfo(roleId, info);
		}
		return info;
	}

	@Override
	public void recharge(int roleId, PayType type, int actualRmb) {
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return;
		}
		
		HallFirstPayInfo info = init(roleId, template.getId());
		if(info == null) {
			return;
		}
		
		if(info.canDraw())
			return;
		
		if(info.hadReward())
			return;
		
		PayTemplate payTemplate = GameContext.getPayApp().getFirstPay();
		if(payTemplate == null) {
			return;
		}
		
		RolePay rolePay = GameContext.getPayApp().getRolePay(roleId);
		int totalRmb = rolePay.getRmb();
		
		if(totalRmb < payTemplate.getRmb()) {
			return;
		}
		
		info.setCanDraw(true);
		
		saveInfo(roleId, info);
		
		prompt(roleId);
	}
	
	public Result reward(int roleId) {
		HallFirstPayInfo info = getInfo(roleId);
		if(info == null) {
			return Result.newFailure("活动信息不存在");
		}
		
		if(info.hadReward()) {
			return Result.newFailure("奖励已领取");
		}
		
		if (!info.canDraw()) {
			return Result.newFailure("奖励不可领取");
		}
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(roleId);
		if (role.getPark() < allTankList.size() + 1) {
			return Result.newFailure("车位不足，请先购买车位");
		}
		
		info.setCanDraw(false);
		info.setHadReward(true);
		saveInfo(roleId, info);
		
		String origin = "首充活动奖励";
		if(rewardTemplate.getTankId() > 0) {
			RoleTank tank = GameContext.getTankApp().tankAdd(roleId, rewardTemplate.getTankId(), origin);
			GameContext.getTankApp().tankPush(tank);
			GameContext.getQuestTriggerApp().tankBuy(roleId, tank.getTankId());
		}
		
		GameContext.getUserAttrApp().changeAttribute(roleId, rewardTemplate.getAttrList(), OutputType.firstPayInc);
		GameContext.getGoodsApp().addGoods(roleId, rewardTemplate.getGoodsMap(), origin);
		return Result.newSuccess();
	}
	
	

	public HallFirstPayInfo getInfo(int roleId) {
		return activityDao.getFirstPayInfo(roleId, ActivityLogicEnum.FirstPay.logicId);
	}
	
	private void saveInfo(int roleId, HallFirstPayInfo info) {
		activityDao.saveFirstPayInfo(roleId, ActivityLogicEnum.FirstPay.logicId, info);
	}
	
	public HallFirstPayRewardTemplate getRewardTemplate() {
		return rewardTemplate;
	}
	

	private void prompt(int roleId) {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			
			connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, STC_PROMPT_MSG.
					newBuilder().
					setPrompt(PROMPT.FIRST_PAY).build().toByteArray());
		}
	}
	
}
