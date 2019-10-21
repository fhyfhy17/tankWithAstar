package com.ourpalm.tank.action;

import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.CTS_GOODS_USE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GoodsItem;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_GOODS_BOX_RESULT_MSG;
import com.ourpalm.tank.vo.result.GoodsBoxOpenResult;

@Command(
	type = PACKAGE_MSG.CMD_TYPE.CMD_TYPE_PACKAGE_VALUE, 
	id = PACKAGE_MSG.CMD_ID.CTS_GOODS_USE_VALUE
)
public class GoodsUseAction implements Action<CTS_GOODS_USE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_GOODS_USE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int count = Math.max(reqMsg.getCount(), 1);
		int roleId = connect.getRoleId();
		GoodsBoxOpenResult result = GameContext.getGoodsApp().openBox(roleId, reqMsg.getGoodsId(), count);
		if (!result.isSuccess()) {
			return null;
		}
		
		STC_GOODS_BOX_RESULT_MSG.Builder builder = STC_GOODS_BOX_RESULT_MSG.newBuilder();
		builder.setGold(result.getGold());
		builder.setHonor(result.getHonor());
		builder.setIron(result.getIron());
		for(Map.Entry<Integer, Integer> entry : result.getGoodsMap().entrySet()){
			builder.addChangeItem(buildItem(entry.getKey(), entry.getValue()));
		}
		
		//触发任务
		GameContext.getQuestTriggerApp().boxOpen(roleId);
		
		return builder.build();
	}

	private GoodsItem buildItem(Integer goodsId, Integer count){
		return GoodsItem.newBuilder()
					.setId(goodsId)
					.setCount(count)
					.build();
	}
}
