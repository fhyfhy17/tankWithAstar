package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_REFUSE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_REFUSE_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
	id = MATCH_MSG.CMD_ID.CTS_TEAM_REFUSE_VALUE
)
public class BattleTeamRefuseAction implements Action<CTS_TEAM_REFUSE_MSG>{
	
	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_REFUSE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		
		//给邀请者发送被拒绝消息
		STC_TEAM_REFUSE_MSG refuseMsg = STC_TEAM_REFUSE_MSG.newBuilder()
				.setInfo(role.getRoleName() + " 拒绝了您的邀请\n(可能该玩家设置了拒绝组队)")
				.build();
		
		final int tagerRoleId = reqMsg.getReqRoleId();
		RoleConnect targetConnect = GameContext.getUserApp().getRoleConnect(tagerRoleId);
		if(targetConnect != null){
			targetConnect.sendMsg(refuseMsg);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("玩家 roleId = {} 拒绝了 roleId = {}的邀请...", roleId, tagerRoleId);
		}
		
		return null;
	}

}
