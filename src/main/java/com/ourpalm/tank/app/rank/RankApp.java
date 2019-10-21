package com.ourpalm.tank.app.rank;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.AreaIp;
import com.ourpalm.tank.domain.RankRoleIdAndScore;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.SeasonMatch;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_GET_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_REWARD_INFOS_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_SEARCH_MSG;
import com.ourpalm.tank.vo.result.Result;

public interface RankApp extends Service {

	/**
	 * 加入排行
	 * 
	 * @param roleId
	 * @param titleId
	 * @param score
	 * @param currentTitle 军衔用，不用的传0
	 */
	void addRank(int roleId, int score, RankEnum rankEnum, int areaId,int currentTitle);

	/**
	 * 获取排行
	 * 
	 * @return
	 */
	List<RankRoleIdAndScore> getRanks(int start, int end, int areaId, RankEnum rankEnum);

	/**
	 * 初始化排行 每天
	 * 
	 * @param season
	 */
	void resetDayRank();

	/**
	 * 初始化排行 每周
	 * 
	 * @param season
	 */
	void resetWeekRank();

	/**
	 * 获取玩家排名
	 * 
	 * @param roleId
	 * @return 排名（-1表示未上榜）
	 */
	int getRoleRank(int roleId, int areaId, RankEnum rankEnum);

	/**
	 * 获取玩家分
	 * 
	 * @param roleId
	 * @return 排名（-1表示未上榜）
	 */
	int getRoleScore(int roleId, int areaId, RankEnum rankEnum);

	/**
	 * 打印当前排行榜
	 */
	void printRank();

	void rankAddKill(int roleId);

	void rankAddHelp(int roleId);

	void rankAddWin(int roleId);

	void rankAddBattleNum(int roleId);

	int getRewardState(int roleId, RankEnum rankEnum);

	Result rankGet(int roleId, RankEnum rankEnum, STC_RANK_GET_MSG.Builder builder);

	void setRoleGiftState(int schedule);

	/**
	 * 搜索玩家
	 **/
	int searchRole(int roleId, STC_RANK_SEARCH_MSG.Builder builder, RankEnum rankEnum);
	void seasonSendReward(int type);
	void jiashuju(RoleConnect connect);
	void getRewardInfo(STC_RANK_REWARD_INFOS_MSG.Builder builder,RankEnum rankEnum);
}
