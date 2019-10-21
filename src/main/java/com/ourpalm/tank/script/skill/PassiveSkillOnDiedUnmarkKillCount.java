package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

public class PassiveSkillOnDiedUnmarkKillCount extends Skill {

	public PassiveSkillOnDiedUnmarkKillCount(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
	}

	@Override
	public boolean hadIngoreKillCountWhenDeath() {
		if(this.passiveTrigger()){
			this.notifyEffectMsg();
			logger.debug("触发不屈...");
			return true;
		}
		return false;
	}
}
