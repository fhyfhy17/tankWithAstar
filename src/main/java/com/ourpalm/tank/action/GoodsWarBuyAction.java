package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.CTS_QUICK_BUY_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_QUICK_BUY_MSG;

@Command(
	type = PACKAGE_MSG.CMD_TYPE.CMD_TYPE_PACKAGE_VALUE, 
	id = PACKAGE_MSG.CMD_ID.CTS_QUICK_BUY_VALUE
)
public class GoodsWarBuyAction implements Action<CTS_QUICK_BUY_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_QUICK_BUY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		int goodsId = reqMsg.getGoodsId();
		int num = reqMsg.getGoodsNum();
		
		boolean isOk = GameContext.getGoodsApp().quickBuy(connect.getRoleId(), goodsId, num);
		
		if(!isOk)
			return null;
		
		STC_QUICK_BUY_MSG.Builder builder = STC_QUICK_BUY_MSG.newBuilder();
		builder.setGoodsId(goodsId);
		builder.setNum(num);
		return builder.build();
	}

}
