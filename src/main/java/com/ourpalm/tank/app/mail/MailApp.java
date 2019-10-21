package com.ourpalm.tank.app.mail;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.Mail;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MAIL_MSG.STC_MAIL_LIST_MSG;

public interface MailApp extends Service{
	
	/**
	 * 角色创建，初始化邮件
	 * @param roleId
	 */
	public void createUser(int roleId);
	/**
	 * 获取邮件列表
	 * @param reciverId
	 * @return
	 */
	public List<Mail> getMailList(int reciverId);
	
	/**
	 * 设置邮件为已读
	 * @param reciverId
	 * @param mailId
	 */
	public void setMailRead(int reciverId,String mailId);
	
	/**
	 * 删除邮件
	 * @param reciverId
	 * @param mailId
	 */
	public boolean deleteMail(int roleId, Mail mail);
	
	/**
	 * 删除所有邮件
	 * @param reciverId
	 */
	public void deleteAll(int reciverId);
	
	/**
	 * 查找邮件
	 * @param reciverId
	 * @param mailId
	 * @return
	 */
	public Mail findMail(int reciverId,String mailId);
	
	/**
	 * 构建邮件列表协议
	 * @param mails
	 * @return
	 */
	public STC_MAIL_LIST_MSG.Builder mailListBuilder(List<Mail> mails);
	
	/**
	 * 保存
	 * @param roleId
	 * @param list
	 */
	public void saveBatch(int roleId, List<Mail> list);
	
	/**
	 * 保存
	 * @param mail
	 */
	public void save(Mail mail);

	public void promit(RoleConnect connect);
	
	void sendMail(int roleId, String title, String content, int gold, int iron, int exp, int honor, Map<Integer, Integer> goods);
	
	List<Mail> getGmMailList(int roleId);
	
	public void login(int roleId);
}
