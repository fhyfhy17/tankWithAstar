package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.UserAttrDao;
import com.ourpalm.tank.domain.PayOrderInfo;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.domain.UserAttr;

public class UserAttrDaoImpl extends AbstractJedisDao implements UserAttrDao{

	private final static String USER_ATTR_KEY = "USER_ATTR_";	//玩家属性key
	private final static String ROLE_PAY_KEY = "ROLE_PAY_";		//角色支付对象KEY
	private final static String ROLE_ORDER_KEY = "ROLE_PAY_ORDER_";		//角色支付记录对象KEY
	
	
	@Override
	public void saveUserAttr(UserAttr attr) {
		String key = USER_ATTR_KEY + attr.getRoleId();
		client.set(key, JSON.toJSONString(attr));
	}

	@Override
	public UserAttr getUserAttr(int roleId) {
		String str = client.get(USER_ATTR_KEY + roleId);
		return JSON.parseObject(str, UserAttr.class);
	}
	
	@Override
	public void saveRolePay(RolePay rolePay){
		String key = ROLE_PAY_KEY + rolePay.getRoleId();
		client.set(key, JSON.toJSONString(rolePay));
	}

	@Override
	public RolePay getRolePay(int roleId){
		String str = client.get(ROLE_PAY_KEY + roleId);
		if(Util.isEmpty(str)){
			RolePay rolePay = new RolePay();
			rolePay.setRoleId(roleId);
			this.saveRolePay(rolePay);
			return rolePay;
		}
		return JSON.parseObject(str, RolePay.class);
	}

	@Override
	public void saveOrder(PayOrderInfo order) {
		if(order == null)
			return;
		
		getClient().lpush(ROLE_ORDER_KEY + order.getRoleId(), JSON.toJSONString(order));
	}
	
}
