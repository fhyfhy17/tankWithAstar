package com.ourpalm.tank.app.prompt;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;

public class PromptAppImpl implements PromptApp {

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void prompt(int roleId, PROMPT promptType) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect == null) {
			return;
		}

		connect.sendMsg(STC_PROMPT_MSG.newBuilder().setPrompt(promptType).build());
	}
	

}
