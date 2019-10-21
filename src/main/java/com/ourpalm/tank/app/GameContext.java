package com.ourpalm.tank.app;

import com.ourpalm.core.dao.redis.Client;
import com.ourpalm.core.message.handler.MessageHandler;
import com.ourpalm.core.message.parser.MessageParser;
import com.ourpalm.core.node.Node;
import com.ourpalm.core.node.manager.RemoteManagerApp;
import com.ourpalm.tank.app.achievement.AchievementApp;
import com.ourpalm.tank.app.activity.ActivityApp;
import com.ourpalm.tank.app.battle.BattleApp;
import com.ourpalm.tank.app.blueVip.BlueApp;
import com.ourpalm.tank.app.buff.BuffApp;
import com.ourpalm.tank.app.continuelogin.ContinueLoginApp;
import com.ourpalm.tank.app.corps.CorpsApp;
import com.ourpalm.tank.app.dblog.module.GameDBLogApp;
import com.ourpalm.tank.app.dfa.MutilDfaApp;
import com.ourpalm.tank.app.friend.FriendApp;
import com.ourpalm.tank.app.goods.GoodsApp;
import com.ourpalm.tank.app.honor.HonorShopApp;
import com.ourpalm.tank.app.id.IdFactory;
import com.ourpalm.tank.app.im.InstantMessageApp;
import com.ourpalm.tank.app.log.GameLogApp;
import com.ourpalm.tank.app.luckyWheel.LuckyWheelApp;
import com.ourpalm.tank.app.mail.MailApp;
import com.ourpalm.tank.app.map.MapApp;
import com.ourpalm.tank.app.match.MatchApp;
import com.ourpalm.tank.app.member.MedalApp;
import com.ourpalm.tank.app.member.MemberApp;
import com.ourpalm.tank.app.memberNew.MemberNewApp;
import com.ourpalm.tank.app.online.OnlineCenter;
import com.ourpalm.tank.app.pay.PayApp;
import com.ourpalm.tank.app.platform.PlatformApp;
import com.ourpalm.tank.app.prompt.PromptApp;
import com.ourpalm.tank.app.qqHall.QQHallApp;
import com.ourpalm.tank.app.qqZone.QQZoneApp;
import com.ourpalm.tank.app.quest.MasterQuestApp;
import com.ourpalm.tank.app.quest.QuestApp;
import com.ourpalm.tank.app.quest.QuestTriggerApp;
import com.ourpalm.tank.app.rank.RankApp;
import com.ourpalm.tank.app.redMoney.RedMoneyApp;
import com.ourpalm.tank.app.season.SeasonRankApp;
import com.ourpalm.tank.app.shop.ShopApp;
import com.ourpalm.tank.app.shop.VipShopApp;
import com.ourpalm.tank.app.skill.SkillApp;
import com.ourpalm.tank.app.tank.TankApp;
import com.ourpalm.tank.app.title.RoleArmyTitleApp;
import com.ourpalm.tank.app.user.UserApp;
import com.ourpalm.tank.app.user.UserAttrApp;
import com.ourpalm.tank.app.vip.VipApp;
import com.ourpalm.tank.app.yellowVip.YellowApp;

public class GameContext {

	private static String clientVersion;

	private static int checkLoginControl;

	private static int activeShow;

	private static Node localNode;

	private static String loginCheckUrl;

	private static String txloginCheckUrl;

	private static String activecodeCallbackUrl;

	private static MessageHandler messageHandler;

	private static RemoteManagerApp gatewayManagerApp;

	private static RemoteManagerApp matchManagerApp;

	// private static RemoteManagerApp gameManagerApp;

	private static RemoteManagerApp platformManagerApp;

	private static RemoteManagerApp gmManagerApp;

	private static MessageParser msgParser;

	private static Client lock;

	private static IdFactory idFactory;

	private static OnlineCenter onlineCenter;

	private static UserApp userApp;

	private static BattleApp battleApp;

	private static TankApp tankApp;

	private static MatchApp matchApp;

	private static MapApp mapApp;

	private static GoodsApp goodsApp;

	private static ShopApp shopApp;

	private static HonorShopApp honorShopApp;

	private static UserAttrApp userAttrApp;

	private static RoleArmyTitleApp roleArmyTitleApp;

	private static QuestTriggerApp questTriggerApp;

	private static QuestApp questApp;

	private static AchievementApp achievementApp;

	private static BuffApp buffApp;

	private static MemberApp memberApp;

	private static MedalApp medalApp;

	private static SkillApp skillApp;

	private static CorpsApp corpsApp;

	private static FriendApp friendApp;

	private static MailApp mailApp;

	private static PromptApp promptApp;

	private static MutilDfaApp mutilDfaApp;

	private static InstantMessageApp imApp;

	private static MasterQuestApp masterQuestApp;

	private static VipApp vipApp;
	private static PayApp payApp;

	private static PlatformApp platformApp;

	private static SeasonRankApp seasonRankApp;

	private static ContinueLoginApp continueLoginApp;

	private static ActivityApp activityApp;

	private static VipShopApp vipShopApp;

	private static GameLogApp gameLogApp;

	private static GameDBLogApp gameDBLogApp;

	private static BlueApp blueApp;
	private static YellowApp yellowApp;
	private static QQHallApp qqHallApp;
	private static QQZoneApp qqZoneApp;
	private static LuckyWheelApp luckyWheelApp;

	private static RedMoneyApp redMoneyApp;

	private static RankApp rankApp;

	private static MemberNewApp memberNewApp;
	
	private static String serverPf;

	private static String txAppId;

	private static String txAppKey;

	private static String reportUrl;

	private static int reportNeed;

	private static String txBlueUrl;

	private static String txYellowUrl;

	private static String txQQUrl;

	private static String txTestBlueUserInfo;

	private static String txTestYellowUserInfo;

	private static String txTestNoBlueUserInfo;

	private static String txTestNoYellowUserInfo;

	private static String txTestQQUserInfo;

	private static boolean isPfVIP = true;

	private static int txTestUserInfoOpen;

	private static String txIsLoginUrl;

	private static String txPayPrepareUrl;

	private static int preseason;

	private static int preseasonID = 0;

	private static String mysql_user;
	private static String mysql_password;
	private static boolean mysql_devMode;
	private static String mysql_jdbcUrl_Ip;
	private static String mysql_jdbcUrl_storeName;
	private static int gameTest;
	private static int battleNew;

	
	private static boolean openTankAI = true;

	private static boolean useAiPath = false;
	private static String mapId;
	
	public static VipShopApp getVipShopApp() {
		return vipShopApp;
	}

	public void setVipShopApp(VipShopApp vipShopApp) {
		GameContext.vipShopApp = vipShopApp;
	}

	public static ActivityApp getActivityApp() {
		return activityApp;
	}

	public void setActivityApp(ActivityApp activityApp) {
		GameContext.activityApp = activityApp;
	}

	public static ContinueLoginApp getContinueLoginApp() {
		return continueLoginApp;
	}

	public void setContinueLoginApp(ContinueLoginApp continueLoginApp) {
		GameContext.continueLoginApp = continueLoginApp;
	}

	public static SeasonRankApp getSeasonRankApp() {
		return seasonRankApp;
	}

	public void setSeasonRankApp(SeasonRankApp seasonRankApp) {
		GameContext.seasonRankApp = seasonRankApp;
	}

	public static MailApp getMailApp() {
		return mailApp;
	}

	public void setMailApp(MailApp mailApp) {
		GameContext.mailApp = mailApp;
	}

	public static CorpsApp getCorpsApp() {
		return corpsApp;
	}

	public void setCorpsApp(CorpsApp corpsApp) {
		GameContext.corpsApp = corpsApp;
	}

	public static SkillApp getSkillApp() {
		return skillApp;
	}

	public void setSkillApp(SkillApp skillApp) {
		GameContext.skillApp = skillApp;
	}

	public static BuffApp getBuffApp() {
		return buffApp;
	}

	public void setBuffApp(BuffApp buffApp) {
		GameContext.buffApp = buffApp;
	}

	public static UserApp getUserApp() {
		return userApp;
	}

	public void setUserApp(UserApp userApp) {
		GameContext.userApp = userApp;
	}

	public static OnlineCenter getOnlineCenter() {
		return onlineCenter;
	}

	public void setOnlineCenter(OnlineCenter onlineCenter) {
		GameContext.onlineCenter = onlineCenter;
	}

	public static String getLocalNodeName() {
		return localNode.getHost();
	}

	public static RemoteManagerApp getGatewayManagerApp() {
		return gatewayManagerApp;
	}

	public void setGatewayManagerApp(RemoteManagerApp gatewayManagerApp) {
		GameContext.gatewayManagerApp = gatewayManagerApp;
	}

	public static IdFactory getIdFactory() {
		return idFactory;
	}

	public void setIdFactory(IdFactory idFactory) {
		GameContext.idFactory = idFactory;
	}

	public static BattleApp getBattleApp() {
		return battleApp;
	}

	public void setBattleApp(BattleApp battleApp) {
		GameContext.battleApp = battleApp;
	}

	public static TankApp getTankApp() {
		return tankApp;
	}

	public void setTankApp(TankApp tankApp) {
		GameContext.tankApp = tankApp;
	}

	public static MatchApp getMatchApp() {
		return matchApp;
	}

	public void setMatchApp(MatchApp matchApp) {
		GameContext.matchApp = matchApp;
	}

	public static MapApp getMapApp() {
		return mapApp;
	}

	public void setMapApp(MapApp mapApp) {
		GameContext.mapApp = mapApp;
	}

	public static MessageParser getMsgParser() {
		return msgParser;
	}

	public void setMsgParser(MessageParser msgParser) {
		GameContext.msgParser = msgParser;
	}

	public static GoodsApp getGoodsApp() {
		return goodsApp;
	}

	public void setGoodsApp(GoodsApp goodsApp) {
		GameContext.goodsApp = goodsApp;
	}

	public static ShopApp getShopApp() {
		return shopApp;
	}

	public void setShopApp(ShopApp shopApp) {
		GameContext.shopApp = shopApp;
	}

	public static UserAttrApp getUserAttrApp() {
		return userAttrApp;
	}

	public static void setUserAttrApp(UserAttrApp userAttrApp) {
		GameContext.userAttrApp = userAttrApp;
	}

	public static RoleArmyTitleApp getRoleArmyTitleApp() {
		return roleArmyTitleApp;
	}

	public void setRoleArmyTitleApp(RoleArmyTitleApp roleArmyTitleApp) {
		GameContext.roleArmyTitleApp = roleArmyTitleApp;
	}

	public static QuestApp getQuestApp() {
		return questApp;
	}

	public void setQuestApp(QuestApp questApp) {
		GameContext.questApp = questApp;
	}

	public static AchievementApp getAchievementApp() {
		return achievementApp;
	}

	public void setAchievementApp(AchievementApp achievementApp) {
		GameContext.achievementApp = achievementApp;
	}

	public static QuestTriggerApp getQuestTriggerApp() {
		return questTriggerApp;
	}

	public void setQuestTriggerApp(QuestTriggerApp questTriggerApp) {
		GameContext.questTriggerApp = questTriggerApp;
	}

	public static MemberApp getMemberApp() {
		return memberApp;
	}

	public void setMemberApp(MemberApp memberApp) {
		GameContext.memberApp = memberApp;
	}

	public static MedalApp getMedalApp() {
		return medalApp;
	}

	public static void setMedalApp(MedalApp medalApp) {
		GameContext.medalApp = medalApp;
	}

	public static RemoteManagerApp getMatchManagerApp() {
		return matchManagerApp;
	}

	public void setMatchManagerApp(RemoteManagerApp matchManagerApp) {
		GameContext.matchManagerApp = matchManagerApp;
	}

	public static FriendApp getFriendApp() {
		return friendApp;
	}

	public void setFriendApp(FriendApp friendApp) {
		GameContext.friendApp = friendApp;
	}

	public static PromptApp getPromptApp() {
		return promptApp;
	}

	public void setPromptApp(PromptApp promptApp) {
		GameContext.promptApp = promptApp;
	}

	public static MutilDfaApp getMutilDfaApp() {
		return mutilDfaApp;
	}

	public void setMutilDfaApp(MutilDfaApp mutilDfaApp) {
		GameContext.mutilDfaApp = mutilDfaApp;
	}

	public static String getLoginCheckUrl() {
		return loginCheckUrl;
	}

	public void setLoginCheckUrl(String loginCheckUrl) {
		GameContext.loginCheckUrl = loginCheckUrl;
	}

	public static String getTxloginCheckUrl() {
		return txloginCheckUrl;
	}

	public static void setTxloginCheckUrl(String txloginCheckUrl) {
		GameContext.txloginCheckUrl = txloginCheckUrl;
	}

	public static Client getLock() {
		return lock;
	}

	public void setLock(Client lock) {
		GameContext.lock = lock;
	}

	public void setLocalNode(Node localNode) {
		GameContext.localNode = localNode;
	}

	public static MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		GameContext.messageHandler = messageHandler;
	}

	public static InstantMessageApp getImApp() {
		return imApp;
	}

	public void setImApp(InstantMessageApp imApp) {
		GameContext.imApp = imApp;
	}

	public static HonorShopApp getHonorShopApp() {
		return honorShopApp;
	}

	public void setHonorShopApp(HonorShopApp honorShopApp) {
		GameContext.honorShopApp = honorShopApp;
	}

	public static MasterQuestApp getMasterQuestApp() {
		return masterQuestApp;
	}

	public void setMasterQuestApp(MasterQuestApp masterQuestApp) {
		GameContext.masterQuestApp = masterQuestApp;
	}

	public static RemoteManagerApp getPlatformManagerApp() {
		return platformManagerApp;
	}

	public void setPlatformManagerApp(RemoteManagerApp platformManagerApp) {
		GameContext.platformManagerApp = platformManagerApp;
	}

	public static RemoteManagerApp getGmManagerApp() {
		return gmManagerApp;
	}

	public static void setGmManagerApp(RemoteManagerApp gmManagerApp) {
		GameContext.gmManagerApp = gmManagerApp;
	}

	public static VipApp getVipApp() {
		return vipApp;
	}

	public void setVipApp(VipApp vipApp) {
		GameContext.vipApp = vipApp;
	}

	public static String getActivecodeCallbackUrl() {
		return activecodeCallbackUrl;
	}

	public void setActivecodeCallbackUrl(String activecodeCallbackUrl) {
		GameContext.activecodeCallbackUrl = activecodeCallbackUrl;
	}

	public static PayApp getPayApp() {
		return payApp;
	}

	public void setPayApp(PayApp payApp) {
		GameContext.payApp = payApp;
	}

	public static PlatformApp getPlatformApp() {
		return platformApp;
	}

	public void setPlatformApp(PlatformApp platformApp) {
		GameContext.platformApp = platformApp;
	}

	public static boolean isCheckLogin() {
		return checkLoginControl == 1;
	}

	public void setCheckLoginControl(int checkLoginControl) {
		GameContext.checkLoginControl = checkLoginControl;
	}

	public static boolean getActiveShow() {
		return activeShow == 1 ? true : false;
	}

	public void setActiveShow(int activeShow) {
		GameContext.activeShow = activeShow;
	}

	public static String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		GameContext.clientVersion = clientVersion;
	}

	public static GameLogApp getGameLogApp() {
		return gameLogApp;
	}

	public static void setGameLogApp(GameLogApp gameLogApp) {
		GameContext.gameLogApp = gameLogApp;
	}

	public static String getServerPf() {
		return serverPf;
	}

	public static void setServerPf(String serverPf) {
		GameContext.serverPf = serverPf;
	}

	public static String getTxAppId() {
		return txAppId;
	}

	public static void setTxAppId(String txAppId) {
		GameContext.txAppId = txAppId;
	}

	public static String getTxAppKey() {
		return txAppKey;
	}

	public static void setTxAppKey(String txAppKey) {
		GameContext.txAppKey = txAppKey;
	}

	public static String getReportUrl() {
		return reportUrl;
	}

	public static void setReportUrl(String reportUrl) {
		GameContext.reportUrl = reportUrl;
	}

	public static boolean isReportNeed() {
		return reportNeed == 1;
	}

	public static void setReportNeed(int reportNeed) {
		GameContext.reportNeed = reportNeed;
	}

	public static String getTxBlueUrl() {
		return txBlueUrl;
	}

	public static void setTxBlueUrl(String txBlueUrl) {
		GameContext.txBlueUrl = txBlueUrl;
	}

	public static BlueApp getBlueApp() {
		return blueApp;
	}

	public static void setBlueApp(BlueApp blueApp) {
		GameContext.blueApp = blueApp;
	}

	public static QQHallApp getQqHallApp() {
		return qqHallApp;
	}

	public static void setQqHallApp(QQHallApp qqHallApp) {
		GameContext.qqHallApp = qqHallApp;
	}

	public static int getTxTestUserInfoOpen() {
		return txTestUserInfoOpen;
	}

	public static void setTxTestUserInfoOpen(int txTestUserInfoOpen) {
		GameContext.txTestUserInfoOpen = txTestUserInfoOpen;
	}

	public static String getTxYellowUrl() {
		return txYellowUrl;
	}

	public static void setTxYellowUrl(String txYellowUrl) {
		GameContext.txYellowUrl = txYellowUrl;
	}

	public static String getTxQQUrl() {
		return txQQUrl;
	}

	public static void setTxQQUrl(String txQQUrl) {
		GameContext.txQQUrl = txQQUrl;
	}

	public static String getTxTestBlueUserInfo() {
		return txTestBlueUserInfo;
	}

	public static void setTxTestBlueUserInfo(String txTestBlueUserInfo) {
		GameContext.txTestBlueUserInfo = txTestBlueUserInfo;
	}

	public static String getTxTestYellowUserInfo() {
		return txTestYellowUserInfo;
	}

	public static void setTxTestYellowUserInfo(String txTestYellowUserInfo) {
		GameContext.txTestYellowUserInfo = txTestYellowUserInfo;
	}

	public static String getTxTestQQUserInfo() {
		return txTestQQUserInfo;
	}

	public static void setTxTestQQUserInfo(String txTestQQUserInfo) {
		GameContext.txTestQQUserInfo = txTestQQUserInfo;
	}

	public static String getTxIsLoginUrl() {
		return txIsLoginUrl;
	}

	public static void setTxIsLoginUrl(String txIsLoginUrl) {
		GameContext.txIsLoginUrl = txIsLoginUrl;
	}

	public static String getTxPayPrepareUrl() {
		return txPayPrepareUrl;
	}

	public static void setTxPayPrepareUrl(String txPayPrepareUrl) {
		GameContext.txPayPrepareUrl = txPayPrepareUrl;
	}

	public static LuckyWheelApp getLuckyWheelApp() {
		return luckyWheelApp;
	}

	public static void setLuckyWheelApp(LuckyWheelApp luckyWheelApp) {
		GameContext.luckyWheelApp = luckyWheelApp;
	}

	public static RankApp getRankApp() {
		return rankApp;
	}

	public static void setRankApp(RankApp rankApp) {
		GameContext.rankApp = rankApp;
	}

	public static String getMysql_jdbcUrl_Ip() {
		return mysql_jdbcUrl_Ip;
	}

	public static void setMysql_jdbcUrl_Ip(String mysql_jdbcUrl_Ip) {
		GameContext.mysql_jdbcUrl_Ip = mysql_jdbcUrl_Ip;
	}

	public static String getMysql_jdbcUrl_storeName() {
		return mysql_jdbcUrl_storeName;
	}

	public static void setMysql_jdbcUrl_storeName(String mysql_jdbcUrl_storeName) {
		GameContext.mysql_jdbcUrl_storeName = mysql_jdbcUrl_storeName;
	}

	public static String getMysql_jdbcUrl() {
		StringBuffer sb = new StringBuffer();
		sb.append("	jdbc:mysql://").append(getMysql_jdbcUrl_Ip()).append("/").append(getMysql_jdbcUrl_storeName()).append("?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull");
		return sb.toString();
	}

	public static String getMysql_user() {
		return mysql_user;
	}

	public static void setMysql_user(String mysql_user) {
		GameContext.mysql_user = mysql_user;
	}

	public static String getMysql_password() {
		return mysql_password;
	}

	public static void setMysql_password(String mysql_password) {
		GameContext.mysql_password = mysql_password;
	}

	public static boolean isMysql_devMode() {
		return mysql_devMode;
	}

	public static void setMysql_devMode(boolean mysql_devMode) {
		GameContext.mysql_devMode = mysql_devMode;
	}

	public static GameDBLogApp getGameDBLogApp() {
		return gameDBLogApp;
	}

	public static void setGameDBLogApp(GameDBLogApp gameDBLogApp) {
		GameContext.gameDBLogApp = gameDBLogApp;
	}

	public static YellowApp getYellowApp() {
		return yellowApp;
	}

	public static void setYellowApp(YellowApp yellowApp) {
		GameContext.yellowApp = yellowApp;
	}

	public static int getGameTest() {
		return gameTest;
	}

	public static void setGameTest(int gameTest) {
		GameContext.gameTest = gameTest;
	}

	public static boolean isOpenTankAI() {
		return openTankAI;
	}

	public static void setOpenTankAI(boolean openTankAI) {
		GameContext.openTankAI = openTankAI;
	}

	public static boolean isUseAiPath() {
		return useAiPath;
	}

	public static void setUseAiPath(boolean useAiPath) {
		GameContext.useAiPath = useAiPath;
	}

	public static int getPreseason() {
		return preseason;
	}

	public static void setPreseason(int preseason) {
		GameContext.preseason = preseason;
	}

	public static int getPreseasonID() {
		return preseasonID;
	}

	public static void setPreseasonID(int preseasonID) {
		GameContext.preseasonID = preseasonID;
	}

	public static String getTxTestNoBlueUserInfo() {
		return txTestNoBlueUserInfo;
	}

	public static void setTxTestNoBlueUserInfo(String txTestNoBlueUserInfo) {
		GameContext.txTestNoBlueUserInfo = txTestNoBlueUserInfo;
	}

	public static String getTxTestNoYellowUserInfo() {
		return txTestNoYellowUserInfo;
	}

	public static void setTxTestNoYellowUserInfo(String txTestNoYellowUserInfo) {
		GameContext.txTestNoYellowUserInfo = txTestNoYellowUserInfo;
	}

	public static boolean isPfVIP() {
		return isPfVIP;
	}

	public static void setPfVIP(boolean isPfVIP) {
		GameContext.isPfVIP = isPfVIP;
	}

	public static RedMoneyApp getRedMoneyApp() {
		return redMoneyApp;
	}

	public static void setRedMoneyApp(RedMoneyApp redMoneyApp) {
		GameContext.redMoneyApp = redMoneyApp;
	}

	public static int getBattleNew() {
		return battleNew;
	}

	public static void setBattleNew(int battleNew) {
		GameContext.battleNew = battleNew;
	}

	public static QQZoneApp getQqZoneApp() {
		return qqZoneApp;
	}

	public static void setQqZoneApp(QQZoneApp qqZoneApp) {
		GameContext.qqZoneApp = qqZoneApp;
	}

	public static String getMapId() {
		return mapId;
	}

	public static void setMapId(String mapId) {
		GameContext.mapId = mapId;
	}

	public static MemberNewApp getMemberNewApp() {
		return memberNewApp;
	}

	public static void setMemberNewApp(MemberNewApp memberNewApp) {
		GameContext.memberNewApp = memberNewApp;
	}


	
}
