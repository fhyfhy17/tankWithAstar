package com.ourpalm.tank.domain;

public class RoleWarInfo {

	private int roleId; // 角色ID
	private int rankAllKillCount; // 排位赛总击杀数
	private int rankAllDedCount; // 排位赛总死亡数
	private int rankMaxOneLifeKillCount; // 排位赛一生最大击杀数
	private int rankMaxKillCount; // 排位赛单场最大击杀数
	private int rankMaxHelperCount; // 排位赛单场最大助攻数
	private int rankMaxOutputHurt; // 排位赛单场最大输出伤害数
	private int rankMaxInputHurt; // 排位赛单场最大伤害承受数
	private int rankWinCount; // 排位赛获胜次数
	private int rankLostCount; // 排位赛失败次数
	private int rankLoopWinCount; // 排位赛连胜次数
	private int rankMaxLoopWinCount; // 排位赛最高连胜次数
	private int rankMvp; // 排位赛MVP次数

	private int battleWinCount; // 对战模式获胜次数
	private int battleLostCount; // 对战模式失败次数
	private int freeReviveCount; // 免费复活次数

	private int rankKill;// 排行榜功能击杀数
	private int rankHelper;// 排行榜功能助攻数
	private int rankWin;// 排行榜功能，胜场数

	private int loopWinCount;  // 连赢次数
	private int loopLoseCount; // 连输次数

	private int preseason = 1;// 新手赛 -1为不是新手 1，2，3，4，5 为要进行的新手赛场次

	public void accumuBattleWinCount() {
		this.battleWinCount += 1;
		if (this.preseason < 5 && this.preseason > 0) {
			this.preseason += 1;
		} else {
			this.preseason = -1;
		}
	}

	public void accumuBattleLostCount() {
		this.battleLostCount += 1;
		if (this.preseason < 5 && this.preseason > 0) {
			this.preseason += 1;
		} else {
			this.preseason = -1;
		}
	}

	public void accumuRankAllKillCount(int killCount) {
		this.rankAllKillCount += killCount;
	}

	public void accumuRankAllDedCount(int dedCount) {
		this.rankAllDedCount += dedCount;
	}

	/** 累计一个生命周期最大击杀数 */
	public void accumuRankMaxOneLifeKillCount(int count) {
		if (count > this.rankMaxOneLifeKillCount) {
			this.rankMaxOneLifeKillCount = count;
		}
	}

	/** 累计排位赛单场最大击杀数 */
	public void accumuRankMaxKillCount(int count) {
		if (count > this.rankMaxKillCount) {
			this.rankMaxKillCount = count;
		}
	}

	/** 累计最高助攻数 */
	public void accumuRankMaxHelperCount(int count) {
		if (count > this.rankMaxHelperCount) {
			this.rankMaxHelperCount = count;
		}
	}

	/** 累计最大伤害输出 */
	public void accumuRankMaxOutputHurt(int hurt) {
		if (hurt > this.rankMaxOutputHurt) {
			this.rankMaxOutputHurt = hurt;
		}
	}

	/** 累计最大承受伤害 */
	public void accumuRankMaxInputHurt(int hurt) {
		if (hurt > this.rankMaxInputHurt) {
			this.rankMaxInputHurt = hurt;
		}
	}

	public void accumuRankWinCount() {
		this.rankWinCount += 1;
	}

	public void accumuRankLostCount() {
		this.rankLostCount += 1;
	}

	/** 累计连胜次数 */
	public void accumuRankLoopWinCount() {
		this.rankLoopWinCount += 1;
		if (this.rankLoopWinCount > this.rankMaxLoopWinCount) {
			this.rankMaxLoopWinCount = this.rankLoopWinCount;
		}
	}

	public void accumuLoopWinCount() {
		this.loopWinCount += 1;
	}

	public void accumuLoopLoseCount() {
		this.loopLoseCount += 1;
	}

	public void accumuRankMvp() {
		this.rankMvp += 1;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getRankAllKillCount() {
		return rankAllKillCount;
	}

	public void setRankAllKillCount(int rankAllKillCount) {
		this.rankAllKillCount = rankAllKillCount;
	}

	public int getRankAllDedCount() {
		return rankAllDedCount;
	}

	public void setRankAllDedCount(int rankAllDedCount) {
		this.rankAllDedCount = rankAllDedCount;
	}

	public int getRankMaxOneLifeKillCount() {
		return rankMaxOneLifeKillCount;
	}

	public void setRankMaxOneLifeKillCount(int rankMaxOneLifeKillCount) {
		this.rankMaxOneLifeKillCount = rankMaxOneLifeKillCount;
	}

	public int getRankMaxKillCount() {
		return rankMaxKillCount;
	}

	public void setRankMaxKillCount(int rankMaxKillCount) {
		this.rankMaxKillCount = rankMaxKillCount;
	}

	public int getRankMaxHelperCount() {
		return rankMaxHelperCount;
	}

	public void setRankMaxHelperCount(int rankMaxHelperCount) {
		this.rankMaxHelperCount = rankMaxHelperCount;
	}

	public int getRankMaxOutputHurt() {
		return rankMaxOutputHurt;
	}

	public void setRankMaxOutputHurt(int rankMaxOutputHurt) {
		this.rankMaxOutputHurt = rankMaxOutputHurt;
	}

	public int getRankMaxInputHurt() {
		return rankMaxInputHurt;
	}

	public void setRankMaxInputHurt(int rankMaxInputHurt) {
		this.rankMaxInputHurt = rankMaxInputHurt;
	}

	public int getRankWinCount() {
		return rankWinCount;
	}

	public void setRankWinCount(int rankWinCount) {
		this.rankWinCount = rankWinCount;
	}

	public int getRankLostCount() {
		return rankLostCount;
	}

	public void setRankLostCount(int rankLostCount) {
		this.rankLostCount = rankLostCount;
	}

	public int getRankLoopWinCount() {
		return rankLoopWinCount;
	}

	public void setRankLoopWinCount(int rankLoopWinCount) {
		this.rankLoopWinCount = rankLoopWinCount;
	}

	public int getRankMvp() {
		return rankMvp;
	}

	public void setRankMvp(int rankMvp) {
		this.rankMvp = rankMvp;
	}

	public int getRankMaxLoopWinCount() {
		return rankMaxLoopWinCount;
	}

	public void setRankMaxLoopWinCount(int rankMaxLoopWinCount) {
		this.rankMaxLoopWinCount = rankMaxLoopWinCount;
	}

	public int getBattleWinCount() {
		return battleWinCount;
	}

	public void setBattleWinCount(int battleWinCount) {
		this.battleWinCount = battleWinCount;
	}

	public int getBattleLostCount() {
		return battleLostCount;
	}

	public void setBattleLostCount(int battleLostCount) {
		this.battleLostCount = battleLostCount;
	}

	public int getFreeReviveCount() {
		return freeReviveCount;
	}

	public void setFreeReviveCount(int freeReviveCount) {
		this.freeReviveCount = freeReviveCount;
	}

	public int getRankKill() {
		return rankKill;
	}

	public void setRankKill(int rankKill) {
		this.rankKill = rankKill;
	}

	public int getRankHelper() {
		return rankHelper;
	}

	public void setRankHelper(int rankHelper) {
		this.rankHelper = rankHelper;
	}

	public int getRankWin() {
		return rankWin;
	}

	public void setRankWin(int rankWin) {
		this.rankWin = rankWin;
	}

	public int getPreseason() {
		return preseason;
	}

	public void setPreseason(int preseason) {
		this.preseason = preseason;
	}

	public int getLoopWinCount() {
		return loopWinCount;
	}

	public void setLoopWinCount(int loopWinCount) {
		this.loopWinCount = loopWinCount;
	}

	public int getLoopLoseCount() {
		return loopLoseCount;
	}

	public void setLoopLoseCount(int loopLoseCount) {
		this.loopLoseCount = loopLoseCount;
	}

}