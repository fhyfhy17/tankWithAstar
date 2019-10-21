package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_ROBOT_MOVE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_MOVE_MSG;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;


@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
	id = BATTLE_MSG.CMD_ID.CTS_ROBOT_MOVE_VALUE
)
public class AITankMove implements Action<CTS_ROBOT_MOVE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_ROBOT_MOVE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int instanceId = reqMsg.getId();
		AbstractInstance tankInstance = GameContext.getTankApp().getInstance(instanceId);
		if(tankInstance == null){
			return null;
		}
		//设置坦克位置
		Coordinate3D location = reqMsg.getPosition();
		tankInstance.setX(location.getPx());
		tankInstance.setY(location.getPy());
		tankInstance.setZ(location.getPz());
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tankInstance.getMapInstanceId());
		if(mapInstance == null){
			return null;
		}
		
		//构建消息
		STC_MOVE_MSG msg = STC_MOVE_MSG.newBuilder()
									.setId(instanceId)
									.setPosition(reqMsg.getPosition())
									.setAimDirection(reqMsg.getAimDirection())
									.setMoveDirection(reqMsg.getMoveDirection())
									.setMoveSpeed(reqMsg.getMoveSpeed())
									.setState(reqMsg.getState())
									.setRotation(reqMsg.getRotation())
									.setForce(reqMsg.getForce())
									.setTickCount(reqMsg.getTickCount())
									.build();
		
		//广播给房间除主机外的其他人
		mapInstance.aiBrodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_MOVE_VALUE, msg.toByteArray());
		return null;
	}

}
