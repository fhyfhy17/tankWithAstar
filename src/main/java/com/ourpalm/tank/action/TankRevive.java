package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.MapInstance;

/**
 * 坦克复活
 *
 */
@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
	id = BATTLE_MSG.CMD_ID.CTS_RELIVE_VALUE
)
public class TankRevive implements Action<Object>{

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		logger.debug("收到请求复活...");
		
		int roleId = connect.getRoleId();
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
			return null;
		}
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance == null){
			return null;
		}
		
		//军衔排行模式不允许金币复活
		if(mapInstance.getWarType() == WAR_TYPE.RANK_VALUE){
			return null;
		}
		
		if(tank.getDeathTime() <= 0){
			return null;
		}
		
		TankTemplate t = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		
		RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
		int freeCount = roleWarInfo.getFreeReviveCount();
		if(freeCount > 0){
			roleWarInfo.setFreeReviveCount(freeCount - 1);
			GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
			
			//死亡时间清空，让地图状态机复活
			tank.setDeathTime(0);
			return null;
		}
		
		boolean consumeGoldMoney = GameContext.getUserAttrApp().changeAttribute(roleId, 
				AttrUnit.build(RoleAttr.gold, Operation.decrease, tank.getRebirthGold()), OutputType.tankReliveDec.type(), StringUtil.buildLogOrigin(t.getName_s(), OutputType.tankReliveDec.getInfo()));
		if(consumeGoldMoney){
			//死亡时间清空，让地图状态机复活
			tank.setDeathTime(0);
		}
		return null;
	}

}
