package com.ourpalm.tank.action;

import java.util.List;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.FRIEND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.FriendGoods;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_RECEIVEPAGE_MSG;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_RECEIVEPAGE_VALUE
)
public class FriendReceiveGoodsListAction implements Action<Object> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int roleId = connect.getRoleId();		
		List<FriendGoods> list = GameContext.getFriendApp().receiveGoodsList(roleId);
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 请求收礼物界面, size {}", roleId, list.size());
		}
		
		STC_FRIEND_RECEIVEPAGE_MSG.Builder builder = STC_FRIEND_RECEIVEPAGE_MSG.newBuilder();
		builder.addAllGoods(list);
		return builder.build();
	}
	
	

}
