package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SHOP_MSG;
import com.ourpalm.tank.message.SHOP_MSG.CTS_VIP_SHOP_BUY_MSG;
import com.ourpalm.tank.message.SHOP_MSG.STC_HONOR_SHOP_BUY_RESULT_MSG;
import com.ourpalm.tank.vo.result.ShopBuyResult;

@Command(
	type = SHOP_MSG.CMD_TYPE.CMD_TYPE_SHOP_VALUE, 
	id = SHOP_MSG.CMD_ID.CTS_VIP_SHOP_BUY_VALUE
)
public class VipShopBuyAction implements Action<CTS_VIP_SHOP_BUY_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_VIP_SHOP_BUY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		ShopBuyResult result = GameContext.getVipShopApp().buy(connect.getRoleId(), reqMsg.getItemId(), reqMsg.getMoneyType());
		
		return STC_HONOR_SHOP_BUY_RESULT_MSG.newBuilder()
					.setSuccess(result.isSuccess())
					.setInfo(result.getInfo())
					.setMoneyType(result.getMoneyType())
					.setMoney(result.getMoney())
					.setGoodsName(result.getGoodsName())
					.setNum(result.getGoodsNum())
					.build();
	}

}
