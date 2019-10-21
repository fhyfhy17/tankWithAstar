package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.message.SHOP_MSG;
import com.ourpalm.tank.message.SHOP_MSG.STC_SHOP_LIST_MSG;
import com.ourpalm.tank.message.SHOP_MSG.ShopItem;
import com.ourpalm.tank.template.ShopItemTemplate;

@Command(
	type = SHOP_MSG.CMD_TYPE.CMD_TYPE_SHOP_VALUE, 
	id = SHOP_MSG.CMD_ID.CTS_SHOP_REFRESH_VALUE
)
public class ShopRefreshAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		RoleShop roleShop = GameContext.getShopApp().refreshShopList(connect.getRoleId());
		if(roleShop == null)
			return null;
		
		STC_SHOP_LIST_MSG.Builder builder = STC_SHOP_LIST_MSG.newBuilder();
		
		for(Integer itemId : roleShop.getGoodsIds()) {
			builder.addItemList(buildShopItem(itemId, roleShop.hadBuy(itemId)));
		}
		
		builder.setLeftTime(GameContext.getShopApp().getLeftRefreshTime(connect.getRoleId()));
		builder.setRefreshCost(GameContext.getShopApp().getRefreshCost(connect.getRoleId()));
		return builder.build();
	}
	
	private ShopItem buildShopItem(int itemId, boolean isHadBuy) {
		ShopItemTemplate template = GameContext.getShopApp().getShopItemTemplate(itemId);
		if(template == null)
			return null;
		
		ShopItem.Builder itemBuilder = ShopItem.newBuilder();
		itemBuilder.setItemId(template.getId());
		itemBuilder.setGoodsId(template.getGoodsId());
		itemBuilder.setCount(template.getCount());
		itemBuilder.setPrice(template.getPrice());
		itemBuilder.setCurrPrice(template.getDis_price());
		itemBuilder.setDiscount(template.getDiscount());
		itemBuilder.setHadBuy(isHadBuy);
		itemBuilder.setMoneyType(template.getMoneyType());
		return itemBuilder.build();
	}

}
