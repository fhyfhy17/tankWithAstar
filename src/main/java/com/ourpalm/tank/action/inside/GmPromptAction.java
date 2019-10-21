package com.ourpalm.tank.action.inside;

import java.util.Collection;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.GMTS_PROMPT_MSG;

@Command(
		type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
		id = SERV_MSG.CMD_ID.GMTS_PROMPT_VALUE
)
public class GmPromptAction implements Action<GMTS_PROMPT_MSG>{

	@Override
	public MessageLite execute(ActionContext context, GMTS_PROMPT_MSG reqMsg) {
		int roleId = reqMsg.getRoleId();
		int prompt = reqMsg.getPrompt();
		int areaId = reqMsg.getAreaId();
		String serviceId = reqMsg.getServiceId();
		
		STC_PROMPT_MSG.Builder builder = STC_PROMPT_MSG.newBuilder();
		builder.setPrompt(PROMPT.valueOf(prompt));
		
		if(roleId == -1) {
			Collection<RoleConnect> connectList = GameContext.getOnlineCenter().getAllRoleConnect();
			for(RoleConnect connect : connectList) {
				if(connect.getAreaId() != areaId)
					continue;
				
				if(!serviceId.equals("all")) {
					RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
					if(account == null)
						continue;
					
					if(!account.getServiceId().equals("") && !account.getServiceId().equals(serviceId)) {
						continue;
					}
				}
				
				connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, builder.build().toByteArray());
			}
		} else {
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
			if(connect != null) {
				connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, builder.build().toByteArray());
			}
		}
		
		return null;
	}

}
