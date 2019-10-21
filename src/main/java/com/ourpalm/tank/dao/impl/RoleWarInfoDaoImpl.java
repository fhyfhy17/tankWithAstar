package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.RoleWarInfoDao;
import com.ourpalm.tank.domain.RoleWarInfo;

public class RoleWarInfoDaoImpl extends AbstractJedisDao implements RoleWarInfoDao{

	private final static String KEY = "ROLE_WAR_INFO_";

	
	@Override
	public RoleWarInfo get(int roleId) {
		String strWar = client.get(KEY + roleId);
		if(Util.isEmpty(strWar)){
			RoleWarInfo warInfo = new RoleWarInfo();
			warInfo.setRoleId(roleId);
			return warInfo;
		}
		return JSON.parseObject(strWar, RoleWarInfo.class);
	}
	

	@Override
	public void save(RoleWarInfo roleWarInfo) {
		client.set(KEY + roleWarInfo.getRoleId(), JSON.toJSONString(roleWarInfo));
	}
	
	
}
