package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 攻击时有几率触发立即填弹
 * 
 * @author wangkun
 *
 */
public class PassiveSkillAttackClearCD extends Skill{

	public PassiveSkillAttackClearCD(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
	}

	
	/** 攻击时 */
	@Override
	public void fire(){
		if(this.passiveTrigger()){
			this.notifyEffectMsg();
			//重置开火冷却时间
			selfTank.clearFireCoolTime();
			//通知cd立即冷却
			selfTank.sendMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_CLEAR_FILE_COOL_VALUE);
			logger.debug("触发立即填弹...");
		}
		
		
	}
}
