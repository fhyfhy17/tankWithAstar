package com.ourpalm.tank.action;

import java.util.Collection;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_WAREHOUSE_SCORE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_WAREHOUSE_SCORE_MSG;
import com.ourpalm.tank.message.TANK_MSG.TankScoreItem;

@Command(
		type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
		id = TANK_MSG.CMD_ID.CTS_WAREHOUSE_SCORE_VALUE
	)
public class TankWarehouseScoreAction implements Action<CTS_WAREHOUSE_SCORE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_WAREHOUSE_SCORE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = reqMsg.getRoleId();
		
		STC_WAREHOUSE_SCORE_MSG.Builder builder = STC_WAREHOUSE_SCORE_MSG.newBuilder();
		Collection<RoleTank> list = GameContext.getTankApp().getAllRoleTank(roleId);
		if(Util.isEmpty(list)){
			return builder.build();
		}
		
		builder.setRoleId(roleId);
		for(RoleTank roleTank : list){
			if(roleTank == null){
				continue;
			}
			TankScoreItem item = TankScoreItem.newBuilder()
					.setTankId(roleTank.getTankId())
					.setWinCount(roleTank.getWinCount())
					.setLostCount(roleTank.getLostCount())
					.setBattleCount(GameContext.getTankApp().calcAllBattleScore(roleTank))
					.build();
			builder.addItem(item);
		}
		
		
		return builder.build();
	}

}
