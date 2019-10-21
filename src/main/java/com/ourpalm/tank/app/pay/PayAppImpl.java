package com.ourpalm.tank.app.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.constant.PayStateEnum;
import com.ourpalm.tank.dao.UserAttrDao;
import com.ourpalm.tank.domain.PFUserInfo;
import com.ourpalm.tank.domain.PayOrderInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.message.ROLE_MSG.GoodsItem;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.STC_GetPfinfoStr_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_PAY_SUCCESS_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_REWARDS_INFO_MSG;
import com.ourpalm.tank.template.BlueGiftTemplate;
import com.ourpalm.tank.template.PayTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.PayType;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.MapUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.PayExtendParam;

public class PayAppImpl implements PayApp {
	private Logger logger = LogCore.runtime;
	private Map<Integer, PayTemplate> payMap = new HashMap<>();
	private UserAttrDao userAttrDao;

	@Override
	public void start() {
		this.loadPayTemplate();
	}

	@Override
	public PayTemplate getPayTemplate(int id) {
		return null;
	}

	@Override
	public RolePay getRolePay(int roleId) {
		return userAttrDao.getRolePay(roleId);
	}

	@Override
	public PayStateEnum blueRenew(PayOrderInfo order) {
		BlueGiftTemplate template = GameContext.getBlueApp().getBlueRenewGiftTemplate();
		if (template == null) {
			logger.error("蓝钻续费礼包 。蓝钻礼包ID没找到 ");
			return PayStateEnum.ITEM_NON;
		}
		List<int[]> itemInfo = template.getItemList();
		if (itemInfo != null) {
			// 发送物品
			Map<Integer, Integer> goodsMap = MapUtil.arrayListToMap(itemInfo);
			GameContext.getGoodsApp().addGoods(order.getRoleId(), goodsMap, OutputType.blueRenewGift.getInfo());
		}

		// 推送最新蓝钻信息
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(order.getRoleId());
		if (connect != null) {
			STC_GetPfinfoStr_MSG.Builder builder = STC_GetPfinfoStr_MSG.newBuilder();
			String pfInfoStr = "";
			PFUserInfo pfInfo = GameContext.getUserApp().getRefreshedPf(order.getRoleId());
			if (pfInfo != null) {
				pfInfoStr = JSONObject.toJSONString(pfInfo);
			}
			builder.setInfo(pfInfoStr);
			connect.sendMsg(builder.build());
		}

		return PayStateEnum.SUCCESS;
	}

	@Override
	public PayStateEnum pay(PayOrderInfo order) {
		final int propId = order.getItemId();
		final int roleId = order.getRoleId();
		final int itemNum = order.getItemNum();

		// 保存购买记录
		userAttrDao.saveOrder(order);

		PayTemplate template = this.payMap.get(propId);
		if (template == null) {
			LogCore.pay.error("商品不存在 : {}", JSON.toJSONString(order));
			return PayStateEnum.ITEM_NON;
		}
		final int templateRmb = template.getRmb();
		final int actualPrice = order.getActualPrice() / 10;
		if (actualPrice < templateRmb) {
			LogCore.pay.error("支付实际金额小于配置,充值失败... {}", JSON.toJSONString(order));
			return PayStateEnum.MONEY_LESS;
		}
		// 是否为首次充值
		RolePay rolePay = getRolePay(roleId);

		// 添加钻石 如果是首充，调首充的值 ，然后其它按购买数量算
		int diamond = template.getDiamond(rolePay.hadFirst(propId));
		if (actualPrice > templateRmb) {
			diamond += (itemNum - 1) * templateRmb * 10;
		}
		// 添加物品
		Map<Integer, Integer> goodsMap = template.getGoodsMap();
		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.diamonds, Operation.add, diamond), OutputType.payRewardInc);
		GameContext.getGoodsApp().addGoods(roleId, goodsMap, OutputType.payRewardInc.getInfo());

		if (logger.isDebugEnabled()) {
			logger.debug("处理订单:{} 商品ID:{} roleId:{} ....完毕", order.getToken(), propId, roleId);
		}

		rolePay.putProId(propId);
		rolePay.setRmb(rolePay.getRmb() + actualPrice);
		userAttrDao.saveRolePay(rolePay);

		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if (connect != null) {
			STC_REWARDS_INFO_MSG.Builder builder = STC_REWARDS_INFO_MSG.newBuilder();
			builder.setDiamonds(diamond);
			for (Entry<Integer, Integer> entry : goodsMap.entrySet()) {
				GoodsItem item = GoodsItem.newBuilder().setId(entry.getKey()).setNum(entry.getValue()).build();
				builder.addGoodsList(item);
			}
			connect.sendMsg(builder.build());
			connect.sendMsg(STC_PAY_SUCCESS_MSG.newBuilder().setOrderId(order.getToken()).setPropName(template.getName()).setCost(actualPrice).setCurrencyNum(diamond).build());
		}

		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account != null) {
			String tank1Area = account.getTank1Area();
			String tank1AccountName = account.getTank1AccountName();
			if (!Util.isEmpty(tank1Area) && !Util.isEmpty(tank1AccountName)) {
				LogCore.tank1Pay.info("tank2角色id：{}, tank2角色名：{}, tank1分区：{}, tank1角色名：{}, 充值RMB：{}", account.getRoleId(), account.getRoleName(), tank1Area, tank1AccountName, actualPrice);
			}
		}

		// 触发vip
		GameContext.getVipApp().rechargeRmb(roleId, rolePay.getRmb());
		// 触发活动充值
		GameContext.getActivityApp().recharge(roleId, PayType.valueOf(template.getType()), actualPrice);

		return PayStateEnum.SUCCESS;
	}

	@Override
	public List<PayTemplate> getAllPayTemplate() {
		List<PayTemplate> list = new ArrayList<>();
		for (PayTemplate template : payMap.values()) {
			if (template.getType() == PayType.shop.type) {
				list.add(template);
			}
		}
		return list;
	}

	/** 每日秒杀物品ID */
	@Override
	public PayTemplate getDayGoodsBox() {
		for (PayTemplate template : payMap.values()) {
			if (template.getType() == PayType.day.type) {
				return template;
			}
		}
		return null;
	}

	private void loadPayTemplate() {
		String sourceFile = XlsSheetType.payTemplate.getXlsFileName();
		String sheetName = XlsSheetType.payTemplate.getSheetName();
		try {
			List<PayTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, PayTemplate.class);
			for (PayTemplate template : list) {
				template.init();
				payMap.put(template.getId(), template);
			}
		} catch (Exception e) {
			logger.error("加载");
		}
	}

	@Override
	public void stop() {
	}

	public void setUserAttrDao(UserAttrDao userAttrDao) {
		this.userAttrDao = userAttrDao;
	}

	@Override
	public PayTemplate getMonthCard() {
		for (PayTemplate template : payMap.values()) {
			if (template.getType() == PayType.month_card.type) {
				return template;
			}
		}
		return null;
	}

	@Override
	public PayTemplate getOwnCard() {
		for (PayTemplate template : payMap.values()) {
			if (template.getType() == PayType.forever_card.type) {
				return template;
			}
		}
		return null;
	}

	@Override
	public String getPayParam(int roleId) {
		PayExtendParam param = new PayExtendParam();
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			param.setIoId(connect.getIoId());
		}
		param.setNodeName(GameContext.getLocalNodeName());

		return param.decode();
	}

	@Override
	public PayType getPayTypeById(int id) {
		PayTemplate template = payMap.get(id);
		return PayType.valueOf(template.getType());
	}

	/**
	 * 返回首充信息
	 */
	@Override
	public PayTemplate getFirstPay() {
		for (PayTemplate template : payMap.values()) {
			if (template.getType() == PayType.first.type) {
				return template;
			}
		}
		return null;
	}

}
