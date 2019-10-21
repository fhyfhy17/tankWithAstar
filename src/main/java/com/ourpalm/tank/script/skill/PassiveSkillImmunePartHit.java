package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 免疫部件受损
 * 被动技能
 * 
 * @author wangkun
 *
 */
public class PassiveSkillImmunePartHit extends Skill{

	public PassiveSkillImmunePartHit(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
	}

	
	
	/** 免疫伤害效果(履带断裂、油箱起火、发动机损坏、弹药架损坏、炮塔受损) */
	@Override
	public boolean hadImmuneHitEffect(){
		if( this.passiveTrigger() ){
			this.notifyEffectMsg();
		}
		
		return true;
	}
}
