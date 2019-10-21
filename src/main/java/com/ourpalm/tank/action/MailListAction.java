package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.Mail;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG;
import com.ourpalm.tank.message.MAIL_MSG.STC_MAIL_LIST_MSG;

@Command(
		type = MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE, 
		id = MAIL_MSG.CMD_ID.CTS_MAIL_LIST_VALUE
	)
public class MailListAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		List<Mail> mails = GameContext.getMailApp().getMailList(connect.getRoleId());
		List<Mail> gmMails = GameContext.getMailApp().getGmMailList(connect.getRoleId());
		mails.addAll(gmMails);
		STC_MAIL_LIST_MSG.Builder builder = GameContext.getMailApp().mailListBuilder(mails);
		return builder.build();
	}

}
