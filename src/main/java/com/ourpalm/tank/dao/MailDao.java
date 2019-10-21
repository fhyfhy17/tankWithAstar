package com.ourpalm.tank.dao;

import java.util.List;
import java.util.Map;

import com.ourpalm.tank.domain.GmMail;
import com.ourpalm.tank.domain.Mail;

public interface MailDao {

	public void save(Mail mail);
	
	public List<Mail> getMails(int roleId);
	
	public Mail find(int receiverId,String mailId);
	
	public void deleteMail(int receiverId,String mailId);
	
	/**
	 * 批量保存/更新某个玩家的邮件
	 * @param reciverId
	 * @param mails
	 */
	public void saveBatch(int reciverId,List<Mail> mails);
	
	public Map<Integer,  GmMail> getGmMails(int areaId);
	
	public List<Integer> getGmMailIdList(int roleId);
	
	void saveGmMailIdList(int roleId, List<Integer> ids);
}
