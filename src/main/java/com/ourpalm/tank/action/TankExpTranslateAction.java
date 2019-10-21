package com.ourpalm.tank.action;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_TANK_EXP_TRANSLATE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_EXP_TRANSLATE_MSG;
import com.ourpalm.tank.message.TANK_MSG.TankTranslateExpItem;
import com.ourpalm.tank.vo.result.ValueResult;

@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_TANK_EXP_TRANSLATE_VALUE
)
public class TankExpTranslateAction implements Action<CTS_TANK_EXP_TRANSLATE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_TANK_EXP_TRANSLATE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		List<Integer> tankIds = reqMsg.getTankIdsList();
		if(Util.isEmpty(tankIds))
			return null;
		
		int translateExp = reqMsg.getTranslateExp();
		if(translateExp <= 0)
			return null;
		
		ValueResult<Map<Integer, Integer>> result = GameContext.getTankApp().translateTankExp(connect.getRoleId(), tankIds, translateExp, reqMsg.getType());
		
		STC_TANK_EXP_TRANSLATE_MSG.Builder builder = STC_TANK_EXP_TRANSLATE_MSG.newBuilder();
		
		builder.setTankExp(0);
		if(result.isSuccess()) {
			for(Iterator<Map.Entry<Integer, Integer>> it = result.getValue().entrySet().iterator(); it.hasNext();) {
				Map.Entry<Integer, Integer> entry = it.next();
				
				TankTranslateExpItem.Builder itemBuilder = TankTranslateExpItem.newBuilder();
				itemBuilder.setTankId(entry.getKey());
				itemBuilder.setTankExp(entry.getValue());
				builder.addTanks(itemBuilder);
			}
			builder.setTankExp(translateExp);
		}
		
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}
}


