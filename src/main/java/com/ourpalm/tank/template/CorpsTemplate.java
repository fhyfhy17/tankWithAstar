package com.ourpalm.tank.template;

public class CorpsTemplate {

	private int createDiamonds;		//创建军团所需钻石数
	private String initS1;
	private String initS2;
	private String initS3;
	private String initS4;
	private String initS5;
	private String initTech;
	private int coolTime;			//捐献冷却时间(单位: 秒)
	private float donateTimeValue; 	//贡献度时间换算系数值
	private float expGoldValue;		//科技经验对金币换算系数
	private float timeGoldValue;	//冷却时间对金币转换系数
	private float goldContrValue;	//金币捐献对贡献值换算系数
	private float goldCorpsMoneyValue;	//金币捐献对军团币换算系数
	
	private int donateOverstepTime;	//单次捐献允许超出的最大时间(单位: 秒)
	private int shopFlushTime;		//商店自动刷新时间间隔(秒)
	private int shopFlushGold1;		//刷新商品所需金币
	private int shopFlushGold2;
	private int shopFlushGold3;
	private int shopFlushGold4;
	
	private int saluteGold;		//向长官敬礼所得金币数
	
	public String getInitTech() {
		return initTech;
	}
	public void setInitTech(String initTech) {
		this.initTech = initTech;
	}
	public int getCreateDiamonds() {
		return createDiamonds;
	}
	public void setCreateDiamonds(int createDiamonds) {
		this.createDiamonds = createDiamonds;
	}
	public String getInitS1() {
		return initS1;
	}
	public void setInitS1(String initS1) {
		this.initS1 = initS1;
	}
	public String getInitS2() {
		return initS2;
	}
	public void setInitS2(String initS2) {
		this.initS2 = initS2;
	}
	public String getInitS3() {
		return initS3;
	}
	public void setInitS3(String initS3) {
		this.initS3 = initS3;
	}
	public String getInitS4() {
		return initS4;
	}
	public void setInitS4(String initS4) {
		this.initS4 = initS4;
	}
	public String getInitS5() {
		return initS5;
	}
	public void setInitS5(String initS5) {
		this.initS5 = initS5;
	}
	public int getCoolTime() {
		return coolTime;
	}
	public void setCoolTime(int coolTime) {
		this.coolTime = coolTime;
	}
	public float getDonateTimeValue() {
		return donateTimeValue;
	}
	public void setDonateTimeValue(float donateTimeValue) {
		this.donateTimeValue = donateTimeValue;
	}
	public int getDonateOverstepTime() {
		return donateOverstepTime;
	}
	public void setDonateOverstepTime(int donateOverstepTime) {
		this.donateOverstepTime = donateOverstepTime;
	}
	public float getExpGoldValue() {
		return expGoldValue;
	}
	public void setExpGoldValue(float expGoldValue) {
		this.expGoldValue = expGoldValue;
	}
	public int getShopFlushTime() {
		return shopFlushTime;
	}
	public void setShopFlushTime(int shopFlushTime) {
		this.shopFlushTime = shopFlushTime;
	}
	public int getShopFlushGold1() {
		return shopFlushGold1;
	}
	public void setShopFlushGold1(int shopFlushGold1) {
		this.shopFlushGold1 = shopFlushGold1;
	}
	public int getShopFlushGold2() {
		return shopFlushGold2;
	}
	public void setShopFlushGold2(int shopFlushGold2) {
		this.shopFlushGold2 = shopFlushGold2;
	}
	public int getShopFlushGold3() {
		return shopFlushGold3;
	}
	public void setShopFlushGold3(int shopFlushGold3) {
		this.shopFlushGold3 = shopFlushGold3;
	}
	public int getShopFlushGold4() {
		return shopFlushGold4;
	}
	public void setShopFlushGold4(int shopFlushGold4) {
		this.shopFlushGold4 = shopFlushGold4;
	}
	public float getTimeGoldValue() {
		return timeGoldValue;
	}
	public void setTimeGoldValue(float timeGoldValue) {
		this.timeGoldValue = timeGoldValue;
	}
	public float getGoldContrValue() {
		return goldContrValue;
	}
	public void setGoldContrValue(float goldContrValue) {
		this.goldContrValue = goldContrValue;
	}
	public float getGoldCorpsMoneyValue() {
		return goldCorpsMoneyValue;
	}
	public void setGoldCorpsMoneyValue(float goldCorpsMoneyValue) {
		this.goldCorpsMoneyValue = goldCorpsMoneyValue;
	}
	public int getSaluteGold() {
		return saluteGold;
	}
	public void setSaluteGold(int saluteGold) {
		this.saluteGold = saluteGold;
	}
}
