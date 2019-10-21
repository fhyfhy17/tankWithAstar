package com.ourpalm.tank.action;

import java.util.Collection;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.GoldTankItem;
import com.ourpalm.tank.message.TANK_MSG.STC_GOLD_TANK_LIST_MSG;
import com.ourpalm.tank.template.GoldTankTemplate;


@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_GOLD_TANK_LIST_VALUE
)
public class TankGoldListAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		Collection<GoldTankTemplate> goldTanks = GameContext.getTankApp().getGoldTankTemplates();
		
		STC_GOLD_TANK_LIST_MSG.Builder builder = STC_GOLD_TANK_LIST_MSG.newBuilder();
		for(GoldTankTemplate t : goldTanks) {
			GoldTankItem.Builder goldTankBuilder = GoldTankItem.newBuilder();
			goldTankBuilder.setTankId(t.getTankId());
			goldTankBuilder.setGold(t.getGold());
			goldTankBuilder.setDiamond(t.getDiamond());
			
			builder.addTanks(goldTankBuilder);
		}
		
		return builder.build();
	}
	
	
}
