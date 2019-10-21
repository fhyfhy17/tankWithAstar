package com.ourpalm.tank.action;

import java.util.Collection;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_LIST_MSG;
import com.ourpalm.tank.message.TANK_MSG.TankItem;


@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_TANK_LIST_VALUE
)
public class TankListAction implements Action<Object>{
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
	
		STC_TANK_LIST_MSG.Builder builder = STC_TANK_LIST_MSG.newBuilder();
		Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(connect.getRoleId());
		if(Util.isEmpty(allTankList)){
			return builder.build();
		}
		
		for(RoleTank tank : allTankList){
			TankItem item = GameContext.getTankApp().buildTankItem(tank);
			builder.addTanks(item);
		}
		RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		builder.setPark(role.getPark());
		builder.setFreezeInfo(GameContext.getTankApp().buildFreezeInfo(connect.getRoleId()));
		return builder.build();
	}

}
