package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_GOODS_WAR_CLEAR_MSG;


@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_GOODS_WAR_CLEAR_VALUE
)
public class GoodsWarClearAction implements Action<CTS_GOODS_WAR_CLEAR_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_GOODS_WAR_CLEAR_MSG reqMsg) {
//		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
//		if(connect == null){
//			return null;
//		}
//		
//		final int tankId = reqMsg.getTankId();
//		final int type = reqMsg.getType();
//		RoleTank tank = DAOFactory.getRoleTankDao().getRoleTank(connect.getRoleId(), tankId);
//		if(tank == null){
//			return null;
//		}
//		//炮弹清空
//		if(type == 1){
//			tank.getGoods()[0] = 0;
//			DAOFactory.getRoleTankDao().update(tank);
//		} else {
//			int[] goods = tank.getGoods();
//			goods[1] = 0;
//			goods[2] = 0;
//			goods[3] = 0;
//			DAOFactory.getRoleTankDao().update(tank);
//		}
//		
//		return  STC_GOODS_WAR_CLEAR_MSG.newBuilder()
//				.setType(type)
//				.setTankId(tankId)
//				.build();
		return null;
	}

}
