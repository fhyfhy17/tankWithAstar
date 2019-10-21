package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.Mail;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG;
import com.ourpalm.tank.message.MAIL_MSG.CTS_MAIL_DELETE_MSG;
import com.ourpalm.tank.message.MAIL_MSG.STC_MAIL_DELETE_MSG;

@Command(type = MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE,id = MAIL_MSG.CMD_ID.CTS_MAIL_DELETE_VALUE)
public class MailDeleteAction implements Action<CTS_MAIL_DELETE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_MAIL_DELETE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		String mailId = reqMsg.getMailId();
		Mail mail = GameContext.getMailApp().findMail(connect.getRoleId(), mailId);
		STC_MAIL_DELETE_MSG.Builder builder = STC_MAIL_DELETE_MSG.newBuilder();
		if(mail == null) {
			return builder.setSuccess(false).setMailId(mailId).setInfo("邮件不存在").build();
		}
		
		if(!mail.isRead()) {
			return builder.setSuccess(false).setMailId(mailId).setInfo("邮件未读取").build();
		}
		
		if(mail.getGold() > 0 || mail.getIron() > 0 || (mail.getAttach() != null && !mail.getAttach().isEmpty())) {
			if(!mail.isAttachget()) {
				return builder.setSuccess(false).setInfo("附件未领取").setMailId(mailId).build();
			}
		}
		
		boolean isDel = GameContext.getMailApp().deleteMail(connect.getRoleId(), mail);
		return builder.setSuccess(isDel).setMailId(mailId).build();
	}

}
