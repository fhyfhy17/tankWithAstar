package com.ourpalm.tank.app.match;

import static com.ourpalm.tank.app.GameContext.getTankApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.greenpineyu.fel.parser.FelParser.integerLiteral_return;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.tank.TankAppImpl.AiCondition;
import com.ourpalm.tank.app.title.MatchScore;
import com.ourpalm.tank.dao.MatchDao;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.MATCH_MSG.FriendItem;
import com.ourpalm.tank.message.MATCH_MSG.MATCH_SCEDULE_INFO;
import com.ourpalm.tank.message.MATCH_MSG.STC_MATCH_SCHEDULE_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_CHANGE_TANK_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_INCOME_ADD_NUM_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_INFO_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_INFO_TYPE;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_REQ_FRIEND;
import com.ourpalm.tank.message.MATCH_MSG.TankTeamItem;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.MatchItem;
import com.ourpalm.tank.message.SERV_MSG.STG_SWITCH_LINK_MSG;
import com.ourpalm.tank.message.SERV_MSG.STM_MATCH_MSG;
import com.ourpalm.tank.template.ArmyTitleTemplate;
import com.ourpalm.tank.template.PreseasonTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.template.TeamIncomeAddTemplate;
import com.ourpalm.tank.template.UserLevelTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.MatchParam;
import com.ourpalm.tank.vo.MatchTank;
import com.ourpalm.tank.vo.TeamIncomeTypeInfo;

public class MatchAppImpl implements MatchApp {

	private final Logger logger = LogCore.runtime;
	private MatchDao matchDao;
	private Map<Integer, RoleBattle> roleMap = new ConcurrentHashMap<>();
	private Map<Integer,PreseasonTemplate> preseasonMap = new HashMap<>();//新手赛对应地图
	private Map<Integer,TeamIncomeAddTemplate> teamIncomeAddTemplateMap = new HashMap<>();//新手赛对应地图
	private int friendTeamIncomeNum;//好友组队收益
	private Map<Integer,Integer> corpTeamIncomeNumMap = new HashMap<>();//军团组队收益
	@Override
	public void matchNotifyEnter(MatchParam matchParam) {

		final List<com.ourpalm.tank.message.SERV_MSG.MatchTeam> matchList = matchParam.getMatchList();
		if (Util.isEmpty(matchList)) {
			return;
		}

		int mapIndex = matchParam.getMapIndex();

		if (!"0".equals(GameContext.getMapId())) {
			String[] ss = GameContext.getMapId().split(Cat.comma);
			int index = RandomUtil.randomInt(ss.length);
			mapIndex = Integer.valueOf(ss[index]);
		}
		final int teamMaxScore = matchParam.getTeamMaxScore();
		final int warType = matchParam.getWarType();
//		matchParam.setPreseason(1);//TODO 测试
		if( GameContext.getPreseasonID() > 0 && GameContext.getPreseasonID() <= 5 ){
			matchParam.setPreseason(GameContext.getPreseasonID());
		}
		//新手赛
		if (false && GameContext.getPreseason() != 0) {
			if (matchParam.getPreseason() > 0) {
				preseasonNotifyEnter(warType, matchParam.getPreseason(), teamMaxScore,matchList);
				return;
			}
		}

		int totalScore = 0;
		int averageLevel = 0;
		int numOfPlayer = 0;

		for (com.ourpalm.tank.message.SERV_MSG.MatchTeam team : matchList) {
			for (MatchItem item : team.getItemsList()) {
				if (item.getRoleId() > 0) {
					totalScore += getTankApp().getTankTemplate(item.getTankId()).getShowBattleNum();
					numOfPlayer += 1;
					averageLevel += item.getTankLevel();
				}
			}
		}
		averageLevel = (int) (averageLevel * 1.0 / numOfPlayer);
		
		// List<AbstractInstance> tanks = new ArrayList<>();
		Map<Integer, AbstractInstance> tanks = new HashMap<>();
		for (com.ourpalm.tank.message.SERV_MSG.MatchTeam team : matchList) {
			for (MatchItem item : team.getItemsList()) {
				if (item.getRoleId() < 0 && !GameContext.isOpenTankAI()) {
					continue;
				}
				// 通知网关切换链接
				this.changeRoleLink(item);
				// 加载数据
				this.loadingRole(item);
				// 创建坦克实例
				AbstractInstance tank = null;

				if (GameContext.getGameTest() == 0) {
					AiCondition ac = new AiCondition();
					ac.battleScore = (int) (totalScore * 1.0 / numOfPlayer);
					ac.minLevel = averageLevel - 1;
					ac.maxLevel = averageLevel;
					tank = this.createTankInstance(item, ac, 0);
					// tank = this.createFuckingTankInstance(item, teamMaxScore, 0);
				} else {
					tank = this.createTankInstanceTest(item, 0);
				}
				logger.error("创建 战斗时的坦克的 tank的 templateID ={}",tank.getTemplateId());
				tank.setWeakTeam(isWeakTeam(matchParam.getWeakTeam(), item));
				tank.setMatchFriendId(item.getFriendId());
				tank.setMatchCorpMemberId(item.getCorpMemberId());
				// if(tank.getRoleId()>0){
				// tanks.add(tank);
				tanks.put(tank.getRoleId(), tank);
				// }

				// 建立战场存档信息
				this.createRoleBattle(item, warType, tank, mapIndex);
				// 删除匹配io记录
				this.removeIo(item.getIoId());
			}
		}

		List<Team> teams = new ArrayList<>();
		for (com.ourpalm.tank.message.SERV_MSG.MatchTeam matchTeam : matchList) {
			Map<Integer, TeamItem> teamItems = new HashMap<>();
			for (MatchItem item : matchTeam.getItemsList()) {
				TeamItem ti = new TeamItem();
				ti.setId(item.getRoleId());
				if (ti.getId() < 0) {
					ti.setScore(tanks.get(ti.getId()).getActualBattleScore() / 100.0);
				} else {
					ti.setScore(item.getBattleScore() / 100.0);
				}
				ti.setLevel(item.getTankLevel());
				teamItems.put(ti.getId(), ti);
			}
			Team t = new Team(teamItems);
			teams.add(t);
		}


		// --- 遗传算法分队伍 ---
		List<Team> red = TeamAllocator.allocateWithGeneticAlgorithm(teams);
		teams.removeAll(red);

		for (Team team : red) {
			for (TeamItem ti : team.getTanks()) {
				AbstractInstance tank = tanks.get(ti.getId());
				tank.setTeam(TEAM.RED);
			}
		}
		for (Team team : teams) {
			for (TeamItem ti : team.getTanks()) {
				AbstractInstance tank = tanks.get(ti.getId());
				tank.setTeam(TEAM.BLUE);
			}
		}
		// ---             ---

		// 创建战场
		// 通知客户端进入
		GameContext.getMapApp().createBattleNotifyEnter(new ArrayList<>(tanks.values()), warType, mapIndex,0);
	}

	/**
	 * 新手赛
	 *
	 * @param warType
	 *            战斗类型
	 * @param preseason
	 *            新手关号
	 * @param teamMaxScore
	 *            最高匹配分
	 * @param matchList
	 */
	private void preseasonNotifyEnter(int warType, int preseason, int teamMaxScore, List<com.ourpalm.tank.message.SERV_MSG.MatchTeam> matchList) {
//		List<AbstractInstance> tanks = new ArrayList<>();

		Map<Integer, AbstractInstance> tanks = new HashMap<>();
		if(preseasonMap.isEmpty()){
			logger.error("新手赛地图表未配置  map.xls preseaon ");
			return;
		}
		PreseasonTemplate preseasonTemplate = preseasonMap.get(preseason);
		int mapIndex = preseasonTemplate.getMapId();
		//强制更改mapID
		if (!"0".equals(GameContext.getMapId())) {
			String[] ss = GameContext.getMapId().split(Cat.comma);
			int index = RandomUtil.randomInt(ss.length);
			mapIndex = Integer.valueOf(ss[index]);
		}

		int rindex = preseasonTemplate.getoTankList().size() - 1;
		int bindex = preseasonTemplate.geteTankList().size() - 1;
		

		int rmemberIdx = 1;
		int bmemberIdx = 1;
		
		for (com.ourpalm.tank.message.SERV_MSG.MatchTeam team : matchList) {
			for (MatchItem item : team.getItemsList()) {
				// 通知网关切换链接
				this.changeRoleLink(item);
				// 加载数据
				this.loadingRole(item);
				// 创建坦克实例
				AbstractInstance tank = null;
				if (item.getRoleId() > 0) {
					tank = this.createPreseasonTankInstance(item, 0, preseason);
					tank.setTeam(TEAM.RED);
					tank.setMemberId(rmemberIdx);
					rmemberIdx++;
				} else if (rindex > 0) {
					int id = preseasonTemplate.getoTankList().get(rindex);
					rindex --;
					if (id <= 0) {
						continue;
					}
					tank = this.createPreseasonTankInstance(item, id, preseason);
					tank.setTeam(TEAM.RED);
					tank.setMemberId(rmemberIdx);
					rmemberIdx++;
				} else if (bindex >= 0) {
					int id = preseasonTemplate.geteTankList().get(bindex);
					bindex --;
					tank = this.createPreseasonTankInstance(item, id, preseason);
					tank.setTeam(TEAM.BLUE);
					tank.setMemberId(bmemberIdx);
					bmemberIdx++; 
				}

				tanks.put(tank.getRoleId(), tank);
				// 建立战场存档信息
				this.createRoleBattle(item, warType, tank, mapIndex);
				// 删除匹配io记录
				this.removeIo(item.getIoId());
			}
		}

//		List<Team> teams = new ArrayList<>();
//		for (com.ourpalm.tank.message.SERV_MSG.MatchTeam matchTeam : matchList) {
//			Map<Integer, TeamItem> teamItems = new HashMap<>();
//			for (MatchItem item : matchTeam.getItemsList()) {
//				TeamItem ti = new TeamItem();
//				ti.setId(item.getRoleId());
//				if (ti.getId() < 0) {
//					ti.setScore(tanks.get(ti.getId()).getBattleScore());
//				} else {
//					ti.setScore(item.getBattleScore());
//				}
//				ti.setLevel(item.getTankLevel());
//				teamItems.put(ti.getId(), ti);
//			}
//			Team t = new Team(teamItems);
//			teams.add(t);
//		}
//
//
//		// --- 遗传算法分队伍 ---
//		List<Team> red = TeamAllocator.allocateWithGeneticAlgorithm(teams);
//		teams.removeAll(red);
//
//		for (Team team : red) {
//			int memberIdx = 1;
//			for (TeamItem ti : team.getTanks()) {
//				AbstractInstance tank = tanks.get(ti.getId());
//				tank.setTeam(TEAM.RED);
//				tank.setMemberId(memberIdx);
//				memberIdx++;
//			}
//		}
//		for (Team team : teams) {
//			int memberIdx = 1;
//			for (TeamItem ti : team.getTanks()) {
//				AbstractInstance tank = tanks.get(ti.getId());
//				tank.setTeam(TEAM.BLUE);
//				tank.setMemberId(memberIdx);
//				memberIdx++;
//			}
//		}

		// 创建战场
		// 通知客户端进入
		GameContext.getMapApp().createBattleNotifyEnter(new ArrayList<>(tanks.values()), warType, mapIndex,preseason);
	}

	private boolean isWeakTeam(String weakString, MatchItem item) {
		if (weakString == null || "".equals(weakString)) {
			return false;
		}
		if ("RED".equals(weakString) && item.getCamp() == 1) {
			return true;
		}
		if ("BLUE".equals(weakString) && item.getCamp() == 2) {
			return true;
		}
		return false;
	}

	private void createRoleBattle(MatchItem item, int warType, AbstractInstance tank, int mapIndex) {
		// 过滤掉机器人
		if (item.getRoleId() < 0) {
			return;
		}
		RoleBattle roleBattle = new RoleBattle();
		roleBattle.setCreateTime(System.currentTimeMillis());
		roleBattle.setNodeName(GameContext.getLocalNodeName());
		roleBattle.setRoleId(item.getRoleId());
		roleBattle.setTankInstanceId(tank.getId());
		roleBattle.setTankId(tank.getTemplateId());
		roleBattle.setWarType(warType);
		roleBattle.setMapIndex(mapIndex);

		this.saveRoleBattle(roleBattle);
		logger.debug("创建战斗记录 " + JSON.toJSONString(roleBattle));
	}

	// 加载数据
	private void loadingRole(MatchItem item) {
		int roleId = item.getRoleId();
		if (roleId < 0) {
			return;
		}

		// 本地用户不必操作
		RoleConnect localConnect = GameContext.getOnlineCenter().getRoleConnect(item.getIoId());
		if (localConnect != null) {
			return;
		}

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (role == null) {
			logger.warn("角色不存在 roleId = " + roleId, new NullPointerException());
			return;
		}

		// 注册用户连接
		RoleConnect connect = new RoleConnect();
		connect.setIoId(item.getIoId());
		connect.setGateName(item.getGateNode());
		connect.setNodeName(GameContext.getLocalNodeName());
		connect.setRoleId(roleId);
		connect.setAreaId(role.getAreaId());
		GameContext.getOnlineCenter().register(connect);

		// 加载数据到内存
		GameContext.getOnlineCenter().sysLogin(connect, false);

		logger.debug("加载数据完成... roleId = {}", roleId);
	}

	// 通知网关切换链接
	private void changeRoleLink(MatchItem item) {
		if (item.getRoleId() < 0) {
			return;
		}
		RemoteNode gateNode = GameContext.getGatewayManagerApp().getRemoteNode(item.getGateNode());
		if (gateNode == null) {
			logger.warn("网关节点挂了...nodeName = " + item.getGateNode(), new NullPointerException());
			return;
		}
		STG_SWITCH_LINK_MSG msg = STG_SWITCH_LINK_MSG.newBuilder().setIoId(item.getIoId()).setNewNode(GameContext.getLocalNodeName()).build();

		Message message = new Message();
		message.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
		message.setCmdId((byte) SERV_MSG.CMD_ID.STG_SWITCH_LINK_VALUE);
		message.setFromNode(GameContext.getLocalNodeName());
		message.setIoId(item.getIoId());
		message.setData(msg.toByteArray());

		gateNode.sendReqMsg(message);

		logger.info("通知网关切换链接... roleId = {}", item.getRoleId());
	}

	// 创建坦克实例对象
	private AbstractInstance createTankInstance(MatchItem item, AiCondition ac, int preseasonId) {
		AbstractInstance tank;
		int titleId = item.getTitleId();
		if (item.getRoleId() < 0) {
			tank = GameContext.getRoleArmyTitleApp().buildNpcTank(titleId, ac, preseasonId);
			tank.setRoleId(item.getRoleId());
		} else {
			tank = GameContext.getTankApp().createTankInstance(item.getRoleId(), item.getTankId());
			tank.setBattleScore(item.getBattleScore());
		}
		tank.setTeam(TEAM.valueOf(item.getCamp()));
		tank.setTitleId(titleId);
		tank.setMatchScore(item.getMatchScore());
		return tank;
	}
	
	private AbstractInstance createPreseasonTankInstance(MatchItem item, int tankId, int preseasonId) {
		AbstractInstance tank;
		int titleId = item.getTitleId();
		if (item.getRoleId() < 0) {
			tank = GameContext.getRoleArmyTitleApp().buildSpecificNpcTank(titleId, tankId, preseasonId);
			tank.setBattleScore(getTankApp().calcAllBattleScore(tank));
			tank.setRoleId(item.getRoleId());
		} else {
			tank = GameContext.getTankApp().createTankInstance(item.getRoleId(), item.getTankId());
			tank.setBattleScore(item.getBattleScore());
		}
		tank.setTeam(TEAM.valueOf(item.getCamp()));
		tank.setTitleId(titleId);
		tank.setMatchScore(item.getMatchScore());
		return tank;
	}

	private AbstractInstance createFuckingTankInstance(MatchItem item, int averageScore, int preseasonId) {
		AbstractInstance tank;
		int titleId = item.getTitleId();
		if (item.getRoleId() < 0) {
			tank = GameContext.getRoleArmyTitleApp().buildNpcTankOld(titleId, averageScore, preseasonId);
			tank.setRoleId(item.getRoleId());
		} else {
			tank = GameContext.getTankApp().createTankInstance(item.getRoleId(), item.getTankId());
			tank.setBattleScore(item.getBattleScore());
		}
		tank.setTeam(TEAM.valueOf(item.getCamp()));
		tank.setTitleId(titleId);
		tank.setMatchScore(item.getMatchScore());
		return tank;
	}

	// 创建坦克实例对象
	private AbstractInstance createTankInstanceTest(MatchItem item, int index) {
		AbstractInstance tank;
		int titleId = item.getTitleId();
		if (item.getRoleId() < 0) {
			tank = GameContext.getRoleArmyTitleApp().buildNpcTankTest(titleId, item.getCamp(), index);
		} else {
			tank = GameContext.getTankApp().createTankInstance(item.getRoleId(), item.getTankId());
			tank.setBattleScore(item.getBattleScore());
		}
		tank.setTeam(TEAM.valueOf(item.getCamp()));
		tank.setTitleId(titleId);
		tank.setMatchScore(item.getMatchScore());
		return tank;
	}

	@Override
	public void start() {
		//加载新手赛
		this.loadPreseasonTemplate();
		//组队收益
		this.loadTeamIncomeTemplate();
	}
	//加载新手赛对应地图
	private void loadPreseasonTemplate() {
		String sourceFile = XlsSheetType.Preseason.getXlsFileName();
		String sheetName = XlsSheetType.Preseason.getSheetName();

		try {
			this.preseasonMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, PreseasonTemplate.class);
			for (PreseasonTemplate template : 	this.preseasonMap.values()) {
				template.init();
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}

	private void loadTeamIncomeTemplate() {
		String sourceFile = XlsSheetType.teamIncomeAdd.getXlsFileName();
		String sheetName = XlsSheetType.teamIncomeAdd.getSheetName();

		try {
			this.teamIncomeAddTemplateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TeamIncomeAddTemplate.class);
			for (TeamIncomeAddTemplate teamIncomeAddTemplate : teamIncomeAddTemplateMap.values()) {
				if (teamIncomeAddTemplate.getType() == 1) {
					this.friendTeamIncomeNum = teamIncomeAddTemplate.getIncome();
				} else {
					this.corpTeamIncomeNumMap.put(teamIncomeAddTemplate.getCorpLevel(), teamIncomeAddTemplate.getIncome());
				}

			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}

	@Override
	public void loadTemplate(){
		loadPreseasonTemplate();
	}

	@Override
	public void stop() {

	}

	public void setMatchDao(MatchDao matchDao) {
		this.matchDao = matchDao;
	}

	@Override
	public RoleBattle getLocalRoleBattle(int roleId) {
		return roleMap.get(roleId);
	}

	@Override
	public RoleBattle getRoleBattle(int roleId) {
		return matchDao.getRoleBattle(roleId);
	}

	@Override
	public void saveRoleBattle(RoleBattle roleBattle) {
		roleMap.put(roleBattle.getRoleId(), roleBattle);
		matchDao.saveRoleBattle(roleBattle);
	}

	@Override
	public void removeLocalRoleBattle(int roleId) {
		roleMap.remove(roleId);
	}

	@Override
	public void removeRoleBattle(int roleId) {
		matchDao.removeRoleBattle(roleId);
	}

	@Override
	public void saveMatchIo(int ioId) {
		matchDao.saveMatchIo(ioId);
	}

	@Override
	public boolean hasMatchQueue(int ioId) {
		return matchDao.hasMatchQueue(ioId);
	}

	@Override
	public void removeIo(int ioId) {
		matchDao.removeIo(ioId);
	}

	@Override
	public MatchTeam createTeam(int roleId, int warType) {

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (logger.isDebugEnabled()) {
			logger.debug("开始创建队伍 roleId = {} 请求warType = {} ", roleId, warType);
		}

		// 如果之前有队伍，则退队
		String roleTeamId = role.getTeamId();
		if (!Util.isEmpty(roleTeamId)) {
			this.teamQuit(roleId);
		}

		String teamId = GameContext.getIdFactory().nextStr();
		MatchTeam matchTeam = new MatchTeam();
		matchTeam.setLeaderRoleId(roleId);
		matchTeam.setWarType(warType);
		matchTeam.setTeamId(teamId);
		matchTeam.setHadBegin(false);

		role.setTeamId(teamId);
		GameContext.getUserApp().saveRoleAccount(role);

		int mainTankId = role.getMainTankId();
		RoleTank tank = GameContext.getTankApp().getRoleTank(roleId, mainTankId);
		int battleScore = GameContext.getTankApp().calcAllBattleScore(tank);
		int score = GameContext.getRoleArmyTitleApp().getMatchScore(roleId, mainTankId, WAR_TYPE.valueOf(matchTeam.getWarType()));

		MatchTank matchTank = new MatchTank();
		matchTank.setRoleId(roleId);
		matchTank.setTankId(mainTankId);
		matchTank.setName(role.getRoleName());
		matchTank.setBattleScore(battleScore);
		matchTank.setHadElite(false);
		matchTank.setHadReady(false);
		matchTank.setScore(score);
		// 添加到队伍中
		matchTeam.putMatchTank(matchTank);

		if (warType == WAR_TYPE.RANK_VALUE) {
			ArmyTitleTemplate template = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(role.getCurrTitleId());
			matchTeam.setLimitMin(template.getTeamMatchMin());
			matchTeam.setLimitMax(template.getTeamMatchMax());
			matchTeam.setLimitDesc(template.getMatchDesc());
		}

		matchDao.saveMatchTeam(matchTeam);

		if (logger.isDebugEnabled()) {
			logger.debug("创建队伍 matchTeam = {} ", JSON.toJSONString(matchTeam));
		}

		return matchTeam;
	}

	@Override
	public List<TankTeamItem> getTankTeamItems(String teamId) {
		MatchTeam matchTeam = matchDao.getMatchTeam(teamId);
		if (matchTeam == null) {
			logger.warn("获取队伍信息失败, 没有此队伍 teamId = {}", teamId);
			return new ArrayList<>();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("当前队伍 teamId = {} 坦克列表...", teamId);
		}

		return this.getTankTeamItems(matchTeam);
	}

	@Override
	public List<TankTeamItem> getTankTeamItems(MatchTeam matchTeam) {
		int leaderId = matchTeam.getLeaderRoleId();
		List<TankTeamItem> list = new ArrayList<>();
		for (MatchTank matchTank : matchTeam.allTeamTank()) {
			int roleId = matchTank.getRoleId();
			// RoleConnect connect =
			// GameContext.getUserApp().getRoleConnect(roleId);
			// if(connect == null){
			// continue;
			// }

			TankTeamItem item = TankTeamItem.newBuilder().setRoleId(roleId).setTankId(matchTank.getTankId()).setName(matchTank.getName()).setBattleScore(matchTank.getBattleScore())
					.setHadElite(matchTank.isHadElite()).setHadReady(matchTank.isHadReady()).setHadLeader(matchTank.getRoleId() == leaderId)
					// .setHadMatch(this.hasMatchQueue(connect.getIoId()))
					.setHadMatch(false).setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(roleId)).setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(roleId)).build();
			list.add(item);

			if (logger.isDebugEnabled()) {
				logger.debug("队员坦克信息 matchTank = {}", JSON.toJSONString(matchTank));
			}
		}

		return list;
	}

	@Override
	public List<FriendItem> getCorpsRoleItems(int roleId) {
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		if (corpsId <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("还没有军团");
			}
			return new ArrayList<>();
		}
		List<FriendItem> list = new ArrayList<>();
		List<CorpsRole> corpList = GameContext.getCorpsApp().getCorpsRoleList(corpsId);
		if (Util.isEmpty(corpList)) {
			if (logger.isDebugEnabled()) {
				logger.debug("组队匹配显示军团列表，roleId = {} 军团中没有其它人");
			}
			return new ArrayList<>();
		}
		for (CorpsRole corpsRole : GameContext.getCorpsApp().getCorpsRoleList(corpsId)) {
			int corpsRoleId = corpsRole.getRoleId();
			if (roleId == corpsRoleId) {
				continue;
			}
			RoleAccount _role = GameContext.getUserApp().getRoleAccount(corpsRoleId);
			if (_role == null) {
				continue;
			}
			FriendItem item = FriendItem.newBuilder().setRoleId(corpsRoleId).setName(_role.getRoleName()).setLevel(_role.getLevel()).setTitleId(_role.getCurrTitleId())
					.setBattleScore(_role.getBattleScore()).setVipLvl(_role.getVipLevel()).setState(this.getFriendsState(_role))
					.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(_role.getRoleId())).setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(_role.getRoleId())).build();
			list.add(item);
		}
		return list;
	}

	@Override
	public List<FriendItem> getFriendItems(int roleId) {
		Set<Integer> friendSet = GameContext.getFriendApp().getAllFriend(roleId);
		if (Util.isEmpty(friendSet)) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("组队匹配显示好友列表，roleId = {} 还没有好友");
//			}
			return new ArrayList<>();
		}
		List<FriendItem> list = new ArrayList<>();
		for (int friendsId : friendSet) {
			RoleAccount friendRole = GameContext.getUserApp().getRoleAccount(friendsId);
			if (friendRole == null) {
				logger.error("好友列表中 roleId = {} 该用户不存在", friendsId);
				continue;
			}
			FriendItem item = FriendItem.newBuilder().setRoleId(friendsId).setName(friendRole.getRoleName()).setLevel(friendRole.getLevel()).setTitleId(friendRole.getCurrTitleId())
					.setBattleScore(friendRole.getBattleScore()).setVipLvl(friendRole.getVipLevel()).setState(this.getFriendsState(friendRole))
					.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(friendsId)).setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(friendsId)).build();
			list.add(item);
		}
		return list;
	}

	private int getFriendsState(RoleAccount role) {
		// 判断在线
		int roleId = role.getRoleId();
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if (connect == null) {
			return 4; // 离线
		}

		RoleBattle roleBattle = this.getRoleBattle(roleId);
		if (roleBattle != null && !roleBattle.hadOverTime()) {
			return 3; // 战斗中
		}

		String teamId = role.getTeamId();
		if (!Util.isEmpty(teamId)) {
			return 2; // 组队中
		}

		return 1; // 在线
	}

	/** 加入队伍 */
	@Override
	public String addTeam(int roleId, String teamId,TEAM_REQ_FRIEND friendType,int reqRoleId) {
		if (logger.isDebugEnabled()) {
			logger.debug("roleId = {} 同意加入队伍 teamId = {} .... ", roleId, teamId);
		}

		MatchTeam matchTeam = matchDao.getMatchTeam(teamId);
		if (matchTeam == null) {
			return "队伍已不存在";
		}
		if (matchTeam.isHadBegin()) {
			return "战斗已开始";
		}

		// 判断队伍是否已满
		if (matchTeam.hadFull()) {
			return "队伍已满";
		}

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int mainTankId = role.getMainTankId();
		if (!Util.isEmpty(role.getTeamId())) {
			return "您已有队伍";
		}
		role.setTeamId(teamId);
		GameContext.getUserApp().saveRoleAccount(role);

		RoleTank tank = GameContext.getTankApp().getRoleTank(roleId, mainTankId);
		int battleScore = GameContext.getTankApp().calcAllBattleScore(tank);
		int score = GameContext.getRoleArmyTitleApp().getMatchScore(roleId, mainTankId, WAR_TYPE.valueOf(matchTeam.getWarType()));

		MatchTank matchTank = new MatchTank();
		matchTank.setRoleId(roleId);
		matchTank.setTankId(mainTankId);
		matchTank.setName(role.getRoleName());
		matchTank.setBattleScore(battleScore);
		matchTank.setHadElite(false);
		matchTank.setScore(score);
		matchTank.setHadReady(false);

		if (friendType != null) {
			if (friendType == TEAM_REQ_FRIEND.FRIEND_TYPE) {
				matchTank.setFriendId(reqRoleId);
				for (MatchTank mTank : matchTeam.allTeamTank()) {
					if (mTank.getRoleId() == reqRoleId) {
						mTank.setFriendId(roleId);
					}
				}
			}
			if (friendType == TEAM_REQ_FRIEND.CORP_TYPE) {
				matchTank.setCorpMemberId(reqRoleId);
				for (MatchTank mTank : matchTeam.allTeamTank()) {
					if (mTank.getRoleId() == reqRoleId) {
						mTank.setCorpMemberId(roleId);
					}
				}
			}
		}
		matchTeam.putMatchTank(matchTank);
		// 保存队伍信息
		matchDao.saveMatchTeam(matchTeam);

		// 给所有队员推送队伍信息
		List<TankTeamItem> tanks = this.getTankTeamItems(matchTeam);
		for (MatchTank mTank : matchTeam.allTeamTank()) {
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(mTank.getRoleId());
			if (_connect != null) {
				STC_TEAM_INFO_MSG teamInfoMsg = STC_TEAM_INFO_MSG.newBuilder().setTeamId(teamId).setWarType(WAR_TYPE.valueOf(matchTeam.getWarType()))
						.addAllFriends(this.getFriendItems(mTank.getRoleId())).addAllTanks(tanks).setLimitMin(matchTeam.getLimitMin()).setLimitMax(matchTeam.getLimitMax())
						.setLimitDesc(matchTeam.getLimitDesc()).setType(TEAM_INFO_TYPE.TEAM_AGREE_TYPE).addAllCorpsRoles(this.getCorpsRoleItems(roleId)).build();
				_connect.sendMsg(teamInfoMsg);
				if (mTank.getFriendId() != 0 || mTank.getCorpMemberId() != 0) {
					// 推送组队收益加成
					STC_TEAM_INCOME_ADD_NUM_MSG.Builder teamIncomeAddBuilder = STC_TEAM_INCOME_ADD_NUM_MSG.newBuilder();
					if (mTank.getFriendId() != 0) {
						teamIncomeAddBuilder.setNum(friendTeamIncomeNum);
					} else {
						int corpLevel = GameContext.getCorpsApp().getCorpLevel(mTank.getRoleId());
						Integer num = corpTeamIncomeNumMap.get(corpLevel);
						teamIncomeAddBuilder.setNum(num == null ? 0 : num);
					}
					_connect.sendMsg(teamIncomeAddBuilder.build());
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("roleId = {} 加入队伍 teamId = {} warType = {} ", roleId, teamId, matchTeam.getWarType());
		}

		return null;
	}


	/** 更换所使用的坦克 */
	@Override
	public void teamChangeTank(int roleId, String teamId, int tankId) {
		if (logger.isDebugEnabled()) {
			logger.debug("组队对战更换坦克 roleId = {} 更换坦克 tankId = {} teamId = {} .... ", roleId, tankId, teamId);
		}

		MatchTeam matchTeam = matchDao.getMatchTeam(teamId);
		if (matchTeam == null) {
			logger.warn("组队对战更换坦克, 队伍已不存在 teamId = {}", teamId);
			return;
		}

		Map<Integer, MatchTank> teamMap = matchTeam.getTeamMap();
		MatchTank matchTank = teamMap.get(roleId);
		if (matchTank == null) {
			logger.warn("组队对战更换坦克, roleId = {} 不属于该队伍 teamId = {}", roleId, teamId);
			return;
		}

		// 判断是否拥有该坦克
		RoleTank roleTank = GameContext.getTankApp().getRoleTank(roleId, tankId);
		if (roleTank == null) {
			logger.warn("组队对战更换坦克, roleId = {}  teamId = {} 不曾拥有该坦克 tankId = {}", roleId, teamId, tankId);
			return;
		}
		int battleScore = GameContext.getTankApp().calcAllBattleScore(roleTank);
		int matchSocre = GameContext.getRoleArmyTitleApp().getMatchScore(roleId, tankId, WAR_TYPE.valueOf(matchTeam.getWarType()));
		matchTank.setTankId(tankId);
		matchTank.setBattleScore(battleScore);
		matchTank.setScore(matchSocre);
		matchTank.setHadElite(false);
		matchTeam.putMatchTank(matchTank);
		matchDao.saveMatchTeam(matchTeam);

		if (logger.isDebugEnabled()) {
			logger.debug("roleId = {} 已更换坦克 tankId = {} battleScore = {} teamId = {}", roleId, tankId, battleScore, teamId);
		}

		// 给所有人推送更换变化坦克消息
		List<TankTeamItem> tanks = this.getTankTeamItems(matchTeam);
		STC_TEAM_CHANGE_TANK_MSG tankMsg = STC_TEAM_CHANGE_TANK_MSG.newBuilder().addAllTanks(tanks).build();

		for (MatchTank mTank : matchTeam.allTeamTank()) {
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(mTank.getRoleId());
			if (_connect != null) {
				_connect.sendMsg(tankMsg);
			}
		}
	}

	/** 出战准备 */
	@Override
	public void teamReady(int roleId, String teamId) {
		MatchTeam matchTeam = matchDao.getMatchTeam(teamId);
		if (matchTeam == null) {
			logger.warn("组队准备, 队伍已不存在 teamId = {}", teamId);
			return;
		}

		Map<Integer, MatchTank> teamMap = matchTeam.getTeamMap();
		MatchTank matchTank = teamMap.get(roleId);
		if (matchTank == null) {
			logger.warn("组队准备, roleId = {} 不属于该队伍 teamId = {}", roleId, teamId);
			return;
		}

		matchTank.setHadReady(true);
		matchDao.saveMatchTeam(matchTeam);

		// 给其他人推送坦克变化
		STC_TEAM_CHANGE_TANK_MSG tankMsg = STC_TEAM_CHANGE_TANK_MSG.newBuilder().addAllTanks(this.getTankTeamItems(matchTeam)).build();
		for (MatchTank mTank : matchTeam.allTeamTank()) {
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(mTank.getRoleId());
			if (_connect != null) {
				_connect.sendMsg(tankMsg);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("组队准备 roleId = {} 已准备完毕 ", roleId);
		}
	}

	@Override
	public void teamQuit(int roleId) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		String teamId = role.getTeamId();
		if (Util.isEmpty(teamId)) {
			logger.debug("roleId = {} 已没有队伍", roleId);
			return;
		}
		// 退出队伍
		role.setTeamId(null);
		GameContext.getUserApp().saveRoleAccount(role);

		MatchTeam matchTeam = matchDao.getMatchTeam(teamId);
		if (matchTeam == null) {
			logger.debug("roleId = {} 退出队伍, 队伍teamId = {} 已不存在", roleId, teamId);
			return;
		}
		// 删除列表
		matchTeam.removeMatchTank(roleId);

		// 删除匹配io
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			this.removeIo(connect.getIoId());
		}

		// 删除成员，如果无人销毁队伍信息
		if (matchTeam.teamIsEmpty()) {
			matchDao.removeMatchTeam(teamId);

			if (logger.isDebugEnabled()) {
				logger.debug("队伍无人回收... team = {} ", JSON.toJSONString(matchTeam));
			}

			return;
		}

		// 如果是队长，换个队长
		if (roleId == matchTeam.getLeaderRoleId()) {
			matchTeam.randomTeamLeader();

			if (logger.isDebugEnabled()) {
				logger.debug("roleId = {} 为队长，随机一位其他队友作为队长 team = {}", roleId, JSON.toJSONString(matchTeam));
			}
		}

		// 给其他队员推送队伍信息
		List<TankTeamItem> tanks = this.getTankTeamItems(matchTeam);
		for (MatchTank mTank : matchTeam.allTeamTank()) {
			if (mTank.getRoleId() == roleId) {
				continue;
			}
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(mTank.getRoleId());
			if (_connect != null) {
				STC_TEAM_INFO_MSG teamInfoMsg = STC_TEAM_INFO_MSG.newBuilder().setTeamId(teamId).setWarType(WAR_TYPE.valueOf(matchTeam.getWarType()))
						.addAllFriends(this.getFriendItems(mTank.getRoleId())).addAllTanks(tanks).setLimitMin(matchTeam.getLimitMin()).setLimitMax(matchTeam.getLimitMax())
						.setLimitDesc(matchTeam.getLimitDesc()).setType(TEAM_INFO_TYPE.TEAM_QUIT_TYPE).addAllCorpsRoles(this.getCorpsRoleItems(roleId)).build();
				_connect.sendMsg(teamInfoMsg);
				//组队收益发0
				STC_TEAM_INCOME_ADD_NUM_MSG.Builder teamIncomeBuilder=STC_TEAM_INCOME_ADD_NUM_MSG.newBuilder();
				teamIncomeBuilder.setNum(0);
				_connect.sendMsg(teamIncomeBuilder.build());
			}
		}
		// 保存队伍信息
		matchDao.saveMatchTeam(matchTeam);

		if (logger.isDebugEnabled()) {
			logger.debug("roleId = {} 已退出队伍 team = {}", roleId, JSON.toJSONString(matchTeam));
		}
	}

	/** 组队匹配申请 */
	@Override
	public void teamMatch(String teamId) {
		MatchTeam matchTeam = matchDao.getMatchTeam(teamId);
		if (matchTeam == null) {
			return;
		}
		STM_MATCH_MSG.Builder builder = STM_MATCH_MSG.newBuilder();
		if (matchTeam.getWarType() == WAR_TYPE.RANK_VALUE) {
			for (MatchTank matchTank : matchTeam.allTeamTank()) {

				TankTemplate tankTemplate = GameContext.getTankApp().getTankTemplate(matchTank.getTankId());
				if (tankTemplate.getLevel_i() < 5) {
					// TODO 暂时只写这，做这个要在点击军衔匹配赛界面时就弹出，不让进，还有邀请组队时，也要判断，
					System.out.println("主坦克低于5级不让进排位赛");
					return;
				}
			}
		}
		builder.setTeamId(matchTeam.getTeamId());
		// 队伍匹配分值
		MatchScore matchScore = GameContext.getRoleArmyTitleApp().getTeamMatchScore(matchTeam);
		builder.setScore(matchScore.getScore());
		builder.setMinScore(matchScore.getMinScore());

		// 按队长随机地图
		int roleId = matchTeam.getLeaderRoleId();
		int level = GameContext.getUserApp().getRoleAccount(roleId).getLevel();
		UserLevelTemplate template = GameContext.getUserApp().getLevelTemplate(level);
		int mapIndex = template.randomMapIndex();
		builder.setMapIndex(mapIndex);
		builder.setPreseason(-1);

		// 对战模式时，随机一种模式（对战、歼灭）
		int warType = matchTeam.getWarType();
		if (warType == WAR_TYPE.BATTLE_VALUE) {
			// 玩家和ai
			builder.setMatchType(2);

			// 随机歼灭模式
			// TODO 策划要求不出现歼灭模式，需要时还要打开
			// if (RandomUtil.randomInt(100) > 50) {
			// warType = WAR_TYPE.PERISH_VALUE;
			// }

			// 如果是战前赛 阶段只允许对战模式
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			if (GameContext.getPreseason() != 0) {
				if (roleWarInfo.getPreseason() < 6 && roleWarInfo.getPreseason() > 0) {
					builder.setMatchType(1); // 匹配AI
					warType = WAR_TYPE.BATTLE_VALUE;
					builder.setPreseason(roleWarInfo.getPreseason());
				}
			}
		}

		// 军衔模式
		if (matchTeam.getWarType() == WAR_TYPE.RANK_VALUE) {
			builder.setMatchType(3); // 玩家
		}

		builder.setType(warType);
		int teamGrade = 0;
		for (MatchTank matchTank : matchTeam.allTeamTank()) {
			final int _roleId = matchTank.getRoleId();
			RoleConnect connect = GameContext.getUserApp().getRoleConnect(_roleId);
			if (connect == null) {
				continue;
			}
			MatchItem.Builder itemBuilder = MatchItem.newBuilder();
			itemBuilder.setIoId(connect.getIoId());
			itemBuilder.setRoleId(_roleId);
			itemBuilder.setCamp(0);
			itemBuilder.setGateNode(connect.getGateName());
			itemBuilder.setTankId(matchTank.getTankId());
			itemBuilder.setMatchScore(0);
			itemBuilder.setFriendId(matchTank.getFriendId());
			itemBuilder.setCorpMemberId(matchTank.getCorpMemberId());
			TankTemplate tankTemplate = GameContext.getTankApp().getTankTemplate(matchTank.getTankId());

			itemBuilder.setTankLevel(tankTemplate.getLevel_i());
			itemBuilder.setType(tankTemplate.getTankType_i());
			RoleAccount role = GameContext.getUserApp().getRoleAccount(_roleId);
			itemBuilder.setTitleId(role.getCurrTitleId());
			// 查询军衔段
			ArmyTitleTemplate armyTitleTemplate = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(role.getCurrTitleId());
			if (armyTitleTemplate == null) {
				logger.error("军衔段查找 失败");
			} else {
				if (armyTitleTemplate.getTitleGrade() > teamGrade) {
					teamGrade = armyTitleTemplate.getTitleGrade();
				}
			}

			itemBuilder.setBattleScore(matchTank.getBattleScore());
			builder.addItems(itemBuilder);

			// 匹配人数计数
			this.cumWarMatchCount(matchTeam.getWarType(), connect.getIoId());
		}
		builder.setTitleGrade(teamGrade);
		RemoteNode remoteNode = GameContext.getMatchManagerApp().randomRemoteNode();
		if (remoteNode != null) {
			Message message = new Message();
			message.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
			message.setCmdId((byte) SERV_MSG.CMD_ID.STM_MATCH_VALUE);
			message.setFromNode(GameContext.getLocalNodeName());
			message.setIoId(0);
			message.setData(builder.build().toByteArray());
			remoteNode.sendReqMsg(message);

			logger.debug("向匹配服务器发送匹配请求, teamId = {}, roleId = {}", teamId, roleId);

			matchTeam.enterMatch();
			this.matchDao.saveMatchTeam(matchTeam);
		}

		STC_TEAM_MATCH_MSG matchMsg = STC_TEAM_MATCH_MSG.newBuilder().setResult(true).setInfo("").build();
		for (MatchTank mTank : matchTeam.allTeamTank()) {
			RoleConnect _connect = GameContext.getUserApp().getRoleConnect(mTank.getRoleId());
			if (_connect != null) {
				_connect.sendMsg(matchMsg);
			}
		}
	}

	@Override
	public MatchTeam getMatchTeam(String teamId) {
		return matchDao.getMatchTeam(teamId);
	}

	@Override
	public void cumWarMatchCount(int warType, int ioId) {
		matchDao.saveWarMatchCount(warType, ioId);
	}

	@Override
	public int warMatchCount(int warType) {
		return matchDao.getMatchCount(warType);
	}

	@Override
	public void saveMatchTeam(MatchTeam matchTeam) {
		if (matchTeam == null) {
			return;
		}
		matchDao.saveMatchTeam(matchTeam);
	}

	@Override
	public boolean ifLevelMate(int selfLevel, int otherLevel) {
		if (Math.abs(selfLevel - otherLevel) <= 1)
			return true;
		return false;
	}

	@Override
	public Map<Integer, PreseasonTemplate> getPreseasonMap() {
		return preseasonMap;
	}

	public void setPreseasonMap(Map<Integer, PreseasonTemplate> preseasonMap) {
		this.preseasonMap = preseasonMap;
	}

	@Override
	public Map<Integer, PreseasonTemplate> getPreseasonTemplateMap() {
		return preseasonMap;
	}

	public Map<Integer, TeamIncomeAddTemplate> getTeamIncomeAddTemplateMap() {
		return teamIncomeAddTemplateMap;
	}

	public void setTeamIncomeAddTemplateMap(Map<Integer, TeamIncomeAddTemplate> teamIncomeAddTemplateMap) {
		this.teamIncomeAddTemplateMap = teamIncomeAddTemplateMap;
	}

	public int getFriendTeamIncomeNum() {
		return friendTeamIncomeNum;
	}

	public void setFriendTeamIncomeNum(int friendTeamIncomeNum) {
		this.friendTeamIncomeNum = friendTeamIncomeNum;
	}

	public Map<Integer, Integer> getCorpTeamIncomeNumMap() {
		return corpTeamIncomeNumMap;
	}

	public void setCorpTeamIncomeNumMap(Map<Integer, Integer> corpTeamIncomeNumMap) {
		this.corpTeamIncomeNumMap = corpTeamIncomeNumMap;
	}

	@Override
	public TeamIncomeTypeInfo getTeamIncome(AbstractInstance tank) {
		TeamIncomeTypeInfo info = new TeamIncomeTypeInfo();
		if (tank.getMatchFriendId() != 0) {
			info.setType(1);
			info.setIncome(friendTeamIncomeNum);
			return info;
		}
		if (tank.getMatchCorpMemberId() != 0) {
			int corpLevel = GameContext.getCorpsApp().getCorpLevel(tank.getRoleId());
			Integer num = corpTeamIncomeNumMap.get(corpLevel);
			if (num != null) {
				info.setType(2);
				info.setIncome(num);
				return info;
			}
		}
		info.setType(0);
		info.setIncome(0);
		return info;
	}


	@Override
	public void matchSchedule(int mapInstanceId, int roleId, int schedule, STC_MATCH_SCHEDULE_MSG.Builder builder) {
		MapInstance map = GameContext.getMapApp().getMapInstance(mapInstanceId);
		if (map == null) {
			return;
		}
		Map<Integer, Integer> scheduleMap = map.getTankEnterScheduleMap();
		scheduleMap.put(roleId, schedule);
//		for (Entry<Integer, Integer> entry : scheduleMap.entrySet()) {
//			System.out.println(entry.getKey() + "  &&&&");
//		}

		for (Entry<Integer, Integer> entry : scheduleMap.entrySet()) {
			MATCH_SCEDULE_INFO.Builder infoBuilder = MATCH_SCEDULE_INFO.newBuilder();
			infoBuilder.setRoleId(entry.getKey());
			infoBuilder.setSchedule(entry.getValue());
			builder.addInfos(infoBuilder);
		}

		for (Entry<Integer, Integer> entry : scheduleMap.entrySet()) {
			RoleConnect connect = GameContext.getUserApp().getRoleConnect(entry.getKey());
			if (connect != null) {
//				System.out.println(connect.getRoleId() + " ============");
				connect.sendMsg(builder.build());
			}
		}
	}

}
