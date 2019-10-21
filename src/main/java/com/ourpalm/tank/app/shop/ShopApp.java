package com.ourpalm.tank.app.shop;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.message.SHOP_MSG.ITEM_GROUP;
import com.ourpalm.tank.template.ShopItemTemplate;
import com.ourpalm.tank.template.ShopTemplate;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.ShopBuyResult;

public interface ShopApp extends Service{
	
	/** 商品列表 */
	RoleShop getShopList(int roleId);

	int getLeftRefreshTime(int roleId);
	
	RoleShop refreshShopList(int roleId);
	
	/** 购买商品 */
	ShopBuyResult buy(int roleId, int itemId);
	
	Result sell(int roleId, int goodsId, int count);
	
	/** 登录处理 */
	void login(int roleId, boolean first);
	
	void offline(int roleId);
	
	ShopTemplate getShopTemplate();
	
	ShopItemTemplate getShopItemTemplate(int itemId);
	
	int getRefreshCost(int roleId);

	/** 获取商品分组 */
	ITEM_GROUP getItemGroup(int group);
}
