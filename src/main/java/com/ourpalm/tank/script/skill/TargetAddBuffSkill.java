package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;


/**
 * 对目标添加buff
 * 
 * @author wangkun
 *
 */
public class TargetAddBuffSkill extends Skill{

	protected int buffId;

	public TargetAddBuffSkill(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId = template.getAddBuff();
	}
	
	
	/** 使用技能 */
	@Override
	public void use(AbstractInstance target){
		if(! this.hadCoolTime()){
			return ;
		}
		if(this.triggerRat()){
			GameContext.getBuffApp().putBuff(selfTank, target, buffId);
			this.notifyEffectMsg();
			logger.debug("使用技能添加buff = {}", buffId);
		}
	}
}
