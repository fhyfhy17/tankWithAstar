package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_CREATE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_INFO_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_INFO_TYPE;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_TEAM_CREATE_VALUE
)
public class BattleCreateTeamAction implements Action<CTS_TEAM_CREATE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_CREATE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		int warType = reqMsg.getWarType().getNumber();
		
		//创建队伍
		MatchTeam matchTeam = GameContext.getMatchApp().createTeam(roleId, warType);
		STC_TEAM_INFO_MSG msg = STC_TEAM_INFO_MSG.newBuilder()
				.setTeamId(matchTeam.getTeamId())
				.setWarType(reqMsg.getWarType())
				.addAllFriends(GameContext.getMatchApp().getFriendItems(roleId))
				.addAllTanks(GameContext.getMatchApp().getTankTeamItems(matchTeam))
				.setLimitMin(matchTeam.getLimitMin())
				.setLimitMax(matchTeam.getLimitMax())
				.setLimitDesc(matchTeam.getLimitDesc())
				.setType(TEAM_INFO_TYPE.TEAM_CREATE_TYPE)
				.addAllCorpsRoles(GameContext.getMatchApp().getCorpsRoleItems(roleId))
			
				.build();
		
		return msg;
	}

}
