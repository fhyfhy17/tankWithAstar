package com.ourpalm.tank.app.quest;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.domain.QuestPhase;
import com.ourpalm.tank.app.quest.domain._11_RoleOccupyFlagWinPhase;
import com.ourpalm.tank.app.quest.domain._14_OneBattleBearHurtWinPhase;
import com.ourpalm.tank.app.quest.domain._1_RoleLoginPhase;
import com.ourpalm.tank.app.quest.domain._24_TankBuyPhase;
import com.ourpalm.tank.app.quest.domain._28_ShopBuyPhase;
import com.ourpalm.tank.app.quest.domain._32_RoleBattlePhase;
import com.ourpalm.tank.app.quest.domain._33_TotalKillTankPhase;
import com.ourpalm.tank.app.quest.domain._34_RoleMvpPhase;
import com.ourpalm.tank.app.quest.domain._35_RoleUpLevelPhase;
import com.ourpalm.tank.app.quest.domain._36_RoleBattleIronPhase;
import com.ourpalm.tank.app.quest.domain._37_OneBattleKillOccupyFlagPhase;
import com.ourpalm.tank.app.quest.domain._3_TankUpPartPhase;
import com.ourpalm.tank.app.quest.domain._41_TotalBattleHonorPhase;
import com.ourpalm.tank.app.quest.domain._42_OneBattleAliveHelpPhase;
import com.ourpalm.tank.app.quest.domain._43_OneBattleAliveContinueKillPhase;
import com.ourpalm.tank.app.quest.domain._44_OneBattleAliveKillPhase;
import com.ourpalm.tank.app.quest.domain._45_OneBattleAliveFireBulletKillPhase;
import com.ourpalm.tank.app.quest.domain._46_OneBattleDeadKillPhase;
import com.ourpalm.tank.app.quest.domain._47_TankBuyPhase;
import com.ourpalm.tank.app.quest.domain._48_OneBattleHelpHurtWinPhase;
import com.ourpalm.tank.app.quest.domain._4_MemberLotteryPhase;
import com.ourpalm.tank.app.quest.domain._51_TotalCashingIronPhase;
import com.ourpalm.tank.app.quest.domain._52_RoleStartMemberPhase;
import com.ourpalm.tank.app.quest.domain._53_RoleStartLevelMemberPhase;
import com.ourpalm.tank.app.quest.domain._54_RoleLevelMedalPhase;
import com.ourpalm.tank.app.quest.domain._56_TeamBattlePhase;
import com.ourpalm.tank.app.quest.domain._58_MonthCardPhase;
import com.ourpalm.tank.app.quest.domain._59_YearCardPhase;
import com.ourpalm.tank.app.quest.domain._5_MemberUpLevelPhase;
import com.ourpalm.tank.app.quest.domain._6_BoxOpenPhase;
import com.ourpalm.tank.app.quest.domain._7_RoleBattleWinPhase;
import com.ourpalm.tank.app.quest.domain._8_RoleBattleKillTankPhase;
import com.ourpalm.tank.domain.Quest;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.template.QuestTemplate;

public class QuestFactory {
	
	private static Logger logger = LogCore.runtime;
	
	/** 创建任务对象 */
	public static Quest createQuest(int roleId, int questId){
		Quest quest = new Quest();
		quest.setId(questId);
		quest.setRoleId(roleId);
		quest.setState(QuestState.accept_VALUE);
		return buildQuest(roleId, quest, true);
	}
	
	
	/** 初始化任务对象 */
	public static Quest buildQuest(int roleId, Quest quest, boolean initPhaseProgress){
		int questId = quest.getId();
		QuestTemplate template = GameContext.getQuestApp().getQuestTemplate(questId);
		if(template == null){
			logger.error("任务不存在... questId = {}", questId);
			return null;
		}
		QuestPhase behavior =  createQuestBehavior(template.getCondition(), 
				template.getParam1(), template.getParam2(), template.getLimit_i());
		if(behavior == null){
			logger.error("roleId = {}, 任务id = {}, 条件类型 = {} 没有执行类....", roleId, quest.getId(), template.getCondition());
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
	
	
	/** 初始化任务进度执行器 */
	public static QuestPhase createQuestBehavior(int conditionId, String param1, String param2, int limit){
		try{
			switch(conditionId) {
				case 1 : 			return new _1_RoleLoginPhase(limit);
				case 3 :			return new _3_TankUpPartPhase(limit);
				case 4 : 			return new _4_MemberLotteryPhase(limit);
				case 5 : 			return new _5_MemberUpLevelPhase(limit);
				case 6 :			return new _6_BoxOpenPhase(limit);
				case 7 :			return new _7_RoleBattleWinPhase(limit, param1);
				case 8 :			return new _8_RoleBattleKillTankPhase(limit, param1);
				case 11 :			return new _11_RoleOccupyFlagWinPhase(limit, param1);
				case 14 :			return new _14_OneBattleBearHurtWinPhase(limit, param1, param2);
				case 24 :			return new _24_TankBuyPhase(limit, param1);
				case 28 :			return new _28_ShopBuyPhase(limit);
				case 32 :			return new _32_RoleBattlePhase(limit);
				case 33 :			return new _33_TotalKillTankPhase(limit);
				case 34 :			return new _34_RoleMvpPhase(limit, param1);
				case 35 :			return new _35_RoleUpLevelPhase(limit);
				case 36 :			return new _36_RoleBattleIronPhase(limit);
				case 37 :			return new _37_OneBattleKillOccupyFlagPhase(limit, param2);
				case 41 :			return new _41_TotalBattleHonorPhase(limit, param1);
				case 42 :			return new _42_OneBattleAliveHelpPhase(limit, param1, param2);
				case 43 :			return new _43_OneBattleAliveContinueKillPhase(limit, param1, param2);
				case 44 :			return new _44_OneBattleAliveKillPhase(limit, param1, param2);
				case 45 :			return new _45_OneBattleAliveFireBulletKillPhase(limit, param1, param2);
				case 46 :			return new _46_OneBattleDeadKillPhase(limit, param1, param2);
				case 47 :			return new _47_TankBuyPhase(limit, param1);
				case 48 :			return new _48_OneBattleHelpHurtWinPhase(limit, param1, param2);
				case 51 :			return new _51_TotalCashingIronPhase(limit);
				case 52 : 			return new _52_RoleStartMemberPhase(limit, param1);
				case 53 : 			return new _53_RoleStartLevelMemberPhase(limit, param1, param2);
				case 54 : 			return new _54_RoleLevelMedalPhase(limit, param1);
				case 56 : 			return new _56_TeamBattlePhase(limit, param1);
				case 58 : 			return new _58_MonthCardPhase(limit);
				case 59 : 			return new _59_YearCardPhase(limit);
			default: return null;
			}
		}catch(Exception e){
			logger.error("任务阶段初始化异常, 条件condition = " + conditionId, e);
		}
		return null;
	}


}
