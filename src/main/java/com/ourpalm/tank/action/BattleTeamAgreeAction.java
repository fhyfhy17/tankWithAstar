package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_AGREE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_REQ_FRIEND;
import com.ourpalm.tank.message.ROLE_MSG.STC_TIPS_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_TEAM_AGREE_VALUE
)
public class BattleTeamAgreeAction implements Action<CTS_TEAM_AGREE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_AGREE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		//判断是否在匹配中
		if(GameContext.getMatchApp().hasMatchQueue(context.getIoId())){
			return null;
		}
		
		String teamId = reqMsg.getTeamId();
		TEAM_REQ_FRIEND  teamReqFriendType= reqMsg.getTeamReqFriend();
		int roleId = connect.getRoleId();
		int reqRoleId = reqMsg.getReqRoleId();
		String result = null;
		String key = BATTLE_TEAM_LOCK + teamId;
		GameContext.getLock().lock(key);
		try{
			result = GameContext.getMatchApp().addTeam(roleId, teamId,teamReqFriendType,reqRoleId);
		}finally{
			GameContext.getLock().unlock(key);
		}
		
		if(!Util.isEmpty(result)){
			return STC_TIPS_MSG.newBuilder()
					.setTips(result)
					.build();
		}
		return null;
	}

}
