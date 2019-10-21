package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.template.BuffTemplate;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 死亡加buff
 * 
 * @author wangkun
 *
 */
public class PassiveSkillDeathAddBuff extends Skill{

	private int buffId;
	
	public PassiveSkillDeathAddBuff(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.buffId = template.getAddBuff();
	}

	
	
	/** 死亡时触发效果 返回true外围应不发送死亡通知 */
	@Override
	public boolean death(){
		if(passiveTrigger()){
			BuffTemplate template = GameContext.getBuffApp().getBuffTemplate(buffId);
			if(template == null){
				return false;
			}
			
			GameContext.getBuffApp().putBuff(selfTank, selfTank, buffId);
			
			this.notifyEffectMsg();
			
			return true;
		}
		
		return false;
	}
}
