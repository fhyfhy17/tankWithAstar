package com.ourpalm.tank.constant;

/**
 * 系统常量,不常改变的值
 * 
 * @author wangkun
 *
 */
public interface Constant {

	/** 计算助攻次数的击杀时间间隔(死亡前5秒的攻击者都算助攻) */
	long HELP_KILL_DEATH_TIME_INTERVAL = 5 * 1000;
	
	/** 计算连续击杀数的时间间隔(击杀第一个玩家10秒内击杀另一个玩家算连杀) */
	long CONTINUE_KILL_TIME_INTERVAL = 10 * 1000;
	
	/** 坦克仓库最大存放坦克数目 */
	int MAX_TANK_WAREHOUSE_SAVE_COUNT = 5;
	
	/** 坦克宽度 */
	int TANK_WIDTH = 5;
}
