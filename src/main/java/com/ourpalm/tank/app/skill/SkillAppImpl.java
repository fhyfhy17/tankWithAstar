package com.ourpalm.tank.app.skill;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.script.skill.ImmuneBuffSkill;
import com.ourpalm.tank.script.skill.PassiveSkillAttackClearCD;
import com.ourpalm.tank.script.skill.PassiveSkillAttackDouble;
import com.ourpalm.tank.script.skill.PassiveSkillDeathAddBuff;
import com.ourpalm.tank.script.skill.PassiveSkillHitAddBuff;
import com.ourpalm.tank.script.skill.PassiveSkillHpLowerAddBuff;
import com.ourpalm.tank.script.skill.PassiveSkillImmuneHitAddBuff;
import com.ourpalm.tank.script.skill.PassiveSkillImmunePartHit;
import com.ourpalm.tank.script.skill.PassiveSkillOnAttackedClearCD;
import com.ourpalm.tank.script.skill.PassiveSkillOnDiedUnmarkKillCount;
import com.ourpalm.tank.script.skill.SelfAddBuffSkill;
import com.ourpalm.tank.script.skill.SelfAddDelBuffSkill;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.script.skill.TargetAddBuffSkill;
import com.ourpalm.tank.script.skill.TargetAddBuffThisHitPerSkill;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AbstractInstance;

public class SkillAppImpl implements SkillApp{
	
	private Map<Integer, SkillTemplate> allSkillMap = new HashMap<Integer, SkillTemplate>();
	
	

	@Override
	public void start() {
		try{
			String sourceFile = XlsSheetType.skillTemplate.getXlsFileName();
			String sheetName = XlsSheetType.skillTemplate.getSheetName();
			allSkillMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SkillTemplate.class);
		}catch(Exception e){
			LogCore.startup.error("", e);
		}
	}

	
	
	@Override
	public void stop() {
		
	}

	
	@Override
	public Skill createSkill(AbstractInstance tank, int skillId){
		SkillTemplate template = this.getSkillTemplate(skillId);
		if(template == null){
			return null;
		}
		
		switch(template.getLogic()){
			case 101 : return new SelfAddBuffSkill(tank, template);
			case 102 : return new SelfAddDelBuffSkill(tank, template);
			case 103 : return new ImmuneBuffSkill(tank, template);
			case 105 : return new TargetAddBuffSkill(tank, template);
			case 106 : return new TargetAddBuffThisHitPerSkill(tank, template);
			
			case 201 : return new PassiveSkillAttackClearCD(tank, template);
			case 202 : return new PassiveSkillAttackDouble(tank, template);
			case 203 : return new PassiveSkillDeathAddBuff(tank, template);
			case 204 : return new PassiveSkillHitAddBuff(tank, template);
			case 205 : return new PassiveSkillHpLowerAddBuff(tank, template);
			case 206 : return new PassiveSkillImmuneHitAddBuff(tank, template);
			case 207 : return new PassiveSkillImmunePartHit(tank, template);
			case 208 : return new PassiveSkillOnAttackedClearCD(tank, template);
			case 209 : return new PassiveSkillOnDiedUnmarkKillCount(tank, template);
		}
		
		return null;
	}


	@Override
	public SkillTemplate getSkillTemplate(int templateId) {
		return allSkillMap.get(templateId);
	}

}
