package com.ourpalm.tank.action;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.FRIEND_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_RECEIVEGOODS_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_RECEIVEGOODS_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_RECEIVEGOODS_VALUE
)
public class FriendReceiveGoodsAction implements Action<CTS_FRIEND_RECEIVEGOODS_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_RECEIVEGOODS_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		List<Integer> fIds = reqMsg.getRoleIdsList();
		if(fIds == null || fIds.isEmpty())
			return null;
		
		int roleId = connect.getRoleId();		
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 接收礼物列表={}", roleId, Arrays.toString(fIds.toArray()));
		}
		
		List<Integer> addList = GameContext.getFriendApp().receiveGoods(roleId, fIds);
		int goldCount = GameContext.getFriendApp().getGoldCount();
		if(!addList.isEmpty()) {
			GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.add, goldCount * addList.size()), OutputType.friendReceiveGoodsInc);
		}
		
		STC_FRIEND_RECEIVEGOODS_MSG.Builder builder = STC_FRIEND_RECEIVEGOODS_MSG.newBuilder();
		builder.setSuccess(true);
		builder.addAllRoleIds(addList);
		return builder.build();
	}
	
	

}
