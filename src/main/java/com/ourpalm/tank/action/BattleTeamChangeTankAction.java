package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_CHANGE_TANK_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_TEAM_CHANGE_TANK_VALUE
)
public class BattleTeamChangeTankAction implements Action<CTS_TEAM_CHANGE_TANK_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_CHANGE_TANK_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final String teamId = reqMsg.getTeamId();
		final int roleId = connect.getRoleId();
		final int tankId = reqMsg.getTankId();
		
		String key = BATTLE_TEAM_LOCK + teamId;
		GameContext.getLock().lock(key);
		try{
			GameContext.getMatchApp().teamChangeTank(roleId, teamId, tankId);
		}finally{
			GameContext.getLock().unlock(key);
		}
		return null;
	}

}
