package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_TANK_DEVELOP_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_DEVELOP_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 坦克研发
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_TANK_DEVELOP_VALUE)
public class DeveLopAction implements Action<CTS_TANK_DEVELOP_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_TANK_DEVELOP_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		if (account == null)
			return null;
		int tankTemplateId = reqMsg.getTankTemplateId();
		STC_TANK_DEVELOP_MSG.Builder builder = STC_TANK_DEVELOP_MSG.newBuilder();

		Result r = GameContext.getTankApp().developTank(connect.getRoleId(), tankTemplateId, builder);
		builder.setResult(r.getResult());
		builder.setDesc(r.getInfo());
		return builder.build();
	}
}
