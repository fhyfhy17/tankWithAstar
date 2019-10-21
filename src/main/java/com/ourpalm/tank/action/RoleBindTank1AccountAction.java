package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._107_BindTank1AccountLogic;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_BIND_TANK1_ACCOUNT_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_BIND_TANK1_ACCOUNT_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 创建账号
 */
@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_BIND_TANK1_ACCOUNT_VALUE
)
public class RoleBindTank1AccountAction implements Action<CTS_BIND_TANK1_ACCOUNT_MSG>{

	
	@Override
	public MessageLite execute(ActionContext context, CTS_BIND_TANK1_ACCOUNT_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		String area = reqMsg.getArea();
		String accountName = reqMsg.getAccountName();
		
		_107_BindTank1AccountLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.bindTank1Account);
		Result result = Result.newFailure("活动不存在");
		if(logic != null) {
			result = logic.bind(connect.getRoleId(), area, accountName);
		}
		STC_BIND_TANK1_ACCOUNT_MSG.Builder builder =  STC_BIND_TANK1_ACCOUNT_MSG.newBuilder();
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		return builder.build();
	}

	
}
