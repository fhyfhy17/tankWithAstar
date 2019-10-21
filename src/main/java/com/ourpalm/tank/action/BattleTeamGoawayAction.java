package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_GOAWAY_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_INCOME_ADD_NUM_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_TEAM_GOAWAY_VALUE
)
public class BattleTeamGoawayAction implements Action<CTS_TEAM_GOAWAY_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_GOAWAY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		final String teamId = reqMsg.getTeamId();
		final int goawayRoleId = reqMsg.getRoleId();
		
		String key = BATTLE_TEAM_LOCK + teamId;
		GameContext.getLock().lock(key);
		try{
			GameContext.getMatchApp().teamQuit(goawayRoleId);
		}finally{
			GameContext.getLock().unlock(key);
		}
		//给发起者发组队收益0
		STC_TEAM_INCOME_ADD_NUM_MSG.Builder teamIncomeBuilder=STC_TEAM_INCOME_ADD_NUM_MSG.newBuilder();
		teamIncomeBuilder.setNum(0);
		connect.sendMsg(teamIncomeBuilder.build());
		//给被踢人发送消息
		RoleConnect goawayConnect = GameContext.getUserApp().getRoleConnect(goawayRoleId);
		if(goawayConnect != null){
			goawayConnect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_TEAM_GOAWAY_VALUE, null);
		}
		
		return null;
	}

}
