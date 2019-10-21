package com.ourpalm.tank.app.quest;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.vo.AbstractInstance;

public class QuestTriggerAppImpl implements QuestTriggerApp {
	
	private static final Logger logger = LogCore.runtime;

	@Override
	public void tankUpPart(int roleId) {
		try{
			GameContext.getQuestApp().tankUpPart(roleId);
			GameContext.getMasterQuestApp().tankUpPart(roleId);
			GameContext.getAchievementApp().tankUpPart(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleBattle(int roleId) {
		try{
			GameContext.getQuestApp().roleBattle(roleId);
			GameContext.getMasterQuestApp().roleBattle(roleId);
			GameContext.getAchievementApp().roleBattle(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void memberLottery(int roleId, int count) {
		try{
			GameContext.getQuestApp().memberLottery(roleId, count);
			GameContext.getMasterQuestApp().memberLottery(roleId, count);
			GameContext.getAchievementApp().memberLottery(roleId, count);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void memberUpLevel(int roleId) {
		try{
			GameContext.getQuestApp().memberUpLevel(roleId);
			GameContext.getMasterQuestApp().memberUpLevel(roleId);
			GameContext.getAchievementApp().memberUpLevel(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void shopBuy(int roleId) {
		try{
			GameContext.getQuestApp().shopBuy(roleId);
			GameContext.getMasterQuestApp().shopBuy(roleId);
			GameContext.getAchievementApp().shopBuy(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void boxOpen(int roleId) {
		try{
			GameContext.getQuestApp().boxOpen(roleId);
			GameContext.getMasterQuestApp().boxOpen(roleId);
			GameContext.getAchievementApp().boxOpen(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleBattleWin(int roleId, int warType) {
		try{
			GameContext.getQuestApp().roleBattleWin(roleId, warType);
			GameContext.getMasterQuestApp().roleBattleWin(roleId, warType);
			GameContext.getAchievementApp().roleBattleWin(roleId, warType);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleBattleKillTank(int roleId, int warType, int killCount) {
		try{
			GameContext.getQuestApp().roleBattleKillTank(roleId, warType, killCount);
			GameContext.getMasterQuestApp().roleBattleKillTank(roleId, warType, killCount);
			GameContext.getAchievementApp().roleBattleKillTank(roleId, warType, killCount);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void occupyFlagWin(int roleId, int warType) {
		try{
			GameContext.getQuestApp().occupyFlagWin(roleId, warType);
			GameContext.getMasterQuestApp().occupyFlagWin(roleId, warType);
			GameContext.getAchievementApp().occupyFlagWin(roleId, warType);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleBattleIron(int roleId, int iron) {
		try{
			GameContext.getQuestApp().roleBattleIron(roleId, iron);
			GameContext.getMasterQuestApp().roleBattleIron(roleId, iron);
			GameContext.getAchievementApp().roleBattleIron(roleId, iron);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void tankBuy(int roleId, int tankId) {
		try{
			GameContext.getQuestApp().tankBuy(roleId, tankId);
			GameContext.getMasterQuestApp().tankBuy(roleId, tankId);
			GameContext.getAchievementApp().tankBuy(roleId, tankId);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void totalKillTank(int roleId, int count) {
		try{
			GameContext.getQuestApp().totalKillTank(roleId, count);
			GameContext.getMasterQuestApp().totalKillTank(roleId, count);
			GameContext.getAchievementApp().totalKillTank(roleId, count);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void roleUpLevel(int roleId, int curLevel) {
		try{
			GameContext.getQuestApp().roleUpLevel(roleId, curLevel);
			GameContext.getMasterQuestApp().roleUpLevel(roleId, curLevel);
			GameContext.getAchievementApp().roleUpLevel(roleId, curLevel);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleMvp(int roleId, int warType) {
		try{
			GameContext.getQuestApp().roleMvp(roleId, warType);
			GameContext.getMasterQuestApp().roleMvp(roleId, warType);
			GameContext.getAchievementApp().roleMvp(roleId, warType);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void oneBattleAliveHelp(int roleId, int warType, int helpCount) {
		try{
			GameContext.getQuestApp().oneBattleAliveHelp(roleId, warType, helpCount);
			GameContext.getMasterQuestApp().oneBattleAliveHelp(roleId, warType, helpCount);
			GameContext.getAchievementApp().oneBattleAliveHelp(roleId, warType, helpCount);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void oneBattleAliveContinueKill(int roleId, int warType, int killCount) {
		try{
			GameContext.getQuestApp().oneBattleAliveContinueKill(roleId, warType, killCount);
			GameContext.getMasterQuestApp().oneBattleAliveContinueKill(roleId, warType, killCount);
			GameContext.getAchievementApp().oneBattleAliveContinueKill(roleId, warType, killCount);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void oneBattleAliveKill(int roleId, int warType, int killCount) {
		try{
			GameContext.getQuestApp().oneBattleAliveKill(roleId, warType, killCount);
			GameContext.getMasterQuestApp().oneBattleAliveKill(roleId, warType, killCount);
			GameContext.getAchievementApp().oneBattleAliveKill(roleId, warType, killCount);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void oneBattleAliveFireBulletKill(int roleId, int warType, int killCount) {
		try{
			GameContext.getQuestApp().oneBattleAliveFireBulletKill(roleId, warType, killCount);
			GameContext.getMasterQuestApp().oneBattleAliveFireBulletKill(roleId, warType, killCount);
			GameContext.getAchievementApp().oneBattleAliveFireBulletKill(roleId, warType, killCount);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void oneBattleDeadKill(int roleId, int warType, int killCount) {
		try{
			GameContext.getQuestApp().oneBattleDeadKill(roleId, warType, killCount);
			GameContext.getMasterQuestApp().oneBattleDeadKill(roleId, warType, killCount);
			GameContext.getAchievementApp().oneBattleDeadKill(roleId, warType, killCount);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void oneBattleKillOcccpyFlag(int roleId, int killCount) {
		try{
			GameContext.getQuestApp().oneBattleKillOcccpyFlag(roleId, killCount);
			GameContext.getMasterQuestApp().oneBattleKillOcccpyFlag(roleId, killCount);
			GameContext.getAchievementApp().oneBattleKillOcccpyFlag(roleId, killCount);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void totalBattleHonor(int roleId, int warType, int honor) {
		try{
			GameContext.getQuestApp().totalBattleHonor(roleId, warType, honor);
			GameContext.getMasterQuestApp().totalBattleHonor(roleId, warType, honor);
			GameContext.getAchievementApp().totalBattleHonor(roleId, warType, honor);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void oneBattleBearHurtPercentWin(int roleId, int warType, int percent) {
		try{
			GameContext.getQuestApp().oneBattleBearHurtPercentWin(roleId, warType, percent);
			GameContext.getMasterQuestApp().oneBattleBearHurtPercentWin(roleId, warType, percent);
			GameContext.getAchievementApp().oneBattleBearHurtPercentWin(roleId, warType, percent);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void oneBattleHelpHurtPercentWin(int roleId, int warType, int percent) {
		try{
			GameContext.getQuestApp().oneBattleHelpHurtPercentWin(roleId, warType, percent);
			GameContext.getMasterQuestApp().oneBattleHelpHurtPercentWin(roleId, warType, percent);
			GameContext.getAchievementApp().oneBattleHelpHurtPercentWin(roleId, warType, percent);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void totalCashingIron(int roleId, int iron) {
		try{
			GameContext.getQuestApp().totalCashingIron(roleId, iron);
			GameContext.getMasterQuestApp().totalCashingIron(roleId, iron);
			GameContext.getAchievementApp().totalCashingIron(roleId, iron);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void roleStartMember(int roleId, int quality) {
		try{
			GameContext.getQuestApp().roleStartMember(roleId, quality);
			GameContext.getMasterQuestApp().roleStartMember(roleId, quality);
			GameContext.getAchievementApp().roleStartMember(roleId, quality);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleStartLevelMember(int roleId, int quality, int level) {
		try{
			GameContext.getQuestApp().roleStartLevelMember(roleId, quality, level);
			GameContext.getMasterQuestApp().roleStartLevelMember(roleId, quality, level);
			GameContext.getAchievementApp().roleStartLevelMember(roleId, quality, level);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void roleLevelMedal(int roleId, int medalId, int count) {
		try{
			GameContext.getQuestApp().roleLevelMedal(roleId, medalId, count);
			GameContext.getMasterQuestApp().roleLevelMedal(roleId, medalId, count);
			GameContext.getAchievementApp().roleLevelMedal(roleId, medalId, count);
		}catch(Exception e){
			logger.error("", e);
		}
		
	}

	@Override
	public void battleResult(int roleId, BattleResultRecord record) {
		try{
			int warType = record.getWarType();
			AbstractInstance tank = record.getTank();
			if(record.isWin()){
				//战斗胜利
				this.roleBattleWin(roleId, warType);
				//一场战斗对就战场协助伤害超过N%且获得胜利
				this.oneBattleHelpHurtPercentWin(roleId, warType, record.getHelpDamagePercent());
				//一场战斗战场累计承受伤害排名，且本方获胜
				this.oneBattleBearHurtPercentWin(roleId, warType, record.getReceiveDamagePercent());
			}
			
			if(record.isFlagWin()){
				//占旗获胜
				this.occupyFlagWin(roleId, warType);
			}
			
			//获得mvp
			if(record.isMvp()){
				this.roleMvp(roleId, warType);
			}
			
			//不死情况下
			if(tank.getDeathCount() <= 0){
				//一场对应战场整场战斗不死，获得N个助攻
				this.oneBattleAliveHelp(roleId, warType, tank.getHelpKill());
				//一场对应战场整场战斗不死，连杀N个敌人
				this.oneBattleAliveContinueKill(roleId, warType, tank.getMaxContinueKillCount());
				//一场对应战场整场战斗不死，击杀N个敌人
				this.oneBattleAliveKill(roleId, warType, tank.getKillCount());
				//一场对应战场整场战斗不死，使用燃烧弹击杀N个敌人
				this.oneBattleAliveFireBulletKill(roleId, warType, record.getFireBulletKillCount());
			}
			
			//参与N场战斗
			this.roleBattle(roleId);
			//战场击杀N辆坦克
			this.roleBattleKillTank(roleId, warType, tank.getKillCount());
			//累计击杀n辆坦克
			this.totalKillTank(roleId, tank.getKillCount());
			//战场死亡复仇同时炸死N个敌人
			this.oneBattleDeadKill(roleId, warType, record.getDeadKillMaxCount());
			//一场战斗击杀N个正在夺旗的敌人
			this.oneBattleKillOcccpyFlag(roleId, record.getKillOcccpyFlagCount());
			
			//组队参与战斗
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			String teamId = role.getTeamId();
			if(!Util.isEmpty(teamId)){
				MatchTeam matchTeam = GameContext.getMatchApp().getMatchTeam(teamId);
				if(matchTeam != null && matchTeam.getTeamMap().size() > 1){
					this.teamBattle(roleId, warType);
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleMonthCard(int roleId) {
		try{
			GameContext.getQuestApp().roleMonthCard(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void teamBattle(int roleId, int warType) {
		try{
			GameContext.getQuestApp().teamBattle(roleId, warType);
			GameContext.getMasterQuestApp().teamBattle(roleId, warType);
		}catch(Exception e){
			logger.error("", e);
		}
	}

	@Override
	public void roleYearCard(int roleId) {
		try{
			GameContext.getQuestApp().roleYearCard(roleId);
		}catch(Exception e){
			logger.error("", e);
		}
	}



}
