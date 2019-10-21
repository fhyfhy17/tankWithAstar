package com.ourpalm.tank.app.mail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.MailDao;
import com.ourpalm.tank.domain.GmMail;
import com.ourpalm.tank.domain.Mail;
import com.ourpalm.tank.domain.MailAttach;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG.MailContent;
import com.ourpalm.tank.message.MAIL_MSG.MailGoodsItem;
import com.ourpalm.tank.message.MAIL_MSG.STC_MAIL_LIST_MSG;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.InitMailTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;

public class MailAppImpl implements MailApp{
	private static final Logger logger = LogCore.mail;
	
	private MailDao mailDao;
	
	private List<InitMailTemplate> initMailTemplateList;
	
	@Override
	public void start() {
		loadInitMailTemplate();
	}

	@Override
	public void stop() {
		
	}
	
	public void loadInitMailTemplate() {
		String sourceFile = XlsSheetType.UserInitMailTemplate.getXlsFileName();
		String sheetName = XlsSheetType.UserInitMailTemplate.getSheetName();
		try {
			initMailTemplateList = XlsPojoUtil.sheetToList(sourceFile, sheetName, InitMailTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}
	
	
	@Override
	public void createUser(int roleId) {
		if(!Util.isEmpty(initMailTemplateList)) {
			List<Mail> mailList = new ArrayList<>();
			for(InitMailTemplate t : initMailTemplateList) {
				if(Util.isEmpty(t.getTitle()))
					continue;
				
				if(t.getContent() == null)
					t.setContent("");
				
				String mailId = GameContext.getIdFactory().nextStr();
				Mail mail = new Mail(mailId);
				mail.setReciverId(roleId);
				mail.setTitle(t.getTitle());
				mail.setContent(t.getContent());
				mail.setAttachget(false);
				
				mail.setGold(t.getGold());
				mail.setIron(t.getIron());
				mail.setExp(t.getExp());
				if (!Util.isEmpty(t.getGoods())) {
					String[] goodsINfos = t.getGoods().split(";");
					for(String info : goodsINfos){
						mail.addAttach(Integer.parseInt(info.split(",")[0]), Integer.parseInt(info.split(",")[1]));
					}
				}
				mailList.add(mail);
			}
			
			GameContext.getMailApp().saveBatch(roleId, mailList);
		}
	}
	
	/**
	 * 获取邮件列表
	 * @param reciverId
	 * @return
	 */
	public List<Mail> getMailList(int reciverId){
		List<Mail> mails = mailDao.getMails(reciverId);
		Iterator<Mail> mailit = mails.iterator();
		while(mailit.hasNext()){
			Mail mail = mailit.next();
			if(mail.getCreatTime() == null) {
				continue;
			}
			
			if((System.currentTimeMillis() - mail.getCreatTime().getTime()) >= Mail.SAVETIME*1000L){
				mailit.remove();
				deleteMail(reciverId, mail);
			}
		}
		return mails;
	}
	
	/**
	 * 查找邮件
	 * @param reciverId
	 * @param mailId
	 * @return
	 */
	@Override
	public Mail findMail(int reciverId,String mailId){
		return mailDao.find(reciverId, mailId);
	}
	
	/**
	 * 设置邮件为已读
	 * @param reciverId
	 * @param mailId
	 */
	public void setMailRead(int reciverId,String mailId){
		Mail mail = mailDao.find(reciverId, mailId);
		mail.setRead(true);
		mailDao.save(mail);
	}
	
	/**
	 * 删除邮件
	 * @param reciverId
	 * @param mailId
	 */
	public boolean deleteMail(int roleId, Mail mail){
		mailDao.deleteMail(roleId, mail.getId());
		if(logger.isDebugEnabled()) {
			logger.debug("roleId: {} 删除邮件：{}", roleId, JSON.toJSONString(mail));
		}
		return true;
	}
	
	/**
	 * 删除所有邮件
	 * @param reciverId
	 */
	public void deleteAll(int reciverId){
		List<Mail> mails = mailDao.getMails(reciverId);
		Iterator<Mail> mailit = mails.iterator();
		while(mailit.hasNext()){
			Mail mail = mailit.next();
			if((System.currentTimeMillis() - mail.getCreatTime().getTime()) >= Mail.SAVETIME*1000L){
				deleteMail(reciverId, mail);
				continue;
			}
			if(mail.isRead() && mail.isAttachget() ){
				deleteMail(reciverId, mail);
			}
		}
		List<Mail> newmails = getMailList(reciverId);
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(reciverId);
		if (connect!=null) {//同步客户端邮件列表
			connect.sendMsg(mailListBuilder(newmails).build());
		}
	}
	
	public STC_MAIL_LIST_MSG.Builder mailListBuilder(List<Mail> mails){
		DateFormat dateformat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		STC_MAIL_LIST_MSG.Builder builder = STC_MAIL_LIST_MSG.newBuilder();
		boolean hasAttach = false;//是否有可领取附件
		boolean hasDelete = false;//是否有可删除附件
		for(Mail mail : mails){
			if(mail.getCreatTime() == null)
				continue;
			
			MailContent.Builder contentBuilder = MailContent.newBuilder();
			contentBuilder.setId(mail.getId()).setTitle(mail.getTitle()).setContent(mail.getContent());
			String creattime = dateformat.format(mail.getCreatTime());
			contentBuilder.setCreattime(creattime);
			contentBuilder.setDeletetime(Mail.SAVETIME - (int)((System.currentTimeMillis() - mail.getCreatTime().getTime())/1000L));
			contentBuilder.setIsread(mail.isRead());
			contentBuilder.setGold(mail.getGold());
			contentBuilder.setIron(mail.getIron());
			contentBuilder.setExp(mail.getExp());
			contentBuilder.setHonor(mail.getHonor());
			contentBuilder.setTankExp(mail.getTankExp());
			contentBuilder.setDiamond(mail.getDiamond());
			if (!hasAttach && (mail.getGold() > 0 || mail.getIron() > 0 
					|| mail.getExp() > 0 || mail.getHonor() > 0 || mail.getTankExp() > 0 || mail.getDiamond() > 0
					|| (!mail.isAttachget() && !mail.getAttach().isEmpty()))) {
				hasAttach = true;
			}
			if (!hasDelete && mail.isRead() && (mail.isAttachget() || mail.getAttach().isEmpty())) {
				hasDelete = true;
			}
			for(MailAttach attach : mail.getAttach().values()){
				
				MailGoodsItem.Builder goodsBuilder = MailGoodsItem.newBuilder();
				goodsBuilder.setGoodsId(attach.getGoodsId());
				goodsBuilder.setNum(attach.getCount());
				contentBuilder.addGoodsItem(goodsBuilder.build());
			}
			contentBuilder.setGetattach(mail.isAttachget());
			builder.addMails(contentBuilder.build());
		}
		builder.setHaveAttach(hasAttach);
		builder.setHaveDelete(hasDelete);
		return builder;
	}
	
	public void saveBatch(int roleId, List<Mail> list) {
		if(Util.isEmpty(list)) {
			return;
		}
		
		mailDao.saveBatch(roleId, list);
	}

	public void setMailDao(MailDao mailDao) {
		this.mailDao = mailDao;
	}

	@Override
	public void save(Mail mail) {
		if(mail == null){
			return;
		}
		if(mail.getReciverId() <= 0){
			new NullPointerException("邮件接收者为空...");
		}
		mailDao.save(mail);
	}

	@Override
	public void promit(RoleConnect connect) {
		List<Mail> mails = getMailList(connect.getRoleId());
		List<Mail> gmMails = GameContext.getMailApp().getGmMailList(connect.getRoleId());
		mails.addAll(gmMails);
		
		if(mails != null && !mails.isEmpty()) {
			boolean unread = false;
			for(Mail mail : mails) {
				if(!mail.isRead()) {
					unread = true;
					break;
				}
				
				if(mail.getGold() > 0 || mail.getIron() > 0 || mail.getExp() > 0 || (mail.getAttach() != null && !mail.getAttach().isEmpty())) {
					if(!mail.isAttachget()) {
						unread = true;
						break;
					}
				}
				
			}
			
			if(unread) {
				STC_PROMPT_MSG.Builder builder = STC_PROMPT_MSG.newBuilder();
				builder.setPrompt(PROMPT.MAIL);
				connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, builder.build().toByteArray());
			}
		}
	}

	@Override
	public void sendMail(int roleId, String title, String content, int gold, int iron, int exp, int honor,
			Map<Integer, Integer> goods) {
		
		if(title == null || title.equals(""))
			return;
		
		if(content == null)
			content = "";
		
		String mailId = GameContext.getIdFactory().nextStr();
		Mail mail = new Mail(mailId);
		mail.setReciverId(roleId);
		mail.setTitle(title);
		mail.setContent(content);
		mail.setAttachget(false);
		
		mail.setGold(gold);
		mail.setIron(iron);
		mail.setExp(exp);
		mail.setHonor(honor);
		
		if (!Util.isEmpty(goods)) {
			for(Integer goodsId : goods.keySet()) {
				mail.addAttach(goodsId, goods.get(goodsId));
			}
		}
		save(mail);
	}

	@Override
	public List<Mail> getGmMailList(int roleId) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		int areaId = account.getAreaId();
		String serviceId = account.getServiceId();
		
		Map<Integer, GmMail> gmMails = mailDao.getGmMails(areaId);
		List<Integer> getGmMailIdList = mailDao.getGmMailIdList(roleId);
		//去掉过期id
		Iterator<Integer> it = getGmMailIdList.iterator();
		while(it.hasNext()) {
			Integer id = it.next();
			if(!gmMails.containsKey(id)) {
				it.remove();
			}
		}
		
		List<Mail> result = new ArrayList<>();
		
		long curTime = System.currentTimeMillis();
		for(GmMail gmail : gmMails.values()) {
			if(!gmail.isToNewPlayer()) {
				long playerCreateTime = account.getCreateDate().getTime();
				if(playerCreateTime > gmail.getCreateTime()) {
					continue;
				}
			}
			
			//已经获得了
			if(getGmMailIdList.contains(gmail.getId()))
				continue;
			
			//过期了
			if(curTime > gmail.getExpireTime())
				continue;
			
			if(!gmail.getServiceId().equals("-1")) {
				if(!Util.isEmpty(serviceId) && !serviceId.equals(gmail.getServiceId()))
					continue;
			}
			
			String mailId = GameContext.getIdFactory().nextStr();
			Mail mail = gmail.getMail();
			mail.setId(mailId);
			mail.setReciverId(roleId);
			mail.setCreatTime(new Date());
			mailDao.save(mail);
			
			getGmMailIdList.add(gmail.getId());
			mailDao.saveGmMailIdList(roleId, getGmMailIdList);
			
			result.add(mail);
		}
		
		return result;
	}

	@Override
	public void login(int roleId) {
			RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
			if(connect != null) {
				promit(connect);
			}
	}
}
