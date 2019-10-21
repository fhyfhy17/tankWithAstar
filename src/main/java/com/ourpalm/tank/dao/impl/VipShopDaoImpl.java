package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.tank.dao.ShopRecordDao;
import com.ourpalm.tank.domain.RoleShop;

public class VipShopDaoImpl extends AbstractJedisDao implements ShopRecordDao {
	private static final String KEY = "ROLE_VIP_SHOP_KEY_";

	@Override
	public RoleShop get(int roleId) {
		return JSON.parseObject(getClient().get(KEY + roleId), RoleShop.class);
	}

	@Override
	public void save(int roleId, RoleShop roleShop) {
		if (roleShop == null) {
			return;
		}
		getClient().set(KEY + roleId, JSON.toJSONString(roleShop));

	}

}
