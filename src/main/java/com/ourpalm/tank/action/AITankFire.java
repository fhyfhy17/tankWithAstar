package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_ROBOT_FIRE_MSG;


@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE,
	id = BATTLE_MSG.CMD_ID.CTS_ROBOT_FIRE_VALUE
)
public class AITankFire implements Action<CTS_ROBOT_FIRE_MSG>{
	
	@Override
	public MessageLite execute(ActionContext context, CTS_ROBOT_FIRE_MSG reqMsg) {
//		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
//		if(connect == null){
//			return null;
//		}
//		int instanceId = reqMsg.getId();
//		AbstractInstance tankInstance = GameContext.getTankApp().getInstance(instanceId);
//		if(tankInstance == null){
//			return null;
//		}
//		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tankInstance.getMapInstanceId());
//		if(mapInstance == null){
//			return null;
//		}
		
//		logger.info("AI FIRE {} --> {}", instanceId, reqMsg.getTargetId());
		
//		this.useGoods(tankInstance, mapInstance);
		
//		//构建消息
//		STC_FIRE_MSG msg = STC_FIRE_MSG.newBuilder()
//									.setSourceId(instanceId)
//									.setTargetId(reqMsg.getTargetId())
//									.setTargetPosition(reqMsg.getTargetPosition())
//									.setFireType(FireType.fire)
//									.setDirection(reqMsg.getDirection())
//									.setStdItem(reqMsg.getStdItem())
//									.setHadDodge(reqMsg.getHadDodge())
//									.setSkewX(reqMsg.getSkewX())
//									.setSkewY(reqMsg.getSkewY())
//									.build();
//		//广播给房间其他人
//		mapInstance.aiBrodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
//				BATTLE_MSG.CMD_ID.STC_FIRE_VALUE, msg.toByteArray());
		return null;
	}

//	private void useGoods(AbstractInstance tankInstance, MapInstance mapInstance){
//		try{
//			TankAiTemplate aiTemplate = GameContext.getTankApp().getTankAiTemplate(tankInstance.getLevel());
//			if(aiTemplate == null){
//				return ;
//			}
//			int goodsRat = aiTemplate.getGoodsRat();
//			if(goodsRat <= 0){
//				return ;
//			}
//			int rat = RandomUtil.randomIntWithoutZero(100);
//			if(rat > goodsRat || tankInstance.getBuffers().size() <= 0){
//				return ;
//			}
//			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
//			int count = tankInstance.getGoods(goodsId);
//			if(count <= 0){
//				return ;
//			}
//			tankInstance.putGoods(goodsId, count - 1);
//			
//			GoodsWarTemplate template = GameContext.getGoodsApp().getGoodsWarTemplate(goodsId);
//			//广播使用效果
//			STC_USE_WAR_EFFECT_MSG efffectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder()
//					.setId(tankInstance.getId())
//					.setGoodsId(goodsId)
//					.build();
//			if(mapInstance != null){
//				mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
//						BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE, efffectMsg.toByteArray());
//			}
//			//删除buff
//			for(String strId : template.getDelBuff().split(Cat.comma)){
//				if(Util.isEmpty(strId)){
//					continue;
//				}
//				int delBuffId = Integer.parseInt(strId);
//				GameContext.getBuffApp().remove(tankInstance, delBuffId);
//			}
//		}catch(Exception e){
//			LogCore.runtime.error("", e);
//		}
//	}
}
