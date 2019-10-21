package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_INFO_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_INFO_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_INFO_TYPE;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.template.ArmyTitleTemplate;


@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_TEAM_INFO_VALUE
)
public class BattleTeamInfoAction implements Action<CTS_TEAM_INFO_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_INFO_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		final int roleId = connect.getRoleId();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		
		final String teamId = role.getTeamId();
		if(Util.isEmpty(teamId)){
			return null;
		}
		TEAM_INFO_TYPE type =reqMsg.getType();
		
		String key = BATTLE_TEAM_LOCK + teamId;
		GameContext.getLock().lock(key);
		try{
			MatchTeam matchTeam = GameContext.getMatchApp().getMatchTeam(teamId);
			if(matchTeam == null){
				return null;
			}
			//重置战斗开始标记
			if(matchTeam.isHadBegin()){
				matchTeam.setHadBegin(false);
				GameContext.getMatchApp().saveMatchTeam(matchTeam);
			}
			
			if(matchTeam.getWarType() == WAR_TYPE.RANK_VALUE){
				ArmyTitleTemplate template = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(role.getCurrTitleId());
				matchTeam.setLimitMin(template.getTeamMatchMin());
				matchTeam.setLimitMax(template.getTeamMatchMax());
				matchTeam.setLimitDesc(template.getMatchDesc());
			}
			
			STC_TEAM_INFO_MSG msg = STC_TEAM_INFO_MSG.newBuilder()
					.setTeamId(matchTeam.getTeamId())
					.setWarType(WAR_TYPE.valueOf(matchTeam.getWarType()))
					.addAllFriends(GameContext.getMatchApp().getFriendItems(roleId))
					.addAllTanks(GameContext.getMatchApp().getTankTeamItems(matchTeam))
					.setLimitMin(matchTeam.getLimitMin())
					.setLimitMax(matchTeam.getLimitMax())
					.setLimitDesc(matchTeam.getLimitDesc())
					.addAllCorpsRoles(GameContext.getMatchApp().getCorpsRoleItems(roleId))
					.setType(type)
					.build();
			return msg;
		}finally{
			GameContext.getLock().unlock(key);
		}
	}

}
