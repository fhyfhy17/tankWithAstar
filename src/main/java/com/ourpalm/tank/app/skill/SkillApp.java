package com.ourpalm.tank.app.skill;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.SkillTemplate;
import com.ourpalm.tank.vo.AbstractInstance;

public interface SkillApp extends Service{

	SkillTemplate getSkillTemplate(int templateId);
	
	Skill createSkill(AbstractInstance tank, int skillId);
}
