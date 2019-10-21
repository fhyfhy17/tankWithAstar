package com.ourpalm.tank.app.blueVip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.BlueDao;
import com.ourpalm.tank.domain.BlueExpireInfo;
import com.ourpalm.tank.domain.BlueInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.http.HttpThread;
import com.ourpalm.tank.message.ROLE_MSG.BLUE_EXPIRE_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.BLUE_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.BLUE_LEVEL_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleRewardItem;
import com.ourpalm.tank.message.ROLE_MSG.RoleRewardType;
import com.ourpalm.tank.message.ROLE_MSG.STC_BLUE_GET_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_BLUE_MSG;
import com.ourpalm.tank.template.BlueGiftTemplate;
import com.ourpalm.tank.template.BlueParamTemplate;
import com.ourpalm.tank.thread.ThreadId;
import com.ourpalm.tank.thread.ThreadUtil;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class BlueAppImpl implements BlueApp {
	private final static Logger logger = LogCore.runtime;
	private BlueDao blueDao;
	private Map<Integer, BlueGiftTemplate> blueGiftTemplateMap = new HashMap<>();

	private Map<Integer, List<int[]>> blueLevelMap = new HashMap<>();
	private Map<Integer, List<int[]>> blueGiftMap = new HashMap<>();
	/** 角色等级礼包 */
	private Map<Integer, BlueGiftTemplate> roleLevelTemplateMap = new HashMap<>();
	/** 蓝钻等级礼包 */
	private Map<Integer, BlueGiftTemplate> blueLevelTemplateMap = new HashMap<>();

	/** 蓝钻参数 */
	private BlueParamTemplate blueParamTemplate;

	private int renewTemplateId;
	// 1新手礼包 2 每日礼包 3豪华礼包 4 年费礼包 5等级礼包 6蓝钻续费礼包
	private final static int NEWBIE_INDEX = 1;
	private final static int EVERY_INDEX = 2;
	private final static int SUPER_INDEX = 3;
	private final static int YEAR_INDEX = 4;
	private final static int LEVEL_INDEX = 5;
	private final static int RENEW_INDEX = 6;

	private final static int NO = 0;// 无
	private final static int YES = 1;// 可以领取
	private final static int HAD = 2;// 已领取

	@Override
	public void start() {
		loadBlueGift();
		loadBlueParam();
	}

	@Override
	public void stop() {

	}

	private void loadBlueParam() {
		String fileName = XlsSheetType.BlueParam.getXlsFileName();
		String sheetName = XlsSheetType.BlueParam.getSheetName();
		List<BlueParamTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, BlueParamTemplate.class);
		blueParamTemplate = list.get(0);
	}

	private void loadBlueGift() {
		String fileName = XlsSheetType.BlueItem.getXlsFileName();
		String sheetName = XlsSheetType.BlueItem.getSheetName();
		try {
			this.blueGiftTemplateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, BlueGiftTemplate.class);
			Iterator<BlueGiftTemplate> it = this.blueGiftTemplateMap.values().iterator();
			while (it.hasNext()) {
				BlueGiftTemplate template = it.next();
				template.init();
				if (template.getType() == EVERY_INDEX) {
					blueLevelMap.put(template.getLevelType(), template.getItemList());
					blueLevelTemplateMap.put(template.getId(), template);
				}
				if (template.getType() == NEWBIE_INDEX || template.getType() == EVERY_INDEX || template.getType() == SUPER_INDEX || template.getType() == YEAR_INDEX
						|| template.getType() == RENEW_INDEX) {
					blueGiftMap.put(template.getType(), template.getItemList());
				}
				if (template.getType() == LEVEL_INDEX) {
					roleLevelTemplateMap.put(template.getId(), template);
				}
				if (template.getType() == RENEW_INDEX) {
					renewTemplateId = template.getId();
				}
			}
		} catch (Exception e) {
			logger.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	public BlueDao getBlueDao() {
		return blueDao;
	}

	public void setBlueDao(BlueDao blueDao) {
		this.blueDao = blueDao;
	}

	@Override
	public Result getGift(int roleId, int type, int templateId, STC_BLUE_GET_MSG.Builder builder) {
		BlueInfo blueInfo = blueDao.getBlueInfo(roleId);
		boolean isBlue = blueDao.isBlue(roleId);

		if (!isBlue) {
			pushBlue(roleId);
			return Result.newFailure("您不是蓝钻用户，不可领取");
		}
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		switch (type) {
		case 1:
			// 新手礼包
			if (blueInfo.getNewBieGift() == HAD) {
				pushBlue(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			List<int[]> itemInfos = blueGiftMap.get(NEWBIE_INDEX);
			blueInfo.setNewBieGift(HAD);
			blueDao.update(blueInfo);
			if (itemInfos != null) {
				for (int[] is : itemInfos) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ蓝钻领取新手礼包");
				}
			}

			break;
		case 2:
			// 每日礼包
			if (blueInfo.getEveryDayGift() == HAD && blueInfo.getEveryDayGiftLevel() == blueInfo.getBlueLevel()) {
				pushBlue(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			BlueGiftTemplate btEvery = blueGiftTemplateMap.get(templateId);
			if (btEvery == null) {
				pushBlue(roleId);
				return Result.newFailure("参数错误");
			}

			int blueLevel = blueInfo.getBlueLevel();
			if (btEvery.getLevelType() > blueLevel) {
				pushBlue(roleId);
				return Result.newFailure("未达到蓝钻级别");
			}
			blueInfo.setEveryDayGift(HAD);
			blueInfo.setEveryDayGiftLevel(blueInfo.getBlueLevel());
			blueDao.update(blueInfo);
			List<int[]> itemInfoList = blueLevelMap.get(btEvery.getLevelType());
			if (itemInfoList != null) {
				for (int[] is : itemInfoList) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ蓝钻领取每日礼包");
				}
			}

			break;
		case 3:
			// 豪华礼包
			if (blueInfo.getSuperGift() == HAD) {
				pushBlue(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			if (blueInfo.getSuperGift() == 0) {
				pushBlue(roleId);
				return Result.newFailure("您不是QQ豪华蓝钻，不可领取");
			}
			blueInfo.setSuperGift(HAD);
			blueDao.update(blueInfo);
			List<int[]> itemInfos2 = blueGiftMap.get(SUPER_INDEX);
			if (itemInfos2 != null) {
				for (int[] is : itemInfos2) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ蓝钻领取豪华礼包");
				}
			}

			break;
		case 4:
			// 年费礼包
			if (blueInfo.getYearGift() == HAD) {
				pushBlue(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			if (blueInfo.getYearGift() == NO) {
				pushBlue(roleId);
				return Result.newFailure("您不是QQ年费蓝钻，不可领取");
			}
			blueInfo.setYearGift(HAD);
			blueDao.update(blueInfo);
			List<int[]> itemInfos3 = blueGiftMap.get(YEAR_INDEX);
			if (itemInfos3 != null) {
				for (int[] is : itemInfos3) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ蓝钻领取豪华礼包");
				}
			}

			break;
		case 5:
			// 等级礼包
			if (blueInfo.getHasLevelGift().contains(templateId)) {
				pushBlue(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			int level = role.getLevel();
			BlueGiftTemplate bt = roleLevelTemplateMap.get(templateId);
			int wantGiftLevel = bt.getRoleLevel();
			if (level < wantGiftLevel) {
				pushBlue(roleId);
				return Result.newFailure("您的级别不足");
			}
			blueInfo.getHasLevelGift().add(templateId);
			blueDao.update(blueInfo);
			List<int[]> itemInfos4 = bt.getItemList();
			if (itemInfos4 != null) {
				for (int[] is : itemInfos4) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ蓝钻领取等级礼包");
				}
			}

			break;
		default:
			break;
		}
		// pushBlue(roleId);
		return Result.newSuccess();

	}

	@Override
	public void login(int roleId) {

	}

	@Override
	public void refreshBlue(int roleId, boolean isNextDay) {
		if (isNextDay) {
			BlueInfo blueInfo = blueDao.getBlueInfo(roleId);
			boolean flag = blueDao.isBlue(roleId);
			if (flag) {
				blueInfo.setEveryDayGift(YES);
				blueDao.update(blueInfo);
			}
		}
	}

	/**
	 * 推送蓝钻信息
	 */
	@Override
	public void pushBlue(int roleId) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		GameContext.getRankApp().jiashuju(connect);// 测试排行榜
		if (connect == null) {
			return;
		}
		STC_BLUE_MSG.Builder builder = STC_BLUE_MSG.newBuilder();
		BlueInfo blueInfo = blueDao.getBlueInfo(roleId);
		boolean isBlue = blueDao.isBlue(roleId);
		if (blueInfo == null) {
			builder.setIsBlue(0);
			connect.sendMsg(builder.build());
			return;
		}
		builder.setIsBlue(blueDao.isBlue(roleId) ? 1 : 0);

		BLUE_INFO_MSG.Builder builderNewBie = BLUE_INFO_MSG.newBuilder();
		builderNewBie.setType(NEWBIE_INDEX);

		if (!isBlue && blueInfo.getNewBieGift() == YES) {
			builderNewBie.setState(NO);
		} else {
			builderNewBie.setState(blueInfo.getNewBieGift());
		}
		List<int[]> itemInfos = blueGiftMap.get(NEWBIE_INDEX);
		for (int[] is : itemInfos) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderNewBie.addItems(builderItem1);
		}
		builder.addBlueInfo(builderNewBie);

		BLUE_INFO_MSG.Builder builderEvery = BLUE_INFO_MSG.newBuilder();
		builderEvery.setType(EVERY_INDEX);
		int blueState = blueInfo.getEveryDayGift();
		if (!isBlue && blueState == YES) {
			builderEvery.setState(0);
		} else {
			builderEvery.setState(blueState);
		}
		// 如果玩家蓝钻等级升了，那么重置领取奖励
		if (blueInfo.getEveryDayGift() == HAD && blueInfo.getBlueLevel() != blueInfo.getEveryDayGiftLevel()) {
			blueInfo.setEveryDayGift(YES);
			blueDao.update(blueInfo);
		}

		Iterator<BlueGiftTemplate> itBlue = blueLevelTemplateMap.values().iterator();
		while (itBlue.hasNext()) {
			BlueGiftTemplate template = itBlue.next();
			BLUE_LEVEL_INFO_MSG.Builder builder2 = BLUE_LEVEL_INFO_MSG.newBuilder();
			builder2.setId(template.getId());
			builder2.setRoleLevel(template.getLevelType());
			if (template.getLevelType() == blueInfo.getBlueLevel()) {
				if (blueInfo.getEveryDayGift() == HAD) {
					builder2.setState(HAD);
				} else {
					builder2.setState(YES);
				}
			} else {
				builder2.setState(NO);
			}
			if (!isBlue && builder2.getState() == YES) {
				builder2.setState(NO);
			}

			List<int[]> itemInfosBlue = template.getItemList();
			for (int[] is : itemInfosBlue) {
				RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
				builderItem1.setId(is[0]);
				builderItem1.setCount(is[1]);
				builderItem1.setType(RoleRewardType.GOODS);
				builder2.addItems(builderItem1);
			}
			builderEvery.addBlueLevelInfo(builder2);
		}
		builder.addBlueInfo(builderEvery);

		BLUE_INFO_MSG.Builder builderSuper = BLUE_INFO_MSG.newBuilder();
		builderSuper.setType(SUPER_INDEX);
		if (!isBlue && blueInfo.getSuperGift() == YES) {
			builderSuper.setState(NO);
		} else {
			builderSuper.setState(blueInfo.getSuperGift());
		}

		List<int[]> itemInfos3 = blueGiftMap.get(SUPER_INDEX);
		for (int[] is : itemInfos3) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderSuper.addItems(builderItem1);
		}
		builder.addBlueInfo(builderSuper);

		BLUE_INFO_MSG.Builder builderYear = BLUE_INFO_MSG.newBuilder();
		builderYear.setType(YEAR_INDEX);
		if (!isBlue && blueInfo.getYearGift() == YES) {
			builderYear.setState(NO);
		} else {
			builderYear.setState(blueInfo.getYearGift());
		}

		List<int[]> itemInfos4 = blueGiftMap.get(YEAR_INDEX);
		for (int[] is : itemInfos4) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderYear.addItems(builderItem1);
		}
		builder.addBlueInfo(builderYear);

		builder.setBlueLevel(blueInfo.getBlueLevel());

		// 等级 礼包
		BLUE_INFO_MSG.Builder builderLevel = BLUE_INFO_MSG.newBuilder();
		builderLevel.setType(LEVEL_INDEX);
		List<Integer> hasList = blueInfo.getHasLevelGift();

		Iterator<BlueGiftTemplate> it = roleLevelTemplateMap.values().iterator();
		while (it.hasNext()) {

			BlueGiftTemplate template = it.next();
			BLUE_LEVEL_INFO_MSG.Builder builder2 = BLUE_LEVEL_INFO_MSG.newBuilder();
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			builder2.setId(template.getId());
			if (hasList.contains(template.getId())) {
				builder2.setState(HAD);
			} else {
				if (role.getLevel() >= template.getRoleLevel()) {
					builder2.setState(YES);
				} else {
					builder2.setState(NO);
				}
			}
			if (!isBlue && builder2.getState() == YES) {
				builder2.setState(NO);
			}
			builder2.setRoleLevel(template.getRoleLevel());

			List<int[]> items = template.getItemList();
			for (int[] is : items) {
				RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
				builderItem1.setId(is[0]);
				builderItem1.setCount(is[1]);
				builderItem1.setType(RoleRewardType.GOODS);
				builder2.addItems(builderItem1);
			}
			builderLevel.addLevelInfo(builder2);
		}
		builder.addBlueInfo(builderLevel);

		BlueExpireInfo blueExpireInfo = blueDao.getBlueExpireInfo(roleId);
		if (blueExpireInfo != null) {
			BLUE_EXPIRE_INFO_MSG.Builder builder3 = BLUE_EXPIRE_INFO_MSG.newBuilder();
			builder3.setType(blueExpireInfo.getType());
			builder3.setDay(blueExpireInfo.getDay());
			builder3.setQqb(blueParamTemplate.getQb());

			List<int[]> renewItemList = blueGiftMap.get(RENEW_INDEX);
			for (int[] is : renewItemList) {
				RoleRewardItem.Builder builderRenewItem = RoleRewardItem.newBuilder();
				builderRenewItem.setId(is[0]);
				builderRenewItem.setCount(is[1]);
				builderRenewItem.setType(RoleRewardType.GOODS);
				builder3.addItems(builderRenewItem);
			}

			builder.setExpire(builder3);
		}
		connect.sendMsg(builder.build());
	}

	public RoleRewardItem.Builder builderItem(int id, int num, RoleRewardType type) {
		RoleRewardItem.Builder itemBuilder = RoleRewardItem.newBuilder();
		itemBuilder.setId(id);
		itemBuilder.setCount(num);
		itemBuilder.setType(type);
		return itemBuilder;
	}

	@Override
	public void query(String openId, String openKey) {
		String appId = GameContext.getTxAppId();
		String pf = GameContext.getServerPf();
		String sig = "";
		StringBuffer sb = new StringBuffer();
		sb.append("http://").append(GameContext.getTxloginCheckUrl()).append(GameContext.getTxBlueUrl()).append("?openId=").append(openId).append("&openKey=").append(openKey).append("&appId=")
				.append(appId).append("&pf=").append(pf).append("&sig=").append(sig);
		HttpThread httpThread = (HttpThread) ThreadUtil.getThread(ThreadId.HTTP);
		if (httpThread != null) {
			// HttpEvent httpEvent = new TX_BlueEvent();
			// httpEvent.setGet(true);
			// httpEvent.setParams("");
			// httpThread.add(httpEvent);
		}
	}

	public int getRenewTemplateId() {
		return renewTemplateId;
	}

	public void setRenewTemplateId(int renewTemplateId) {
		this.renewTemplateId = renewTemplateId;
	}

	@Override
	public BlueGiftTemplate getBlueGiftTemplate(int templateId) {
		return blueGiftTemplateMap.get(templateId);
	}

	@Override
	public BlueGiftTemplate getBlueRenewGiftTemplate() {
		return blueGiftTemplateMap.get(renewTemplateId);
	}

}
