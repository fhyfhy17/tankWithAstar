package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_ROBOT_HIT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_EFFECT_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.TankAiTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;


@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
	id = BATTLE_MSG.CMD_ID.CTS_ROBOT_HIT_VALUE
)
public class AITankHit implements Action<CTS_ROBOT_HIT_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_ROBOT_HIT_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
//		
//		int instanceId = reqMsg.getSourceId();
//		AbstractInstance sourceTank = GameContext.getTankApp().getInstance(instanceId);
//		if(sourceTank == null){
//			return null;
//		}
//		AbstractInstance targetTank = GameContext.getTankApp().getInstance(reqMsg.getTargetId());
//		if(targetTank == null){
//			return null;
//		}
//		
//		HitParam param = new HitParam();
//		param.setSource(sourceTank);
//		param.setTarget(targetTank);
//		param.setHadDodge(reqMsg.getHadDodge());
//		param.setHitPart(reqMsg.getHitPart());
//		param.setHadDodge(reqMsg.getHadDodge());
		
//		TankAiTemplate aiTemplate = GameContext.getTankApp().getTankAiTemplate(sourceTank.getLevel());
//		int rate = RandomUtil.randomIntWithoutZero(aiTemplate.getFireAllRat());
//		if(rate <= aiTemplate.getSkillFireRat()){
//			this.useSkill(param);
//		} else if(rate <= aiTemplate.getGoldFireRat()){
//			this.useGoldShell(param);
//		} else {
//			this.useGoods(sourceTank);
//		}
//		logger.info("命中消息：{} --> {}", sourceTank.getId(), reqMsg.getTargetId());
		
//		GameContext.getBattleApp().hitTank(param);
		
		return null;
	}

	
	private void useSkill(HitParam param){
		AbstractInstance sourceTank = param.getSource();
		for(Skill skill : sourceTank.getAllSkill()){
			if(!skill.isActive()){
				continue;
			}
			if(skill.finishCoolTime()){
				param.setFireType(FireType.skill);
				param.setItemId(skill.getId());
				break;
			}
		}
	}
	
	private void useGoldShell(HitParam param){
		AbstractInstance sourceTank = param.getSource();
		TankTemplate template = GameContext.getTankApp().getTankTemplate(sourceTank.getTemplateId());
		if(template == null){
			return ;
		}
		int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.valueOf(template.getShellType_i()));
		int count = sourceTank.getGoods(goodsId);
		if(count > 0){
			return ;
		}
		sourceTank.putGoods(goodsId, count - 1);
		param.setFireType(FireType.goods);
		param.setItemId(goodsId);
	}
	
	
	private void useGoods(AbstractInstance tankInstance){
		try{
			TankAiTemplate aiTemplate = GameContext.getTankApp().getTankAiTemplate2(tankInstance.getLevel());
			if(aiTemplate == null){
				return ;
			}
			// int goodsRat = aiTemplate.getGoodsRat();
			int goodsRat = 0;
			if(goodsRat <= 0){
				return ;
			}
			int rat = RandomUtil.randomIntWithoutZero(100);
			if(rat > goodsRat || tankInstance.getBuffers().size() <= 0){
				return ;
			}
			int goodsId = GameContext.getGoodsApp().getSpecialGoodsId(GOODS_TYPE.battle_item);
			int count = tankInstance.getGoods(goodsId);
			if(count <= 0){
				return ;
			}
			tankInstance.putGoods(goodsId, count - 1);
			
			GoodsWarTemplate template = GameContext.getGoodsApp().getGoodsWarTemplate(goodsId);
			//广播使用效果
			STC_USE_WAR_EFFECT_MSG efffectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder()
					.setId(tankInstance.getId())
					.setGoodsId(goodsId)
					.build();
			 MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tankInstance.getMapInstanceId());
			if(mapInstance != null){
				mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
						BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE, efffectMsg.toByteArray());
			}
			//删除buff
			for(String strId : template.getDelBuff().split(Cat.comma)){
				if(Util.isEmpty(strId)){
					continue;
				}
				int delBuffId = Integer.parseInt(strId);
				GameContext.getBuffApp().remove(tankInstance, delBuffId);
			}
		}catch(Exception e){
			LogCore.runtime.error("", e);
		}
	}
}
