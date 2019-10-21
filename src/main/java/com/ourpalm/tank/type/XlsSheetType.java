package com.ourpalm.tank.type;

public enum XlsSheetType {

	UserInitTemplate("user.xls","init"),
	UserLevelTemplate("user.xls","level"),
	UserInitMailTemplate("user.xls","initMail"),
	UserVipTemplate("user.xls","vip"),

	TankTemplate("tank.xls","tank"),
	TankPartTemplate("tank.xls","parts"),
	TankRobotNameTemplate("tank.xls", "name"),
	XTankRobotNameTemplate("tank.xls", "xname"),
	TankAiTemplate("tank.xls", "ai"),
	TankAiTestTemplate("tank.xls", "aiTest"),
	TankAiConfigTemplate("tank.xls", "aiConfig"),
	TankGoldTemplate("tank.xls", "goldTank"),
	TankCoefficient("tank.xls", "tankCoefficient"),

	BuildTemplate("build.xls", "build"),

	MapTemplate("map.xls", "map"),

	buffTemplate("buff.xls", "buff"),

	goodsBaseTemplate("goods.xls","goods"),
	goodsBoxTemplate("goods.xls","box"),
	goodsBoxGoodsGroup("goods.xls", "randomGoods"),
	goodsBoxTankGroup("goods.xls", "randomTank"),
	goodsBoxGoldGroup("goods.xls", "randomGold"),
	goodsBoxIronGroup("goods.xls", "randomIron"),
	goodsShellTemplate("goods.xls", "shell"),
	goodsWarTemplate("goods.xls", "war"),
	goodsMaterialTemplate("goods.xls", "material"),
	goodsChangeTemplate("goods.xls", "changeGoodsInfo"),

	shopTemplate("shop.xls", "shop"),
	shopItemTemplate("shop.xls", "item"),
	HonorShopTemplate("shop.xls", "shop"),
	HonorShopTtemTemplate("shop.xls", "honorShop"),
	VipShopTemplate("shop.xls", "shop"),
	VipShopTtemTemplate("shop.xls", "vipShopItem"),
	VipShopGroupTemplate("shop.xls", "vipShopGroup"),

	armyTitleTemplate("army-title.xls", "title"),
	TankAiGroupTemplate("army-title.xls", "ai-goods"),
	TankAiActionTemplate("army-title.xls", "ai"),
	TankAIPathTemplate("army-title.xls", "ai-path"),
	TankAIPointTypeMoveTemplate("army-title.xls", "path"),
	TankMatchMapTemplate("army-title.xls", "match"),
	RankBattleMatchTemplate("army-title.xls","season"),
	BattleTeamWeightTemplate("army-title.xls","team-weight"),

	questTemplate("quest.xls", "quest"),
	achievementTemplate("quest.xls", "achievement"),
	masterTemplate("quest.xls", "master"),
	questActiveTemplate("quest.xls", "active"),

	memberTemplate("member.xls", "member"),
	memberLotteryConfig("member.xls", "lotteryConfig"),
	memberLottery("member.xls", "lottery"),
	memberComposite("member.xls", "composite"),
	memberLevel("member.xls", "level"),
	memberProperty("member.xls", "property"),
	memberFormular("member.xls", "formular"),

	medalTemplate("goods.xls", "medal"),

	skillTemplate("skill.xls","skill"),

	corpsTemplate("corps.xls", "corps"),
	corpsTechTemplate("corps.xls", "technologies"),
	corpsGoodsTemplate("corps.xls", "goods"),
	corpsShopTemplate("corps.xls", "shop"),


	battleMapTemplate("battle.xls", "gate"),
	battleMapTankTemplate("battle.xls", "tank"),

	FriendTemplate("friend.xls", "friend"),


	campaignBoxTemplate("battle.xls", "box"),

	battleTeachTemplate("map.xls","teach"),
	battleTeachTankTemplate("map.xls", "teachTank"),
	battleRewardBase("map.xls", "reward"),
	mapBirthTemplate("map.xls", "birth"),
	battleWeakTemplate("map.xls", "weak"),
	battleHandUpTemplate("map.xls", "handup"),

	payTemplate("pay.xls", "pay"),
	SeasonRank("rank.xls", "seasonRank"),

	DailyActivityNotice("activity-daily.xls", "activityNotice"),
	DailyActivity("activity-daily.xls", "dailyActivity"),
	DailyActivity101Item("activity-daily.xls", "101_FightingCompetition"),
	DailyActivity102Item("activity-daily.xls", "102_FreeDouble"),
	DailyActivity103Item("activity-daily.xls", "103_ArmyTitlteLevel"),
	DailyActivity104Item("activity-daily.xls", "104_OnlineTime"),
	DailyActivity106Item("activity-daily.xls", "106_LoginReward"),

	HallActivity("activity-hall.xls", "hallActivity"),
	HallSevenLogin("activity-hall.xls", "sevenLogin"),
	HallMonthLoginSignCost("activity-hall.xls", "monthLoginSignCost"),
	HallMonthLoginReward("activity-hall.xls", "monthLoginReward"),
	HallMonthLoginPointReward("activity-hall.xls", "monthLoginPointReward"),
	HallRedPacketNode("activity-hall.xls", "redPacketNode"),
	HallRedPacketGroup("activity-hall.xls", "redPacketGroup"),
	HallRedPacketDraw("activity-hall.xls", "redPacketDraw"),
	HallDailyGrab("activity-hall.xls", "dailyGrab"),
	HallFirstPayReward("activity-hall.xls", "firstPayReward"),
	HallKeepOnline("activity-hall.xls", "keepOnline"),
	LuckyWheel("activity-hall.xls", "luckyWheel"),
	LuckyWheelDiamond("activity-hall.xls", "luckyWheelDiamond"),

	BlueItem("blue.xls", "blueGift"),
	QQHallItem("blue.xls", "qqhallGift"),
	QQZoneItem("blue.xls", "qqzoneGift"),
	BlueParam("blue.xls","blueParam"),
	YellowItem("blue.xls", "yellowGift"),

	sysconfig("sysconfig.xls", "config"),
	RankReward("rank.xls","rankReward"),

	Preseason("map.xls","preseason"),//新手赛
	redMoney("activity.xls","redMoney"),
	seasonRankReward("rank.xls","seasonRankReward"),
	teamIncomeAdd("friend.xls","teamIncomeAdd"),//组队收益

	PartNew("partNew.xls","part"),//新 改装
	Groove("partNew.xls","groove"),//槽
	PartDraw("partNew.xls","partDraw"),//开槽
	ClearFreeze("partNew.xls","clearFreeze"),//改装清CD
	Strengthen("partNew.xls","strengthen"),//新 改装

	Income("income.xls","income"),//收益
	ExtraIncome("income.xls","extraIncome"),//额外收益
	ExtraIncomeText("income.xls","extraIncomeText"),//额外收益文字 
	
	MemberNew("memberNew.xls","member"),//新乘员表
	MemberNewStar("memberNew.xls","star"),//新乘员星 
	MemberNewProperty("memberNew.xls","property"),//新乘员属性表
	MemberNewCombine("memberNew.xls","combine"),//新乘员组合技
	MemberNewCombineProperty("memberNew.xls","combineProperty"),//新乘员组合技属性
	MemberNewTankLevelLimit("memberNew.xls","tankLevelLimit"),//新乘员坦克限制
	MemberNewTankPropertyId("memberNew.xls","propertyId"),//新乘员属性ID
	;

	private String path = getResourcePath();
	private String xlsFileName;
	private String sheetName;

	static String getResourcePath(){
		if( System.getProperty("curpath") != null ){
			return System.getProperty("curpath")+"resources/";
		}
		return "resources/";
	}

	XlsSheetType(String xlsFileName, String sheetName){
		this.xlsFileName = path + xlsFileName;
		this.sheetName = sheetName;
	}

	public String getXlsFileName() {
		return xlsFileName;
	}

	public void setXlsFileName(String xlsFileName) {
		this.xlsFileName = xlsFileName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

}
