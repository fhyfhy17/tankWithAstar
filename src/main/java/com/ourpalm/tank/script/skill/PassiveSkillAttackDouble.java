package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 有几率触发攻击翻倍
 * 
 * @author wangkun
 *
 */
public class PassiveSkillAttackDouble extends Skill{

	private float doubleRat ;
	
	public PassiveSkillAttackDouble(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.doubleRat = template.getParam();
	}
	
	
	
	/** 攻击伤害加成倍率 */
	@Override
	public float attackDamagePlusRat(HitParam hirParam){
		//普通穿甲攻击时
		if(hirParam.isHadChuanjia() 
				&& hirParam.getFireType() == FireType.fire
				&& this.passiveTrigger()){
			
			this.notifyEffectMsg();
			logger.debug("触发攻击翻4倍...");
			return doubleRat;
		}
		return 1;
	}

}
