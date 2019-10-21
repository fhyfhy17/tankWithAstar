package com.ourpalm.tank.app.yellowVip;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.YellowDao;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.YellowInfo;
import com.ourpalm.tank.message.ROLE_MSG.RoleRewardItem;
import com.ourpalm.tank.message.ROLE_MSG.RoleRewardType;
import com.ourpalm.tank.message.ROLE_MSG.STC_YELLOW_GET_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_YELLOW_MSG;
import com.ourpalm.tank.message.ROLE_MSG.YELLOW_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.YELLOW_LEVEL_INFO_MSG;
import com.ourpalm.tank.template.YellowGiftTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class YellowAppImpl implements YellowApp {
	private final static Logger logger = LogCore.runtime;
	private YellowDao yellowDao;
	private Map<Integer, YellowGiftTemplate> yellowGiftTemplateMap = new HashMap<>();

	private Map<Integer, List<int[]>> yellowLevelMap = new HashMap<>();
	private Map<Integer, List<int[]>> yellowGiftMap = new HashMap<>();
	/** 角色等级礼包 */
	private Map<Integer, YellowGiftTemplate> roleLevelTemplateMap = new HashMap<>();
	/** 黄钻等级礼包 */
	private Map<Integer, YellowGiftTemplate> yellowLevelTemplateMap = new HashMap<>();

	private int renewTemplateId;
	// 1新手礼包 2 每日礼包 3豪华礼包 4 年费礼包 5等级礼包
	private final static int NEWBIE_INDEX = 1;
	private final static int EVERY_INDEX = 2;
	private final static int SUPER_INDEX = 3;
	private final static int YEAR_INDEX = 4;
	private final static int LEVEL_INDEX = 5;

	@Override
	public void start() {
		loadYellowGift();
	}

	@Override
	public void stop() {

	}

	private void loadYellowGift() {
		String fileName = XlsSheetType.YellowItem.getXlsFileName();
		String sheetName = XlsSheetType.YellowItem.getSheetName();
		try {
			this.yellowGiftTemplateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, YellowGiftTemplate.class);
			Iterator<YellowGiftTemplate> it = this.yellowGiftTemplateMap.values().iterator();
			while (it.hasNext()) {
				YellowGiftTemplate template = it.next();
				template.init();
				if (template.getType() == EVERY_INDEX) {
					yellowLevelMap.put(template.getLevelType(), template.getItemList());
					yellowLevelTemplateMap.put(template.getId(), template);
				}
				if (template.getType() == NEWBIE_INDEX || template.getType() == EVERY_INDEX || template.getType() == SUPER_INDEX || template.getType() == YEAR_INDEX) {
					yellowGiftMap.put(template.getType(), template.getItemList());
				}
				if (template.getType() == LEVEL_INDEX) {
					roleLevelTemplateMap.put(template.getId(), template);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	public YellowDao getYellowDao() {
		return yellowDao;
	}

	public void setYellowDao(YellowDao yellowDao) {
		this.yellowDao = yellowDao;
	}

	@Override
	public Result getGift(int roleId, int type, int templateId, STC_YELLOW_GET_MSG.Builder builder) {
		YellowInfo yellowInfo = yellowDao.getYellowInfo(roleId);
		boolean isYellow = yellowDao.isYellow(roleId);

		if (!isYellow) {
			pushYellow(roleId);
			return Result.newFailure("您不是黄钻用户，不可领取");
		}
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		switch (type) {
		case 1:
			// 新手礼包
			if (yellowInfo.getNewBieGift() == 2) {
				pushYellow(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			List<int[]> itemInfos = yellowGiftMap.get(NEWBIE_INDEX);
			yellowInfo.setNewBieGift(2);
			yellowDao.update(yellowInfo);
			if (itemInfos != null) {
				for (int[] is : itemInfos) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ黄钻领取新手礼包");
				}
			}

			break;
		case 2:
			// 每日礼包
			if (yellowInfo.getEveryDayGift() == 2 && yellowInfo.getEveryDayGiftLevel() == yellowInfo.getYellowLevel()) {
				pushYellow(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			YellowGiftTemplate btEvery = yellowGiftTemplateMap.get(templateId);
			if (btEvery == null) {
				pushYellow(roleId);
				return Result.newFailure("参数错误");
			}

			int yellowLevel = yellowInfo.getYellowLevel();
			if (btEvery.getLevelType() > yellowLevel) {
				pushYellow(roleId);
				return Result.newFailure("未达到黄钻级别");
			}
			yellowInfo.setEveryDayGift(2);
			yellowInfo.setEveryDayGiftLevel(yellowInfo.getYellowLevel());
			yellowDao.update(yellowInfo);
			List<int[]> itemInfoList = yellowLevelMap.get(btEvery.getLevelType());
			if (itemInfoList != null) {
				for (int[] is : itemInfoList) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ黄钻领取每日礼包");
				}
			}

			break;
		case 3:
			// 豪华礼包
			if (yellowInfo.getSuperGift() == 2) {
				pushYellow(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			if (yellowInfo.getSuperGift() == 0) {
				pushYellow(roleId);
				return Result.newFailure("您不是QQ豪华黄钻，不可领取");
			}
			yellowInfo.setSuperGift(2);
			yellowDao.update(yellowInfo);
			List<int[]> itemInfos2 = yellowGiftMap.get(SUPER_INDEX);
			if (itemInfos2 != null) {
				for (int[] is : itemInfos2) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ黄钻领取豪华礼包");
				}
			}

			break;
		case 4:
			// 年费礼包
			if (yellowInfo.getYearGift() == 2) {
				pushYellow(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			if (yellowInfo.getYearGift() == 0) {
				pushYellow(roleId);
				return Result.newFailure("您不是QQ年费黄钻，不可领取");
			}
			yellowInfo.setYearGift(2);
			yellowDao.update(yellowInfo);
			List<int[]> itemInfos3 = yellowGiftMap.get(YEAR_INDEX);
			if (itemInfos3 != null) {
				for (int[] is : itemInfos3) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ黄钻领取豪华礼包");
				}
			}

			break;
		case 5:
			// 等级礼包
			if (yellowInfo.getHasLevelGift().contains(templateId)) {
				pushYellow(roleId);
				return Result.newFailure("您已领取过该物品");
			}
			int level = role.getLevel();
			YellowGiftTemplate bt = roleLevelTemplateMap.get(templateId);
			int wantGiftLevel = bt.getRoleLevel();
			if (level < wantGiftLevel) {
				pushYellow(roleId);
				return Result.newFailure("您的级别不足");
			}
			yellowInfo.getHasLevelGift().add(templateId);
			yellowDao.update(yellowInfo);
			List<int[]> itemInfos4 = bt.getItemList();
			if (itemInfos4 != null) {
				for (int[] is : itemInfos4) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ黄钻领取等级礼包");
				}
			}

			break;
		default:
			break;
		}
		return Result.newSuccess();

	}

	@Override
	public void login(int roleId) {

	}

	@Override
	public void refreshYellow(int roleId, boolean isNextDay) {
		if (isNextDay) {
			YellowInfo yellowInfo = yellowDao.getYellowInfo(roleId);
			boolean flag = yellowDao.isYellow(roleId);
			if (flag) {
				yellowInfo.setEveryDayGift(1);
				yellowDao.update(yellowInfo);
			}
		}
	}

	/**
	 * 推送黄钻信息
	 */
	@Override
	public void pushYellow(int roleId) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect == null) {
			return;
		}
		STC_YELLOW_MSG.Builder builder = STC_YELLOW_MSG.newBuilder();
		YellowInfo yellowInfo = yellowDao.getYellowInfo(roleId);
		boolean isYellow = yellowDao.isYellow(roleId);
		if (yellowInfo == null) {
			builder.setIsYellow(0);
			connect.sendMsg(builder.build());
			return;
		}
		builder.setIsYellow(yellowDao.isYellow(roleId) ? 1 : 0);

		YELLOW_INFO_MSG.Builder builderNewBie = YELLOW_INFO_MSG.newBuilder();
		builderNewBie.setType(NEWBIE_INDEX);

		if (!isYellow && yellowInfo.getNewBieGift() == 1) {
			builderNewBie.setState(0);
		} else {
			builderNewBie.setState(yellowInfo.getNewBieGift());
		}
		List<int[]> itemInfos = yellowGiftMap.get(NEWBIE_INDEX);
		for (int[] is : itemInfos) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderNewBie.addItems(builderItem1);
		}
		builder.addYellowInfo(builderNewBie);

		YELLOW_INFO_MSG.Builder builderEvery = YELLOW_INFO_MSG.newBuilder();
		builderEvery.setType(EVERY_INDEX);
		int yellowState = yellowInfo.getEveryDayGift();
		if (!isYellow && yellowState == 1) {
			builderEvery.setState(0);
		} else {
			builderEvery.setState(yellowState);
		}
		// 如果玩家黄钻等级升了，那么重置领取奖励
		if (yellowInfo.getEveryDayGift() == 2 && yellowInfo.getYellowLevel() != yellowInfo.getEveryDayGiftLevel()) {
			yellowInfo.setEveryDayGift(1);
			yellowDao.update(yellowInfo);
		}

		Iterator<YellowGiftTemplate> itYellow = yellowLevelTemplateMap.values().iterator();
		while (itYellow.hasNext()) {
			YellowGiftTemplate template = itYellow.next();
			YELLOW_LEVEL_INFO_MSG.Builder builder2 = YELLOW_LEVEL_INFO_MSG.newBuilder();
			builder2.setId(template.getId());
			builder2.setRoleLevel(template.getLevelType());
			if (template.getLevelType() == yellowInfo.getYellowLevel()) {
				if (yellowInfo.getEveryDayGift() == 2) {
					builder2.setState(2);
				} else {
					builder2.setState(1);
				}
			} else {
				builder2.setState(0);
			}
			if (!isYellow && builder2.getState() == 1) {
				builder2.setState(0);
			}

			List<int[]> itemInfosYellow = template.getItemList();
			for (int[] is : itemInfosYellow) {
				RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
				builderItem1.setId(is[0]);
				builderItem1.setCount(is[1]);
				builderItem1.setType(RoleRewardType.GOODS);
				builder2.addItems(builderItem1);
			}
			builderEvery.addYellowLevelInfo(builder2);
		}
		builder.addYellowInfo(builderEvery);

		YELLOW_INFO_MSG.Builder builderSuper = YELLOW_INFO_MSG.newBuilder();
		builderSuper.setType(SUPER_INDEX);
		if (!isYellow && yellowInfo.getSuperGift() == 1) {
			builderSuper.setState(0);
		} else {
			builderSuper.setState(yellowInfo.getSuperGift());
		}

		List<int[]> itemInfos3 = yellowGiftMap.get(SUPER_INDEX);
		for (int[] is : itemInfos3) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderSuper.addItems(builderItem1);
		}
		builder.addYellowInfo(builderSuper);

		YELLOW_INFO_MSG.Builder builderYear = YELLOW_INFO_MSG.newBuilder();
		builderYear.setType(YEAR_INDEX);
		if (!isYellow && yellowInfo.getYearGift() == 1) {
			builderYear.setState(0);
		} else {
			builderYear.setState(yellowInfo.getYearGift());
		}

		List<int[]> itemInfos4 = yellowGiftMap.get(YEAR_INDEX);
		for (int[] is : itemInfos4) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderYear.addItems(builderItem1);
		}
		builder.addYellowInfo(builderYear);

		builder.setYellowLevel(yellowInfo.getYellowLevel());

		// 等级 礼包
		YELLOW_INFO_MSG.Builder builderLevel = YELLOW_INFO_MSG.newBuilder();
		builderLevel.setType(LEVEL_INDEX);
		List<Integer> hasList = yellowInfo.getHasLevelGift();

		Iterator<YellowGiftTemplate> it = roleLevelTemplateMap.values().iterator();
		while (it.hasNext()) {

			YellowGiftTemplate template = it.next();
			YELLOW_LEVEL_INFO_MSG.Builder builder2 = YELLOW_LEVEL_INFO_MSG.newBuilder();
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			builder2.setId(template.getId());
			if (hasList.contains(template.getId())) {
				builder2.setState(2);
			} else {
				if (role.getLevel() >= template.getRoleLevel()) {
					builder2.setState(1);
				} else {
					builder2.setState(0);
				}
			}
			if (!isYellow && builder2.getState() == 1) {
				builder2.setState(0);
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
		builder.addYellowInfo(builderLevel);

		connect.sendMsg(builder.build());

	}

	public RoleRewardItem.Builder builderItem(int id, int num, RoleRewardType type) {
		RoleRewardItem.Builder itemBuilder = RoleRewardItem.newBuilder();
		itemBuilder.setId(id);
		itemBuilder.setCount(num);
		itemBuilder.setType(type);
		return itemBuilder;
	}

	public int getRenewTemplateId() {
		return renewTemplateId;
	}

	public void setRenewTemplateId(int renewTemplateId) {
		this.renewTemplateId = renewTemplateId;
	}

}
