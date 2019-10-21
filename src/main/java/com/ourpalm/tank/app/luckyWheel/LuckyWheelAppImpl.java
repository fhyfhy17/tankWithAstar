package com.ourpalm.tank.app.luckyWheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.LuckyWheelDao;
import com.ourpalm.tank.domain.BlueInfo;
import com.ourpalm.tank.domain.LuckyWheelInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_LuckyWheel_GET_MSG.Builder;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_LuckyWheel_Info_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.LuckyWheelDiamondTemplate;
import com.ourpalm.tank.template.LuckyWheelTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.LuckyWheelPeshe;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

public class LuckyWheelAppImpl implements LuckyWheelApp {
	private final static Logger logger = LogCore.runtime;
	private LuckyWheelDao luckyWheelDao;
	private Map<Integer, LuckyWheelTemplate> map = new HashMap<>();
	private Map<Integer, LuckyWheelDiamondTemplate> diamondMap = new HashMap<>();

	private List<LuckyWheelPeshe> freePeshe = new ArrayList<>();
	private List<LuckyWheelPeshe> diamondPeshe = new ArrayList<>();
	public static int FREE_COUNT;
	public static int FREEZE_TIME;// 分钟

	private static final String NOMAL = "0";
	private static final String DIAMOND = "1";

	@Override
	public void start() {
		load();
		loadDiamond();
		FREE_COUNT = SysConfig.get(3);
		FREEZE_TIME = SysConfig.get(4);
	}

	@Override
	public void stop() {

	}

	private void load() {
		try {
			String fileName = XlsSheetType.LuckyWheel.getXlsFileName();
			String sheetName = XlsSheetType.LuckyWheel.getSheetName();
			map = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, LuckyWheelTemplate.class);
			Iterator<LuckyWheelTemplate> it = map.values().iterator();
			while (it.hasNext()) {
				LuckyWheelTemplate template = it.next();
				template.init();
				if (template.getRate() > 0) {
					freePeshe.add(buildLuckyWhellPeshe(template, NOMAL));
				}
				if (template.getDiamondRate() > 0) {
					diamondPeshe.add(buildLuckyWhellPeshe(template, DIAMOND));
				}
			}
		} catch (Exception e) {
			logger.error("加载 LuckyWheel 配表错误 ");
		}
	}

	private void loadDiamond() {
		try {
			String fileName = XlsSheetType.LuckyWheelDiamond.getXlsFileName();
			String sheetName = XlsSheetType.LuckyWheelDiamond.getSheetName();
			diamondMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, LuckyWheelDiamondTemplate.class);
		} catch (Exception e) {
			logger.error("加载 LuckyWheelDiamond 配表错误 ");
		}
	}

	@Override
	public Result getGift(int roleId, String type, Builder builder) {
		if (!NOMAL.equals(type) && !DIAMOND.equals(type)) {
			return Result.newFailure("参数错误");
		}
		LuckyWheelInfo info = luckyWheelDao.getLuckyWheelInfo(roleId);
		// RewardItem.Builder rewardItemBuilder = RewardItem.newBuilder();
		if (NOMAL.equals(type)) {
			if (info.getCount() >= FREE_COUNT) {
				return Result.newFailure("没有剩余次数");
			}
			if (System.currentTimeMillis() - info.getLastFreeTime() < FREEZE_TIME * DateUtil.MIN) {
				return Result.newFailure("冷却时间未到");
			}
			info.setCount(info.getCount() + 1);
			info.setLastFreeTime(System.currentTimeMillis());
			luckyWheelDao.update(info);
			LuckyWheelPeshe luckyWheelPeshe = RandomUtil.getPeshe(freePeshe);
			if (luckyWheelPeshe != null) {
				// rewardItemBuilder.setType(RewardType.GOODS);
				// rewardItemBuilder.setId(luckyWheelPeshe.getGoodsId());
				// rewardItemBuilder.setCount(luckyWheelPeshe.getNum());
				builder.setIndex(luckyWheelPeshe.getIndex());
				// builder.addItems(rewardItemBuilder);
				GameContext.getGoodsApp().addGoods(roleId, luckyWheelPeshe.getGoodsId(), luckyWheelPeshe.getNum(), "幸运转盘免费抽奖");
			} else {
				logger.error("幸运转盘  免费 随机 luckyWheelPeshe为空");
			}

		}
		// 钻石转盘
		if (DIAMOND.equals(type)) {
			int diamondCount = info.getDiamondCount();
			LuckyWheelDiamondTemplate luckyWheelDiamondTemplate = diamondMap.get(diamondCount + 1);
			if (luckyWheelDiamondTemplate == null) {
				logger.error("钻石幸运转盘  LuckyWheelDiamond 领取时  模板ID找不到 玩家 roleId ={}  diamondCount={}  ", roleId, diamondCount);
				return Result.newFailure("参数错误,登录重试可能会解决您的问题");
			}

			if (!GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.diamonds, Operation.decrease, luckyWheelDiamondTemplate.getDiamond()), OutputType.luckyWheelGetGift)) {
				return Result.newFailure(Tips.NEDD_DIAMONDS);
			}
			if (diamondCount >= 10) {
				info.setDiamondCount(10);
			} else {
				info.setDiamondCount(info.getDiamondCount() + 1);
			}
			luckyWheelDao.update(info);
			LuckyWheelPeshe luckyWheelPeshe = RandomUtil.getPeshe(diamondPeshe);
			if (luckyWheelPeshe != null) {
				// rewardItemBuilder.setType(RewardType.GOODS);
				// rewardItemBuilder.setId(luckyWheelPeshe.getGoodsId());
				// rewardItemBuilder.setCount(luckyWheelPeshe.getNum());
				builder.setIndex(luckyWheelPeshe.getIndex());
				// builder.addItems(rewardItemBuilder);
				GameContext.getGoodsApp().addGoods(roleId, luckyWheelPeshe.getGoodsId(), luckyWheelPeshe.getNum(), "幸运转盘钻石抽奖");
			} else {
				logger.error("幸运转盘  钻石 随机 luckyWheelPeshe为空");
			}

		}
		return Result.newSuccess();
	}

	private LuckyWheelPeshe buildLuckyWhellPeshe(LuckyWheelTemplate template, String type) {
		if (type.equals(DIAMOND)) {
			LuckyWheelPeshe peshe = new LuckyWheelPeshe();
			peshe.setGoodsId(template.getItemi()[0]);
			peshe.setNum(template.getItemi()[1]);
			peshe.setGon(template.getDiamondRate());
			peshe.setIndex(template.getId());
			return peshe;
		} else {
			LuckyWheelPeshe peshe = new LuckyWheelPeshe();
			peshe.setGoodsId(template.getItemi()[0]);
			peshe.setNum(template.getItemi()[1]);
			peshe.setGon(template.getRate());
			peshe.setIndex(template.getId());
			return peshe;
		}
	}

	@Override
	public void refresh(int roleId, boolean isNextDay) {
		if (isNextDay) {
			LuckyWheelInfo luckyWheelInfo = luckyWheelDao.getLuckyWheelInfo(roleId);
			luckyWheelInfo.setLastFreeTime(0);
			luckyWheelInfo.setCount(0);
			luckyWheelInfo.setDiamondCount(0);
			luckyWheelDao.update(luckyWheelInfo);
		}
	}

	@Override
	public LuckyWheelDao getLuckyWheelDao() {
		return luckyWheelDao;
	}

	public void setLuckyWheelDao(LuckyWheelDao luckyWheelDao) {
		this.luckyWheelDao = luckyWheelDao;
	}

	@Override
	public void push(int roleId) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		LuckyWheelInfo luckyWheelInfo = luckyWheelDao.getLuckyWheelInfo(roleId);
		STC_LuckyWheel_Info_MSG.Builder builder = STC_LuckyWheel_Info_MSG.newBuilder();
		builder.setFreeCount(FREE_COUNT - luckyWheelInfo.getCount());
		int diamondCount = luckyWheelInfo.getDiamondCount();
		LuckyWheelDiamondTemplate luckyWheelDiamondTemplate = diamondMap.get(diamondCount + 1);
		if (luckyWheelDiamondTemplate == null) {
			logger.error("钻石幸运转盘  LuckyWheelDiamond 模板ID找不到 玩家 roleId ={}  diamondCount={}  ", roleId, diamondCount);
		}
		builder.setNeedDiamond(luckyWheelDiamondTemplate.getDiamond());
		long now = System.currentTimeMillis();
		long lastFreeTime = luckyWheelInfo.getLastFreeTime();
		long freezeTime = FREEZE_TIME * DateUtil.MIN;
		if (now - lastFreeTime < freezeTime) {
			builder.setRestFreeTime((int) ((freezeTime - (now - lastFreeTime)) / DateUtil.SECOND));
		} else {
			builder.setRestFreeTime(0);
		}
		Iterator<LuckyWheelTemplate> it = map.values().iterator();
		while (it.hasNext()) {
			LuckyWheelTemplate template = it.next();
			RewardItem.Builder reBuilder = RewardItem.newBuilder();
			reBuilder.setType(RewardType.GOODS);
			reBuilder.setId(template.getItemi()[0]);
			reBuilder.setCount(template.getItemi()[1]);
			builder.addItems(reBuilder);
		}

		connect.sendMsg(builder.build());
	}

	@Override
	public void login(int roleId) {
		// TODO Auto-generated method stub

	}

}
