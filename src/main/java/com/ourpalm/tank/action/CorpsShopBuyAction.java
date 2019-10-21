package com.ourpalm.tank.action;

import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.ShopItem;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_SHOP_BUY_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_SHOP_BUY_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_SHOP_BUY_VALUE
)
public class CorpsShopBuyAction extends AbstractCorpAction implements Action<CTS_CORPS_SHOP_BUY_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_SHOP_BUY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_SHOP_BUY_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);

		STC_CORPS_SHOP_BUY_MSG.Builder builder = STC_CORPS_SHOP_BUY_MSG.newBuilder();
		CorpsRole corpsRole = GameContext.getCorpsApp().getCorpsRole(corpsId, roleId);
		if(corpsRole == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CORPS_NO_EXIST);
			return builder.build();
		}

		//商品ID
		int id = reqMsg.getId();
		Map<Integer, ShopItem> shopMap = corpsRole.getShopMap();
		ShopItem shop = shopMap.get(id);
		if(shop == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CORPS_SHOP_NO_EXIST);
			return builder.build();
		}

		//判断军团币
		boolean result = GameContext.getGoodsApp().removeGoods(roleId,
								GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.corps_gold),
								shop.getGold(), OutputType.corpsShopBuyDec.getInfo());

		if(!result){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CORPS_GOLD_NOT_ENOUGH);
			return builder.build();
		}

		//给物品
		GameContext.getGoodsApp().addGoods(roleId, shop.getGoodsId(), shop.getNum(), OutputType.corpsShopBuyInc.getInfo());

		//更新商城物品数量
		shop.setNum(0);
		GameContext.getCorpsApp().saveCorpsRole(corpsRole);

		builder.setResult(Result.SUCCESS);
		builder.setId(id);
		return builder.build();
	}

}
