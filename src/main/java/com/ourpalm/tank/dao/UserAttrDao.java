package com.ourpalm.tank.dao;

import com.ourpalm.tank.domain.PayOrderInfo;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.domain.UserAttr;

public interface UserAttrDao {

	void saveUserAttr(UserAttr attr);
	
	UserAttr getUserAttr(int roleId);
	
	void saveRolePay(RolePay rolePay);
	
	RolePay getRolePay(int roleId);
	
	void saveOrder(PayOrderInfo order);
}
