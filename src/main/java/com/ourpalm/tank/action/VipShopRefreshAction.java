package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.message.SHOP_MSG;
import com.ourpalm.tank.message.SHOP_MSG.STC_VIP_SHOP_LIST_MSG;
import com.ourpalm.tank.message.SHOP_MSG.VipShopItem;
import com.ourpalm.tank.template.VipShopItemTemplate;

@Command(
	type = SHOP_MSG.CMD_TYPE.CMD_TYPE_SHOP_VALUE, 
	id = SHOP_MSG.CMD_ID.CTS_VIP_SHOP_REFRESH_VALUE
)
public class VipShopRefreshAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		RoleShop roleShop = GameContext.getVipShopApp().refreshShopList(connect.getRoleId());
		if(roleShop == null)
			return null;
		
		STC_VIP_SHOP_LIST_MSG.Builder builder = STC_VIP_SHOP_LIST_MSG.newBuilder();
		
		
		for(Integer itemId : roleShop.getGoodsIds()) {
			builder.addItemList(buildShopItem(itemId, roleShop.hadBuy(itemId)));
		}
		
		builder.setRefreshTime(GameContext.getVipShopApp().getLeftRefreshTime(connect.getRoleId()));
		builder.setRefreshCost(GameContext.getVipShopApp().getRefreshCost(connect.getRoleId()));
		return builder.build();
	}
	
	private VipShopItem buildShopItem(int itemId, boolean isHadBuy) {
		VipShopItemTemplate template = GameContext.getVipShopApp().getShopItemTemplate(itemId);
		if(template == null)
			return null;
		
		VipShopItem.Builder itemBuilder = VipShopItem.newBuilder();
		itemBuilder.setItemId(template.getId());
		itemBuilder.setGoodsId(template.getGoodsId());
		itemBuilder.setCount(template.getCount());
		itemBuilder.setPrice1(template.getPrice1());
		itemBuilder.setPrice2(template.getPrice2());
		itemBuilder.setHadBuy(isHadBuy);
		itemBuilder.setMoneyType1(template.getMoneyType1());
		itemBuilder.setMoneyType2(template.getMoneyType2());
		return itemBuilder.build();
	}

}
