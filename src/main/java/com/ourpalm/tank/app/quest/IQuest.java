package com.ourpalm.tank.app.quest;

public interface IQuest {

	/** 登录完成任务 */
	boolean login();
	
	/** 坦克升级配件 */
	boolean tankUpPart();
	
	/** 进行N场战斗 */
	boolean roleBattle();
	
	/**进行N次武将抽取*/
	boolean memberLottery(int count);
	
	/**进行N次武将升级 */
	boolean memberUpLevel();
	
	/**道具商店购买道具*/
	boolean shopBuy();
	
	/**开启N个材料卡包*/
	boolean boxOpen();
	
	/**
	 * 获得N场对应战场胜利
	 * @param warType 战场类型	
	 * @return
	 */
	boolean roleBattleWin(int warType);
	/**
	 * 在对应战场击杀N辆坦克
	 * @param warType	战场类型
	 * @param killCount	击杀坦克数量
	 * @return
	 */
	boolean roleBattleKillTank(int warType, int killCount);
	
	/**
	 * 占旗胜利
	 * @param warType
	 * @return
	 */
	boolean occupyFlagWin(int warType);
	
	/**
	 * 战场获取银币
	 * @param iron 银币数
	 * @return
	 */
	boolean roleBattleIron(int iron);
	
	/**
	 * 坦克研发
	 * @param tankId 
	 * @return
	 */
	boolean tankBuy(int tankId);
	
	/**
	 * 累计击杀N辆坦克
	 * @param count	击杀数
	 * @return
	 */
	boolean totalKillTank(int count);
	
	/**
	 * 角色升级
	 * @param curLevel	当前等级
	 * @return
	 */
	boolean roleUpLevel(int curLevel);
	
	/**
	 * 获得mvp
	 * @param warType	战场类型
	 * @return
	 */
	boolean roleMvp(int warType);
	
	/**
	 * 一场对应战场整场战斗不死，获得N个助攻
	 * @param warType	战场类型
	 * @param helpCount	助攻数
	 * @return
	 */
	boolean oneBattleAliveHelp(int warType, int helpCount);
	
	/**
	 * 一场对应战场整场战斗不死，连杀N个敌人
	 * @param warType	战场类型
	 * @param killCount	连杀数
	 * @return
	 */
	boolean oneBattleAliveContinueKill(int warType, int killCount);
	
	/**
	 * 一场对应战场整场战斗不死，击杀N个敌人
	 * @param warType	战场类型
	 * @param killCount	击杀数
	 * @return
	 */
	boolean oneBattleAliveKill(int warType, int killCount);
	
	/**
	 * 一场对应战场整场战斗不死，使用燃烧弹击杀N个敌人
	 * @param warType	战场类型
	 * @param killCount	击杀数
	 * @return
	 */
	boolean oneBattleAliveFireBulletKill(int warType, int killCount);
	
	/**
	 * 一场对应战场死亡复仇同时炸死N个敌人
	 * @param warType	战场类型
	 * @param killCount	击杀数
	 * @return
	 */
	boolean oneBattleDeadKill(int warType, int killCount);
	
	/**
	 * 一场战斗击杀N个正在夺旗的敌人
	 * @param killCount	N个正在夺旗的敌人
	 * @return
	 */
	boolean oneBattleKillOcccpyFlag(int killCount);
	
	/**
	 * 战斗累计获取N个荣誉点
	 * @param warType	战场类型
	 * @param honor		荣誉点
	 * @return
	 */
	boolean totalBattleHonor(int warType, int honor);
	
	/**
	 * 一场战斗战场累计承受伤害超过N%，且本方获胜
	 * @param warType
	 * @param rank
	 * @return
	 */
	boolean oneBattleBearHurtPercentWin(int warType, int hurtPercent);
	/**
	 * 一场战斗对就战场协助伤害超过N%且获得胜利(或关系）
	 * @param warType
	 * @param rank
	 * @return
	 */
	boolean oneBattleHelpHurtPercentWin(int warType, int percent);
	/**
	 * 通过兑换累计获取N银币
	 * @param warType
	 * @param rank
	 * @return
	 */
	boolean totalCashingIron(int iron);
	
	/**
	 * 获得N星乘员
	 * @param quality
	 * @return
	 */
	boolean roleStartMember(int quality);
	/**
	 * 拥有N级以上的N星乘员
	 * @param quality
	 *  @param level
	 * @return
	 */
	boolean roleStartLevelMember(int quality, int level);
	/**
	 * 获得N级勋章
	 * @param memberId
	 * @param count	获得数量
	 * @return
	 */
	boolean roleLevelMedal(int medalId, int count);
	/**
	 * 佩戴勋章总等级之和达到N
	 * @param memberId
	 * @return
	 */
	boolean totalMedalLevel(int totalLevel);
	
	/**
	 * 拥有月卡
	 * @return
	 */
	boolean roleMonthCard();
	
	/**
	 * 拥有年卡
	 * @return
	 */
	boolean roleYearCard();
	
	
	/**
	 * 与好友组队进行战斗
	 * @return
	 */
	boolean teamBattle(int warType);
}
