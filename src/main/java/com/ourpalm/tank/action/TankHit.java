package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_HIT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.template.PreseasonTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.TankPreseasonDeal;

@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
	id = BATTLE_MSG.CMD_ID.CTS_HIT_VALUE
)
public class TankHit implements Action<CTS_HIT_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_HIT_MSG req) {
		
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		AbstractInstance sourceTank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(sourceTank == null){
			return null;
		}
		AbstractInstance targetTank = GameContext.getTankApp().getInstance(req.getTargetId());
		if(targetTank == null){
			return null;
		}
		
		
		//是否可开火
		 if(sourceTank.isDeath()
				|| targetTank.isDeath()
				|| !sourceTank.view(targetTank)){//是否进入视野
//					|| !sourceTank.hadHitFireCoolTime() //命中是否冷却
//					|| !source.fireRange(target)){ //进入射程
//					|| !source.isFireElevation(target)){ //俯仰角判断
//					|| source.hadBarrier(target)){ //是否有阻挡
			 return null;
		}
		
		logger.debug("命中消息：{} --> {}", sourceTank.getId(), req.getTargetId());
		
		final int itemId = req.getStdItem();
		FireType fireType = req.getFireType();
		
		
		HitParam param = new HitParam();
		param.setSource(sourceTank);
		param.setTarget(targetTank);
		param.setHadDodge(req.getHadDodge());
		param.setHitPart(req.getHitPart());
		param.setItemId(itemId);
		param.setFireType(fireType);
		
		TankPreseasonDeal.INSTANCE.preseasonCalc(param, sourceTank,targetTank);
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(targetTank.getMapInstanceId());
		if(mapInstance != null){
			mapInstance.putHitQueue(param);
		}
	
		return null;
	}
	
}
