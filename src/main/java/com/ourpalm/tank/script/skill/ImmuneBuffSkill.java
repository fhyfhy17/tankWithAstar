package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 删除buff，免疫buff
 * 
 * @author wangkun
 *
 */
public class ImmuneBuffSkill extends SelfAddBuffSkill{
	
	private int buffId1;
	private int buffId2;

	public ImmuneBuffSkill(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId1 = template.getDelBuff1();
		this.buffId2 = template.getDelBuff2();
	}
	
	/** 使用技能 */
	@Override
	public void use(AbstractInstance target){
		if(! this.hadCoolTime()){
			return ;
		}
		if(this.triggerRat()){
			GameContext.getBuffApp().putBuff(selfTank, selfTank, buffId);
			GameContext.getBuffApp().remove(selfTank, buffId1);
			GameContext.getBuffApp().remove(selfTank, buffId2);
			this.notifyEffectMsg();
		}
	}

	
	/** 免疫buff */
	@Override
	public boolean hadImmuneBuff(int buffId) {
		// 判断目标身上是否有本技能所加buff
		// 有就免疫所删除的buff
		if (selfTank.getBuff(this.buffId) != null) {
			this.notifyEffectMsg();
			logger.debug("触发免疫 buffId = {}", buffId);
			return buffId == buffId1 || buffId == buffId2;
		}
		return false;
	}
}
