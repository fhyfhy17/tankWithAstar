package com.ourpalm.tank.app.quest;

import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.MasterQuest;
import com.ourpalm.tank.template.MasterQuestTemplate;
import com.ourpalm.tank.vo.result.Result;

public interface MasterQuestApp extends Service{
	
	MasterQuestTemplate getMasterQuestTemplate(int questId);
	
	Result drawReward(int roleId, int questId);
	
	List<MasterQuest> getRoleQuest(int roleId);
	
	void createUser(int roleId, List<Integer> initQuestList);
	
	void login(int roleId);
	
	void offline(int roleId);
	
	void roleBattle(int roleId);
	
	void roleBattleWin(int roleId, int warType);
	
	void tankBuy(int roleId, int tankId);
	
	void totalKillTank(int roleId, int killCount);
	
	void tankUpPart(int roleId);
	
	void roleUpLevel(int roleId, int curLevel);
	
	void roleBattleIron(int roleId, int iron);
	
	void memberLottery(int roleId, int count);
	
	void memberUpLevel(int roleId);
	
	void roleMvp(int roleId, int warType);
	
	void shopBuy(int roleId);
	
	void boxOpen(int roleId);
	
	void roleBattleKillTank(int roleId, int warType, int killCount);
	
	void occupyFlagWin(int roleId, int warType);
	
	void oneBattleAliveHelp(int roleId, int warType, int helpCount);
	
	void oneBattleAliveContinueKill(int roleId, int warType, int killCount);
	
	void oneBattleAliveKill(int roleId, int warType, int killCount);
	
	void oneBattleAliveFireBulletKill(int roleId, int warType, int killCount);
	
	void oneBattleDeadKill(int roleId, int warType, int killCount);
	
	void oneBattleKillOcccpyFlag(int roleId, int killCount);
	
	void totalBattleHonor(int roleId, int warType, int honor);
	
	
	/**
	 * 一场战斗战场累计承受伤害超过N%，且本方获胜
	 * @param warType
	 * @param rank
	 * @return
	 */
	void oneBattleBearHurtPercentWin(int roleId, int warType, int percent);
	
	/**
	 * 一场战斗对就战场协助伤害超过N%且获得胜利(或关系）
	 * @param warType
	 * @param rank
	 * @return
	 */
	void oneBattleHelpHurtPercentWin(int roleId, int warType, int percent);
	
	/**
	 * 通过兑换累计获取N银币
	 * @param warType
	 * @param rank
	 * @return
	 */
	void totalCashingIron(int roleId, int iron);
	
	/**
	 * 获得N星乘员
	 * @param quality
	 * @return
	 */
	void roleStartMember(int roleId, int quality);
	/**
	 * 拥有N级以上的N星乘员
	 * @param quality
	 *  @param level
	 * @return
	 */
	void roleStartLevelMember(int roleId, int quality, int level);
	/**
	 * 获得N级勋章
	 * @param memberId
	 * @return
	 */
	void roleLevelMedal(int roleId, int medalId, int count);
	
	/**
	 * 组队进行战斗
	 * @param memberId
	 * @return
	 */
	void teamBattle(int roleId, int warType);
}
