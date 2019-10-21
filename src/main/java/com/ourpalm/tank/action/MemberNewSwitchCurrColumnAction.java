package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBERNEW_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.CTS_MEMBER_NEW_CURR_COLUMN_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_STAR_UP_MSG;

/**
 * 新乘员，切换当前乘员栏
 * 
 * @author fhy
 *
 */
@Command(type = MEMBERNEW_MSG.CMD_TYPE.CMD_TYPE_MEMBERNEW_VALUE, id = MEMBERNEW_MSG.CMD_ID.CTS_MEMBER_NEW_CURR_COLUMN_VALUE)
public class MemberNewSwitchCurrColumnAction implements Action<CTS_MEMBER_NEW_CURR_COLUMN_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_NEW_CURR_COLUMN_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int column = reqMsg.getColumn();
		STC_MEMBER_NEW_STAR_UP_MSG.Builder builder = STC_MEMBER_NEW_STAR_UP_MSG.newBuilder();
		int result = 0;
		if (!(column != 1 && column != 2 && column != 3)) {
			result = 1;
			builder.setSuccess(result);
			connect.sendMsg(builder.build());
			return null;
		}

		RoleAccount account = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		account.setCurrMemberColumn(column);
		GameContext.getUserApp().saveRoleAccount(account);
		builder.setSuccess(result);
		connect.sendMsg(builder.build());
		// 主推一下
		GameContext.getMemberNewApp().columnUpdateSend(connect.getRoleId(), column);

		return null;
	}

}