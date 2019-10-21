package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_GOODS_WAR_CHANGE_MSG;


@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_GOODS_WAR_CHANGE_VALUE
)
public class GoodsWarUpdateAction implements Action<CTS_GOODS_WAR_CHANGE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_GOODS_WAR_CHANGE_MSG reqMsg) {
//		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
//		if(connect == null){
//			return null;
//		}
//		
//		final int index = reqMsg.getIndex();
//		final int goodsId = reqMsg.getGoodsId();
//		final int tankId = reqMsg.getTankId();
//		final int roleId = connect.getRoleId();
//		
//		STC_GOODS_WAR_CHANGE_MSG.Builder resp = STC_GOODS_WAR_CHANGE_MSG.newBuilder();
//		//判断物品是否存在
//		GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
//		if(template == null){
//			resp.setResult(Result.FAILURE);
//			resp.setInfo(Tips.GOODS_NO_EXIST);
//			return resp.build();
//		}
//		
//		RoleTank tank = DAOFactory.getRoleTankDao().getRoleTank(roleId, tankId);
//		if(tank == null){
//			resp.setResult(Result.FAILURE);
//			resp.setInfo(Tips.TANK_NO_EXIST);
//			return resp.build();
//		}
//		
//		//判断物品是否存在
//		int hadCount = DAOFactory.getPackageDao().getCount(roleId, goodsId);
//		if(hadCount <= 0){
//			resp.setResult(Result.FAILURE);
//			resp.setInfo(Tips.GOODS_NO_EXIST);
//			return resp.build();
//		}
//		
//		int maxCount = template.getCarryNum_i();
//		if(hadCount > maxCount){
//			hadCount = maxCount;
//		}
//		//放入
//		int[] goods = tank.getGoods();
//		if(index >= goods.length){
//			resp.setResult(Result.FAILURE);
//			resp.setInfo(Tips.LOCATION_ERROR);
//			return resp.build();
//		}
//		
//		goods[index] = goodsId;
//		DAOFactory.getRoleTankDao().update(tank);
//		
//		resp.setResult(Result.SUCCESS);
//		resp.setInfo("");
//		resp.setTankId(tankId);
//		GoodsWarItem item = GoodsWarItem.newBuilder()
//				.setIndex(index)
//				.setId(goodsId)
//				.build();
//		resp.setItem(item);
//		
//		return resp.build();
		return null;
	}

}
