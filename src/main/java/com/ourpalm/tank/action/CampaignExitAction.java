package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CAMP_MSG;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

@Command(
	type = CAMP_MSG.CMD_TYPE.CMD_TYPE_CAMP_VALUE, 
	id = CAMP_MSG.CMD_ID.CTS_CAMP_EXIT_VALUE
)
public class CampaignExitAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(connect.getRoleId());
		if(tank == null){
			return null;
		}
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance != null){
			mapInstance.leave(tank.getId());
		}
		
		return null;
	}

}
