package com.ourpalm.tank.dao;

import java.util.List;
import java.util.Map;

import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.AreaIp;
import com.ourpalm.tank.domain.RankRoleIdAndScore;

public interface SeasonRankDao {

	/**
	 * 添加玩家排名
	 * 
	 * @param roleid
	 * @param score
	 * @param areaId
	 *            几服，不区分服的 写-1
	 */
	void saveRank(int roleid, int score, RankEnum type, int areaId);

	/**
	 * 删除玩家排名
	 * 
	 * @param areaId
	 *            几服，不区分服的 写-1
	 * 
	 * @param roleId
	 */
	void delRank(int roleId, RankEnum type, int areaId);

	/**
	 * 获取前几名排行
	 * 
	 * @param showCount
	 *            前多少名
	 * @param areaId
	 *            几服，不区分服的 写-1
	 * @return
	 */
	List<Integer> getRanks(int start, int end, RankEnum type, int areaId);

	/**
	 * 清除排行榜
	 */
	void clearRank(RankEnum type, int areaId);

	/**
	 * 获取玩家排名
	 * 
	 * @param roleId
	 * @param type
	 * @param areaId
	 *            几服，不区分服的 写-1
	 * @return
	 */
	int getRoleRank(int roleId, RankEnum type, int areaId);

	int getRoleScore(int roleId, RankEnum type, int areaId);

	Map<Integer, AreaIp> getAllIp();

	List<RankRoleIdAndScore> getRanksScoreAndRank(int start, int end, RankEnum type, int server);

}
