package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SHOP_MSG;
import com.ourpalm.tank.message.SERV_MSG.STS_ACTIVE_REWS_MSG;
import com.ourpalm.tank.message.SHOP_MSG.STC_ACTIVE_REWS_MSG;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.STS_ACTIVE_REWS_VALUE
)
public class ActiveCodeRewsAction implements Action<STS_ACTIVE_REWS_MSG>{

	@Override
	public MessageLite execute(ActionContext context, STS_ACTIVE_REWS_MSG reqMsg) {
		int roleId = reqMsg.getRoleId();
		int goodsId = reqMsg.getGoodsId();
		
		GameContext.getGoodsApp().addGoods(roleId, goodsId, 1, OutputType.activeCodeRewsInc.getInfo());
		
		STC_ACTIVE_REWS_MSG.Builder builder = STC_ACTIVE_REWS_MSG.newBuilder();
		builder.setGoodsId(goodsId);
		
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect != null){
			connect.sendMsg(SHOP_MSG.CMD_TYPE.CMD_TYPE_SHOP_VALUE, SHOP_MSG.CMD_ID.STC_ACTIVE_REWS_VALUE, builder.build().toByteArray());
		}
		return null;
	}

}
