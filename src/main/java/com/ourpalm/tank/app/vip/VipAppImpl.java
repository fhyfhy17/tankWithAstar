package com.ourpalm.tank.app.vip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.VipDao;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.domain.VipInfo;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RewardState;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttrItem;
import com.ourpalm.tank.message.ROLE_MSG.STC_ROLE_ATTR_SYNC_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_VIP_LEVEL_UP_MSG;
import com.ourpalm.tank.template.VipTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class VipAppImpl implements VipApp{
	private static Logger logger = LogCore.runtime;
	
	private VipDao vipDao;
	
	private Map<Integer, VipTemplate> vipTemplates = new HashMap<>();
	
	@Override
	public void start() {
		loadTemplate();
	}

	@Override
	public void stop() {
		
	}
	
	
	private void loadTemplate() {
		String fileName = XlsSheetType.UserVipTemplate.getXlsFileName();
		String sheetName = XlsSheetType.UserVipTemplate.getSheetName();
		try {
			List<VipTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, VipTemplate.class);
			for (VipTemplate template : list) {
				template.init();
				vipTemplates.put(template.getLevel(), template);
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	@Override
	public void rechargeRmb(int roleId, int totalRmb) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		VipInfo info = vipDao.get(roleId);
		if(account == null || info == null){
			logger.warn("!!!!!!!!!!!!! 充值vip 账号或vipInfo 不存在 roleId = "+ roleId, new NullPointerException());
			return;
		}
		
		int curLevel = account.getVipLevel();
		int newLevel = curLevel;
		for(VipTemplate template : vipTemplates.values()) {
			if(totalRmb >= template.getLimit()) {
				newLevel = template.getLevel();
			}
		}
		
		List<Integer> rewardLevels = new ArrayList<>();
		if(curLevel < newLevel){
			for(int i = curLevel + 1; i <= newLevel; i++) {
				info.getHadDraw().put(i, RewardState.REWARD_VALUE);
				rewardLevels.add(i);
			}
			vipDao.save(roleId, info);
			
			account.setVipLevel(newLevel);
			GameContext.getUserApp().saveRoleAccount(account);
			
			logger.debug("role: {}, vip up level. before: {}, after: {}", account.getRoleName(), curLevel, newLevel);
		
			//设置免费复活次数
			int freeReviveCount = this.vipTemplates.get(newLevel).getPrivilegeAlive();
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			roleWarInfo.setFreeReviveCount(freeReviveCount);
			GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
		}
	
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			//1.先推送等级
			if(newLevel > curLevel) {
				STC_ROLE_ATTR_SYNC_MSG.Builder attrBuilder = STC_ROLE_ATTR_SYNC_MSG.newBuilder();
				attrBuilder.setSource("vip升级");
				RoleAttrItem.Builder item = RoleAttrItem.newBuilder();
				item.setType(RoleAttr.vip);
				item.setValue(newLevel);
				attrBuilder.addAttrs(item);
				connect.sendMsg(attrBuilder.build());
			}
			//2.再推送金额
			//客户单需要更新充值进度，没有提升vip等级也需要推送该消息
			STC_VIP_LEVEL_UP_MSG.Builder builder = STC_VIP_LEVEL_UP_MSG.newBuilder();
			builder.addAllDrawLevels(rewardLevels);
			builder.setRmb(totalRmb);
			connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_VIP_LEVEL_UP_VALUE, builder.build().toByteArray());
			
		}
	}

	public void setVipDao(VipDao vipDao) {
		this.vipDao = vipDao;
	}

	@Override
	public Result draw(int roleId, int vipLevel) {
		VipTemplate template = vipTemplates.get(vipLevel);
		VipInfo info = vipDao.get(roleId);
		if(template == null || info == null) {
			return Result.newFailure("奖励不存在");
		}
		
		if(info.getState(vipLevel) != RewardState.REWARD_VALUE) {
			return Result.newFailure("奖励不可领取");
		}
		
		info.getHadDraw().put(vipLevel, RewardState.FINISH_VALUE);
		vipDao.save(roleId, info);
		
		// 奖励
		GameContext.getGoodsApp().addGoods(roleId, template.getGoodsMap(), StringUtil.buildLogOrigin("vip等级奖励", vipLevel + ""));
		GameContext.getUserAttrApp().changeAttribute(roleId, template.getAttrList(), OutputType.vipRewardInc.type(), StringUtil.buildLogOrigin("vip等级奖励", vipLevel + ""));
						
		return Result.newSuccess();
	}

	@Override
	public Collection<VipTemplate> getTemplates() {
		return vipTemplates.values();
	}

	@Override
	public VipInfo getVipInfo(int roleId) {
		return vipDao.get(roleId);
	}


	@Override
	public int getPrivilegeAliveCount(int roleId) {
		VipTemplate template = getRoleVipTemplate(roleId);
		if(template == null)
			return 0;
		
		return template.getPrivilegeAlive();
	}

	@Override
	public float getPrivilegeRoleTankExpPercent(int roleId) {
		VipTemplate template = getRoleVipTemplate(roleId);
		if(template == null){
			return 0;
		}
		return template.getPrivilegeRoleTankExp();
	}

	@Override
	public float getPrivilegeIronPercent(int roleId) {
		VipTemplate template = getRoleVipTemplate(roleId);
		if(template == null){
			return 0;
		}
		
		return template.getPrivilegeIron();
	}

	@Override
	public float getPrivilegeIronMaxPercent(int roleId) {
		VipTemplate template = getRoleVipTemplate(roleId);
		if(template == null)
			return 0;
		
		return template.getPrivilegeIronMax();
	}

	@Override
	public float getPrivilegeTankExpPercent(int roleId) {
		VipTemplate template = getRoleVipTemplate(roleId);
		if(template == null)
			return 0;
		
		return template.getPrivilegeTankExp();
	}
	
	private VipTemplate getRoleVipTemplate(int roleId) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null)
			return null;
		
		return vipTemplates.get(account.getVipLevel());
	}

	@Override
	public void createUser(int roleId) {
		VipInfo info = vipDao.get(roleId);
		if(info == null) {
			info = new VipInfo();
			vipDao.save(roleId, info);
		}
	}

	@Override
	public VipTemplate getVipTemplate(int vipLvl) {
		return vipTemplates.get(vipLvl);
	}

}
