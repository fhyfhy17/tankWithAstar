package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.BattleApp;
import com.ourpalm.tank.app.map.AIThreadHandler;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_ENTER_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.result.Result;

/**
 *	进入战场 
 */
@Command(
	type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE,
	id = MATCH_MSG.CMD_ID.CTS_ENTER_VALUE
)
public class BattleEnterAction extends AbstractEnterMap implements Action<Object>{
	
	private static int LOOP_TIME = AIThreadHandler.AI_LOOP_TIME;
	
	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		STC_ENTER_MSG.Builder builder = STC_ENTER_MSG.newBuilder();
		
		AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.BATTLE_OVER);
			builder.setLoopTime(LOOP_TIME);
			return builder.build();
		}
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.BATTLE_OVER);
			builder.setLoopTime(LOOP_TIME);
			return builder.build();
		}
		
		// 自身坦克属性
		TankItem tankItem = GameContext.getBattleApp().buildTankItem(tank, BattleApp.selfTankAttrs);
		
		builder.setResult(Result.SUCCESS);
		builder.setTankItem(tankItem);
		builder.addAllOtherTanks(this.getOtherTanks(mapInstance, tank.getRoleId()));
		
		//战场道具
		builder.addAllGoodsItem(this.getGoodsItem(tank));
		
		//技能列表
		builder.addAllSkillIds(this.getSkillItem(tank));
		
		//玩家位置同步间隔
		builder.setLoopTime(LOOP_TIME);
		
		return builder.build();
	}
	
}
