package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.CTS_GOODS_SELL_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_GOODS_SELL_RESULT_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command (
	type = PACKAGE_MSG.CMD_TYPE.CMD_TYPE_PACKAGE_VALUE,
	id = PACKAGE_MSG.CMD_ID.CTS_GOODS_SELL_VALUE
)
public class GoodsSellAction implements Action<CTS_GOODS_SELL_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_GOODS_SELL_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		Result result = GameContext.getShopApp().sell(connect.getRoleId(), reqMsg.getGoodsId(), reqMsg.getCount());
		
		
		return STC_GOODS_SELL_RESULT_MSG
					.newBuilder()
					.setSuccess(result.isSuccess())
					.setInfo(result.getInfo())
					.build();
	}

}
