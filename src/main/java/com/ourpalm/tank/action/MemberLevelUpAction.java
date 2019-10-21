package com.ourpalm.tank.action;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.CTS_MEMBER_LEVEL_UP_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.GoodsItem;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEMBER_LEVEL_UP_MSG;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE,
	id = MEMBER_MSG.CMD_ID.CTS_MEMBER_LEVEL_UP_VALUE
)
public class MemberLevelUpAction implements Action<CTS_MEMBER_LEVEL_UP_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_LEVEL_UP_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		Result result = GameContext.getMemberApp().memberLevelUp(connect.getRoleId(), reqMsg.getInstanceId(), reqMsg.getEatInstanceIdsList(), getGoodsMap(reqMsg));
		
		STC_MEMBER_LEVEL_UP_MSG.Builder builder = STC_MEMBER_LEVEL_UP_MSG.newBuilder();
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}
	
	private Map<Integer, Integer> getGoodsMap(CTS_MEMBER_LEVEL_UP_MSG msg) {
		Map<Integer, Integer> result = new HashMap<>();
		if (Util.isEmpty(msg.getGoodsItemsList())){
			return result;
		}
		
		for (GoodsItem item : msg.getGoodsItemsList()){
			result.put(item.getGoodsId(), item.getNum());
		}
		return result;
	}

}
