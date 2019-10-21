package com.ourpalm.tank.app.log;

public enum OutputType {

	/**
	 * 系统产出  1 开头
	 * 玩家消耗  2 开头
	 */
	
	questInc(1001, "任务"),								//任务产出
	shotInc(1002, "商城"),			 					//商城产出
	corpsShopBuyInc(1003, "军团商城"), 					//军团商城产出
	corpsSaluteInc(1004, "军团向长官敬礼"), 				//军团向长官敬礼
	goodsOpenBoxInc(1005, "开宝箱"), 					//开宝箱 
	achievementDrawInc(1006, "领取成就"), 				//领取成就
	friendReceiveGoodsInc(1007, "接收好友礼物"), 			//接收好友礼物
	friendSendGoodsInc(1008, "赠送好友礼物"), 	 		//赠送好友礼物
	materialDecomponseInc(1009, "分解的材料"), 			//分解的材料
	titleLevelRewardInc(1010, "军衔等级奖励结果"), 		//军衔等级奖励结果
	goodsSellInc(1011, "物品出售"), 			 			//物品出售
	corpsDonateGoodsInc(1012, "军团捐献"), 	 			//军团捐献
	corpsDonateGoldInc(1013, "科技金钱捐献"), 			//科技金钱捐献
	campaignBoxDrawInc(1014, "战役宝箱奖励"), 			//战役宝箱奖励
	mailGetAttachInc(1015, "领取邮件"), 	 	 			//领取附件
	mailGetAllAttachInc(1016, "领取邮件"), 	 			//领取全部附件
	materialComponseInc(1017, "合成的材料"), 	 			//合成的材料
	passCommonBarrierInc(1018, "战役通关普通奖励"),  		//战役通关普通奖励
	lotteryInc(1019, "抽奖"),
	shopBuyInc(1020, "商店购买"),  						//商店购买
	goodsQuickBuyInc(1021, "战斗快速购买"), 	 			//战斗快速购买
	medalComposeInc(1022, "勋章合成"), 		 			//勋章合成
	medalExchangeInc(1023, "勋章穿戴"), 					//勋章穿戴
	tankUpInc(1024, "坦克升级"),				 			//坦克升级
	lotteryDiamondsOnceInc(1025, "钻石单抽"),			//单抽
	lotteryDiamondsMutilInc(1026, "钻石五连抽"),			//多抽
	lotteryIronOnceInc(1027, "银币单抽"),			//单抽
	lotteryIronMutilInc(1028, "银币五连抽"),			//多抽
	memberComposeInc(1029, "成员合成"),		//成员合成
	memberLevelUpInc(1030, "吃成员"), 	//吃成员
	activeCodeRewsInc(1031, "激活码兑换"),
	gmRewardInc(1032, "gm发送全服奖励"), 
	battleEndRewardInc(1033, "战斗结束奖励"),
	armyTitleDayRewardInc(1034, "军衔每日领取奖励"),
	masterQuestRewardInc(1035, "主线任务奖励"),
	questActiveRewardInc(1036, "领取活跃奖励"),
	payRewardInc(1037, "充值奖励"),
	KeepOnlineInc(1038, "在线经验奖励"),
	diamondTranslateToGoldInc(1039,"钻石对换金币"),		//钻石对换金币
	goldTranslateToIronInc(1040,"金币对换银币"),		//金币对换银币
	newRoleGuideRewardInc(1041,"领取新手引导奖励"),		//领取新手引导奖励
	battlefieldDropRewardInc(1042,"战场掉落获得"),		//战场掉落获得
	sevenLoginInc(1043,"七日登录奖励"),		//七日登录奖励
	vipRewardInc(1044,"vip等级奖励"),		//vip等级奖励
	monthLoginAddUpInc(1045,"领取月累节点奖励"),	//领取月累节点奖励
	monthLoginSignInc(1046,"月累积活动签到"),	//月累积活动签到	
	firstPayInc(1047,"首充活动奖励"),	//首充活动奖励
	redPacketInc(1048,"领取红包奖励"),	//领取红包奖励
	dailyLoginInc(1049,"领取日常活动累计登录奖励"),	//领取日常活动累计登录奖励
	fightingCompetitionInc(1050,"领取日常活动战力比拼奖励"),	//领取日常活动战力比拼奖励
	expToTankExpInc(1051,"坦克个人经验转换为全局经验"),	//坦克个人经验转换为全局经验
	dailyOnlineRewardInc(1052,"领取日常活动在线时长奖励"),	//领取日常活动在线时长奖励
	dailyArmyTitleLevelInc(1053,"领取日常活动军衔冲级奖励"),	//领取日常活动军衔冲级奖励
	
	
	questDec(2001, "任务消耗"),
	lotteryGoldOnceDec(2002, "金币单抽消耗"),			//单抽
	lotteryGoldMutilDec(2003, "金币五连抽消耗"),			//多抽
	lotteryIronOnceDec(2004, "银币单抽消耗"),			//单抽
	lotteryIronMutilDec(2005, "银币五连抽消耗"),			//多抽
	corpsCreateDec(2006, "创建军团消耗"), 				//创建军团
	corpsShopRefreshDec(2007, "军团商城手动刷新消耗"), 	//军团商城手动刷新
	corpsDonateGoldDec(2008, "科技金钱捐献消耗"), 		//科技金钱捐献
	corpsDonateCdDec(2009, "捐献CD加速消耗"), 			//捐献CD加速
	materialComponseDec(2010, "合成的材料消耗"), 			//合成的材料
	tankReliveDec(2011, "坦克复活消耗"),			 		//坦克复活
	tankUpDec(2012, "坦克升级消耗"), 			//坦克升级
	tankBuyDec(2013, "坦克购买消耗"), 			//坦克购买
	shopBuyDec(2014, "商店购买消耗"), 			//商店购买
	shopRefreshDec(2015, "商店刷新消耗"), 		//商店刷新 
	goodsOpenBoxDec(2016, "开宝箱消耗"), 		//开宝箱
	goodsUseDec(2017, "物品使用消耗"), 			//物品使用
	corpsDonateGoodsDec(2018, "军团捐献消耗"), 	//军团捐献
	corpsShopBuyDec(2019, "军团商城购买消耗"), 		//军团购买
	materialDecomponseDec(2020, "分解的材料消耗"), 	//分解的材料
	renameDec(2021, "改名消耗"), 						//改名
	tankFireUseGoodsDec(2022, "坦克开火使用道具消耗"), 	//坦克开火使用道具
	tankUpEliteDec(2023, "坦克精英配件升级消耗"), 		//坦克精英配件升级
	memberComposeDec(2024, "成员合成消耗"), 		//成员合成
	memberLevelUpDec(2025, "成员升级消耗"), 		//成员升级
	goodsSellDec(2026, "物品出售消耗"), 			//物品出售
	goodsQuickBuyDec(2027, "战斗快速购买消耗"), 	//战斗快速购买
	medalComposeDec(2028, "勋章合成消耗"), 		//勋章合成
	medalExchangeDec(2029, "勋章穿戴消耗"), 		//勋章穿戴
	memberOpenHoleDec(2030, "成员开孔消耗"), 		//成员开孔
	blueRenewGift(2031, "蓝钻续费礼包"), 			//蓝钻续费礼包
	luckyWheelGetGift(2032, "幸运转盘钻石消耗"), 		//幸运转盘钻石消耗
	diamondTranslateToGoldDec(2033,"钻石对换金币"),	//钻石对换金币
	goldTranslateToIronDec(2034,"金币对换银币"),	//金币对换银币
	activityMonthLoginRetroactiveDec(2035,"月累积活动补签"),	//月累积活动补签
	allServerTellDec(2035,"全服聊天"),	//全服聊天
	localServerTellDec(2036,"本服聊天"),	//本服聊天
	VipShopRefreshDec(2037,"VIP商店刷新"),	//VIP商店刷新
	VipShopBuyDec(2038,"VIP商店购买"),	//VIP商店购买	
	expToTankExpDec(2039,"坦克个人经验转换为全局经验"),	//坦克个人经验转换为全局经验
	buyGoldTankDec(2040,"金币坦克购买"),	//金币坦克购买
	redMoney(2041,"红包获得"),	//红包获得
	rankMoney(2042,"排行榜领取"),	//排行榜领取
	buyPark(2043, "购买车位"), 	//购买车位
	IronStrengthen(2044, "银币强化配件"), 	//银币强化配件
	GoldStrengthen(2045,"金币强化配件"),//金币强化配件
	OneKeyStrengthen(2046,"一键强化配件"),//一键强化配件
	OpenGrooveDraw(2047,"抽奖购买槽"),//抽奖购买槽
	OpenGrooveBuy(2048,"直接购买槽"),//直接购买槽
	ClearFreezePart(2049,"清除改装CD"),//清除改装CD
	tankDevelop(2050, "坦克研发消耗"), //坦克研发消耗
	;
	
	private int type;
	private String info;
	
	OutputType(int type, String info){
		this.type = type;
		this.info = info;
	}
	
	public int type(){
		return this.type;
	}
	
	public String getInfo(){
		return this.info;
	}
}
