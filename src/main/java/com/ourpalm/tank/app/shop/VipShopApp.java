package com.ourpalm.tank.app.shop;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.template.VipShopItemTemplate;
import com.ourpalm.tank.template.VipShopTemplate;
import com.ourpalm.tank.vo.result.ShopBuyResult;

public interface VipShopApp extends Service{
	
	/** 商品列表 */
	RoleShop getShopList(int roleId);

	int getLeftRefreshTime(int roleId);
	
	RoleShop refreshShopList(int roleId);
	
	/** 购买商品 */
	ShopBuyResult buy(int roleId, int itemId, int moneyType);
	
	/** 登录处理 */
	void login(int roleId, boolean first);
	
	void offline(int roleId);
	
	VipShopTemplate getShopTemplate();
	
	VipShopItemTemplate getShopItemTemplate(int itemId);
	
	int getRefreshCost(int roleId);

}
