package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.ShopItem;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_SHOP_LIST_MSG;
import com.ourpalm.tank.vo.result.CorpsShopResult;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_SHOP_LIST_VALUE
)
public class CorpsShopListAction implements Action<Object>{

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		STC_CORPS_SHOP_LIST_MSG.Builder builder = STC_CORPS_SHOP_LIST_MSG.newBuilder();
		
		CorpsShopResult result = GameContext.getCorpsApp().shopList(connect.getRoleId());
		
		if(logger.isDebugEnabled()){
			logger.debug("军团商城列表: {}", JSON.toJSONString(result));
		}
		
		if(!result.isSuccess()){
			builder.setResult(false);
			builder.setInfo(result.getInfo());
			return builder.build();
		}
		
		builder.setResult(true);
		builder.setFlushGold(result.getFlushGold());
		builder.setFlushTime(result.getFlushTime());
		
		for(ShopItem item : result.getShopList()){
			CORPS_MSG.ShopItem _item = CORPS_MSG.ShopItem.newBuilder()
					.setId(item.getId())
					.setGoodsId(item.getGoodsId())
					.setNum(item.getNum())
					.setCorpsGold(item.getGold())
					.build(); 
			builder.addShops(_item);
		}
		
		return builder.build();
	}

}
