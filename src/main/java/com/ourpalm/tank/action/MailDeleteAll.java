package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG;

/**
 * 一键删除邮件
 * @author lizengcun
 *
 */
@Command(type = MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE,id = MAIL_MSG.CMD_ID.CTS_MAIL_DELETEALL_VALUE)
public class MailDeleteAll implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		GameContext.getMailApp().deleteAll(connect.getRoleId());
		connect.sendMsg(MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE, MAIL_MSG.CMD_ID.STC_MAIL_DELETEALL_VALUE, null);
		return null;
	}

}
