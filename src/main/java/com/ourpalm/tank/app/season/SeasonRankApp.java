package com.ourpalm.tank.app.season;

import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.domain.SeasonMatch;
import com.ourpalm.tank.template.ArmyTitleRankTemplate;

public interface SeasonRankApp extends Service{

	/**
	 * 加入排行 
	 * @param roleId
	 * @param titleId
	 * @param score
	 */
	void addRank(int roleId, int titleId, int score);
	
	/**
	 * 获取排行
	 * @return
	 */
	List<Integer> getRanks();
	
	/**
	 * 初始化排行
	 * @param season
	 */
	void resetRank(SeasonMatch season);
	
	/**
	 * 获取玩家排名
	 * @param roleId 
	 * @return 排名（-1表示未上榜）
	 */
	int getRoleRank(int roleId);
	
	
	/**
	 * 打印当前排行榜
	 */
	void printRank();
	ArmyTitleRankTemplate getArmyTitleRankTemplate();
}
