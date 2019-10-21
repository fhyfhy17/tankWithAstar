package com.ourpalm.tank.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.MailDao;
import com.ourpalm.tank.domain.GmMail;
import com.ourpalm.tank.domain.Mail;

public class MailDaoImpl extends AbstractJedisDao implements MailDao{
	
	private static final String KEY = "ROLE_MAIL_";
	private static final String ONE_AREA_KEY = "ROLE_MAIL_ONE_AREA_KEY_";
	private static final String GET_GM_MAIL_KEY = "ROLE_GET_GM_MAIL_KEY_";

	
	public void save(Mail mail){
		//容错处理
		if(Util.isEmpty(mail.getId())){
			String newId = GameContext.getIdFactory().nextStr();
			mail.setId(newId);
		}
		getClient().hset(KEY + mail.getReciverId(), mail.getId() + "", JSON.toJSONString(mail));
	}
	
	/**
	 * 批量保存/更新某个玩家的邮件
	 * @param reciverId
	 * @param mails
	 */
	public void saveBatch(int reciverId,List<Mail> mails){
		if (mails.size() == 0) {
			return;
		}
		Map<String, String> value = new HashMap<String, String>();
		for(Mail mail : mails){
			//容错处理
			if(Util.isEmpty(mail.getId())){
				String newId = GameContext.getIdFactory().nextStr();
				mail.setId(newId);
			}
			value.put(mail.getId(), JSON.toJSONString(mail));
		}
		getClient().hmSet(KEY + reciverId, value);
	}
	
	@Override
	public List<Mail> getMails(int roleId) {
		Map<String, String> jsonMap = getClient().hgetAll(KEY + roleId);
		if (Util.isEmpty(jsonMap)) {
			return new ArrayList<Mail>();
		}

		List<Mail> result = new ArrayList<Mail>();
		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			result.add(JSON.parseObject(entry.getValue(), Mail.class));
		}
		return result;
	}
	
	@Override
	public Mail find(int receiverId,String mailId){
		if(getClient().hexiste(KEY+receiverId, mailId)){
			return JSON.parseObject(getClient().hget(KEY+receiverId, mailId),Mail.class);
		}else{
			return null;
		}
	}
	
	@Override
	public void deleteMail(int receiverId,String mailId){
		if(getClient().hexiste(KEY+receiverId, mailId)){
			getClient().hdel(KEY+receiverId, mailId);
		}
	}

	@Override
	public Map<Integer, GmMail> getGmMails(int areaId) {
		Map<Integer, GmMail> result = new HashMap<>();
		Map<String, String> jsonMap = getClient().hgetAll(ONE_AREA_KEY + areaId);
		if (Util.isEmpty(jsonMap)) {
			return result;
		}
		
		for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
			result.put(Integer.parseInt(entry.getKey()), JSON.parseObject(entry.getValue(), GmMail.class));
		}
		return result;
	}

	@Override
	public List<Integer> getGmMailIdList(int roleId) {
		List<Integer> gmMailIdList = new ArrayList<>();
		String json = getClient().get(GET_GM_MAIL_KEY + roleId);
		if(json == null) {
			return gmMailIdList;
		}
		List<Integer> jsonList = JSON.parseObject(json, List.class);
		for(Integer id : jsonList) {
			gmMailIdList.add(id);
		}
		
		return gmMailIdList;
	}

	@Override
	public void saveGmMailIdList(int roleId, List<Integer> ids) {
		if(ids == null)
			return;
		getClient().set(GET_GM_MAIL_KEY + roleId, JSON.toJSONString(ids));
	}
	

}
