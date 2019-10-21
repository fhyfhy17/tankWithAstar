package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.CTS_REDMONEY_RECEIVE_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_REDMONEY_RECEIVE_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 红包领取
 * 
 * @author fhy
 *
 */
@Command(type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE, id = ACTIVITY_MSG.CMD_ID.CTS_REDMONEY_RECEIVE_VALUE)
public class RedMoneyReceiveAction implements Action<CTS_REDMONEY_RECEIVE_MSG> {
	private static final String RED_MONEY_KEY_LOCK = "RED_MONEY_KEY_LOCK";

	@Override
	public MessageLite execute(ActionContext context, CTS_REDMONEY_RECEIVE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = reqMsg.getRoleId();
		String uniqueId = reqMsg.getUnqueId();
		STC_REDMONEY_RECEIVE_MSG.Builder builder = STC_REDMONEY_RECEIVE_MSG.newBuilder();

		GameContext.getLock().lock(RED_MONEY_KEY_LOCK + uniqueId);
		Result r = GameContext.getRedMoneyApp().getGift(roleId, uniqueId, builder);
		GameContext.getLock().unlock(RED_MONEY_KEY_LOCK + uniqueId);
		builder.setSuccess(r.getResult() == (byte) 1 ? true : false);
		return builder.build();

	}

}
