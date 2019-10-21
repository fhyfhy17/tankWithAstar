package com.ourpalm.tank.app.season;

import java.util.List;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.ConstantRedis;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.dao.SeasonRankDao;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.SeasonMatch;
import com.ourpalm.tank.template.ArmyTitleRankTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;

public class SeasonRankAppImpl implements SeasonRankApp {

	private SeasonRankDao seasonRankDao;

	private ArmyTitleRankTemplate infoTemplate;

	@Override
	public void start() {
		loadTemplate();
	}

	private void loadTemplate() {
		String sourceFile = XlsSheetType.SeasonRank.getXlsFileName();
		String sheetName = XlsSheetType.SeasonRank.getSheetName();
		try {
			List<ArmyTitleRankTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ArmyTitleRankTemplate.class);
			if (!Util.isEmpty(list)) {
				infoTemplate = list.get(0);
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public void addRank(int roleId, int titleId, int score) {
		int limitTitleId = infoTemplate.getLimitTitleId();
		if (titleId < limitTitleId) {
			seasonRankDao.delRank(roleId, RankEnum.SeasonRankKey, -1);
			return;
		}

		seasonRankDao.saveRank(roleId, score, RankEnum.SeasonRankKey, -1);
	}

	@Override
	public List<Integer> getRanks() {
		return seasonRankDao.getRanks(0,infoTemplate.getShowCount() - 1, RankEnum.SeasonRankKey, -1);
	}

	@Override
	public void resetRank(SeasonMatch season) {
		Logger log = LogCore.rank;
		List<Integer> roleIds = getRanks();
		int count = 1;
		log.info("=============== 赛季结束排行榜 =================");
		log.info("赛季ID:{} 开始时间:{} 结束时间:{}", season.getId(), DateUtil.date2Str(season.getBeginTime()), DateUtil.date2Str(season.getEndTime()));
		for (Integer roleId : roleIds) {
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			if (role == null) {
				continue;
			}
			log.info("{} {} {}", count, role.getRoleName(), role.getScore());
			count++;
		}
		log.info("===============================================");
		GameContext.getLock().lock(ConstantRedis.SEASON_REWARD_RANK_UPDATE_LOCK);
		try {
			// 发送赛季奖励
			GameContext.getRankApp().seasonSendReward(1);
			seasonRankDao.clearRank(RankEnum.SeasonRankKey, -1);
		} catch (Exception e) {
			log.error("军衔排位赛季结束发奖报错",e);
		}finally {
			GameContext.getLock().unlock(ConstantRedis.SEASON_REWARD_RANK_UPDATE_LOCK);
		}
	}

	@Override
	public void printRank() {
		Logger log = LogCore.rank;
		List<Integer> roleIds = getRanks();
		int count = 1;
		for (Integer roleId : roleIds) {
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			if (role == null) {
				continue;
			}
			log.info("{} {} {}", count, role.getRoleName(), role.getScore());
			count++;
		}
	}

	@Override
	public int getRoleRank(int roleId) {
		return seasonRankDao.getRoleRank(roleId, RankEnum.SeasonRankKey, -1);
	}

	public void setSeasonRankDao(SeasonRankDao seasonRankDao) {
		this.seasonRankDao = seasonRankDao;
	}

	@Override
	public ArmyTitleRankTemplate getArmyTitleRankTemplate() {
		return infoTemplate;
	}

}
