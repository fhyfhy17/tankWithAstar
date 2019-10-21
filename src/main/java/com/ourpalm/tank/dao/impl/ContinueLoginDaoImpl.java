package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.tank.dao.ContinueLoginDao;
import com.ourpalm.tank.domain.ContinueLoginInfo;

public class ContinueLoginDaoImpl  extends AbstractJedisDao implements ContinueLoginDao {
	private static final String LOGIN_THREE_DAY = "SERVER_LOGIN_THREE_DAY_CODE_KEY_";
	private static final String LOGIN_SEVEN_DAY = "SERVER_LOGIN_SEVEN_DAY_CODE_KEY_";
	private static final String CONTINUE_LOGIN = "ROLE_CONTINUE_LOGIN_KEY_";
	
	
	@Override
	public ContinueLoginInfo getInfo(int roleId) {
		String json = getClient().get(CONTINUE_LOGIN + roleId);
		if(json == null)
			return null;
		
		return JSON.parseObject(json, ContinueLoginInfo.class);
	}

	@Override
	public void saveInfo(int roleId, ContinueLoginInfo info) {
		if(info == null)
			return;
		
		getClient().set(CONTINUE_LOGIN + roleId, JSON.toJSONString(info));
	}

	@Override
	public String randomThreeDayCode() {
		String code = getClient().srandmember(LOGIN_THREE_DAY);
		if(code == null)
			return null;
		
		getClient().srem(LOGIN_THREE_DAY, code);
		return code;
	}

	@Override
	public String randomSevenDayCode() {
		String code = getClient().srandmember(LOGIN_SEVEN_DAY);
		if(code == null)
			return null;
		
		getClient().srem(LOGIN_SEVEN_DAY, code);
		return code;
	}

}
