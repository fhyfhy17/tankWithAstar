package com.ourpalm.tank.script.skill;

import com.ourpalm.tank.app.battle.HitParam;
import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

/**
 * 向目标添加buff，本次输出总伤害加成
 *  
 * @author wangkun
 *
 */
public class TargetAddBuffThisHitPerSkill extends TargetAddBuffSkill{

	private float hitPre;
	
	public TargetAddBuffThisHitPerSkill(AbstractInstance selfTank, SkillTemplate template) {
		super(selfTank, template);
		this.hitPre = template.getParam();
	}

	
	/** 本次攻击伤害加成值 */
	public int attackDamagePlusValue(HitParam hirParam, int damage){
		
		//判断是否使用该技能触发伤害，否则不作处理
		FireType fireType = hirParam.getFireType();
		int skillId = hirParam.getItemId();
		
		if(fireType != FireType.skill
				&& skillId != this.getId()){
			return 0;
		}
		
		float value = damage *  hitPre;
		
		return (int)value ;
	}
}
