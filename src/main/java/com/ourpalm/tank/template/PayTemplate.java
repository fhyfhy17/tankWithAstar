package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;

public class PayTemplate implements Comparable<PayTemplate> {

	private int id;
	private int type;
	private String name;
	private String icon;
	private String desc;
	private int rmb;
	private int showDiamond;
	private int diamond;
	private int firstDiamond;
	private int goodsId;
	private int goodsNum;
	private String txIcon;

	public void init() {
		this.checkGoods(goodsId, goodsNum);
	}

	private void checkGoods(int goodsId, int num) {
		if (goodsId <= 0 || num <= 0) {
			return;
		}
		GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if (template == null) {
			LogCore.startup.error("!!!!!!!!!!!!!!!!!!!!!!! 充值奖励表 pay 中goodsId={} 不存在该物品  id={} !!!!!!!!!!!!!!!!!!!",
					goodsId, id);
		}
	}

	/** 充值所获钻石数 */
	public int getDiamond(boolean hasFirst) {
		return hasFirst ? firstDiamond : diamond;
	}

	/** 充值所获物品 */
	public Map<Integer, Integer> getGoodsMap() {
		Map<Integer, Integer> goodsMap = new HashMap<>();
		if (goodsId <= 0 || goodsNum <= 0) {
			return goodsMap;
		}
		goodsMap.put(goodsId, goodsNum);
		return goodsMap;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getRmb() {
		return rmb;
	}

	public void setRmb(int rmb) {
		this.rmb = rmb;
	}

	public int getShowDiamond() {
		return showDiamond;
	}

	public void setShowDiamond(int showDiamond) {
		this.showDiamond = showDiamond;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getFirstDiamond() {
		return firstDiamond;
	}

	public void setFirstDiamond(int firstDiamond) {
		this.firstDiamond = firstDiamond;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public int getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}

	@Override
	public int compareTo(PayTemplate o) {
		return this.rmb > o.getRmb() ? -1 : 1;
	}

	public String getTxIcon() {
		return txIcon;
	}

	public void setTxIcon(String txIcon) {
		this.txIcon = txIcon;
	}

}
