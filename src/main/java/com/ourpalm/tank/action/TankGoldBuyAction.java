package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_GOLD_TANK_BUY_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_BUY_MSG;
import com.ourpalm.tank.message.TANK_MSG.TankItem;
import com.ourpalm.tank.vo.result.TankBuyResult;


@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_GOLD_TANK_BUY_VALUE
)
public class TankGoldBuyAction implements Action<CTS_GOLD_TANK_BUY_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_GOLD_TANK_BUY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		int tankId = reqMsg.getTankId();
		int moneyType = reqMsg.getMoneyType();
		
		STC_TANK_BUY_MSG.Builder builder = STC_TANK_BUY_MSG.newBuilder();
		
		TankBuyResult result = GameContext.getTankApp().tankGoldBuy(roleId, tankId, moneyType);
		//返回消息
		builder.setResult(result.getResult());
		builder.setInfo(result.getInfo());
		
		RoleTank roleTank = result.getRoleTank();
		if(roleTank != null){
			TankItem item = GameContext.getTankApp().buildTankItem(roleTank);
			builder.setTank(item);
		}
		
		return builder.build();
	}
	
	
}
