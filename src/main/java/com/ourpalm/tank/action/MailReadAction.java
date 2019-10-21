package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG;
import com.ourpalm.tank.message.MAIL_MSG.CTS_MAIL_READ_MSG;

/**
 * 邮件读取
 * @author LIZENGCUN
 */
@Command(type = MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE,id = MAIL_MSG.CMD_ID.CTS_MAIL_READ_VALUE)
public class MailReadAction implements Action<CTS_MAIL_READ_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MAIL_READ_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		String mailId = reqMsg.getMailId();
		GameContext.getMailApp().setMailRead(connect.getRoleId(), mailId);
		return null;
	}


}
