package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_MOVE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_MOVE_MSG;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

@Command(type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, id = BATTLE_MSG.CMD_ID.CTS_MOVE_VALUE)
public class TankMove implements Action<CTS_MOVE_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MOVE_MSG req) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		int roleId = connect.getRoleId();
		AbstractInstance tankInstance = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tankInstance == null){
			return null;
		}
		//坦克死亡 
		if(tankInstance.isDeath()){
			return null;
		}
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tankInstance.getMapInstanceId());
		if(mapInstance == null){
			return null;
		}
		
		Coordinate3D location = req.getPosition();
		
		//验证移动
		tankInstance.checkMoveTime();
		
		//验证移动位置
		tankInstance.checkMove(location.getPx(), location.getPz());
		
		Grid grid = mapInstance.getGrid();
		//释放网格
		List<Node> beforeNodes = grid.getNearByNodes(tankInstance.getX(), tankInstance.getZ());
		for(Node node : beforeNodes){
			if(node == null){
				continue;
			}
			node.setTankOccupy(false);
		}
		
		//当前位置格子设为不可行走
		List<Node> currNodes = grid.getNearByNodes(location.getPx(), location.getPz());
		for(Node node : currNodes){
			if(node == null){
				continue;
			}
			node.setTankOccupy(true);
		}
		
		//设置坦克位置
		tankInstance.setX(location.getPx());
		tankInstance.setY(location.getPy());
		tankInstance.setZ(location.getPz());

		
		//构建消息
		STC_MOVE_MSG msg = STC_MOVE_MSG.newBuilder()
									.setId(tankInstance.getId())
									.setPosition(req.getPosition())
									.setAimDirection(req.getAimDirection())
									.setMoveDirection(req.getMoveDirection())
									.setMoveSpeed(req.getMoveSpeed())
									.setState(req.getState())
									.setRotation(req.getRotation())
									.setForce(req.getForce())
									.setTickCount(req.getTickCount())
									.build();
		//广播给房间其他人
		mapInstance.brodcastMsg(roleId, BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_MOVE_VALUE, msg.toByteArray());
		return null;
	}
}
