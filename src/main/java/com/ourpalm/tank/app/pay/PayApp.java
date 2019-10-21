package com.ourpalm.tank.app.pay;

import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.constant.PayStateEnum;
import com.ourpalm.tank.domain.PayOrderInfo;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.template.PayTemplate;
import com.ourpalm.tank.type.PayType;

public interface PayApp extends Service {

	PayTemplate getPayTemplate(int id);

	List<PayTemplate> getAllPayTemplate();

	RolePay getRolePay(int roleId);

	/**
	 * 月卡
	 * 
	 * @return
	 */
	PayTemplate getMonthCard();

	/**
	 * 永久卡
	 * 
	 * @return
	 */
	PayTemplate getOwnCard();

	/** 每日秒杀物品ID */
	PayTemplate getDayGoodsBox();

	/**
	 * 获取支付透传参数
	 * 
	 * @return
	 */
	String getPayParam(int roleId);

	/**
	 * 根据商品id获取商品类型
	 * 
	 * @param id
	 * @return
	 */
	PayType getPayTypeById(int id);

	/**
	 * 返回首充信息
	 */
	PayTemplate getFirstPay();

	/** 处理支付 */
	PayStateEnum pay(PayOrderInfo order);

	/** 蓝钻续费 */
	PayStateEnum blueRenew(PayOrderInfo order);
}
