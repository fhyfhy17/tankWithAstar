package com.ourpalm.tank.app.qqZone;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.QQZoneDao;
import com.ourpalm.tank.domain.QQZoneInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG.BLUE_LEVEL_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.QQHall_INFO_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleRewardItem;
import com.ourpalm.tank.message.ROLE_MSG.RoleRewardType;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQHall_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQZone_GET_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQZone_MSG;
import com.ourpalm.tank.template.QQZoneGiftTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

public class QQZoneAppImpl implements QQZoneApp {
	private final static Logger logger = LogCore.runtime;
	private QQZoneDao qqZoneDao;
	private Map<Integer, QQZoneGiftTemplate> qqZoneGiftTemplateMap = new HashMap<>();
	/** 空间礼包 <类型，奖励物品> */
	private Map<Integer, List<int[]>> qqZoneGiftMap = new HashMap<>();
	/** 角色等级礼包 */
	private Map<Integer, QQZoneGiftTemplate> roleLevelTemplateMap = new HashMap<>();
	// 1新手礼包 2 每日礼包 3等级礼包
	private final static int NEWBIE_INDEX = 1;
	private final static int EVERY_INDEX = 2;
	private final static int LEVEL_INDEX = 3;

	@Override
	public void start() {
		loadQQHallGift();
	}

	@Override
	public void stop() {

	}

	private void loadQQHallGift() {
		String fileName = XlsSheetType.QQZoneItem.getXlsFileName();
		String sheetName = XlsSheetType.QQZoneItem.getSheetName();
		try {
			this.qqZoneGiftTemplateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, QQZoneGiftTemplate.class);
			Iterator<QQZoneGiftTemplate> it = this.qqZoneGiftTemplateMap.values().iterator();
			while (it.hasNext()) {
				QQZoneGiftTemplate template = it.next();
				template.init();
				if (template.getType() == NEWBIE_INDEX || template.getType() == EVERY_INDEX) {
					qqZoneGiftMap.put(template.getType(), template.getItemList());
				}
				if (template.getType() == LEVEL_INDEX) {
					roleLevelTemplateMap.put(template.getId(), template);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	public void setQqZoneDao(QQZoneDao qqZoneDao) {
		this.qqZoneDao = qqZoneDao;
	}

	@Override
	public Result getGift(int roleId, int type, int templateId, STC_QQZone_GET_MSG.Builder builder) {
		QQZoneInfo qqZoneInfo = qqZoneDao.getQQZoneInfo(roleId);
		logger.info("QQZone领取 ： roleId = " + roleId + " type=" + type + "   templateId = " + templateId);
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (qqZoneInfo.getIsQQZone() == 0) {
			return Result.newFailure("您没有资格领取该物品");
		}
		switch (type) {
		case 1:
			// 新手礼包
			if (qqZoneInfo.getNewBieGift() == 2) {
				return Result.newFailure("您已领取过该物品");
			}
			qqZoneInfo.setNewBieGift(2);
			qqZoneDao.update(qqZoneInfo);
			List<int[]> itemInfos = qqZoneGiftMap.get(NEWBIE_INDEX);
			if (itemInfos != null) {
				for (int[] is : itemInfos) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ空间领取新手礼包");
				}
			}

			break;
		case 2:
			// 每日礼包
			if (qqZoneInfo.getEveryDayGift() == 2) {
				return Result.newFailure("您已领取过该物品");
			}

			qqZoneInfo.setEveryDayGift(2);
			qqZoneDao.update(qqZoneInfo);
			List<int[]> itemInfoList = qqZoneGiftMap.get(EVERY_INDEX);
			if (itemInfoList != null) {
				for (int[] is : itemInfoList) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ空间领取每日礼包");
				}
			}

			break;
		case 3:
			// 等级礼包
			if (qqZoneInfo.getHasLevelGift().contains(templateId)) {
				return Result.newFailure("您已领取过该物品");
			}
			int level = role.getLevel();
			QQZoneGiftTemplate qt = roleLevelTemplateMap.get(templateId);
			int wantGiftLevel = qt.getRoleLevel();
			if (level < wantGiftLevel) {
				return Result.newFailure("您的级别不足");
			}
			qqZoneInfo.getHasLevelGift().add(templateId);
			qqZoneDao.update(qqZoneInfo);
			List<int[]> itemInfos4 = qt.getItemList();
			if (itemInfos4 != null) {
				for (int[] is : itemInfos4) {
					RoleRewardItem.Builder itemBuilder = builderItem(is[0], is[1], RoleRewardType.GOODS);
					builder.addItems(itemBuilder);
					GameContext.getGoodsApp().addGoods(roleId, is[0], is[1], "QQ空间领取等级礼包");
				}
			}

			break;
		default:
			break;
		}
		push(roleId);

		return Result.newSuccess();

	}

	public RoleRewardItem.Builder builderItem(int id, int num, RoleRewardType type) {
		RoleRewardItem.Builder itemBuilder = RoleRewardItem.newBuilder();
		itemBuilder.setId(id);
		itemBuilder.setCount(num);
		itemBuilder.setType(type);
		return itemBuilder;
	}

	@Override
	public void refresh(int roleId) {
		QQZoneInfo qqZoneInfo = qqZoneDao.getQQZoneInfo(roleId);
		qqZoneInfo.setEveryDayGift(1);
		qqZoneDao.update(qqZoneInfo);
	}

	@Override
	public void login(int roleId, boolean first) {
		if (first) {
			refresh(roleId);
		}
	}

	/**
	 * 推送QQ空间信息
	 */
	@Override
	public void push(int roleId) {
		STC_QQZone_MSG.Builder builder = STC_QQZone_MSG.newBuilder();
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect == null) {
			return;
		}

		QQZoneInfo qqZoneInfo = qqZoneDao.getQQZoneInfo(roleId);
		qqZoneInfo.setIsQQZone(1);
		qqZoneDao.update(qqZoneInfo);
		QQHall_INFO_MSG.Builder builderNewBie = QQHall_INFO_MSG.newBuilder();
		builderNewBie.setType(NEWBIE_INDEX);
		builderNewBie.setState(qqZoneInfo.getNewBieGift());
		List<int[]> itemInfos = qqZoneGiftMap.get(NEWBIE_INDEX);
		for (int[] is : itemInfos) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderNewBie.addItems(builderItem1);
		}
		builder.addInfos(builderNewBie);

		QQHall_INFO_MSG.Builder builderEvery = QQHall_INFO_MSG.newBuilder();
		builderEvery.setType(EVERY_INDEX);
		builderEvery.setState(qqZoneInfo.getEveryDayGift());
		List<int[]> itemInfos2 = qqZoneGiftMap.get(EVERY_INDEX);
		for (int[] is : itemInfos2) {
			RoleRewardItem.Builder builderItem1 = RoleRewardItem.newBuilder();
			builderItem1.setId(is[0]);
			builderItem1.setCount(is[1]);
			builderItem1.setType(RoleRewardType.GOODS);
			builderEvery.addItems(builderItem1);
		}
		builder.addInfos(builderEvery);

		List<Integer> hasList = qqZoneInfo.getHasLevelGift();
		QQHall_INFO_MSG.Builder builderLevel = QQHall_INFO_MSG.newBuilder();
		builderLevel.setType(LEVEL_INDEX);
		Iterator<QQZoneGiftTemplate> it = roleLevelTemplateMap.values().iterator();
		while (it.hasNext()) {

			QQZoneGiftTemplate template = it.next();
			BLUE_LEVEL_INFO_MSG.Builder builder2 = BLUE_LEVEL_INFO_MSG.newBuilder();
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
		builder.addInfos(builderLevel);

		connect.sendMsg(builder.build());

	}

	@Override
	public QQZoneDao getQQZoneDao() {
		return qqZoneDao;
	}

}
