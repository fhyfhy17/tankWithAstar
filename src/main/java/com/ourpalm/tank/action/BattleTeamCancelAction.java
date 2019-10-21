package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_CANCEL_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_INFO_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_INFO_TYPE;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.vo.MatchTank;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_TEAM_CANCEL_VALUE
)
public class BattleTeamCancelAction implements Action<CTS_TEAM_CANCEL_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_CANCEL_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		String teamId = reqMsg.getTeamId();
		
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
			
			for(MatchTank matchTank : matchTeam.allTeamTank()){
				int roleId = matchTank.getRoleId();
				RoleConnect _connect = GameContext.getUserApp().getRoleConnect(roleId);
				if(_connect == null){
					continue;
				}
				//删除匹配IO
				GameContext.getMatchApp().removeIo(_connect.getIoId());
				
				//推送取消匹配
				_connect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_TEAM_CANCEL_VALUE, null);
				
				STC_TEAM_INFO_MSG msg = STC_TEAM_INFO_MSG.newBuilder()
						.setTeamId(matchTeam.getTeamId())
						.setWarType(WAR_TYPE.valueOf(matchTeam.getWarType()))
						.addAllFriends(GameContext.getMatchApp().getFriendItems(roleId))
						.addAllTanks(GameContext.getMatchApp().getTankTeamItems(matchTeam))
						.setLimitMin(matchTeam.getLimitMin())
						.setLimitMax(matchTeam.getLimitMax())
						.setLimitDesc(matchTeam.getLimitDesc())
						.setType(TEAM_INFO_TYPE.TEAM_CANCEL_TYPE)
						.addAllCorpsRoles(GameContext.getMatchApp().getCorpsRoleItems(roleId))
						.build();
				
				_connect.sendMsg(msg);
			}
			
		}finally{
			GameContext.getLock().unlock(key);
		}
		
		return null;
	}

}
