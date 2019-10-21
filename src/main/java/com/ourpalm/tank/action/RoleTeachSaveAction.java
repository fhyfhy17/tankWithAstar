package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_SAVE_TEACH_MSG;


@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
	id = ROLE_MSG.CMD_ID.CTS_SAVE_TEACH_VALUE
)
public class RoleTeachSaveAction implements Action<CTS_SAVE_TEACH_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_SAVE_TEACH_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		role.setTeachId(reqMsg.getId());
		
		GameContext.getUserApp().saveRoleAccount(role);
		
		return null;
	}

}
