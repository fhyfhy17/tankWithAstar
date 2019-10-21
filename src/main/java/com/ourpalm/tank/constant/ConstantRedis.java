package com.ourpalm.tank.constant;

public class ConstantRedis {

	/** 更新排行榜 锁 ，天 */
	public static final String RANK_UPDATE_LOCK = "RANK_UPDATE_LOCK";
	/** 更新排行榜 时间 */
	public static final String RANK_UPDATE_TIME = "RANK_UPDATE_TIME";

	/** 更新排行榜 锁每周 */
	public static final String RANK_UPDATE_WEEK_LOCK = "RANK_UPDATE_WEEK_LOCK";
	/** 更新排行榜 时间 每周 */
	public static final String RANK_UPDATE_WEEK_TIME = "RANK_UPDATE_WEEK_TIME";
	
	/** 军衔赛季发奖锁 */
	public static final String SEASON_REWARD_RANK_UPDATE_LOCK = "SEASON_REWARD_RANK_UPDATE_LOCK";
}
