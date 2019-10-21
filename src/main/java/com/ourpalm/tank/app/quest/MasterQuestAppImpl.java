package com.ourpalm.tank.app.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.QuestDao;
import com.ourpalm.tank.domain.MasterQuest;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.template.MasterQuestTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class MasterQuestAppImpl implements MasterQuestApp{
	
	private QuestDao questDao;
	
	private Map<Integer, MasterQuestTemplate> templates = new HashMap<>();
	
	private Map<Integer, Long> versionMap = new ConcurrentHashMap<>();
	//<角色ID, List>
	private Map<Integer, List<MasterQuest>> roleQuests = new ConcurrentHashMap<>();
	
	@Override
	public void start() {
		loadTemplate();
	}
	
	private void loadTemplate() {
		String fileName = XlsSheetType.masterTemplate.getXlsFileName();
		String sheetName = XlsSheetType.masterTemplate.getSheetName();
		try {
			this.templates = XlsPojoUtil.sheetToGenericMap(fileName, sheetName,MasterQuestTemplate.class);
			
			for (MasterQuestTemplate template : templates.values()) {
				template.init();
				
				for(Integer branchId : template.getBranchList()) {
					if(!templates.containsKey(branchId)) {
						LogCore.startup.error("master quest: {}, branch: {} 不存在", template.getId(), branchId);
					}
				}
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	@Override
	public void stop() {
	}
	
	@Override
	public void createUser(int roleId, List<Integer> initQuestList) {
		List<MasterQuest> result = new ArrayList<>();
		for(Integer questId : initQuestList) {
			MasterQuest quest = MasterQuestFactory.createQuest(roleId, questId);
			if(quest == null) {
				continue;
			}
			result.add(quest);
		}
		
		questDao.saveAllMaster(roleId, result);
	}
	
	@Override
	public void login(int roleId) {
		List<MasterQuest> roleQuestList = questDao.getAllMaster(roleId);
		//容错处理
		if(roleQuestList == null) {
			roleQuests.put(roleId, new ArrayList<MasterQuest>());
			return;
		}
		
		List<MasterQuest> result = new ArrayList<>();
		boolean sendTip = false;
		for(MasterQuest questData : roleQuestList) {
			MasterQuest quest = MasterQuestFactory.buildQuest(roleId, questData, false);
			if(quest == null){
				continue;
			}
			quest.setRoleId(roleId);
			result.add(quest);
			
			//发送领取红点提示
			if(!sendTip && quest.getState() == QuestState.reward_VALUE) {
				sendTip = true;
				quest.sendRewardTip();
			}
		}
		
		roleQuests.put(roleId, result);
		
		//初始化版本号
		this.updateVersion(roleId);
	}
	
	//验证版本
	private void checkVersion(int roleId){
		/*long version = versionMap.get(roleId);
		long currVersion = this.questDao.getVersion(roleId);
		if(version != currVersion){
			this.login(roleId);
			versionMap.put(roleId, currVersion);
		}*/
	}
	
	//更新版本号
	private void updateVersion(int roleId){
		/*long version = questDao.updateVersion(roleId);
		this.versionMap.put(roleId, version);*/
	}

	@Override
	public void offline(int roleId) {
		roleQuests.remove(roleId);
		this.versionMap.remove(roleId);
	}
	

	@Override
	public MasterQuestTemplate getMasterQuestTemplate(int questId) {
		return templates.get(questId);
	}

	@Override
	public Result drawReward(int roleId, int questId) {
		
		MasterQuestTemplate template = getMasterQuestTemplate(questId);
		if (template == null) {
			return Result.newFailure("任务不存在");
		}
		
		//验证版本
		this.checkVersion(roleId);
		
		List<MasterQuest> questList = getRoleQuest(roleId);
		if (Util.isEmpty(questList)) {
			return Result.newFailure("您已经完成过该任务了");
		}
		MasterQuest quest = null;
		for (MasterQuest q : questList) {
			if (q.getId() == questId) {
				quest = q;
				break;
			}
		}
		if (quest == null) {
			return Result.newFailure("您已经完成过该任务了");
		}
		if (quest.getState() != QuestState.reward_VALUE) {
			return Result.newFailure("无法领取该任务奖励");
		}

		// 移除任务
		questList.remove(quest);
		questDao.deleteMaster(roleId, questId);
		
		//创建分支任务
		buildBranchQuest(roleId, template.getBranchList());
		
		// 奖励
		GameContext.getGoodsApp().addGoods(roleId, template.getGoodsMap(), StringUtil.buildLogOrigin(template.getTitle(), OutputType.masterQuestRewardInc.getInfo()));
		GameContext.getUserAttrApp().changeAttribute(roleId, template.getAttrList(), OutputType.masterQuestRewardInc.type(), StringUtil.buildLogOrigin(template.getTitle(), OutputType.masterQuestRewardInc.getInfo()));
		
		GameContext.getGameDBLogApp().getQuestLog().questFinish(roleId, questId, template.getTitle());
		return Result.newSuccess();
	}

	@Override
	public List<MasterQuest> getRoleQuest(int roleId) {
		//验证版本
		this.checkVersion(roleId);
		return roleQuests.get(roleId);
	}

	public void setQuestDao(QuestDao questDao) {
		this.questDao = questDao;
	}
	
	private void buildBranchQuest(int roleId, List<Integer> branchList) {
		if(branchList == null || branchList.isEmpty())
			return;
		
		for(Integer questId : branchList) {
			MasterQuest quest = MasterQuestFactory.createQuest(roleId, questId);
			if(quest == null) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
			roleQuests.get(roleId).add(quest);
		}
		
	}
	
	@Override
	public void roleBattle(int roleId) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleBattle()) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void roleBattleWin(int roleId, int warType) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleBattleWin(warType)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void tankBuy(int roleId, int tankId) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.tankBuy(tankId)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void totalKillTank(int roleId, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.totalKillTank(killCount)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void tankUpPart(int roleId) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.tankUpPart()) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void roleUpLevel(int roleId, int curLevel) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleUpLevel(curLevel)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void roleBattleIron(int roleId, int iron) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleBattleIron(iron)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void memberLottery(int roleId, int count) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.memberLottery(count)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void memberUpLevel(int roleId) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.memberUpLevel()) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}		
	}

	@Override
	public void roleMvp(int roleId, int warType) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleMvp(warType)) {
				continue;
			}
			
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void shopBuy(int roleId) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.shopBuy()) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void boxOpen(int roleId) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.boxOpen()) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
		
	}

	@Override
	public void roleBattleKillTank(int roleId, int warType, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleBattleKillTank(warType, killCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	
	@Override
	public void occupyFlagWin(int roleId, int warType) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.occupyFlagWin(warType)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveHelp(int roleId, int warType, int helpCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleAliveHelp(warType, helpCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
		
	}

	@Override
	public void oneBattleAliveContinueKill(int roleId, int warType, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleAliveContinueKill(warType, killCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveKill(int roleId, int warType, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleAliveKill(warType, killCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveFireBulletKill(int roleId, int warType, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleAliveFireBulletKill(warType, killCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void oneBattleDeadKill(int roleId, int warType, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleDeadKill(warType, killCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
		
	}

	@Override
	public void oneBattleKillOcccpyFlag(int roleId, int killCount) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleKillOcccpyFlag(killCount)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void totalBattleHonor(int roleId, int warType, int honor) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.totalBattleHonor(warType, honor)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
		
	}

	@Override
	public void oneBattleBearHurtPercentWin(int roleId, int warType, int percent) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleBearHurtPercentWin(warType, percent)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void oneBattleHelpHurtPercentWin(int roleId, int warType, int percent) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.oneBattleHelpHurtPercentWin(warType, percent)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void totalCashingIron(int roleId, int iron) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.totalCashingIron(iron)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void roleStartMember(int roleId, int quality) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleStartMember(quality)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void roleStartLevelMember(int roleId, int quality, int level) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleStartLevelMember(quality, level)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void roleLevelMedal(int roleId, int medalId, int count) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.roleLevelMedal(medalId, count)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}

	@Override
	public void teamBattle(int roleId, int warType) {
		List<MasterQuest> questList = getRoleQuest(roleId);
		for(MasterQuest quest : questList) {
			if(!quest.teamBattle(warType)) {
				continue;
			}
			questDao.saveMaster(roleId, quest);
		}
	}
}

