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
import com.ourpalm.tank.message.MAIL_MSG.STC_MAIL_GETALLATTACH_MSG;

@Command(type = MAIL_MSG.CMD_TYPE.CMD_TYPE_MAIL_VALUE,id = MAIL_MSG.CMD_ID.CTS_MAIL_GETALLATTACH_VALUE)
public class MailAttachAllAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		synchronized (connect) {
			int allgold = 0;
			int alliron = 0;
			int allexp  = 0;
			int allHonor = 0;
			int allTankExp = 0;
			int allDiamond = 0;
			HashMap<Integer, Integer> awards = new HashMap<Integer, Integer>();
			List<Mail> mails = GameContext.getMailApp().getMailList(connect.getRoleId());
			List<Mail> needSaveMails = new ArrayList<Mail>();
			for(Mail mail : mails){
				if (!mail.isAttachget()) {
					allgold += mail.getGold();
					alliron += mail.getIron();
					allexp += mail.getExp();
					allHonor += mail.getHonor();
					allTankExp += mail.getTankExp();
					allDiamond += mail.getDiamond();
					
					Map<Integer, Integer> goodsMap = new HashMap<Integer,Integer>();
					for(MailAttach attach : mail.getAttach().values()){
						goodsMap.put(attach.getGoodsId(), attach.getCount());
						if (awards.containsKey(attach.getGoodsId())) {
							awards.put(attach.getGoodsId(), attach.getCount()+awards.get(attach.getGoodsId()));
						}else{
							awards.put(attach.getGoodsId(), attach.getCount());
						}
					}
					GameContext.getGoodsApp().addGoods(context.getIoId(), goodsMap, OutputType.mailGetAllAttachInc.getInfo());
					needSaveMails.add(mail);
					mail.setAttachget(true);
				}
			}
			STC_MAIL_GETALLATTACH_MSG.Builder builder = STC_MAIL_GETALLATTACH_MSG.newBuilder();
			if (needSaveMails.size() == 0) {
				builder.setSuccess(0);
				builder.setError("没有附件可领取");
			}else{
				builder.setSuccess(1);
				builder.setError("");
				GameContext.getMailApp().saveBatch(connect.getRoleId(), needSaveMails);
			}
			builder.setGold(allgold);
			builder.setIron(alliron);
			builder.setExp(allexp);
			builder.setHonor(allHonor);
			builder.setTankExp(allTankExp);
			builder.setDiamond(allDiamond);
			for(int key : awards.keySet()){
				AwardItem.Builder awardBuilder = AwardItem.newBuilder();
				awardBuilder.setGoodsId(key);
				awardBuilder.setCount(awards.get(key));
				builder.addAwards(awardBuilder.build());
			}
			return builder.build();
			
		}
	}

}
