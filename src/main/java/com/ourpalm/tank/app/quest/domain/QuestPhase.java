package com.ourpalm.tank.app.quest.domain;

import java.util.HashSet;
import java.util.Set;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.quest.IQuest;

public abstract class QuestPhase implements IQuest{
	
	/** 角色id */
	protected int roleId;
	
	/**完成数量*/
	protected int limit;
	
	/** 当前进度 */
	protected int progress;
	
	
	protected QuestPhase(int limit) {
		this.limit = limit;
	}


	public void setRoleId(int roleId){
		this.roleId = roleId;
	}
	
	
	public int getLimit(){
		return this.limit;
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean tankUpPart() {
		return false;
	}
	
	@Override
	public boolean roleBattle() {
		return false;
	}
	
	@Override
	public boolean memberLottery(int count) {
		return false;
	}
	
	@Override
	public boolean memberUpLevel() {
		return false;
	}
	
	@Override
	public boolean shopBuy() {
		return false;
	}

	@Override
	public boolean boxOpen() {
		return false;
	}
	
	@Override
	public boolean roleBattleWin(int warType) {
		return false;
	}

	@Override
	public boolean roleBattleKillTank(int warType, int killCount) {
		return false;
	}
	
	@Override
	public boolean occupyFlagWin(int warType) {
		return false;
	}
	
	@Override
	public boolean roleBattleIron(int iron) {
		return false;
	}
	

	@Override
	public boolean tankBuy(int tankId) {
		return false;
	}
	
	@Override
	public boolean totalKillTank(int count) {
		return false;
	}
	
	@Override
	public boolean roleUpLevel(int curLevel) {
		return false;
	}
	
	@Override
	public boolean roleMvp(int warType) {
		return false;
	}
	
	@Override
	public boolean oneBattleAliveHelp(int warType, int helpCount) {
		return false;
	}
	

	@Override
	public boolean oneBattleAliveContinueKill(int warType, int killCount) {
		return false;
	}

	@Override
	public boolean oneBattleAliveKill(int warType, int killCount) {
		return false;
	}

	@Override
	public boolean oneBattleAliveFireBulletKill(int warType, int killCount) {
		return false;
	}

	@Override
	public boolean oneBattleDeadKill(int warType, int killCount) {
		return false;
	}
	
	@Override
	public boolean oneBattleKillOcccpyFlag(int killCount) {
		return false;
	}
	
	@Override
	public boolean totalBattleHonor(int warType, int honor) {
		return false;
	}
	
	@Override
	public boolean oneBattleBearHurtPercentWin(int warType, int hurtPercent) {
		return false;
	}

	@Override
	public boolean oneBattleHelpHurtPercentWin(int warType, int percent) {
		return false;
	}

	@Override
	public boolean totalCashingIron(int iron) {
		return false;
	}

	@Override
	public boolean roleStartMember(int quality) {
		return false;
	}

	@Override
	public boolean roleStartLevelMember(int quality, int level) {
		return false;
	}

	@Override
	public boolean roleLevelMedal(int medalId, int count) {
		return false;
	}

	@Override
	public boolean totalMedalLevel(int totalLevel) {
		return false;
	}

	/** 是否完成 */
	public boolean hadFinish() {
		return this.progress >= limit;
	}
	
	/** 初始化进度 */
	public abstract boolean initProgress();
	
	public int getProgress() {
		return this.progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	protected Set<Integer> splitParamToSet(String param) {
		if(Util.isEmpty(param) || "0".equalsIgnoreCase(param)){
			return new HashSet<>();
		}
		
		Set<Integer> result = new HashSet<>();
		try{
			String[] params = param.split(Cat.comma);
			for(String paramStr : params){
				result.add(Integer.valueOf(paramStr));
			}
			return result;
		}catch(Exception e){
			String errorStr = String.format("任务条件错误, param=%s", param);
			LogCore.startup.error(errorStr, e);
			throw new IllegalArgumentException(errorStr, e);
		}
	}
	
	protected boolean isInvalid(String param) {
		if(Util.isEmpty(param)  || "0".equals(param) ) {
			return true;
		}
		return false;
	}


	@Override
	public boolean roleMonthCard() {
		return false;
	}
	
	@Override
	public boolean roleYearCard() {
		return false;
	}


	@Override
	public boolean teamBattle(int warType) {
		return false;
	}
	
}
