package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 受到致命伤害时, 免疫伤害
 * 
 * @author wangkun
 *
 */
public class PassiveSkillImmuneHitAddBuff extends Skill{
	
	private int buffId;
	private int buffId2;
	
	public PassiveSkillImmuneHitAddBuff(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId = template.getAddBuff();
		this.buffId2 = template.getAddBuff2();
	}

	
	/** 抵挡伤害值 */
	@Override
	public float defenseDamageValue(AbstractInstance attck, float damage){
		//致命伤害
		if(damage >= selfTank.get(AttrType.hp)
				&& this.passiveTrigger()){
			
			this.notifyEffectMsg();
			
			logger.debug("触发致命伤害，免疫此伤害 damage = {}", damage);
			
			GameContext.getBuffApp().putBuff(attck, selfTank, buffId);
			GameContext.getBuffApp().putBuff(attck, selfTank, buffId2);
			return damage * -1;
		}
		
		return 0;
	}
}
