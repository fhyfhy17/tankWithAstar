package com.ourpalm.tank.app.honor;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.template.HonorShopTemplate;
import com.ourpalm.tank.template.ShopItemTemplate;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.ShopBuyResult;

public interface HonorShopApp extends Service{
	
	/** 商品列表 */
	RoleShop getShopList(int roleId);

	int getLeftRefreshTime(int roleId);
	
	RoleShop refreshShopList(int roleId);
	
	/** 购买商品 */
	ShopBuyResult buy(int roleId, int itemId,int itemNum);
	
	Result sell(int roleId, int goodsId, int count);
	
	/** 登录处理 */
	void login(int roleId, boolean first);
	
	void offline(int roleId);
	
	HonorShopTemplate getShopTemplate();
	
	ShopItemTemplate getShopItemTemplate(int itemId);
	
	int getRefreshCost(int roleId);
}
