package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_AIM_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_AIM_MSG;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

@Command(type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, id = BATTLE_MSG.CMD_ID.CTS_AIM_VALUE)
public class TankAim implements Action<CTS_AIM_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_AIM_MSG req) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		AbstractInstance tankInstance = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tankInstance == null){
			return null;
		}
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tankInstance.getMapInstanceId());
		if(mapInstance == null){
			return null;
		}
		
		
		//构建消息
		STC_AIM_MSG msg = STC_AIM_MSG.newBuilder()
									.setId(tankInstance.getId())
									.setAimPosition(req.getAimPosition())
									.setAimState(req.getAimState())
									.build();
		//广播给房间其他人
		mapInstance.brodcastMsg(roleId, BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_AIM_VALUE, msg.toByteArray());
		return null;
	}

}
