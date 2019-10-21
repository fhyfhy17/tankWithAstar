package com.ourpalm.tank.action;

import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GoodsItem;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_GOODS_LIST_MSG;

@Command(
	type = PACKAGE_MSG.CMD_TYPE.CMD_TYPE_PACKAGE_VALUE,
	id = PACKAGE_MSG.CMD_ID.CTS_GOODS_LIST_VALUE
)
public class GoodsListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		Map<Integer, Integer> goodsMap = GameContext.getGoodsApp().getRoleGoods(connect.getRoleId());
		
		//登录时推送离线消息
		GameContext.getImApp().login(connect.getRoleId());

		STC_GOODS_LIST_MSG.Builder builder = STC_GOODS_LIST_MSG.newBuilder();
		for(Map.Entry<Integer, Integer> entry : goodsMap.entrySet()){
			builder.addGoodsList(GoodsItem.newBuilder()
										.setId(entry.getKey())
										.setCount(entry.getValue()));
		}
		return builder.build();
	}
}
