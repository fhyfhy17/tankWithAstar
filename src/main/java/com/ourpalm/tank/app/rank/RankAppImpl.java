package com.ourpalm.tank.app.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.dao.SeasonRankDao;
import com.ourpalm.tank.domain.AreaIp;
import com.ourpalm.tank.domain.RankRewardStateInfo;
import com.ourpalm.tank.domain.RankRoleIdAndScore;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_INFO_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_REWARD_INFOS_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_SEARCH_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_SELF_MSG;
import com.ourpalm.tank.message.RANK_MSG.RANK_REWARD_INFO;
import com.ourpalm.tank.message.RANK_MSG.RankItem;
import com.ourpalm.tank.message.RANK_MSG.RoleRewardItem;
import com.ourpalm.tank.message.RANK_MSG.RoleRewardType;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_REWARD_INFOS_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_GET_MSG.Builder;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.SERV_MSG.STS_LOGIN_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_SEARCH_MSG;
import com.ourpalm.tank.template.RankRewardTemplate;
import com.ourpalm.tank.template.SeasonRankRewardTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

/**
 * 
 * 按服分配的排行榜
 * 
 * @author Administrator
 *
 */
public class RankAppImpl implements RankApp {

	private SeasonRankDao seasonRankDao;
	private Map<Integer, RankRewardTemplate> templateMap = new HashMap<>();
	/** 榜单限制人数 */
	public static int RANK_LIMIT;
	/** 助攻榜单最低分 */
	public static int RANK_HELP_LEAST;
	/** 战力榜单最低分 */
	public static int RANK_BATTLE_NUM_LEAST;
	/** 击杀榜单最低分 */
	public static int RANK_KILL_LEAST;
	/** 胜场榜单最低分 */
	public static int RANK_WIN_LEAST;
	/** 排行榜每页条数 */
	public static int RANK_PAGE_SHOW_NUM;
	/** 排位赛奖励区间 */
	private Map<String, Integer> seasonRankRewardIntervalMap = new HashMap<>();

	private Map<Integer, SeasonRankRewardTemplate> seasonRankRewardTemplateMap = new HashMap<>();

	private static final int DAY_SCHEDULE = 1;
	private static final int WEEK_SCHEDULE = 2;

	@Override
	public void start() {
		loadTemplate();
		loadTemplateSeasonRank();
		RANK_LIMIT = SysConfig.get(5);
		RANK_BATTLE_NUM_LEAST = SysConfig.get(6);
		RANK_HELP_LEAST = SysConfig.get(7);
		RANK_KILL_LEAST = SysConfig.get(8);
		RANK_WIN_LEAST = SysConfig.get(9);
		RANK_PAGE_SHOW_NUM = SysConfig.get(10);
	}

	private void loadTemplate() {
		String fileName = XlsSheetType.RankReward.getXlsFileName();
		String sheetName = XlsSheetType.RankReward.getSheetName();
		templateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, RankRewardTemplate.class);
		for (RankRewardTemplate template : templateMap.values()) {
			template.init();
		}

	}

	private void loadTemplateSeasonRank() {
		String fileName = XlsSheetType.seasonRankReward.getXlsFileName();
		String sheetName = XlsSheetType.seasonRankReward.getSheetName();
		seasonRankRewardTemplateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, SeasonRankRewardTemplate.class);
		for (SeasonRankRewardTemplate seasonRankRewardTemplate : seasonRankRewardTemplateMap.values()) {
			seasonRankRewardTemplate.init();
			int[] is = seasonRankRewardTemplate.getSeasonRankArray();
			seasonRankRewardIntervalMap.put(is[0] + "_" + is[1], seasonRankRewardTemplate.getId());
		}
	}

	@Override
	public void stop() {

	}

	public void jiashuju(RoleConnect connect) {
		int[] a = new int[] { 259, 458, 391, 419, 162, 179, 294, 195, 206, 224, 192, 179 };
		for (int i = 0; i < a.length; i++) {
			int id = a[i];
			for (RankEnum r : RankEnum.values()) {
				addRank(id, RandomUtil.randomInt(100, 1000), r, r == RankEnum.SeasonRankKey ? -1 : 4, 23);
			}
		}
		// CTS_RANK_INFO_MSG.Builder loginBuilder =
		// CTS_RANK_INFO_MSG.newBuilder();
		// loginBuilder.setPage(1);
		// loginBuilder.setType(1);
		//
		// Message m = new Message();
		// m.setCmdType((byte) RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE);
		// m.setCmdId((byte) RANK_MSG.CMD_ID.CTS_RANK_INFO_VALUE);
		// m.setData(loginBuilder.build().toByteArray());
		// m.setFromNode(GameContext.getLocalNodeName());
		// m.setIoId(connect.getIoId());
		//
		// GameContext.getMessageHandler().messageReceived(m);

		// CTS_RANK_SELF_MSG.Builder loginBuilder =
		// CTS_RANK_SELF_MSG.newBuilder();
		//
		// Message m = new Message();
		// m.setCmdType((byte) RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE);
		// m.setCmdId((byte) RANK_MSG.CMD_ID.CTS_RANK_SELF_VALUE);
		// m.setData(loginBuilder.build().toByteArray());
		// m.setFromNode(GameContext.getLocalNodeName());
		// m.setIoId(connect.getIoId());
		//
		// GameContext.getMessageHandler().messageReceived(m);

		// CTS_RANK_REWARD_INFOS_MSG.Builder loginBuilder =
		// CTS_RANK_REWARD_INFOS_MSG.newBuilder();
		//
		// loginBuilder.setType(1);
		//
		// Message m = new Message();
		// m.setCmdType((byte) RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE);
		// m.setCmdId((byte) RANK_MSG.CMD_ID.CTS_RANK_REWARD_INFOS_VALUE);
		// m.setData(loginBuilder.build().toByteArray());
		// m.setFromNode(GameContext.getLocalNodeName());
		// m.setIoId(connect.getIoId());
		//
		// GameContext.getMessageHandler().messageReceived(m);
		// CTS_RANK_SEARCH_MSG.Builder loginBuilder =
		// CTS_RANK_SEARCH_MSG.newBuilder();
		//
		// loginBuilder.setType(0);
		// loginBuilder.setName("45");
		//
		// Message m = new Message();
		// m.setCmdType((byte) RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE);
		// m.setCmdId((byte) RANK_MSG.CMD_ID.CTS_RANK_SEARCH_VALUE);
		// m.setData(loginBuilder.build().toByteArray());
		// m.setFromNode(GameContext.getLocalNodeName());
		// m.setIoId(connect.getIoId());
		//
		// GameContext.getMessageHandler().messageReceived(m);
	}

	@Override
	public void addRank(int roleId, int score, RankEnum rankEnum, int areaId, int currentTitleId) {
		switch (rankEnum) {
		case BattleNum:
			if (score < RANK_BATTLE_NUM_LEAST) {
				seasonRankDao.delRank(roleId, rankEnum, areaId);
				return;
			}
		case Help:
			if (score < RANK_HELP_LEAST) {
				seasonRankDao.delRank(roleId, rankEnum, areaId);
				return;
			}
		case Kill:
			if (score < RANK_KILL_LEAST) {
				seasonRankDao.delRank(roleId, rankEnum, areaId);
				return;
			}
		case Win:
			if (score < RANK_WIN_LEAST) {
				seasonRankDao.delRank(roleId, rankEnum, areaId);
				return;
			}
		case SeasonRankKey:
			int limitTitleId = GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getLimitTitleId();
			if (currentTitleId < limitTitleId) {
				seasonRankDao.delRank(roleId, RankEnum.SeasonRankKey, -1);
				return;
			}

			seasonRankDao.saveRank(roleId, score, RankEnum.SeasonRankKey, -1);

		default:
			break;
		}

		seasonRankDao.saveRank(roleId, score, rankEnum, areaId);
	}

	@Override
	public List<RankRoleIdAndScore> getRanks(int start, int end, int areaId, RankEnum rankEnum) {
		return seasonRankDao.getRanksScoreAndRank(start, end, rankEnum, areaId);
	}

	private int seasonIntervalGetTemplateId(int rank) {
		for (Entry<String, Integer> entry : seasonRankRewardIntervalMap.entrySet()) {
			String[] is = entry.getKey().split(Cat.underline);
			if (rank >= Integer.parseInt(is[0]) && rank <= Integer.parseInt(is[1])) {
				return entry.getValue();
			}
		}
		return 0;
	}

	@Override
	public void resetDayRank() {
		RankEnum[] ranks = RankEnum.values();
		List<Integer> areaIdList = getAllAreaId();
		for (RankEnum rankEnum : ranks) {
			if (rankEnum.getSchedule() == DAY_SCHEDULE) {
				for (Integer areaId : areaIdList) {
					seasonRankDao.clearRank(rankEnum, areaId);
				}
			}
			// 军衔每日发奖
			if (rankEnum.getSchedule() == 0) {
				seasonSendReward(0);
			}
		}
	}

	@Override
	public void seasonSendReward(int type) {
		List<RankRoleIdAndScore> list = getRanks(0, GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getLimitTitleId() - 1, -1, RankEnum.SeasonRankKey);
		int rank = 0;
		for (int i = 0; i < list.size(); i++) {
			rank++;
			RankRoleIdAndScore rankRoleIdAndScore = list.get(i);
			int templateId = seasonIntervalGetTemplateId(rank);
			SeasonRankRewardTemplate seasonRankRewardTemplate = seasonRankRewardTemplateMap.get(templateId);
			if (seasonRankRewardTemplate == null)
				break;
			List<int[]> reward = null;
			if (type == 1) {
				reward = seasonRankRewardTemplate.getSeasonFinalList();
			} else {
				reward = seasonRankRewardTemplate.getSeasonEveryList();
			}
			if (reward != null) {
				Map<Integer, Integer> goods = new HashMap<>();
				int iron = 0;
				int gold = 0;
				for (int[] is : reward) {
					if (is[0] == RoleRewardType.GOODS_VALUE) {
						goods.put(is[1], is[2]);
					}
					if (is[0] == RoleRewardType.IRON_VALUE) {
						iron = is[2];
					}
					if (is[0] == RoleRewardType.GOLD_VALUE) {
						gold = is[2];
					}
				}
				GameContext.getMailApp().sendMail(rankRoleIdAndScore.getRoleId(), type == 1 ? "排位赛最终奖励" : "排位赛奖励", type == 1 ? "排位赛最终奖励" : "排位赛奖励", gold, iron, 0, 0, goods);
			}
		}
	}

	@Override
	public void resetWeekRank() {
		RankEnum[] ranks = RankEnum.values();
		List<Integer> areaIdList = getAllAreaId();
		for (RankEnum rankEnum : ranks) {
			if (rankEnum.getSchedule() == WEEK_SCHEDULE) {
				for (Integer areaId : areaIdList) {
					seasonRankDao.clearRank(rankEnum, areaId);
				}
			}
		}
	}

	@Override
	public void printRank() {
		// Logger log = LogCore.rank;
		// List<Integer> roleIds = getRanks(0, -1,1,RankEnum.Kill);
		// int count = 1;
		// for (Integer roleId : roleIds) {
		// RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		// if (role == null) {
		// continue;
		// }
		// log.info("{} {} {}", count, role.getRoleName(), role.getScore());
		// count++;
		// }
	}

	@Override
	public int getRoleRank(int roleId, int areaId, RankEnum rankEnum) {
		// RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		return seasonRankDao.getRoleRank(roleId, rankEnum, areaId);
	}

	@Override
	public int getRoleScore(int roleId, int areaId, RankEnum rankEnum) {
		return seasonRankDao.getRoleScore(roleId, rankEnum, areaId);
	}

	public void setSeasonRankDao(SeasonRankDao seasonRankDao) {
		this.seasonRankDao = seasonRankDao;
	}

	public List<Integer> getAllAreaId() {
		List<Integer> list = new ArrayList<>();
		Iterator<AreaIp> it = seasonRankDao.getAllIp().values().iterator();
		while (it.hasNext()) {
			AreaIp areaIp = it.next();
			list.add(areaIp.getAreaId());
		}
		return list;
	}

	@Override
	public void rankAddKill(int roleId) {
		RoleAccount roleAccount = GameContext.getUserApp().getRoleAccount(roleId);
		if (roleAccount != null) {
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			if (roleWarInfo != null) {
				roleWarInfo.setRankKill(roleWarInfo.getRankKill() + 1);
				GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
				addRank(roleId, roleWarInfo.getRankKill(), RankEnum.Kill, roleAccount.getAreaId(), 0);
			}
		}
	}

	@Override
	public void rankAddHelp(int roleId) {
		RoleAccount roleAccount = GameContext.getUserApp().getRoleAccount(roleId);
		if (roleAccount != null) {
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			if (roleWarInfo != null) {
				roleWarInfo.setRankHelper(roleWarInfo.getRankHelper() + 1);
				GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
				addRank(roleId, roleWarInfo.getRankHelper(), RankEnum.Help, roleAccount.getAreaId(), 0);
			}

		}
	}

	@Override
	public void rankAddWin(int roleId) {
		RoleAccount roleAccount = GameContext.getUserApp().getRoleAccount(roleId);
		if (roleAccount != null) {
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			if (roleWarInfo != null) {
				roleWarInfo.setRankWin(roleWarInfo.getRankWin() + 1);
				GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
				addRank(roleId, roleWarInfo.getRankWin(), RankEnum.Win, roleAccount.getAreaId(), 0);
			}
		}
	}

	@Override
	public void rankAddBattleNum(int roleId) {
		RoleAccount roleAccount = GameContext.getUserApp().getRoleAccount(roleId);
		if (roleAccount != null) {
			addRank(roleId, roleAccount.getBattleScore(), RankEnum.BattleNum, roleAccount.getAreaId(), 0);
		}
	}

	@Override
	public int getRewardState(int roleId, RankEnum rankEnum) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (role == null)
			return 0;
		HashMap<RankEnum, RankRewardStateInfo> map = role.getRankReward();
		RankRewardStateInfo info = map.get(rankEnum);
		long receiveTime = info.getReceiveTime();
		long now = System.currentTimeMillis();
		if (rankEnum.getSchedule() == DAY_SCHEDULE) {
			// 如果更新时间不是今天，那么今天没上榜，去除之前的state
			if (DateUtil.beforeToday(receiveTime) && (info.getState() == 2 || info.getState() == 1)) {
				info.setState(0);
				GameContext.getUserApp().saveRoleAccount(role);
			}

			return info.getState();
		} else if (rankEnum.getSchedule() == WEEK_SCHEDULE) {
			// 如果更新时间不是本周，那么本周没上榜，去除之前的state
			if (!DateUtil.isSameWeek(receiveTime, now) && (info.getState() == 2 || info.getState() == 1)) {
				info.setState(0);
				GameContext.getUserApp().saveRoleAccount(role);
			}
			return info.getState();
		}
		return 0;
	}

	@Override
	public Result rankGet(int roleId, RankEnum rankEnum, Builder builder) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		RankRewardStateInfo info = role.getRankReward().get(rankEnum);
		if (info == null)
			return Result.newFailure("没有可领取的奖励");
		int state = info.getState();
		if (state != 1) {
			return Result.newFailure("不符合领取奖励的条件");
		}
		info.setState(2);
		GameContext.getUserApp().saveRoleAccount(role);

		Iterator<RankRewardTemplate> it = templateMap.values().iterator();
		RankRewardTemplate chooseTemplate = null;
		while (it.hasNext()) {
			RankRewardTemplate template = it.next();
			int rank = info.getRank();
			if (rank >= template.getRankArray()[0] && rank <= template.getRankArray()[1]) {
				chooseTemplate = template;
				break;
			}
		}
		List<int[]> reward = null;
		if (chooseTemplate != null) {
			reward = getRewardByType(rankEnum, chooseTemplate);
		}

		if (reward != null) {
			for (int[] is : reward) {
				RoleRewardItem.Builder itemBuilder = RoleRewardItem.newBuilder();
				List<AttrUnit> attrList = new ArrayList<>();
				if (is[0] == RoleRewardType.GOLD_VALUE) {
					attrList.add(AttrUnit.build(RoleAttr.gold, Operation.add, is[2]));
					itemBuilder.setType(RoleRewardType.GOLD);
				}
				if (is[0] == RoleRewardType.IRON_VALUE) {
					attrList.add(AttrUnit.build(RoleAttr.iron, Operation.add, is[2]));
					itemBuilder.setType(RoleRewardType.IRON);
				}
				if (is[0] == RoleRewardType.DIAMONDS_VALUE) {
					attrList.add(AttrUnit.build(RoleAttr.diamonds, Operation.add, is[2]));
					itemBuilder.setType(RoleRewardType.DIAMONDS);
				}
				if (attrList.size() > 0) {
					GameContext.getUserAttrApp().changeAttribute(roleId, attrList, OutputType.rankMoney.type(),
							StringUtil.buildLogOrigin(String.valueOf(chooseTemplate.getId()), String.valueOf(info.getRank()), OutputType.rankMoney.getInfo()));
				}
				if (is[0] == RoleRewardType.GOODS_VALUE) {
					itemBuilder.setType(RoleRewardType.GOODS);
					GameContext.getGoodsApp().addGoods(roleId, is[1], is[2], "排行榜领奖");
				}
				itemBuilder.setId(is[1]);
				itemBuilder.setCount(is[2]);
				builder.addItem(itemBuilder);
			}
		}
		return Result.newSuccess();
	}

	public List<int[]> getRewardByType(RankEnum rankEnum, RankRewardTemplate template) {
		switch (rankEnum) {
		case BattleNum:
			return template.getBattleNumitemList();
		case Help:
			return template.getHelpitemList();
		case Kill:
			return template.getKillitemList();
		case Win:
			return template.getWinitemList();
		default:
			return null;
		}
	}

	@Override
	public void setRoleGiftState(int schedule) {
		List<Integer> areaIdList = getAllAreaId();
		RankEnum[] ranks = RankEnum.values();
		if (schedule == DAY_SCHEDULE) {
			for (RankEnum rankEnum : ranks) {
				if (rankEnum.getSchedule() == DAY_SCHEDULE) {
					chageState(areaIdList, rankEnum);
				}
			}
			resetDayRank();
		}
		if (schedule == WEEK_SCHEDULE) {
			for (RankEnum rankEnum : ranks) {
				if (rankEnum.getSchedule() == WEEK_SCHEDULE) {
					chageState(areaIdList, rankEnum);
				}
			}
			resetWeekRank();
		}
	}

	public void chageState(List<Integer> areaIdList, RankEnum rankEnum) {
		long now = System.currentTimeMillis();
		for (Integer areaId : areaIdList) {
			List<RankRoleIdAndScore> list = getRanks(0, RANK_LIMIT - 1, areaId, rankEnum);
			for (RankRoleIdAndScore rankRoleIdAndScore : list) {
				int roleId = rankRoleIdAndScore.getRoleId();
				RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
				if (role != null) {
					RankRewardStateInfo rankRewardStateInfo = new RankRewardStateInfo();
					int roleRank = GameContext.getRankApp().getRoleRank(role.getRoleId(), role.getAreaId(), rankEnum);
					rankRewardStateInfo.setRank(roleRank > 0 ? roleRank + 1 : -1);
					rankRewardStateInfo.setRankEnum(rankEnum);
					rankRewardStateInfo.setReceiveTime(now);
					rankRewardStateInfo.setState(1);
					role.getRankReward().put(rankEnum, rankRewardStateInfo);
					GameContext.getUserApp().saveRoleAccount(role);
				}
			}
		}
	}

	@Override
	public int searchRole(int roleId, STC_RANK_SEARCH_MSG.Builder builder, RankEnum rankEnum) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (role == null) {
			return 0;
		}
		int rank = getRoleRank(roleId, rankEnum == RankEnum.SeasonRankKey ? -1 : role.getAreaId(), rankEnum);
		if (rank == -1) {
			return 0;
		}

		int page = (rank / RANK_PAGE_SHOW_NUM) + 1;
		int start = (page - 1) * RANK_PAGE_SHOW_NUM;
		int end = start + RANK_PAGE_SHOW_NUM - 1;
		if (rankEnum == RankEnum.SeasonRankKey) {
			if (rank > GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount() || rank < 0) {
				return 0;
			}
			if (start > GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount()) {
				start = GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount();
			}
			if (end > GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount()) {
				end = GameContext.getSeasonRankApp().getArmyTitleRankTemplate().getShowCount();
			}
		}
		List<RankRoleIdAndScore> list = getRanks(start, end, rankEnum == RankEnum.SeasonRankKey ? -1 : role.getAreaId(), rankEnum);
		for (RankRoleIdAndScore rankRoleIdAndScore : list) {
			start++;
			RankItem.Builder builderRankItem = RankItem.newBuilder();
			RoleAccount roleInfo = GameContext.getUserApp().getRoleAccount(rankRoleIdAndScore.getRoleId());
			if (roleInfo == null)
				continue;

			builderRankItem.setName(roleInfo.getRoleName());
			builderRankItem.setScore(rankRoleIdAndScore.getScore());
			builderRankItem.setRank(start);
			builderRankItem.setTitleId(roleInfo.getCurrTitleId());
			builderRankItem.setVip(roleInfo.getVipLevel());
			builderRankItem.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(roleInfo.getRoleId()));
			builderRankItem.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(roleInfo.getRoleId()));
			builder.addRanks(builderRankItem);
		}
		builder.setRank(rank + 1);
		builder.setIsFinded("1");
		return 1;
	}

	public void getRewardInfo(STC_RANK_REWARD_INFOS_MSG.Builder builder, RankEnum rankEnum) {
		for (RankRewardTemplate template : templateMap.values()) {
			List<int[]> list = getRewardByType(rankEnum, template);
			for (int[] is : list) {
				RANK_REWARD_INFO.Builder infoBuilder = RANK_REWARD_INFO.newBuilder();
				if (is[0] == RoleRewardType.GOLD_VALUE) {
					infoBuilder.setType(RoleRewardType.GOLD);
				}
				if (is[0] == RoleRewardType.IRON_VALUE) {
					infoBuilder.setType(RoleRewardType.IRON);
				}
				if (is[0] == RoleRewardType.GOODS_VALUE) {
					infoBuilder.setType(RoleRewardType.GOODS);
				}
				if (is[0] == RoleRewardType.DIAMONDS_VALUE) {
					infoBuilder.setType(RoleRewardType.DIAMONDS);
				}
				infoBuilder.setItemId(is[1]);
				infoBuilder.setNum(is[2]);
				String rank = template.getRank();
				String[] rankArray = rank.split(Cat.comma);
				if (rankArray.length > 1 && rankArray[0].equals(rankArray[1])) {
					rank = rankArray[0];
				}
				infoBuilder.setRank(rank);
				builder.addInfo(infoBuilder);
			}
		}
	}

	public Map<Integer, SeasonRankRewardTemplate> getSeasonRankRewardTemplateMap() {
		return seasonRankRewardTemplateMap;
	}

	public void setSeasonRankRewardTemplateMap(Map<Integer, SeasonRankRewardTemplate> seasonRankRewardTemplateMap) {
		this.seasonRankRewardTemplateMap = seasonRankRewardTemplateMap;
	}

}
