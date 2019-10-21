package com.ourpalm.tank.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_ROLE_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_ROLE_INFO_MSG;

@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_ROLE_INFO_VALUE)
public class RoleInfoAction implements Action<CTS_ROLE_INFO_MSG> {
	protected Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_ROLE_INFO_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = reqMsg.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null) {
			return null;
		}
		RoleWarInfo warInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
		if (warInfo == null) {
			return null;
		}
		RoleTank tank = GameContext.getTankApp().getRoleTank(roleId, account.getMainTankId());
		if(tank == null){
			return null;
		}
		UserAttr userAttr = GameContext.getUserAttrApp().getUserAttr(roleId);
		if(userAttr == null){
			return null;
		}
		
		DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		
		STC_ROLE_INFO_MSG.Builder builder = STC_ROLE_INFO_MSG.newBuilder();
		builder.setName(account.getRoleName());
		builder.setLevel(account.getLevel());
		builder.setCurrArmyTitle(account.getCurrTitleId());
		builder.setSeasonMaxArmyTitle(account.getSeasonMaxTitleId());
		builder.setMaxArmyTitle(account.getMaxTitleId());
		builder.setRoleId(roleId);
		builder.setVipLvl(account.getVipLevel());
		builder.setMvpCout(warInfo.getRankMvp());
		builder.setMainTankId(account.getMainTankId());
		builder.setBattleScore(GameContext.getTankApp().calcAllBattleScore(roleId, account.getMainTankId()));
		
		PFUserInfo pfUserInfo = GameContext.getUserApp().getPfUserInfo(roleId);
		if( pfUserInfo != null ){
			logger.info(" [PF_TEST] Role Info Action pfUserInfo {} } ", JSON.toJSONString(pfUserInfo) );
			builder.setPfUserInfo(JSON.toJSONString(pfUserInfo));
		} else {
			builder.setPfUserInfo("");
		}
		
		int enterCount = tank.getLostCount() + tank.getWinCount();
		builder.setEnterCount(enterCount);
		builder.setWinPro(0);
		if(enterCount > 0){
			float pro = (float)tank.getWinCount() / (float)enterCount * 100f;
			builder.setWinPro((int)pro);
		}
		builder.setMaxHelper(warInfo.getRankMaxHelperCount());
		builder.setMaxOut(warInfo.getRankMaxOutputHurt());
		builder.setMaxBear(warInfo.getRankMaxInputHurt());
		builder.setMaxSeriesWin(warInfo.getRankMaxLoopWinCount());
		builder.setSingleMaxKill(warInfo.getRankMaxKillCount());
		builder.setMaxOneLifeKill(warInfo.getRankMaxOneLifeKillCount());
		builder.setCreateTime(getFormatTime(format, account.getCreateDate()));
		builder.setLastLoginTime(getFormatTime(format, account.getLastLoginDate()));
		builder.setExp(userAttr.getExp());
		builder.setHadElite(false);
		
		return builder.build();
	}
	
	private String getFormatTime(DateFormat format, Date date){
		if (date == null) {
			return "";
		}
		return format.format(date);
	}
}
