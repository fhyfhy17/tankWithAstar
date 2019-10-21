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
import com.ourpalm.tank.message.FRIEND_MSG.CTS_FRIEND_SENDGOODS_MSG;
import com.ourpalm.tank.message.FRIEND_MSG.STC_FRIEND_SENDGOODS_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

@Command(
	type = FRIEND_MSG.CMD_TYPE.CMD_TYPE_FRIEND_VALUE, 
	id = FRIEND_MSG.CMD_ID.CTS_FRIEND_SENDGOODS_VALUE
)
public class FriendSendGoodsAction implements Action<CTS_FRIEND_SENDGOODS_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FRIEND_SENDGOODS_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		
		List<Integer> fIds = reqMsg.getRoleIdsList();
		if(fIds == null || fIds.isEmpty())
			return null;
		
		int roleId = connect.getRoleId();		
		
		if(logger.isDebugEnabled()){
			logger.debug("roleId = {}, 请求赠送给 {}", connect.getRoleId(), Arrays.toString(fIds.toArray()));
		}
		
		List<Integer> sendList = GameContext.getFriendApp().sendGoods(roleId, fIds);
		int count = GameContext.getFriendApp().getIronCount();
		int goldCount = GameContext.getFriendApp().getGoldCount();
		if(!sendList.isEmpty()) {
			GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, sendList.size() * count), OutputType.friendSendGoodsInc);
		}
		
		STC_FRIEND_SENDGOODS_MSG.Builder builder = STC_FRIEND_SENDGOODS_MSG.newBuilder();
		builder.addAllRoleIds(sendList);
		builder.setIron(sendList.size() * count);
		builder.setGold(sendList.size() * goldCount);
		return builder.build();
	}
	
	

}
