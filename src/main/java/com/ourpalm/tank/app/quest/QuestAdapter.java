package com.ourpalm.tank.app.quest;

import com.ourpalm.tank.app.quest.domain.QuestPhase;
import com.ourpalm.tank.message.QUEST_MSG.QuestState;

public abstract class QuestAdapter implements IQuest{

	protected transient QuestPhase questPhase;
	
	public abstract int getState();
	public abstract void setState(int state);
	
	protected abstract void setProgress(int progress);
	
	public QuestPhase getQuestPhase() {
		return questPhase;
	}
	public void setQuestPhase(QuestPhase questPhase) {
		this.questPhase = questPhase;
	}
	
	
	/** 更新进度 */
	protected abstract void updateProgress();
	
	
	/** 是否已完成 */
	protected boolean hadComplete(){
		return this.getState() != QuestState.accept_VALUE;
	}
	
	
	@Override
	public boolean login() {
		if(hadComplete() || !this.questPhase.login()){
			return false;
		}
		
		this.updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean tankUpPart() {
		if(hadComplete() || !this.questPhase.tankUpPart()){
			return false;
		}
		
		this.updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean roleBattle() {
		if(hadComplete() || !this.questPhase.roleBattle()){
			return false;
		}
		updateProgress();
		
		return true;
	}
	
	
	
	
	@Override
	public boolean memberLottery(int count) {
		if(hadComplete() || !this.questPhase.memberLottery(count)){
			return false;
		}
		updateProgress();
		
		return true;
	}
	@Override
	public boolean memberUpLevel() {
		if(hadComplete() || !this.questPhase.memberUpLevel()){
			return false;
		}
		updateProgress();
		
		return true;
	}
	@Override
	public boolean shopBuy() {
		if(hadComplete() || !this.questPhase.shopBuy()){
			return false;
		}
		updateProgress();
		
		return true;
	}
	
	
	
	
	@Override
	public boolean boxOpen() {
		if(hadComplete() || !this.questPhase.boxOpen()){
			return false;
		}
		updateProgress();
		
		return true;
	}
	
	
	
	
	@Override
	public boolean roleBattleWin(int warType) {
		if(hadComplete() || !this.questPhase.roleBattleWin(warType)){
			return false;
		}
		updateProgress();
		
		return true;
	}
	@Override
	public boolean roleBattleKillTank(int warType, int killCount) {
		if(hadComplete() || !this.questPhase.roleBattleKillTank(warType, killCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean occupyFlagWin(int warType) {
		if(hadComplete() || !this.questPhase.occupyFlagWin(warType)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean roleBattleIron(int iron) {
		if(hadComplete() || !this.questPhase.roleBattleIron(iron)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	@Override
	public boolean tankBuy(int tankId) {
		if(hadComplete() || !this.questPhase.tankBuy(tankId)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	
	@Override
	public boolean totalKillTank(int count) {
		if(hadComplete() || !this.questPhase.totalKillTank(count)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean roleUpLevel(int curLevel) {
		if(hadComplete() || !this.questPhase.roleUpLevel(curLevel)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean roleMvp(int warType) {
		if(hadComplete() || !this.questPhase.roleMvp(warType)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean oneBattleAliveHelp(int warType, int helpCount) {
		if(hadComplete() || !this.questPhase.oneBattleAliveHelp(warType, helpCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean oneBattleAliveContinueKill(int warType, int killCount) {
		if(hadComplete() || !this.questPhase.oneBattleAliveContinueKill(warType, killCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean oneBattleAliveKill(int warType, int killCount) {
		if(hadComplete() || !this.questPhase.oneBattleAliveKill(warType, killCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	
	@Override
	public boolean oneBattleAliveFireBulletKill(int warType, int killCount) {
		if(hadComplete() || !this.questPhase.oneBattleAliveFireBulletKill(warType, killCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	
	@Override
	public boolean oneBattleDeadKill(int warType, int killCount) {
		if(hadComplete() || !this.questPhase.oneBattleDeadKill(warType, killCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	@Override
	public boolean oneBattleKillOcccpyFlag(int killCount) {
		if(hadComplete() || !this.questPhase.oneBattleKillOcccpyFlag(killCount)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	
	@Override
	public boolean totalBattleHonor(int warType, int honor) {
		if(hadComplete() || !this.questPhase.totalBattleHonor(warType, honor)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean oneBattleBearHurtPercentWin(int warType, int hurtPercent) {
		if(hadComplete() || !this.questPhase.oneBattleBearHurtPercentWin(warType, hurtPercent)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean oneBattleHelpHurtPercentWin(int warType, int percent) {
		if(hadComplete() || !this.questPhase.oneBattleHelpHurtPercentWin(warType, percent)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean totalCashingIron(int iron) {
		if(hadComplete() || !this.questPhase.totalCashingIron(iron)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean roleStartMember(int quality) {
		if(hadComplete() || !this.questPhase.roleStartMember(quality)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean roleStartLevelMember(int quality, int level) {
		if(hadComplete() || !this.questPhase.roleStartLevelMember(quality, level)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean roleLevelMedal(int medalId, int count) {
		if(hadComplete() || !this.questPhase.roleLevelMedal(medalId, count)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean totalMedalLevel(int totalLevel) {
		if(hadComplete() || !this.questPhase.totalMedalLevel(totalLevel)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean roleMonthCard() {
		if(hadComplete() || !this.questPhase.roleMonthCard()){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
	@Override
	public boolean roleYearCard() {
		if(hadComplete() || !this.questPhase.roleYearCard()){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	@Override
	public boolean teamBattle(int warType) {
		if(hadComplete() || !this.questPhase.teamBattle(warType)){
			return false;
		}
		
		updateProgress();
		
		return true;
	}
	
	
}
