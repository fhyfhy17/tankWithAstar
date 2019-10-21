package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_FIRE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_FIRE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_SKILL_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_GOODS_MSG;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
	id = BATTLE_MSG.CMD_ID.CTS_FIRE_VALUE
)
public class TankFire implements Action<CTS_FIRE_MSG> {

	private Logger logger = LogCore.runtime;
	
	@Override
	public MessageLite execute(ActionContext context, CTS_FIRE_MSG req) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		AbstractInstance tankInstance = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tankInstance == null){
			return null;
		}
		
		//死亡判断
		if(tankInstance.isDeath()){
			logger.debug("开火 roleId = {} 坦克实例死亡", roleId);
			return null;
		}
		
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tankInstance.getMapInstanceId());
		if(mapInstance == null){
			return null;
		}
		
		int itemId = req.getStdItem();
		FireType fireType = req.getFireType();
		
		logger.debug("开火 FireType={}, stdItem={}, hadDodge={}, targetId={}", 
				fireType, itemId, req.getHadDodge(), req.getTargetId());
		
		switch(fireType){
			case fire : this.fire(tankInstance, mapInstance, req); break;
			case goods : this.useGoldShell(mapInstance, tankInstance, req); break;
			case skill : this.useSkill(tankInstance, mapInstance, req); break;
		}
		
		return null;
	}
	
	
	private void fire(AbstractInstance tank, MapInstance mapInstance, CTS_FIRE_MSG req){
		//判断是否冷却
		if(! tank.hadAndSetFireCoolTime()){
			return ;
		}
		
		//触发被动技能
		for(Skill skill : tank.getAllSkill()){
			skill.fire();
		}
		this.brodcaseMsg(tank, mapInstance, req);
		
		if( req.getTargetId() == 0 ){
			tank.setMissCount(tank.getMissCount() + 1);
		}
		if( req.getHadDodge() ){
			tank.setDodgeCount(tank.getDodgeCount()+1);
		}
	}
	
	
	private void brodcaseMsg(AbstractInstance tank, MapInstance mapInstance, CTS_FIRE_MSG req){
		//TODO 穿甲弹逻辑， 由于穿甲弹逻辑修改，导致只能发一个firetype，因为每一发都是穿甲弹,以后可能会修改
		int stdItem = tank.getBattleGoods();
		FireType f = req.getFireType();
		if (stdItem != 0) {
			f = FireType.goods;
		}

		//构建消息
		STC_FIRE_MSG msg = STC_FIRE_MSG.newBuilder()
				.setSourceId(tank.getId())
				.setTargetId(req.getTargetId())
				.setTargetPosition(req.getTargetPosition())
				.setDirection(req.getDirection())
				.setFireType(f)
				.setStdItem(stdItem)
				.setHadDodge(req.getHadDodge())
				.setSkewX(req.getSkewX())
				.setSkewY(req.getSkewY())
				.build();
		//广播给房间其他人
		mapInstance.brodcastMsg(tank.getRoleId(), BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_FIRE_VALUE, msg.toByteArray());
	}

	
	
	//使用金币弹
	private void useGoldShell(MapInstance mapInstance, AbstractInstance tankInstance, CTS_FIRE_MSG req){
		final int goodsId = req.getStdItem();
		if(goodsId == 0){
			return ;
		}
		
		if(! tankInstance.hadAndSetFireCoolTime()){
			STC_USE_WAR_GOODS_MSG.Builder msg = STC_USE_WAR_GOODS_MSG.newBuilder();
			msg.setResult(Result.FAILURE);
			msg.setInfo(Tips.COOL_TIME_NOT_OVER);
			tankInstance.sendMsg(msg.build());
			return ;
		}
		
		int count = tankInstance.getGoods(goodsId);
		if(count < 0){
			STC_USE_WAR_GOODS_MSG.Builder msg = STC_USE_WAR_GOODS_MSG.newBuilder();
			msg.setResult(Result.FAILURE);
			msg.setInfo(Tips.GOODS_NOT_ENOUGH);
			tankInstance.sendMsg(msg.build());
			return ;
		}
		//扣除道具
		final int currCount = count - 1;
		tankInstance.putGoods(goodsId, currCount);
		
		//扣除物品
		final int roleId = tankInstance.getRoleId();
		GameContext.getGoodsApp().removeGoods(roleId, goodsId, 1, OutputType.tankFireUseGoodsDec.getInfo());
		tankInstance.sendMsg(
				STC_USE_WAR_GOODS_MSG.newBuilder()
				.setResult(1)
				.setInfo("")
				.setId(goodsId)
				.setNum(currCount)
				.build());
		//没特殊效果 注释掉
		//广播效果
//		STC_USE_WAR_EFFECT_MSG.Builder effectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder();
//		effectMsg.setId(tankInstance.getId());
//		effectMsg.setGoodsId(goodsId);
//		mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
//				BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE, effectMsg.build().toByteArray());
		
		this.brodcaseMsg(tankInstance, mapInstance, req);
	}
	
	
	//使用技能炮弹
	private void useSkill(AbstractInstance tankInstance, MapInstance mapInstance, CTS_FIRE_MSG req){
		final int skillId = req.getStdItem();
		STC_USE_SKILL_MSG.Builder builder = STC_USE_SKILL_MSG.newBuilder();
		Skill skill = tankInstance.getSkill(skillId);
		if(skill == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.SKILL_NO_EXIST);
			tankInstance.sendMsg(builder.build());
			return ;
		}
		//判断技能是否冷却
		if(! skill.finishCoolTime()){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.COOL_TIME_NOT_OVER);
			tankInstance.sendMsg(builder.build());
			logger.debug("技能未冷却...");
			return ;
		}
		builder.setResult(Result.SUCCESS);
		builder.setInfo("");
		tankInstance.sendMsg(builder.build());
		
		//广播开火
		this.brodcaseMsg(tankInstance, mapInstance, req);
	}
}
