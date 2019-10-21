package com.ourpalm.tank.app.quest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.QuestDao;
import com.ourpalm.tank.domain.Quest;
import com.ourpalm.tank.domain.RoleActive;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.QUEST_MSG.ActiveRewardsItem;
import com.ourpalm.tank.message.QUEST_MSG.QuestItem;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;
import com.ourpalm.tank.message.QUEST_MSG.STC_ACTIVE_DRAW_MSG;
import com.ourpalm.tank.message.QUEST_MSG.STC_QUEST_DRAW_MSG;
import com.ourpalm.tank.message.QUEST_MSG.STC_QUEST_LIST_MSG;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.QuestActiveTemplate;
import com.ourpalm.tank.template.QuestTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;

public class QuestAppImpl implements QuestApp {
	private Logger logger = LogCore.runtime;
	private final int DAY_ACTIVE_RWDS_TYPE = 1; 	//日活跃类型
	private final int WEEK_ACTIVE_RWDS_TYPE = 2;	//周活跃类型
	private QuestDao questDao;
	
	private Map<Integer, QuestTemplate> templates = new HashMap<Integer, QuestTemplate>();
	//<类型,List>>
	private Map<Integer, List<QuestActiveTemplate>> questActiveMap = new HashMap<>();
	
	//<角色ID, List>
	private Map<Integer, List<Quest>> roleQuests = new ConcurrentHashMap<>();
	private Map<Integer, Long> versionMap = new ConcurrentHashMap<>();
	
	
	@Override
	public void start() {
		this.loadTemplate();
		this.loadActiveTemplate();
	}

	@Override
	public void stop() {
	}
	
	//加载活跃奖励
	private void loadActiveTemplate(){
		String fileName = XlsSheetType.questActiveTemplate.getXlsFileName();
		String sheetName = XlsSheetType.questActiveTemplate.getSheetName();
		try{
			List<QuestActiveTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, QuestActiveTemplate.class);
			for(QuestActiveTemplate template : list){
				template.init();
				
				int type = template.getType();
				List<QuestActiveTemplate> activeList = this.questActiveMap.get(type);
				if(Util.isEmpty(activeList)){
					activeList = new ArrayList<>();
					this.questActiveMap.put(type, activeList);
				}
				activeList.add(template);
			}
			
		}catch(Exception e){
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	

	private void loadTemplate() {
		String fileName = XlsSheetType.questTemplate.getXlsFileName();
		String sheetName = XlsSheetType.questTemplate.getSheetName();
		try {
			this.templates = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, QuestTemplate.class);
			for (QuestTemplate template : templates.values()) {
				template.init();
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	@Override
	public QuestTemplate getQuestTemplate(int questId) {
		return this.templates.get(questId);
	}


	
	
	@Override
	public STC_QUEST_LIST_MSG getQuestList(int roleId){
		STC_QUEST_LIST_MSG.Builder builder = STC_QUEST_LIST_MSG.newBuilder();
		List<Quest> questList = this.roleQuests.get(roleId);
		for(Quest quest : questList){
			QuestItem.Builder item = QuestItem.newBuilder();
			 item.setId(quest.getId());
			 item.setProgress(quest.getProgress());
			 item.setState(QuestState.valueOf(quest.getState()));
			 item.setRemainCount(0); //可以去掉
			 builder.addQuestItems(item);
		}
		
		//今日活跃点
		RoleActive roleActive = questDao.getRoleActive(roleId);
		final int dayActive = roleActive.getDayActive();
		final int weekActive = roleActive.getWeekActive();
		final int dayRewardsIndex = roleActive.getDayRewardsIndex();
		final int weekRewardsIndex = roleActive.getWeekRewardsIndex();
		
		builder.setDayActive(dayActive);
		builder.setWeekActive(weekActive);
		
		//日活跃奖励
		List<QuestActiveTemplate> dayTemplateList = this.questActiveMap.get(DAY_ACTIVE_RWDS_TYPE);
		//按活跃点排序
		Collections.sort(dayTemplateList);
		for(QuestActiveTemplate template : dayTemplateList){
			QuestState state = this.getQuestState(dayActive, dayRewardsIndex, template.getActive());
			ActiveRewardsItem item = ActiveRewardsItem.newBuilder()
					.setNum(template.getActive())
					.setName(template.getName())
					.setExp(template.getExp())
					.setGold(template.getGold())
					.setIron(template.getIron())
					.setDiamond(template.getDiamond())
					.setState(state)
					.build();
			builder.addActiveRewards(item);
		}
		
		//周活跃奖励
		List<QuestActiveTemplate> weekTemplateList = this.questActiveMap.get(WEEK_ACTIVE_RWDS_TYPE);
		Collections.sort(weekTemplateList);
		
		for(QuestActiveTemplate template : weekTemplateList){
			QuestState state = this.getQuestState(weekActive, weekRewardsIndex, template.getActive());
			ActiveRewardsItem item = ActiveRewardsItem.newBuilder()
					.setNum(template.getActive())
					.setName(template.getName())
					.setExp(template.getExp())
					.setGold(template.getGold())
					.setIron(template.getIron())
					.setDiamond(template.getDiamond())
					.setState(state)
					.build();
			builder.addWeekRewards(item);
		}
		
		return builder.build();
	}
	
	private QuestState getQuestState(int currActive, int finishActive, int templateActive){
		QuestState state = QuestState.accept;
		if(finishActive >= templateActive){
			state = QuestState.finish;
		}else if(currActive >= templateActive){
			state = QuestState.reward;
		}
		return state;
	}
	
	
	
	//验证版本号
//	private void checkVersion(int roleId){
//		long currVersion = questDao.getVersion(roleId);
//		long version = versionMap.get(roleId);
//		logger.debug("任务版本 currVersion={}, version={}", currVersion, version);
//		if(currVersion != version){
//			logger.debug("任务版本发生改变, 重新加载...");
//			List<Quest> roleQuestList = initLoading(roleId);
//			roleQuests.put(roleId, roleQuestList);
//			versionMap.put(roleId, currVersion);
//		}
//	}
	
	//更新版本号
//	private void updateVersion(int roleId){
//		long version = questDao.updateVersion(roleId);
//		versionMap.put(roleId, version);
//	}
	
	

	@Override
	public STC_QUEST_DRAW_MSG drawReward(int roleId, int questId) {
		STC_QUEST_DRAW_MSG.Builder builder = STC_QUEST_DRAW_MSG.newBuilder();
		builder.setSuccess(false);
		QuestTemplate template = getQuestTemplate(questId);
		if (template == null) {
			builder.setInfo("任务不存在");
			return builder.build();
		}
		List<Quest> questList = roleQuests.get(roleId);
		if (Util.isEmpty(questList)) {
			builder.setInfo("您已经完成过该任务了");
			return builder.build();
		}
		Quest quest = null;
		for (Quest q : questList) {
			if (q.getId() == questId) {
				quest = q;
				break;
			}
		}
		if (quest.getState() != QuestState.reward_VALUE) {
			builder.setInfo("无法领取该任务奖励");
			return builder.build();
		}
		quest.setState(QuestState.finish_VALUE);
		//更新任务
		questDao.save(roleId, quest);
		
		// 奖励
		GameContext.getGoodsApp().addGoods(roleId, template.getGoodsMap(), StringUtil.buildLogOrigin(template.getTitle_s(), OutputType.questInc.getInfo()));
		GameContext.getUserAttrApp().changeAttribute(roleId, template.getAttrList(), OutputType.questInc.type(), StringUtil.buildLogOrigin(template.getTitle_s(), OutputType.questInc.getInfo()));
		
		//活跃点
		RoleActive roleActive = questDao.getRoleActive(roleId);
		roleActive.setDayActive(roleActive.getDayActive() + template.getActive());
		roleActive.setWeekActive(roleActive.getWeekActive() + template.getActive());
		questDao.saveRoleActive(roleActive);
		
		builder.setSuccess(true);
		builder.setInfo("");
		builder.setDayActive(roleActive.getDayActive());
		builder.setWeekActive(roleActive.getWeekActive());
		builder.setQuestId(questId);
		
		GameContext.getGameDBLogApp().getQuestLog().activeQuestFinish(roleId, questId, template.getTitle_s());
		return builder.build();
	}
	

	/** 领取活跃奖励 */
	@Override
	public STC_ACTIVE_DRAW_MSG drawActiveReward(int roleId, int type, int active){
		STC_ACTIVE_DRAW_MSG.Builder builder = STC_ACTIVE_DRAW_MSG.newBuilder();
		builder.setSuccess(false);
		
		RoleActive roleActive = questDao.getRoleActive(roleId);
		if(roleActive == null){
			builder.setInfo("数据错误");
			return builder.build();
		}
		QuestActiveTemplate template = getQuestActiveTemplate(type, active);
		if(template == null){
			builder.setInfo("该奖励不存在");
			return builder.build();
		}
		
		//判断是否可领取
		switch (type) {
			case DAY_ACTIVE_RWDS_TYPE:
				int dayActive = roleActive.getDayActive();
				if(dayActive < active || roleActive.getDayRewardsIndex() >= active){
					builder.setInfo("活跃点不足或该奖励已领取");
					return builder.build();
				}
				roleActive.setDayRewardsIndex(active);
				questDao.saveRoleActive(roleActive);
				break;
			
			case WEEK_ACTIVE_RWDS_TYPE:
				int weekActive = roleActive.getWeekActive();
				if(weekActive < active || roleActive.getWeekRewardsIndex() >= active){
					builder.setInfo("活跃点不足或该奖励已领取");
					return builder.build();
				}
				roleActive.setWeekRewardsIndex(active);
				questDao.saveRoleActive(roleActive);
				break;
		}
		
		//给奖励
		GameContext.getUserAttrApp().changeAttribute(roleId, template.getAttrList(), OutputType.questActiveRewardInc.type(), OutputType.questActiveRewardInc.getInfo()+active);
		
		builder.setSuccess(true);
		builder.setInfo("");
		builder.setType(type);
		builder.setNum(active);
		
		return builder.build();
	}
	
	
	private QuestActiveTemplate getQuestActiveTemplate(int type, int active){
		List<QuestActiveTemplate> list = questActiveMap.get(type);
		if(Util.isEmpty(list)){
			return null;
		}
		for(QuestActiveTemplate template : list){
			if(template.getActive() == active){
				return template;
			}
		}
		return null;
	}
	
	private QuestActiveTemplate getQuestNextActiveTemplate(int type, int active){
		List<QuestActiveTemplate> list = questActiveMap.get(type);
		if(Util.isEmpty(list)){
			return null;
		}
		for(QuestActiveTemplate template : list){
			if(template.getActive() > active){
				return template;
			}
		}
		return null;
	}
	
	@Override
	public void offline(int roleId) {
		this.roleQuests.remove(roleId);
		this.versionMap.remove(roleId);
	}

	@Override
	public void login(int roleId, boolean first) {
		List<Quest> roleQuestList = null;
		if (first) {
			roleQuestList = buildQuest(roleId);
		} else {
			roleQuestList = initLoading(roleId);
		}
		
		//触发所有任务的登录操作
		boolean sendTip = false;
		for(Quest quest : roleQuestList) {
			if(quest.login()) {
				questDao.save(roleId, quest);
			}
			//发送领取红点提示
			if(!sendTip && quest.getState() == QuestState.reward_VALUE) {
				sendTip = true;
				quest.sendRewardTip();
			}
		}
		
		//放入缓存
		this.roleQuests.put(roleId, roleQuestList);
		
		RoleActive roleActive = questDao.getRoleActive(roleId);
		//活跃点重置
		if(first){
			roleActive.setDayActive(0);
			roleActive.setDayRewardsIndex(0);
			
			//判断周
			long lastLoginTime = roleActive.getLastLoginTime();
			if(!DateUtil.isSameWeek(lastLoginTime, System.currentTimeMillis())){
				roleActive.setWeekActive(0);
				roleActive.setWeekRewardsIndex(0);
			}
			
			roleActive.setLastLoginTime(System.currentTimeMillis());
			questDao.saveRoleActive(roleActive);
		}
		
		//日活跃、周活跃可以领奖时，发送小红点提示
		int dayActiveIndex = roleActive.getDayRewardsIndex();
		QuestActiveTemplate template = getQuestNextActiveTemplate(DAY_ACTIVE_RWDS_TYPE, dayActiveIndex);
		if(template != null && roleActive.getDayActive() >= template.getActive()){
			sendTriggerTip(roleId);
		}
		
		int weekActiveIndex = roleActive.getWeekRewardsIndex();
		QuestActiveTemplate weekTemplate = getQuestNextActiveTemplate(WEEK_ACTIVE_RWDS_TYPE, weekActiveIndex);
		if(weekTemplate != null && roleActive.getWeekActive() >= weekTemplate.getActive()){
			sendTriggerTip(roleId);
		}
	}
	
	/** 初始化加载 */
	private List<Quest> initLoading(int roleId) {
		List<Quest> result = new ArrayList<>();
		List<Quest> roleQuestList = questDao.getAll(roleId);
		for(Quest questData : roleQuestList) {
			Quest quest = QuestFactory.buildQuest(roleId, questData, false);
			if(quest == null){
				continue;
			}
			quest.setRoleId(roleId);
			result.add(quest);
		}
		return result;
	}


	private List<Quest> buildQuest(int roleId) {
		List<Quest> result = new ArrayList<>();
		for (QuestTemplate template : templates.values()) {
			Quest quest = QuestFactory.createQuest(roleId, template.getId_i());
			if(quest == null){
				continue;
			}
			result.add(quest);
		}
		//保存任务
		questDao.saveAll(roleId, result);
		return result;
	}

	
	//发送红点提示
	private void sendTriggerTip(int roleId) {
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if(connect != null) {
			STC_PROMPT_MSG.Builder builder = STC_PROMPT_MSG.newBuilder();
			builder.setPrompt(PROMPT.ACTIVE);//活跃
			connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, builder.build().toByteArray());
		}
	}
	

	public void setQuestDao(QuestDao questDao) {
		this.questDao = questDao;
	}

	
	@Override
	public void tankUpPart(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.tankUpPart()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]坦克配件升级: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleBattle(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleBattle()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]进行战斗: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void memberLottery(int roleId, int count) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.memberLottery(count)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]进行抽奖: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void memberUpLevel(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.memberUpLevel()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]进行成员升级: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void shopBuy(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.shopBuy()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]进行商城购买: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void boxOpen(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.boxOpen()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]进行开启宝箱: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleBattleWin(int roleId, int warType) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleBattleWin(warType)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]某战场类型战斗胜利: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void roleBattleKillTank(int roleId, int warType, int killCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleBattleKillTank(warType, killCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]某战场类型下击杀坦克: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void occupyFlagWin(int roleId, int warType) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.occupyFlagWin(warType)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]某战场类型下占旗获胜: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void roleBattleIron(int roleId, int iron) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleBattleIron(iron)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]战场获得银币: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void tankBuy(int roleId, int tankId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.tankBuy(tankId)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]坦克购买: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void totalKillTank(int roleId, int count) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.totalKillTank(count)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]累计击杀坦克: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleUpLevel(int roleId, int curLevel) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleUpLevel(curLevel)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]账号等级: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleMvp(int roleId, int warType) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleMvp(warType)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]获得MVP: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveHelp(int roleId, int warType, int helpCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleAliveHelp(warType, helpCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]单场战斗不死下累计助攻: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveContinueKill(int roleId, int warType, int killCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleAliveContinueKill(warType, killCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]单场战斗不死下连续击杀: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveKill(int roleId, int warType, int killCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleAliveKill(warType, killCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]单场战斗不死下击杀数: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleAliveFireBulletKill(int roleId, int warType, int killCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleAliveFireBulletKill(warType, killCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]单场战斗不死下使用燃烧弹击杀: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleDeadKill(int roleId, int warType, int killCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleDeadKill(warType, killCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]单场战斗死亡复仇击杀: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleKillOcccpyFlag(int roleId, int killCount) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleKillOcccpyFlag(killCount)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]单场战斗击杀正在占旗者: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void totalBattleHonor(int roleId, int warType, int honor) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.totalBattleHonor(warType, honor)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]战斗累计获得荣誉: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleBearHurtPercentWin(int roleId, int warType, int percent) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleBearHurtPercentWin(warType, percent)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]一场战斗战场累计承受伤害超过N%，且本方获胜: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void oneBattleHelpHurtPercentWin(int roleId, int warType, int percent) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.oneBattleHelpHurtPercentWin(warType, percent)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]一场战斗对就战场协助伤害超过N%且获得胜利: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void totalCashingIron(int roleId, int iron) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.totalCashingIron(iron)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]通过兑换累计获取N银币: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleStartMember(int roleId, int quality) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleStartMember(quality)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]获得N星乘员: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void roleStartLevelMember(int roleId, int quality, int level) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleStartLevelMember(quality, level)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]拥有N级以上的N星乘员: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
		
	}

	@Override
	public void roleLevelMedal(int roleId, int medalId, int count) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleLevelMedal(medalId, count)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]获得N级勋章: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleMonthCard(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleMonthCard()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]拥有月卡: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void teamBattle(int roleId, int warType) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.teamBattle(warType)){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]组队进行战斗: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

	@Override
	public void roleYearCard(int roleId) {
		for(Quest quest : roleQuests.get(roleId)){
			if(!quest.roleYearCard()){
				continue;
			}
			if(logger.isDebugEnabled()){
				logger.debug("[任务]拥有年卡: roleId = {}, questId = {}, {}/{}", roleId, quest.getId(), 
						quest.getProgress(), quest.getLimit());
			}
			//保存进度
			questDao.save(roleId, quest);
		}
	}

}