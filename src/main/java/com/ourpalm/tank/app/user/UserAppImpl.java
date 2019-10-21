package com.ourpalm.tank.app.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.UserAttrDao;
import com.ourpalm.tank.dao.UserDAO;
import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.template.UserLevelTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.BattleRewardResult;

public class UserAppImpl implements UserApp {

	private final static Logger logger = LogCore.runtime;
	private UserInitTemplate initTemplate;
	private Map<Integer, UserLevelTemplate> levels = new HashMap<>();
	private Map<Integer, RoleAccount> allRoleMap = new ConcurrentHashMap<>();
	private UserDAO userDAO;
	private UserAttrDao userAttrDao;

	@Override
	public void start() {
		this.loadInitTemplate();
		this.loadLevelTemplate();
	}

	public void loadInitTemplate() {
		String sourceFile = XlsSheetType.UserInitTemplate.getXlsFileName();
		String sheetName = XlsSheetType.UserInitTemplate.getSheetName();
		try {
			initTemplate = XlsPojoUtil.sheetToList(sourceFile, sheetName, UserInitTemplate.class).get(0);
			initTemplate.init();
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	public void loadLevelTemplate() {
		String sourceFile = XlsSheetType.UserLevelTemplate.getXlsFileName();
		String sheetName = XlsSheetType.UserLevelTemplate.getSheetName();
		try {
			List<UserLevelTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, UserLevelTemplate.class);
			for (UserLevelTemplate template : list) {
				template.init();
				this.levels.put(template.getLevel(), template);
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public void createUser(int areaId, String uid, String roleName, int country) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(areaId, uid);
		if (role != null) {
			return;
		}
		int roleId = GameContext.getIdFactory().nextInt();
		Date now = new Date();

		role = new RoleAccount();
		role.setRoleId(roleId);
		role.setRoleName(roleName);
		role.setUid(uid);
		role.setAreaId(areaId);
		role.setCreateDate(now);
		role.setOfflineDate(now);
		role.setLastLoginDate(now);
		role.setLoginDate(now);
		role.setTeachGold(initTemplate.getTeachGold());
		role.setVipLevel(0);
		role.setLevel(1);
		role.setPark(initTemplate.getPark());//初始车位数

		// 创建玩家属性对象
		UserAttr userAttr = new UserAttr();
		userAttr.setRoleId(roleId);
		userAttr.setExp(0);
		userAttr.setGold(initTemplate.getGold());
		userAttr.setIron(initTemplate.getIron());
		userAttr.setDiamonds(initTemplate.getDiamonds());
		userAttrDao.saveUserAttr(userAttr);

		// 初始化坦克
		// 只送一辆玩家所选择的国家系别坦克
		for (int tankId : initTemplate.getInitTankList()) {
			TankTemplate tankTemplate = GameContext.getTankApp().getTankTemplate(tankId);
			int _country = tankTemplate.getCountry_i();
			if (_country == country) {
				this.initTankLogic(roleId, tankId,initTemplate);
				role.setMainTankId(tankId); // 设置主战坦克
				break;
			}
		}
		// 创建账号
		this.userDAO.createRoleAccount(role);

		// 初始化邮件
		GameContext.getMailApp().createUser(roleId);
		GameContext.getActivityApp().createUser(roleId);
		GameContext.getMasterQuestApp().createUser(roleId, initTemplate.getInitQuestList());
		GameContext.getVipApp().createUser(roleId);
		GameContext.getRoleArmyTitleApp().createInit(roleId, initTemplate.getTitleId());
	}

	// 初始化坦克
	private void initTankLogic(int roleId, int tankId,UserInitTemplate initTemplate) {
		GameContext.getTankApp().createRoleTank(roleId, tankId,initTemplate);
	}

	/**
	 * 登录初始化
	 */
	@Override
	public void loginInit(int roleId, boolean online) {
		RoleAccount role = this.getRoleAccount(roleId);
		// 放入缓存
		allRoleMap.put(roleId, role);

		Date now = new Date();
		// 上线登录操作
		if (online) {
			role.setLastLoginDate(role.getLoginDate());
			role.setLoginDate(now);
			saveRoleAccount(role);
		}

		boolean nextDayLogin = !DateUtil.isSameDay(now, role.getDailyFlushDate());
		if (nextDayLogin) {
			role.setDailyFlushDate(now);
			saveRoleAccount(role);
		}

		/***** 模块初始化 try下不影响别的模块加载 *****/

		// 任务加载
		try {
			GameContext.getQuestApp().login(roleId, nextDayLogin);
			GameContext.getMasterQuestApp().login(roleId);
		} catch (Exception e) {
			logger.error("加载任务异常 roleId=" + roleId, e);
		}

		// 成就加载
		try {
			GameContext.getAchievementApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("加载成就异常 roleId=" + roleId, e);
		}

		// 商场初始化
		try {
			GameContext.getShopApp().login(roleId, nextDayLogin);
			GameContext.getHonorShopApp().login(roleId, nextDayLogin);
			GameContext.getVipShopApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("加载商场异常 roleId=" + roleId, e);
		}

		// 军团处理
		try {
			GameContext.getCorpsApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("登录处理军团异常 roleId=" + roleId, e);
		}
		// 好友处理
		try {
			GameContext.getFriendApp().login(roleId);
		} catch (Exception e) {
			logger.error("登录处理好友异常 roleId=" + roleId, e);
		}

		// 抽奖信息
		try {
			GameContext.getMemberApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("登录初始化抽奖异常 roleId=" + roleId, e);
		}

		// 军衔排位赛
		try {
			GameContext.getRoleArmyTitleApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("登录初始化军衔排位赛 roleId=" + roleId, e);
		}

		// 活动
		try {
			GameContext.getActivityApp().login(roleId, online, role.getServiceId());
		} catch (Exception e) {
			logger.error("登录初始化军衔排位赛 roleId=" + roleId, e);
		}

		// 送激活码
		try {
			GameContext.getContinueLoginApp().login(roleId);
		} catch (Exception e) {
			logger.error("登录初始化军衔排位赛 roleId=" + roleId, e);
		}

		// 邮件
		try {
			GameContext.getMailApp().login(roleId);
		} catch (Exception e) {
			logger.error("登录初始化军衔排位赛 roleId=" + roleId, e);
		}

		// 坦克疲劳度
		try {
			if (online) {
				GameContext.getTankApp().login(roleId, nextDayLogin);
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		// 更新免费复活次数
		try {
			GameContext.getBattleApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 刷新蓝钻信息
		try {
			GameContext.getBlueApp().refreshBlue(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 推送QQ大厅信息
		try {
			GameContext.getQqHallApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 推送QQ空间信息
		try {
			GameContext.getQqZoneApp().login(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}
		// 推送幸运转盘信息
		try {
			GameContext.getLuckyWheelApp().refresh(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}

		// 刷新黄钻信息
		try {
			GameContext.getYellowApp().refreshYellow(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}
		// 刷新每日改装冷却次数
		try {
			GameContext.getTankApp().refreshFreezeNum(roleId, nextDayLogin);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public void saveRoleConnect(RoleConnect roleConnect) {
		userDAO.saveRoleConnect(roleConnect);
	}

	/** 加载账号信息 */
	@Override
	public RoleAccount getRoleAccount(int areaId, String uid) {
		return userDAO.getRoleAccount(areaId, uid);
	}

	@Override
	public void removeRoleConnect(int roleId) {
		userDAO.removeRoleConnect(roleId);
	}

	@Override
	public RoleAccount getRoleAccount(int roleId) {
		RoleAccount role = allRoleMap.get(roleId);
		if (role == null) {
			return userDAO.getRoleAccount(roleId);
		}
		return role;
	}

	@Override
	public void saveRoleAccount(RoleAccount account) {
		this.userDAO.saveRoleAccount(account);
	}

	@Override
	public UserLevelTemplate getLevelTemplate(int level) {
		return this.levels.get(level);
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public RoleConnect getRoleConnect(int roleId) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			return connect;
		}
		return userDAO.getRoleConnect(roleId);
	}

	/** 检测角色名是否存在 */
	@Override
	public boolean roleNameHadExist(String roleName) {
		return this.userDAO.roleNameHadExist(roleName);
	}

	@Override
	public int getRoleId(String roleName) {
		return userDAO.getRoleId(roleName);
	}

	@Override
	public void delNameRoleIdLink(String roleName) {
		userDAO.delNameRoleIdLink(roleName);
	}

	@Override
	public void addNameRoleIdLink(int roleId, String roleName) {
		userDAO.addNameRoleIdLink(roleName, roleId);
	}

	public void setUserAttrDao(UserAttrDao userAttrDao) {
		this.userAttrDao = userAttrDao;
	}

	@Override
	public BattleRewardResult addBattleEndIronReward(int roleId, int iron) {
//		BattleRewardResult rst = new BattleRewardResult();
		RoleAccount role = getRoleAccount(roleId);
//		UserLevelTemplate levelTemplate = GameContext.getUserApp().getLevelTemplate(role.getLevel());
		// 每日银币上限值
//		int ironMax = levelTemplate.getRewardIronMax();
		// 今日累计获得
//		int todayRewardIron = role.getBattleRewardIron();

		// VIP对银币上限的加成
//		final float vipValue = GameContext.getVipApp().getPrivilegeIronMaxPercent(roleId);
//		ironMax = (int) (ironMax * (1 + vipValue));

//		// 今日达到上限
//		if (todayRewardIron >= ironMax) {
//			rst.setIron(0);
//			rst.setDayMaxIron(ironMax);
//			rst.setDayIron(ironMax);
//			return rst;
//		}

//		int canAdd = ironMax - todayRewardIron;
//		int result = iron < canAdd ? iron : canAdd;
//
//		todayRewardIron = todayRewardIron + result;
//		role.setBattleRewardIron(todayRewardIron);
		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, iron), OutputType.battleEndRewardInc);

		// 战场获得银币数
		if (GameContext.getOnlineCenter().hadOnline(roleId)) {
			GameContext.getQuestTriggerApp().roleBattleIron(roleId, iron);
		}
		GameContext.getUserApp().saveRoleAccount(role);

//		rst.setIron(result);
//		rst.setDayIron(todayRewardIron);
//		rst.setDayMaxIron(ironMax);

		return null;
	}

	@Override
	public UserInitTemplate getUserInitTemplate() {
		return initTemplate;
	}

	@Override
	public void offline(int roleId, boolean offline) {
		// 清除缓存
		allRoleMap.remove(roleId);

		if (offline) {
			RoleAccount role = this.getRoleAccount(roleId);
			if (role != null) {
				role.setOfflineDate(new Date());
				saveRoleAccount(role);
			}
			int onlinetime = (int) ((System.currentTimeMillis() - role.getLoginDate().getTime()) / 1000);
			if (GameContext.isReportNeed()) {
				GameContext.getGameLogApp().sendQuitLog(role.getAreaId(), role.getPfId(), role.getUid(), role.getClientIp(), onlinetime);
			}
			GameContext.getGameDBLogApp().getUserLog().userLogout(role.getUid(), roleId, role.getAreaId(), role.getRoleName(), role.getLevel(), role.getPf(), role.getClientIp(), onlinetime);
		}
	}

	@Override
	public int getCorpsId(int roleId) {
		return userDAO.getCorpsId(roleId);
	}

	@Override
	public void saveCorpsId(int roleId, int corpsId) {
		userDAO.saveCorpsId(roleId, corpsId);
	}

	@Override
	public void savePfUserInfo(int roleId, PFUserInfo pfUserInfo) {
		this.userDAO.savePfUserInfo(roleId, pfUserInfo);
	}

	@Override
	public PFUserInfo getPfUserInfo(int roleId) {
		return userDAO.getPfUserInfo(roleId);
	}

	@Override
	public String getPfUserInfoStr(int roleId) {
		PFUserInfo pfUserInfo = GameContext.getUserApp().getPfUserInfo(roleId);
		if (pfUserInfo != null) {
			return JSON.toJSONString(pfUserInfo);
		} else {
			return "";
		}
	}

	@Override
	public PFUserInfo getRefreshedPf(int roleId) {
		return userDAO.getPfUserUpdateInfo(roleId);
	}

	@Override
	public void savePfYellowUserInfo(int roleId, PFYellowUserInfo pfYellowUserInfo) {
		this.userDAO.savePfYellowUserInfo(roleId, pfYellowUserInfo);
	}

	@Override
	public PFYellowUserInfo getPfYellowUserInfo(int roleId) {
		return userDAO.getPfYellowUserInfo(roleId);
	}

	@Override
	public String getPfYellowUserInfoStr(int roleId) {
		PFYellowUserInfo pfYellowUserInfo = GameContext.getUserApp().getPfYellowUserInfo(roleId);
		if (pfYellowUserInfo != null) {
			return JSON.toJSONString(pfYellowUserInfo);
		} else {
			return "";
		}
	}

	@Override
	public PFYellowUserInfo getRefreshedPfYellow(int roleId) {
		return userDAO.getPfYellowUserUpdateInfo(roleId);
	}

	@Override
	public String getNameByRoleId(int roleId) {
		RoleAccount roleAccount = getRoleAccount(roleId);
		if (roleAccount == null)
			return "";
		return roleAccount.getRoleName();
	}
}
