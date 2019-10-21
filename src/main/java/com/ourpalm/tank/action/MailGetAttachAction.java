package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.Mail;
import com.ourpalm.tank.domain.MailAttach;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG;
import com.ourpalm.tank.message.MAIL_MSG.AwardItem;
import com.ourpalm.tank.message.MAIL_MSG.CTS_MAIL_GETATTACH_MSG;
import com.ourpalm.tank.message.MAIL_MSG.STC_MAIL_GETATTACH_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

/**
 * 获取邮件附件
 * @author admin
 *
 */
@Command(type = MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE,id = MAIL_MSG.CMD_ID.CTS_MAIL_GETATTACH_VALUE)
public class MailGetAttachAction implements Action<CTS_MAIL_GETATTACH_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MAIL_GETATTACH_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		STC_MAIL_GETATTACH_MSG.Builder builder = STC_MAIL_GETATTACH_MSG.newBuilder();
		String mailId = reqMsg.getMailId();
		Mail mail = GameContext.getMailApp().findMail(connect.getRoleId(), mailId);
		if(mail == null)
			return builder.setSuccess(0).setError("邮件不存在").setGold(0).setIron(0).build();
		
		
		if((System.currentTimeMillis() - mail.getCreatTime().getTime()) >= Mail.SAVETIME*1000L){
			GameContext.getMailApp().deleteMail(connect.getRoleId(), mail);
			return builder.setSuccess(0).setError("邮件已过期").setGold(0).setIron(0).build();
		}
		
		if (mail.isAttachget()) {
			builder.setSuccess(0);
			builder.setError("附件已领取");
			builder.setGold(0);
			builder.setIron(0);
			builder.setExp(0);
			builder.setHonor(0);
			builder.setDiamond(0);
		}else{
			mail.setAttachget(true);
			GameContext.getMailApp().save(mail);
			
			Map<Integer, MailAttach> attachs = mail.getAttach();
			Map<Integer,Integer> goodsMap = new HashMap<Integer,Integer>();
			for(MailAttach attach : attachs.values()){
				goodsMap.put(attach.getGoodsId(), attach.getCount());
			}
			GameContext.getGoodsApp().addGoods(connect.getRoleId(),goodsMap, OutputType.mailGetAttachInc.getInfo());
			
			List<AttrUnit> attrList = new ArrayList<>();
			if(mail.getGold() > 0)
				attrList.add(AttrUnit.build(RoleAttr.gold, Operation.add, mail.getGold()));
			if(mail.getIron() > 0)
				attrList.add(AttrUnit.build(RoleAttr.iron, Operation.add, mail.getIron()));
			if(mail.getExp() > 0)
				attrList.add(AttrUnit.build(RoleAttr.exp, Operation.add, mail.getExp()));
			
			if(mail.getHonor() > 0) {
				attrList.add(AttrUnit.build(RoleAttr.honor, Operation.add, mail.getHonor()));
			}
			if(mail.getTankExp() > 0) {
				attrList.add(AttrUnit.build(RoleAttr.tankExp, Operation.add, mail.getTankExp()));
			}
			if(mail.getDiamond() > 0) {
				attrList.add(AttrUnit.build(RoleAttr.diamonds, Operation.add, mail.getDiamond()));
			}
			
			GameContext.getUserAttrApp().changeAttribute(connect.getRoleId(), attrList, OutputType.mailGetAttachInc);
			
			builder.setSuccess(1);
			builder.setError("");
			builder.setGold(mail.getGold());
			builder.setIron(mail.getIron());
			builder.setExp(mail.getExp());
			builder.setHonor(mail.getHonor());
			builder.setTankExp(mail.getTankExp());
			builder.setDiamond(mail.getDiamond());
			for(int key : goodsMap.keySet()){
				AwardItem.Builder awardbuilder = AwardItem.newBuilder();
				awardbuilder.setGoodsId(key);
				awardbuilder.setCount(goodsMap.get(key));
				builder.addAwards(awardbuilder.build());
			}
		}
		return builder.build();
	}

}
