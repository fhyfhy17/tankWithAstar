package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_RESEARCH_COST_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_RESEARCH_COST_MSG;
import com.ourpalm.tank.template.TankTemplate;


@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE,
	id = TANK_MSG.CMD_ID.CTS_RESEARCH_COST_VALUE
)
public class TankBuyMoney implements Action<CTS_RESEARCH_COST_MSG>{
	
	@Override
	public MessageLite execute(ActionContext context, CTS_RESEARCH_COST_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int tankId = reqMsg.getTankId();
		TankTemplate tankT = GameContext.getTankApp().getTankTemplate(tankId);
		if(tankT == null)
			return null;
		
		STC_RESEARCH_COST_MSG.Builder builder = STC_RESEARCH_COST_MSG.newBuilder();
		builder.setCost(tankT.getOpen_money_i());
		builder.setTankId(tankId);
		return builder.build();
	}
}
