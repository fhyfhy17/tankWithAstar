package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.SHOP_MSG;
import com.ourpalm.tank.message.SHOP_MSG.STC_ACTIVE_CODE_PARAM_MSG;

@Command(
	type = SHOP_MSG.CMD_TYPE.CMD_TYPE_SHOP_VALUE, 
	id = SHOP_MSG.CMD_ID.CTS_ACTIVE_CODE_PARAM_VALUE
)
public class ActiveCodeParamAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		String nodeName = GameContext.getLocalNodeName();
		return STC_ACTIVE_CODE_PARAM_MSG.newBuilder()
				//.setUrl(GameContext.getActivecodeCallbackUrl())
				.setParam(nodeName + "|" + connect.getIoId())
				.build();
	}

}
