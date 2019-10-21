package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 命中对方有几率给对方加buff
 * 
 * @author wangkun
 *
 */
public class PassiveSkillHitAddBuff extends Skill{
	
	private int buffId;
	
	
	public PassiveSkillHitAddBuff(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId = template.getAddBuff();
	}

	
	
	/** 命中时 */
	@Override
	public void hit(FireType fireType, AbstractInstance target){
		//普通攻击时
		if (fireType == FireType.fire
				&& passiveTrigger()) {
			GameContext.getBuffApp().putBuff(selfTank, target, buffId);
			this.notifyEffectMsg();
		}
	}
	
}
