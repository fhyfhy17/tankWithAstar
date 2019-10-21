package com.ourpalm.tank.app.quest;

public interface QuestTriggerApp {
	
	/** 坦克升级配件 */
	void tankUpPart(int roleId);
	
	/** 进行N场战斗 */
	void roleBattle(int roleId);
	
	/**进行N次武将抽取*/
	void memberLottery(int roleId, int count);
	
	/**进行N次武将升级 */
	void memberUpLevel(int roleId);
	
	/**道具商店购买道具*/
	void shopBuy(int roleId);
	
	/**开启N个材料卡包*/
	void boxOpen(int roleId);
	
	/**
	 * 获得N场对应战场胜利
	 * @param warType 战场类型	
	 * @return
	 */
	void roleBattleWin(int roleId, int warType);
	
	/**
	 * 在对应战场击杀N辆坦克
	 * @param warType	战场类型
	 * @param killCount	击杀坦克数量
	 * @return
	 */
	void roleBattleKillTank(int roleId, int warType, int killCount);
	
	/**
	 * 占旗胜利
	 * @param warType
	 * @return
	 */
	void occupyFlagWin(int roleId, int warType);
	
	/**
	 * 战场获取银币
	 * @param iron 银币数
	 * @return
	 */
	void roleBattleIron(int roleId, int iron);
	
	/**
	 * 坦克研发
	 * @param tankId 
	 * @return
	 */
	void tankBuy(int roleId, int tankId);
	
	/**
	 * 累计击杀N辆坦克
	 * @param count	击杀数
	 * @return
	 */
	void totalKillTank(int roleId, int count);
	
	/**
	 * 角色升级
	 * @param curLevel	当前等级
	 * @return
	 */
	void roleUpLevel(int roleId, int curLevel);
	
	/**
	 * 获得mvp
	 * @param warType	战场类型
	 * @return
	 */
	void roleMvp(int roleId, int warType);
	
	/**
	 * 一场对应战场整场战斗不死，获得N个助攻
	 * @param warType	战场类型
	 * @param helpCount	助攻数
	 * @return
	 */
	void oneBattleAliveHelp(int roleId, int warType, int helpCount);
	
	/**
	 * 一场对应战场整场战斗不死，连杀N个敌人
	 * @param warType	战场类型
	 * @param killCount	连杀数
	 * @return
	 */
	void oneBattleAliveContinueKill(int roleId, int warType, int killCount);
	
	/**
	 * 一场对应战场整场战斗不死，击杀N个敌人
	 * @param warType	战场类型
	 * @param killCount	击杀数
	 * @return
	 */
	void oneBattleAliveKill(int roleId, int warType, int killCount);
	
	/**
	 * 一场对应战场整场战斗不死，使用燃烧弹击杀N个敌人
	 * @param warType	战场类型
	 * @param killCount	击杀数
	 * @return
	 */
	void oneBattleAliveFireBulletKill(int roleId, int warType, int killCount);
	
	/**
	 * 一场对应战场死亡复仇同时炸死N个敌人
	 * @param warType	战场类型
	 * @param killCount	击杀数
	 * @return
	 */
	void oneBattleDeadKill(int roleId, int warType, int killCount);
	
	/**
	 * 一场战斗击杀N个正在夺旗的敌人
	 * @param killCount	N个正在夺旗的敌人
	 * @return
	 */
	void oneBattleKillOcccpyFlag(int roleId, int killCount);
	
	/**
	 * 战斗累计获取N个荣誉点
	 * @param warType	战场类型
	 * @param honor		荣誉点
	 * @return
	 */
	void totalBattleHonor(int roleId, int warType, int honor);
	
	/**
	 * 一场战斗战场累计承受伤害超过N%，且本方获胜
	 * @param warType
	 * @param rank
	 * @return
	 */
	void oneBattleBearHurtPercentWin(int roleId, int warType, int percent);
	
	/**
	 * 一场战斗对就战场协助伤害超过N%且获得胜利
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
	 * @param count 数量
	 * @return 
	 */
	void roleLevelMedal(int roleId, int medalId, int count);
	
	
	/** 战斗结束 */
	void battleResult(int roleId, BattleResultRecord record);
	
	/**拥有月卡*/
	void roleMonthCard(int roleId);
	/**拥有年卡*/
	void roleYearCard(int roleId);
	
	/**组队进行战斗*/
	void teamBattle(int roleId, int warType);
}
