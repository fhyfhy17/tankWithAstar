package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.PACKAGE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GoodsInfoItem;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_GOODS_INFO_MSG;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.GoodsChangeTemplate;

/**
 *	请求需客户端更新的物品信息
 */
@Command(
	type = PACKAGE_MSG.CMD_TYPE.CMD_TYPE_PACKAGE_VALUE,
	id = PACKAGE_MSG.CMD_ID.CTS_GOODS_INFO_VALUE
)
public class GoodsChangeListAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		List<GoodsChangeTemplate> list = GameContext.getGoodsApp().getGoodsChangeList();
		if(Util.isEmpty(list)){
			return null;
		}
		STC_GOODS_INFO_MSG.Builder builder = STC_GOODS_INFO_MSG.newBuilder();
		for(GoodsChangeTemplate template : list){
			int goodsId = template.getGoodsId();
			GoodsBaseTemplate baseTemplate = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
			if(baseTemplate == null){
				continue;
			}
			GoodsInfoItem item = GoodsInfoItem.newBuilder()
					.setGoodsId(goodsId)
					.setName(baseTemplate.getName_s())
					.setDesc(baseTemplate.getDesc_s())
					.build();
			builder.addItems(item);
		}
		
		return builder.build();
	}

}
