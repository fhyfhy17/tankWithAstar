package com.ourpalm.tank.app.activity.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RedPacketInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.ActivityRedPacketDrawTemplate;
import com.ourpalm.tank.template.ActivityRedPacketGroupTemplate;
import com.ourpalm.tank.template.ActivityRedPacketNodeTemplate;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.ValueResult;

public class _201_RedPacketLogic extends HallActivityLogicAdapter{

	private List<ActivityRedPacketNodeTemplate> nodeListTemplate = new ArrayList<>();
	private List<ActivityRedPacketGroupTemplate> groupListTemplate = new ArrayList<>();
	private Map<Integer, ActivityRedPacketDrawTemplate> drawMapTemplate = new HashMap<>();
	
	@Override
	public void start() {
		loadItemTemplates();
		loadDrawTemplate();
		loadGroupTemplate();
	}
	
	private void loadItemTemplates() {
		String sourceFile = XlsSheetType.HallRedPacketNode.getXlsFileName();
		String sheetName = XlsSheetType.HallRedPacketNode.getSheetName();
		try {
			nodeListTemplate = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityRedPacketNodeTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	public void loadGroupTemplate() {
		String sourceFile = XlsSheetType.HallRedPacketGroup.getXlsFileName();
		String sheetName = XlsSheetType.HallRedPacketGroup.getSheetName();
		try {
			groupListTemplate = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityRedPacketGroupTemplate.class);
			for(ActivityRedPacketGroupTemplate t : groupListTemplate) {
				t.init();
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	public void loadDrawTemplate() {
		String sourceFile = XlsSheetType.HallRedPacketDraw.getXlsFileName();
		String sheetName = XlsSheetType.HallRedPacketDraw.getSheetName();
		try {
			List<ActivityRedPacketDrawTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityRedPacketDrawTemplate.class);
			for(ActivityRedPacketDrawTemplate template : list) {
				template.init();
				drawMapTemplate.put(template.getId(), template);
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
		
		RedPacketInfo info = init(roleId, template.getId());
		
		if(info.getDrawCount() > 0) {
			prompt(roleId);
		}
	}
	
	private RedPacketInfo init(int roleId, int activityId) {
		RedPacketInfo info = getInfo(roleId);
		if(info == null || info.getActivityId() != activityId) {
			info = new RedPacketInfo();
			info.setActivityId(activityId);
			saveInfo(roleId, info);
		}
		return info;
	}

	@Override
	public void offline(int roleId, boolean offline) {
	}

	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
	}

	@Override
	public void refreshServerAM0() {
		
	}

	@Override
	public void recharge(int roleId, PayType type, int rmb) {
		if(rmb <= 0)
			return;
		
		ActivityTemplate template = getActivityTemplate();
		if(template == null) {
			return;
		}
		
		RedPacketInfo info = getInfo(roleId);
		if(info == null)
			return;
		
		int beforeRmb = info.getRmb();
		int totalRmb = beforeRmb + rmb;
		
		int addDrawCount = 0;
		for(ActivityRedPacketNodeTemplate t : nodeListTemplate) {
			if(t.getRmbLimit() > beforeRmb && t.getRmbLimit() <= totalRmb) {
				addDrawCount += t.getDrawCount();
			}
		}
		
		if(addDrawCount > 0) {
			info.setDrawCount(info.getDrawCount() + addDrawCount);
		}
		info.setRmb(totalRmb);
		saveInfo(roleId, info);
		
		if(info.getDrawCount() > 0) {
			prompt(roleId);
		}
	}

	public ValueResult<Integer> draw(int roleId) {
		ValueResult<Integer> result = new ValueResult<Integer>();
		RedPacketInfo info = getInfo(roleId);
		if(info == null) {
			result.failure("活动信息不存在");
			return result;
		}
		
		if(!info.canDraw()) {
			result.failure("可拆次数不足，请充值！");
			return result;
		}
		
		if(info.getGroup() == -1) {
			int index = RandomUtil.randomInt(groupListTemplate.size());
			info.setGroup(index);
		}
		int groupIndex = info.getGroup();
		int usedIndex = info.getUsedCount();
		ActivityRedPacketGroupTemplate groupTemplate = groupListTemplate.get(groupIndex);
		List<Integer> rewardList = groupTemplate.getRewardList();
		if(usedIndex >= rewardList.size()) {
			result.failure("红包已领取完");
			return result;
		}
		
		int rewardId = rewardList.get(usedIndex);
		ActivityRedPacketDrawTemplate rewardTemplate = drawMapTemplate.get(rewardId);
		if(rewardTemplate == null) {
			result.failure("奖励不存在");
			return result;
		}
		
		info.setUsedCount(usedIndex + 1);
		info.setDrawCount(info.getDrawCount() - 1);
		saveInfo(roleId, info);
		
		// 奖励
		GameContext.getGoodsApp().addGoods(roleId, rewardTemplate.getGoodsMap(), StringUtil.buildLogOrigin("领取红包奖励"));
		GameContext.getUserAttrApp().changeAttribute(roleId, rewardTemplate.getAttrList(), OutputType.redPacketInc);
		
		result.setValue(rewardId);
		result.success();
		return result;
	}
	
	public RedPacketInfo getInfo(int roleId) {
		return activityDao.getRedPacketInfo(roleId, ActivityLogicEnum.RedPacket.logicId);
	}
	
	private void saveInfo(int roleId, RedPacketInfo info) {
		activityDao.saveRedPacketInfo(roleId, ActivityLogicEnum.RedPacket.logicId, info);
	}
	
	private void prompt(int roleId) {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			
			connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, STC_PROMPT_MSG.
					newBuilder().
					setPrompt(PROMPT.RED_PACKET).build().toByteArray());
		}
	}

	public List<ActivityRedPacketNodeTemplate> getNodeTemplate() {
		return nodeListTemplate;
	}

	public ActivityRedPacketDrawTemplate getDrawTemplate(int drawId) {
		return drawMapTemplate.get(drawId);
	}

}
