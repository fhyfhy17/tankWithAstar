package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 添加删除buff
 * 
 * @author wangkun
 *
 */
public class SelfAddDelBuffSkill extends Skill{
	
	private int buffId;
	private int delBuff1;
	private int delBuff2;

	public SelfAddDelBuffSkill(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId = template.getAddBuff();
		this.delBuff1 = template.getDelBuff1();
		this.delBuff2 = template.getDelBuff2();
	}

	@Override
	public void use(AbstractInstance target){
		if(! this.hadCoolTime()){
			return ;
		}
		if(this.triggerRat()){
			GameContext.getBuffApp().putBuff(selfTank, selfTank, buffId);
			GameContext.getBuffApp().remove(selfTank, delBuff1);
			GameContext.getBuffApp().remove(selfTank, delBuff2);
			this.notifyEffectMsg();
		}
	}
}
