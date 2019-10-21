package com.ourpalm.tank.app.achievement;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.QuestFactory;
import com.ourpalm.tank.app.quest.domain.QuestPhase;
import com.ourpalm.tank.domain.Achievement;
import com.ourpalm.tank.domain.RoleAchievement;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.AchievementState;
import com.ourpalm.tank.template.AchievementTemplate;

public class AchievementFactory {
	private static Logger logger = LogCore.runtime;
	
	public static Achievement create(int roleId, RoleAchievement roleAch, AchievementTemplate template) {
		Achievement ach = new Achievement();
		ach.setId(template.getId_i());
		ach.setRoleId(roleId);
		ach.setGroupId(template.getGroup());
		ach.setState(AchievementState.accept_VALUE);
		
		boolean initPhase = false;
		
		//恢复数据
		if(roleAch.getStateMap().containsKey(ach.getId())) {
			ach.setState(roleAch.getState(ach.getId()));
			ach.setProgress(roleAch.getValue(template.getGroup()));
		} else {
			//新建成就
			initPhase = true;
		}
		
		return buildAchievement(roleId, ach, initPhase);
	}
	
	/** 初始化任务对象 */
	public static Achievement buildAchievement(int roleId, Achievement ach, boolean initPhase){
		int achId = ach.getId();
		AchievementTemplate template = GameContext.getAchievementApp().getTemplate(achId);
		if(template == null){
			logger.error("成就不存在... achId = {}", achId);
			return null;
		}
		QuestPhase behavior =  QuestFactory.createQuestBehavior(template.getCondition(), 
				template.getParam1(), template.getParam2(), template.getLimit_i());
		if(behavior == null){
			logger.error("roleId = {}, 成就id = {}, 条件类型 = {} 没有执行类....", roleId, achId, template.getCondition());
			return null;
		}
		behavior.setRoleId(roleId);
		behavior.setProgress(ach.getProgress());
		
		ach.setQuestPhase(behavior);
		ach.init();
		
		//新建成就，则初始化
		if(initPhase && behavior.initProgress()) {
			ach.updateProgress();
		}
		
		return ach;
	}
}
