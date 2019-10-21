package com.ourpalm.tank.app.achievement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.AchievementDao;
import com.ourpalm.tank.domain.Achievement;
import com.ourpalm.tank.domain.RoleAchievement;
import com.ourpalm.tank.message.ACHIEVEMENT_MSG.AchievementState;
import com.ourpalm.tank.template.AchievementTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class AchievementAppImpl implements AchievementApp {
	
	private Logger logger = LogCore.runtime;
	
	private AchievementDao achievementDao;
	
	private Map<Integer, AchievementTemplate> templates = new HashMap<>();
	private Map<Integer, RoleAchievement> roleAchievementMap = new ConcurrentHashMap<>();
	private Map<Integer, Long> versionMap = new ConcurrentHashMap<>();
	private Map<Integer, List<Achievement>> roleAchievements = new ConcurrentHashMap<>();	//<roleId, List<Achievement>>

	@Override
	public void start() {
		this.loadTemplate();
	}

	@Override
	public void stop() {
	}

	private void loadTemplate() {
		String fileName = XlsSheetType.achievementTemplate.getXlsFileName();
		String sheetName = XlsSheetType.achievementTemplate.getSheetName();
		try {
			this.templates = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, AchievementTemplate.class);
			for (AchievementTemplate template : templates.values()) {
				template.init();
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	@Override
	public AchievementTemplate getTemplate(int questId) {
		return this.templates.get(questId);
	}

	@Override
	public void login(int roleId, boolean first) {
		RoleAchievement ach = achievementDao.get(roleId);
		if(ach == null) {
			ach = new RoleAchievement();
		}
		
		initLoad(roleId, ach);
	}
	
	private void initLoad(int roleId, RoleAchievement roleAch) {
		List<Achievement> achList = new ArrayList<>();
		
		boolean sendTip = false;
		for(AchievementTemplate template : templates.values()) {
			Achievement ach = AchievementFactory.create(roleId, roleAch, template);
			if(ach == null) {
				continue;
			}
			
			achList.add(ach);
			
			//加入新成就
			if(!roleAch.getStateMap().containsKey(ach.getId()))
				roleAch.putState(ach.getId(), ach.getState());
			
			if(!sendTip && roleAch.getState(ach.getId()) == AchievementState.reward_VALUE) {
				sendTip = true;
				ach.sendRewardTip();
			}
		}
		
		achievementDao.save(roleId, roleAch);
		
		roleAchievementMap.put(roleId, roleAch);
		roleAchievements.put(roleId, achList);
	}
	
	
	@Override
	public void offline(int roleId) {
		this.roleAchievementMap.remove(roleId);
		this.roleAchievements.remove(roleId);
		this.versionMap.remove(roleId);
	}

	@Override
	public List<Achievement> getRoleAchievement(int roleId) {
		return roleAchievements.get(roleId);
	}

	@Override
	public Result drawReward(int roleId, int achievementId) {
		AchievementTemplate template = getTemplate(achievementId);
		if (template == null) {
			return Result.newFailure(Tips.ACHIEVEMENT_ID_ERROR);
		}
		
		RoleAchievement ach = this.roleAchievementMap.get(roleId);
		if (ach == null) {
			return Result.newFailure(Tips.ACHIEVEMENT_INFO_ERROR);
		}
		
		if (ach.getState(achievementId) != AchievementState.reward_VALUE) {
			return Result.newFailure(Tips.ACHIEVEMENT_CANNOT_REWARD);
		}
		
		ach.putState(achievementId, AchievementState.complete_VALUE);
		achievementDao.save(roleId, ach);
		
		List<Achievement> achList = roleAchievements.get(roleId);
		for(Achievement achievement : achList) {
			if(achievement.getId() == achievementId) {
				achievement.setState(AchievementState.complete_VALUE);
				break;
			}
		}
		
		logger.debug("角色={}, 领取成就={}, 奖励", roleId, achievementId);
		
		GameContext.getGoodsApp().addGoods(roleId, template.getGoodsMap(), StringUtil.buildLogOrigin(template.getTitle_s(), OutputType.achievementDrawInc.getInfo()));
		GameContext.getUserAttrApp().changeAttribute(roleId, template.getAttrList(), OutputType.achievementDrawInc.type(), StringUtil.buildLogOrigin(template.getTitle_s(), OutputType.achievementDrawInc.getInfo()));

		GameContext.getGameDBLogApp().getQuestLog().achievementFinish(roleId, achievementId, template.getTitle_s());
		return Result.newSuccess();
	}

	public void setAchievementDao(AchievementDao achievementDao) {
		this.achievementDao = achievementDao;
	}

	@Override
	public void tankBuy(int roleId, int tankId) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.tankBuy(tankId)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}


	@Override
	public void totalKillTank(int roleId, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.totalKillTank(killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void roleMvp(int roleId, int warType) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleMvp(warType)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}	
	}

	@Override
	public void roleBattleIron(int roleId, int iron) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleBattleIron(iron)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void oneBattleKillOcccpyFlag(int roleId, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleKillOcccpyFlag(killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}		
	}

	@Override
	public void totalBattleHonor(int roleId, int warType, int honor) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.totalBattleHonor(warType, honor)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}		
	}

	@Override
	public void oneBattleAliveHelp(int roleId, int warType, int helpCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleAliveHelp(warType, helpCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}		
	}

	@Override
	public void oneBattleAliveContinueKill(int roleId, int warType, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleAliveContinueKill(warType, killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}		
	}

	@Override
	public void oneBattleAliveKill(int roleId, int warType, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleAliveKill(warType, killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}	
	}

	@Override
	public void oneBattleAliveFireBulletKill(int roleId, int warType, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleAliveFireBulletKill(warType, killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}	
	}

	@Override
	public void oneBattleDeadKill(int roleId, int warType, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleDeadKill(warType, killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}		
	}

	@Override
	public void tankUpPart(int roleId) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.tankUpPart()) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void roleBattle(int roleId) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleBattle()) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void memberLottery(int roleId, int count) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.memberLottery(count)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void memberUpLevel(int roleId) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.memberUpLevel()) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void shopBuy(int roleId) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.shopBuy()) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void boxOpen(int roleId) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.boxOpen()) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void roleBattleWin(int roleId, int warType) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleBattleWin(warType)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void roleBattleKillTank(int roleId, int warType, int killCount) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleBattleKillTank(warType, killCount)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void occupyFlagWin(int roleId, int warType) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.occupyFlagWin(warType)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void roleUpLevel(int roleId, int curLevel) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleUpLevel(curLevel)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void oneBattleBearHurtPercentWin(int roleId, int warType, int percent) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleBearHurtPercentWin(warType, percent)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void oneBattleHelpHurtPercentWin(int roleId, int warType, int percent) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.oneBattleHelpHurtPercentWin(warType, percent)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void totalCashingIron(int roleId, int iron) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.totalCashingIron(iron)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

	@Override
	public void roleStartMember(int roleId, int quality) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleStartMember(quality)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void roleStartLevelMember(int roleId, int quality, int level) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleStartLevelMember(quality, level)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
		
	}

	@Override
	public void roleLevelMedal(int roleId, int medalId, int count) {
		List<Achievement> achList = roleAchievements.get(roleId);
		if(Util.isEmpty(achList))
			return;
		
		RoleAchievement roleAch = roleAchievementMap.get(roleId);
		
		boolean t = false;
		for(Achievement ach : achList) {
			if(ach.roleLevelMedal(medalId, count)) {
				t = true;
				roleAch.putValue(ach.getGroupId(), ach.getProgress());
				roleAch.putState(ach.getId(), ach.getState());
			}
		}
		
		if(t){
			achievementDao.save(roleId, roleAch);
		}
	}

}
