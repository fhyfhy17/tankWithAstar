package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBERNEW_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.CTS_MEMBER_NEW_LIST_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_LIST_MSG;

/**
 * 新乘员，列表
 * 
 * @author fhy
 *
 */
@Command(type = MEMBERNEW_MSG.CMD_TYPE.CMD_TYPE_MEMBERNEW_VALUE, id = MEMBERNEW_MSG.CMD_ID.CTS_MEMBER_NEW_LIST_VALUE)
public class MemberNewListAction implements Action<CTS_MEMBER_NEW_LIST_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_NEW_LIST_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		STC_MEMBER_NEW_LIST_MSG.Builder builder = STC_MEMBER_NEW_LIST_MSG.newBuilder();
		GameContext.getMemberNewApp().getMemberList(connect.getRoleId(), builder);
		return null;
	}
}