package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBERNEW_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.CTS_MEMBER_NEW_UP_DOWN_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_UP_DOWN_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 新乘员，上阵下阵
 * 
 * @author fhy
 *
 */
@Command(type = MEMBERNEW_MSG.CMD_TYPE.CMD_TYPE_MEMBERNEW_VALUE, id = MEMBERNEW_MSG.CMD_ID.CTS_MEMBER_NEW_UP_DOWN_VALUE)
public class MemberNewUpAndDownAction implements Action<CTS_MEMBER_NEW_UP_DOWN_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_NEW_UP_DOWN_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		String uniqueId = reqMsg.getUniqueId();
		int type = reqMsg.getType();
		int column = reqMsg.getColumn();

		STC_MEMBER_NEW_UP_DOWN_MSG.Builder builder = STC_MEMBER_NEW_UP_DOWN_MSG.newBuilder();

		Result r = GameContext.getMemberNewApp().memerUpAndDown(connect.getRoleId(), uniqueId, builder, type, column);
		builder.setSuccess(r.getResult());
		builder.setInfo(r.getInfo());
		return null;
	}
}