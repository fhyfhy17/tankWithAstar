package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.vo.AbstractInstance;

@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
	id = BATTLE_MSG.CMD_ID.CTS_HANGUP_VALUE
)
public class BattleHandupAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		final int roleId = connect.getRoleId();
		RoleBattle roleBattle = GameContext.getMatchApp().getLocalRoleBattle(roleId);
		if(roleBattle == null){
			return null;
		}
		
		AbstractInstance tank = GameContext.getTankApp().getInstance(roleBattle.getTankInstanceId());
		if(tank == null){
			return null;
		}
		
		//新手第一局忽略挂机状态
//		RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
//		if(roleWarInfo == null
//				|| (roleWarInfo.getBattleWinCount() + roleWarInfo.getBattleLostCount()) == 0){
//			return null;
//		}
		
		
		tank.setHandup(tank.getHandup() + 1);
		
		return null;
	}

}
