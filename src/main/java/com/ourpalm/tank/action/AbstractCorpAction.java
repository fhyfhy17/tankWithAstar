package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG.CMD_ID;
import com.ourpalm.tank.message.CORPS_MSG.CMD_TYPE;

/**
 * 抽象的军团 Action，包含验证玩家是否处于军团的方法
 */
public abstract class AbstractCorpAction {

	protected Logger logger = LogCore.runtime;

	/**
	 * 当且仅当玩家处于军团时返回 false
	 *
	 * @param  role 玩家
	 * @return      玩家是否不处于军团
	 */
	protected boolean checkNotInCorp(int roleId) {
		
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		if(corpsId <= 0){
			return true;
		}
		
		CorpsRole corpsRole = GameContext.getCorpsApp().getCorpsRole(corpsId, roleId);
		if (corpsRole != null) {
			// 玩家在军团内
			return false;
		}
		// 自己已不在军团内，需要设置军团id并通知客户端
		fixCorpsIdIfAbsent(roleId);

		return true;
	}

	/**
	 * 修正服务器上玩家的军团 id 并通知客户端
	 *
	 * @param role 玩家
	 */
	private void fixCorpsIdIfAbsent(int roleId) {
		
		GameContext.getUserApp().saveCorpsId(roleId, 0);

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(
					CMD_TYPE.CMD_TYPE_CORPS_VALUE,
					CMD_ID.STC_CORPS_NO_IN_CORP_VALUE,
					null);
		}
	}
}
