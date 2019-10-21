package com.ourpalm.tank.app.quest;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.domain.QuestPhase;
import com.ourpalm.tank.domain.MasterQuest;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.template.MasterQuestTemplate;

public class MasterQuestFactory {

private static Logger logger = LogCore.runtime;
	
	/** 创建任务对象 */
	public static MasterQuest createQuest(int roleId, int questId){
		MasterQuest quest = new MasterQuest();
		quest.setId(questId);
		quest.setRoleId(roleId);
		quest.setState(QuestState.accept_VALUE);
		return buildQuest(roleId, quest, true);
	}
	
	
	/** 初始化任务对象 */
	public static MasterQuest buildQuest(int roleId, MasterQuest quest, boolean initPhaseProgress){
		int questId = quest.getId();
		MasterQuestTemplate template = GameContext.getMasterQuestApp().getMasterQuestTemplate(questId);
		if(template == null){
			logger.error("主线支线任务不存在... questId = {}", questId);
			return null;
		}
		QuestPhase behavior =  QuestFactory.createQuestBehavior(template.getCondition(), 
				template.getParam1(), template.getParam2(), template.getLimit_i());
		if(behavior == null){
			logger.error("roleId = {}, 主线支线任务id = {}, 条件类型 = {} 没有执行类....", roleId, quest.getId(), template.getCondition());
			return null;
		}
		behavior.setProgress(quest.getProgress());
		behavior.setRoleId(roleId);
		quest.setQuestPhase(behavior);
		
		if(initPhaseProgress && behavior.initProgress()) {
			quest.updateProgress();
		}
		
		return quest;
	}
}
