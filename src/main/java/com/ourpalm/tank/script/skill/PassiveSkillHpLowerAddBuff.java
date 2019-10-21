package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;


/**
 * 血量低于百分比时，添加buff
 * 
 * @author wangkun
 *
 */
public class PassiveSkillHpLowerAddBuff extends Skill{
	
	private int buffId;
	private float hpRat; 

	public PassiveSkillHpLowerAddBuff(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId = template.getAddBuff();
		this.hpRat = template.getParam();
	}
	
	
	/** 血量改变时 */
	@Override
	public void currHpChange(){
		float currHp = selfTank.get(AttrType.hp);
		float value = selfTank.get(AttrType.n_hpMax) * hpRat;
		if(currHp < value && passiveTrigger()){
			GameContext.getBuffApp().putBuff(selfTank, selfTank, buffId);
			this.notifyEffectMsg();
			logger.debug("触发 PassiveSkillHpLowerAddBuff ");
		}
	}

}
