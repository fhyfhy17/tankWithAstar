package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.TANK_MSG;

@Command(
	type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, 
	id = TANK_MSG.CMD_ID.CTS_WAREHOUSE_LIST_VALUE
)
public class TankWarehouseAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
//		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
//		if(connect == null){
//			return null;
//		}
//		final int roleId = connect.getRoleId();
//		STC_WAREHOUSE_LIST_MSG.Builder builder = STC_WAREHOUSE_LIST_MSG.newBuilder();
//		
//		List<Integer> list = DAOFactory.getTankWarehouseDAO().getAll(roleId);
//		if(Util.isEmpty(list)){
//			return builder.build();
//		}
//		
//		final RoleAccount role = DAOFactory.getUserDAO().getRoleAccount(roleId);
//		
//		for(Integer tankId : list){
//			RoleTank roleTank = DAOFactory.getRoleTankDao().getRoleTank(roleId, tankId);
//			if(roleTank == null){
//				continue;
//			}
//			int[] parts = roleTank.getParts();
//			WarehouseItem.Builder item = WarehouseItem.newBuilder();
//			item.setTankId(tankId);
//			item.setPart1(parts[0]);
//			item.setPart2(parts[1]);
//			item.setPart3(parts[2]);
//			item.setPart4(parts[3]);
//			item.setElitePart1(parts[4]);
//			item.setElitePart2(parts[5]);
//			item.setHasMain(false);
//			if(tankId.equals(role.getMainTankId())){
//				item.setHasMain(true);
//			}
//			item.addAllGoodsItem(this.createWarItem(roleTank));
//			item.build();
//			builder.addItem(item);
//		}
//		
//		return builder.build();
		
		return null;
	}

//	private List<GoodsWarItem> createWarItem(RoleTank roleTank){
//		List<GoodsWarItem> list = new ArrayList<>();
//		int[] goods = roleTank.getGoods();
//		for(int i = 0; i < goods.length; i++){
//			GoodsWarItem item = GoodsWarItem.newBuilder()
//					.setIndex(i)
//					.setId(goods[i])
//					.build();
//			list.add(item);
//		}
//		return list;
//	}
	
}
