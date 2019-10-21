package com.ourpalm.tank.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.tank.constant.RankEnum;

public class RoleAccount {

	private int roleId;					//角色ID
	private int areaId;					//分区ID
	private String uid;					//唯一账号ID
	private String roleName;			//角色名称
	private String serviceCode;			//特征串（日志需要调用）
	private int level = 1;				//玩家等级
	private int mainTankId;				//当前主战坦克
	private Date dailyFlushDate ;				//每日刷新时间
	private Date createDate = new Date();		//创建时间
	private Date lastLoginDate = new Date();	//上次登录时间
	private Date loginDate = new Date();		//记录每次登录时间
	private Date offlineDate = new Date();		//上次离线时间
	private String clientIp = ""; // 登录IP
	private String pfId = ""; // tx 各个平台ID
	private String openKey = ""; // tx openKey
	private String pf = ""; // tx 各个平台名称

	// private int corpsId; //军团id
	private int battleScore;			//历史最高战斗力
	private int teachId;				//新手引导
	private int battleRewardIron;  		//每日战斗银币奖励
	private int teachGold;				//完成新手引导金币奖励
	private String serviceId = "";		//用于渠道渠道用户
	private String returnJson = "";		//第三方返回的用户信息
	private int vipLevel;				//vip等级
	private String teamId;				//队伍id

	private long loginForbidBeginTime;	//登录禁止开始时间
	private long loginForbidEndTime;	
	private long talkForbidBeginTime;	//禁言开始时间
	private long talkForbidEndTime;

	private long dayDrawRewardsTime;	//每日最高军衔奖励领取时间
	private int maxTitleId;				//历史最高军衔
	private int seasonMaxTitleId;		//本赛季最高军衔
	private int lastSeansonMaxTitleId;	//昨日达到的本赛季最高军衔,用于每日领奖
	private int currTitleId;			//当前军衔
	private Date seasonEndDate;			//赛季结束时间
	private int score;					//赛季积分

	private long lastSaluteTime;		//上次军团签到时间

	private String tank1Area;			//tank1分区
	private String tank1AccountName;	//tank1角色名

	private int battleScoreNow;// 即时战力

	private HashMap<RankEnum, RankRewardStateInfo> rankReward = new HashMap<>();// 排行榜奖励，<榜单ID，获得时间>
	
	private int park;//当前拥有车位

	private long lastStrengthenTime;// 上次强化时间
	
	private long freezeExpire;//目标冷却时间 

	private boolean canFreeze;// 是否可以强化

	private int clearFreezeNum;// 消除强化CD的次数
	
	private MemberColumn column1 = new MemberColumn();// 乘员栏位1
	private MemberColumn column2 = new MemberColumn();// 乘员栏位2
	private MemberColumn column3 = new MemberColumn();// 乘员栏位3
	private int currMemberColumn =1;//当前使用的乘员栏
	
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getAreaId() {
		return areaId;
	}

	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getMainTankId() {
		return mainTankId;
	}

	public void setMainTankId(int mainTankId) {
		this.mainTankId = mainTankId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Date getOfflineDate() {
		return offlineDate;
	}

	public void setOfflineDate(Date offlineDate) {
		this.offlineDate = offlineDate;
	}

	// public int getCorpsId() {
	// return corpsId;
	// }
	// public void setCorpsId(int corpsId) {
	// this.corpsId = corpsId;
	// }
	public int getBattleScore() {
		return battleScore;
	}

	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}

	public int getTeachId() {
		return teachId;
	}

	public void setTeachId(int teachId) {
		this.teachId = teachId;
	}

	public int getBattleRewardIron() {
		return battleRewardIron;
	}

	public void setBattleRewardIron(int battleRewardIron) {
		this.battleRewardIron = battleRewardIron;
	}

	public int getTeachGold() {
		return teachGold;
	}

	public void setTeachGold(int teachGold) {
		this.teachGold = teachGold;
	}

	public String getReturnJson() {
		return returnJson;
	}

	public void setReturnJson(String returnJson) {
		this.returnJson = returnJson;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public long getLoginForbidBeginTime() {
		return loginForbidBeginTime;
	}

	public void setLoginForbidBeginTime(long loginForbidBeginTime) {
		this.loginForbidBeginTime = loginForbidBeginTime;
	}

	public long getLoginForbidEndTime() {
		return loginForbidEndTime;
	}

	public void setLoginForbidEndTime(long loginForbidEndTime) {
		this.loginForbidEndTime = loginForbidEndTime;
	}

	public long getTalkForbidBeginTime() {
		return talkForbidBeginTime;
	}

	public void setTalkForbidBeginTime(long talkForbidBeginTime) {
		this.talkForbidBeginTime = talkForbidBeginTime;
	}

	public long getTalkForbidEndTime() {
		return talkForbidEndTime;
	}

	public void setTalkForbidEndTime(long talkForbidEndTime) {
		this.talkForbidEndTime = talkForbidEndTime;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getDayDrawRewardsTime() {
		return dayDrawRewardsTime;
	}

	public void setDayDrawRewardsTime(long dayDrawRewardsTime) {
		this.dayDrawRewardsTime = dayDrawRewardsTime;
	}

	public int getMaxTitleId() {
		return maxTitleId;
	}

	public void setMaxTitleId(int maxTitleId) {
		this.maxTitleId = maxTitleId;
	}

	public int getSeasonMaxTitleId() {
		return seasonMaxTitleId;
	}

	public void setSeasonMaxTitleId(int seasonMaxTitleId) {
		this.seasonMaxTitleId = seasonMaxTitleId;
	}

	public int getLastSeansonMaxTitleId() {
		return lastSeansonMaxTitleId;
	}

	public void setLastSeansonMaxTitleId(int lastSeansonMaxTitleId) {
		this.lastSeansonMaxTitleId = lastSeansonMaxTitleId;
	}

	public int getCurrTitleId() {
		return currTitleId;
	}

	public void setCurrTitleId(int currTitleId) {
		this.currTitleId = currTitleId;
	}

	public Date getSeasonEndDate() {
		return seasonEndDate;
	}

	public void setSeasonEndDate(Date seasonEndDate) {
		this.seasonEndDate = seasonEndDate;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getLastSaluteTime() {
		return lastSaluteTime;
	}

	public void setLastSaluteTime(long lastSaluteTime) {
		this.lastSaluteTime = lastSaluteTime;
	}

	public boolean hadLoginForbid() {
		long curTime = System.currentTimeMillis();
		return loginForbidBeginTime <= curTime && curTime <= loginForbidEndTime;
	}

	public boolean hadTalkForbid() {
		long curTime = System.currentTimeMillis();
		return talkForbidBeginTime <= curTime && curTime <= talkForbidEndTime;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getTank1Area() {
		return tank1Area;
	}

	public void setTank1Area(String tank1Area) {
		this.tank1Area = tank1Area;
	}

	public String getTank1AccountName() {
		return tank1AccountName;
	}

	public void setTank1AccountName(String tank1AccountName) {
		this.tank1AccountName = tank1AccountName;
	}

	public Date getDailyFlushDate() {
		return dailyFlushDate;
	}

	public void setDailyFlushDate(Date dailyFlushDate) {
		this.dailyFlushDate = dailyFlushDate;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getPfId() {
		return pfId;
	}

	public void setPfId(String pfId) {
		this.pfId = pfId;
	}

	public String getOpenKey() {
		return openKey;
	}

	public void setOpenKey(String openKey) {
		this.openKey = openKey;
	}

	public String getPf() {
		return pf;
	}

	public void setPf(String pf) {
		this.pf = pf;
	}

	public HashMap<RankEnum, RankRewardStateInfo> getRankReward() {
		return rankReward;
	}

	public void setRankReward(HashMap<RankEnum, RankRewardStateInfo> rankReward) {
		this.rankReward = rankReward;
	}

	public int getBattleScoreNow() {
		return battleScoreNow;
	}

	public void setBattleScoreNow(int battleScoreNow) {
		this.battleScoreNow = battleScoreNow;
	}

	public int getPark() {
		return park;
	}

	public void setPark(int park) {
		this.park = park;
	}

	public long getLastStrengthenTime() {
		return lastStrengthenTime;
	}

	public void setLastStrengthenTime(long lastStrengthenTime) {
		this.lastStrengthenTime = lastStrengthenTime;
	}


	public boolean isCanFreeze() {
		return canFreeze;
	}

	public void setCanFreeze(boolean canFreeze) {
		this.canFreeze = canFreeze;
	}

	public int getClearFreezeNum() {
		return clearFreezeNum;
	}

	public void setClearFreezeNum(int clearFreezeNum) {
		this.clearFreezeNum = clearFreezeNum;
	}

	public long getFreezeExpire() {
		return freezeExpire;
	}

	public void setFreezeExpire(long freezeExpire) {
		this.freezeExpire = freezeExpire;
	}

	public MemberColumn getColumn1() {
		return column1;
	}

	public void setColumn1(MemberColumn column1) {
		this.column1 = column1;
	}

	public MemberColumn getColumn2() {
		return column2;
	}

	public void setColumn2(MemberColumn column2) {
		this.column2 = column2;
	}

	public MemberColumn getColumn3() {
		return column3;
	}

	public void setColumn3(MemberColumn column3) {
		this.column3 = column3;
	}

	public int getCurrMemberColumn() {
		return currMemberColumn;
	}

	public void setCurrMemberColumn(int currMemberColumn) {
		this.currMemberColumn = currMemberColumn;
	}





}
