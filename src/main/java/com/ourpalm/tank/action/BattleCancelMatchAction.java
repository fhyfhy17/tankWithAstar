package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;

/**
 * 取消战斗匹配
 * @author wangkun
 *
 */
@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_MATCH_CANCEL_VALUE
)
public class BattleCancelMatchAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		GameContext.getMatchApp().removeIo(context.getIoId());
		
		connect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
				MATCH_MSG.CMD_ID.STC_MATCH_CANCEL_VALUE, null);
		return null;
	}

}
