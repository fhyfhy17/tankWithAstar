package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.AbstractInstance;


/**
 *	准备开始
 */
@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_READY_VALUE
)
public class BattleReadyAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		RoleBattle roleBattle = GameContext.getMatchApp().getRoleBattle(connect.getRoleId());
		//通知地图准备完毕
		AbstractInstance tank = GameContext.getTankApp().getInstance(roleBattle.getTankInstanceId());
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		
		//为断线重连后做处理
		//进入地图
		mapInstance.enter(tank);
		//战斗信息放入本地
		roleBattle.setNodeName(GameContext.getLocalNodeName());
		GameContext.getMatchApp().saveRoleBattle(roleBattle);
		//清除坦克异常记录
		tank.clearClearMoveCount();
		
		//判断自己是否死亡状态
		synchronized(tank){
			if(tank.isDeath()){
				//重新添加到死亡列表中
				mapInstance.death(tank);
				//发送死亡广播
				mapInstance.deathNotifyMsg(tank.getAttacker(), tank);
			}
		}
		
		//通知地图进入
		mapInstance.notify(NotifyState.enter, tank.getId());
		
		//准备返回
		connect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_READY_VALUE, null);
		
		return null;
	}

}
