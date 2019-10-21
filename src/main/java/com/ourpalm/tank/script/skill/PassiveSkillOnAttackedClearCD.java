package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 挨打时有几率触发立即填装
 *
 */
public class PassiveSkillOnAttackedClearCD extends Skill {

	public PassiveSkillOnAttackedClearCD(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
	}

	/**
	 * 被攻击时有几率触发立即填弹效果
	 */
	@Override
	public float defenseDamageValue(AbstractInstance attck, float damage) {
		//只有走开炮CD时才会触发此效果
		if(selfTank.hadFireCoolTime()){
			return 0;
		}
		
		if (this.passiveTrigger()) {
			this.notifyEffectMsg();
			// 重置开火冷却时间
			selfTank.clearFireCoolTime();
			// 通知cd立即冷却
			selfTank.sendMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, BATTLE_MSG.CMD_ID.STC_CLEAR_FILE_COOL_VALUE);
			logger.debug("触发立即填弹...");
		}
		return 0;
	}
}
