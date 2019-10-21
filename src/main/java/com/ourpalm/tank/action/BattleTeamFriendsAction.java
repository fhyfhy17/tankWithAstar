package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.FriendItem;
import com.ourpalm.tank.message.MATCH_MSG.STC_MATCH_FRI_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_MATCH_FRI_VALUE
)
public class BattleTeamFriendsAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		final int roleId = connect.getRoleId();
		
		List<FriendItem> friendItems = GameContext.getMatchApp().getFriendItems(roleId);
		
		STC_MATCH_FRI_MSG resp = STC_MATCH_FRI_MSG.newBuilder()
				.addAllFriends(friendItems)
				.build();
		
		return resp;
	}

}
