package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardState;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_BIND_TANK1_ACCOUNT_STATE_MSG;

/**
 * 创建账号
 */
@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_BIND_TANK1_ACCOUNT_STATE_VALUE
)
public class RoleBindTank1AccountStateAction implements Action<Object>{

	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		if(account == null)
			return null;
		
		STC_BIND_TANK1_ACCOUNT_STATE_MSG.Builder builder =  STC_BIND_TANK1_ACCOUNT_STATE_MSG.newBuilder();
		builder.setState(RewardState.INIT);

		if(!Util.isEmpty(account.getTank1Area()) || !Util.isEmpty(account.getTank1AccountName())) {
			builder.setState(RewardState.FINISH);
		}
		
		return builder.build();
	}

	
}
