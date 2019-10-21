package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;

@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_BACK_WAR_VALUE
)
public class BattleBackAction extends AbstractEnterMap implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		final int roleId = connect.getRoleId();
		final int ioId = connect.getIoId();
		final String gateNode = connect.getGateName();
		
		this.backBattleWar(roleId, ioId, gateNode, AbstractEnterMap.RELINK_LOGIN);
		logger.debug("申请重回战场....{}", roleId);
		
		try{
			GameContext.getActivityApp().onlineTime(roleId);
		}catch(Exception ex){
			LogCore.runtime.error("", ex);
		}
		
		return null;
	}

}
