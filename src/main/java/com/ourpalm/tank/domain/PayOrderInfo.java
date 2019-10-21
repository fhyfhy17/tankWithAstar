package com.ourpalm.tank.domain;

public class PayOrderInfo {
	private int type;// 1蓝钻 2 黄钻 3支付
	private int roleId; // 游戏角色ID
	private String channelId; // 平台信息
	private String token; // 订单号
	private long logTime; // 日志时间
	private int state; // 订单是否已发放物品完毕 了 0建立 1收到回调，成功 2发货成功
	private int itemId; // 商品ID

	private int actualPrice;// 实际金额
	private String rebateType;// 返利类型
	private int rebatePrice;// 返利金额
	private int rebateGoodsId;// 返利商品ID
	private int pubacctPayAmt;// 折扣券 金额
	private int itemNum;// 购买物品个数

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getLogTime() {
		return logTime;
	}

	public void setLogTime(long logTime) {
		this.logTime = logTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(int actualPrice) {
		this.actualPrice = actualPrice;
	}

	public String getRebateType() {
		return rebateType;
	}

	public void setRebateType(String rebateType) {
		this.rebateType = rebateType;
	}

	public int getRebatePrice() {
		return rebatePrice;
	}

	public void setRebatePrice(int rebatePrice) {
		this.rebatePrice = rebatePrice;
	}

	public int getRebateGoodsId() {
		return rebateGoodsId;
	}

	public void setRebateGoodsId(int rebateGoodsId) {
		this.rebateGoodsId = rebateGoodsId;
	}

	public int getPubacctPayAmt() {
		return pubacctPayAmt;
	}

	public void setPubacctPayAmt(int pubacctPayAmt) {
		this.pubacctPayAmt = pubacctPayAmt;
	}

	public int getItemNum() {
		return itemNum;
	}

	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}

}
