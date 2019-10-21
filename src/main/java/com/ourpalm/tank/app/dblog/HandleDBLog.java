package com.ourpalm.tank.app.dblog;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.UserAttr;

public class HandleDBLog extends DBLog {
	private static final String LOG_ID_KEY = "log_id";
	private static final String LOG_SUB_ID_KEY = "log_sub_id";
	private static final String LOG_ACTION_TIME = "action_time";
	
	private static final String LOG_SERVER_ID_KEY = "server_id";
	private static final String LOG_ROLE_ID_KEY = "role_id";
	private static final String LOG_USRE_ID_KEY = "user_id";
	
	private static final String LOG_VIP_KEY = "vip";
	private static final String LOG_IP_KEY = "ip";
	private static final String LOG_LEVEL_KEY = "level";
	private static final String LOG_NAME_KEY = "name";
	
	private static final String LOG_EXP_KEY = "exp";
	private static final String LOG_TANK_EXP_KEY = "tank_exp";
	private static final String LOG_DIAMONDS_KEY = "diamonds";
	private static final String LOG_GOLD_KEY = "gold";
	private static final String LOG_IRON_KEY = "iron";
	private static final String LOG_HONOR_KEY = "honor";
	private static final String LOG_ATK_KEY = "atk";
	
	public static final String LOG_CHAR1_KEY = "char1";
	public static final String LOG_CHAR2_KEY = "char2";
	public static final String LOG_CHAR3_KEY = "char3";
	public static final String LOG_CHAR4_KEY = "char4";
	public static final String LOG_CHAR5_KEY = "char5";
	public static final String LOG_CHAR6_KEY = "char6";
	public static final String LOG_CHAR7_KEY = "char7";
	public static final String LOG_CHAR8_KEY = "char8";
	public static final String LOG_CHAR9_KEY = "char9";
	public static final String LOG_CHAR10_KEY = "char10";


	public void initLog(int roleId, int logId, int logSubId) {
		this.setLogId(logId);
		this.setLogSubId(logSubId);
		this.setActionTime(System.currentTimeMillis() / 1000);
		this.setDbIndex(roleId % DBLogAppImpl.DB_NUM);
		this.setTableType(DBLogAppImpl.DBLogType.handleInc.type());

		this.getRecord().set(LOG_ID_KEY, logId);
		this.getRecord().set(LOG_SUB_ID_KEY, logSubId);
		this.getRecord().set( LOG_ACTION_TIME, this.getActionTime() );
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		
		this.getRecord().set(LOG_ROLE_ID_KEY, roleId);
		this.getRecord().set(LOG_USRE_ID_KEY, role.getUid());
		this.getRecord().set(LOG_NAME_KEY, role.getRoleName());
		this.getRecord().set(LOG_SERVER_ID_KEY, role.getAreaId());
		
		this.getRecord().set(LOG_LEVEL_KEY, role.getLevel());
		this.getRecord().set(LOG_IP_KEY, role.getClientIp());
		this.getRecord().set(LOG_VIP_KEY, role.getVipLevel());
		this.getRecord().set(LOG_ATK_KEY, role.getBattleScoreNow());
		
		UserAttr userAttr = GameContext.getUserAttrApp().getUserAttr(roleId);

		this.getRecord().set(LOG_EXP_KEY, userAttr.getExp());
		this.getRecord().set(LOG_TANK_EXP_KEY, userAttr.getTankExp());
		this.getRecord().set(LOG_DIAMONDS_KEY, userAttr.getDiamonds());
		this.getRecord().set(LOG_GOLD_KEY, userAttr.getGold());
		this.getRecord().set(LOG_IRON_KEY, userAttr.getIron());
		this.getRecord().set(LOG_HONOR_KEY, userAttr.getHonor());
		
		this.getRecord().set(LOG_CHAR1_KEY, "");
		this.getRecord().set(LOG_CHAR2_KEY, "");
		this.getRecord().set(LOG_CHAR3_KEY, "");
		this.getRecord().set(LOG_CHAR4_KEY, "");
		this.getRecord().set(LOG_CHAR5_KEY, "");
		this.getRecord().set(LOG_CHAR6_KEY, "");
		this.getRecord().set(LOG_CHAR7_KEY, "");
		this.getRecord().set(LOG_CHAR8_KEY, "");
		this.getRecord().set(LOG_CHAR9_KEY, "");
		this.getRecord().set(LOG_CHAR10_KEY, "");
	}
}
