package com.ourpalm.tank.action.inside;

import java.util.Date;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SERV_MSG;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.ITS_SYS_REFRESH_VALUE
)
public class RoleRefreshAction implements Action<Object>{
	
	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();

		// 目前由心跳超时来处理缓存,所有需判断下此玩家是否还在本地
		RoleConnect roleConnect = GameContext.getUserApp().getRoleConnect(roleId);
		if (roleConnect == null) {
			return null;
		}

		// IO不同或不在本地都不处理
		if (roleConnect.getIoId() != connect.getIoId()
				|| !roleConnect.getNodeName().equals(GameContext.getLocalNodeName())) {
			return null;
		}
		Date now = new Date();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		boolean nextDayLogin = !DateUtil.isSameDay(now, role.getDailyFlushDate());
		if (nextDayLogin) {
			role.setDailyFlushDate(now);
			// 清除每日战斗累计银币数
			role.setBattleRewardIron(0);
		}
		
		role.setLastLoginDate(role.getLoginDate());
		role.setLoginDate(now);
		GameContext.getUserApp().saveRoleAccount(role);

		try {
			// 刷新每日任务
			GameContext.getQuestApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		try {
			// 抽奖次数重置
			GameContext.getMemberApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 赛季更新
		try {
			GameContext.getRoleArmyTitleApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 活动
		try {
			GameContext.getActivityApp().refreshAM0(roleId, nextDayLogin, role.getServiceId());
		} catch (Exception e) {
			logger.error("", e);
		}

		// 送激活码
		try {
			GameContext.getContinueLoginApp().login(roleId);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 坦克疲劳度
		try {
			GameContext.getTankApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 更新免费复活次数
		try {
			GameContext.getBattleApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}
//		// 更新蓝钻每日奖励
//		try {
//			GameContext.getBlueApp().refreshBlue(roleId, nextDayLogin);
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//		// 更新QQ大厅每日奖励
//		try {
//			GameContext.getQqHallApp().refresh(roleId);
//		} catch (Exception e) {
//			logger.error("", e);
//		}
		return null;
	}

}
