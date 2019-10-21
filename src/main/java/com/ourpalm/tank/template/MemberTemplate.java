package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class MemberTemplate implements KeySupport<Integer> {

	private int id;
	private int memberId;
	private boolean exp;
	
	private int initHole;			//出生开孔数目		
	private int needGoodsId1;		//开孔需要物品和数目
	private int goodsNum1;
	private int needGoodsId2;
	private int goodsNum2;
	private int needGoodsId3;
	private int goodsNum3;
	private int needGoodsId4;
	private int goodsNum4;
	private int needGoodsId5;
	private int goodsNum5;
	private int needGoodsId6;
	private int goodsNum6;
	
	private int returnGoodsId;		//转换为碎片ID
	private int returnNum;			//转换数量
	
	private String name;
	private String desc;
	private int compositeWeight;
	private int type;
	private int quality;
	private int maxLvl;
	private int aptitudeMin;
	private int aptitudeMax;
	private int aptitudeBase;
	private int spell;
	private int property1;
	private int property2;
	private int property3;
	private int property4;
	private int property5;
	private int property6;
	
	private List<Integer> attrList = new ArrayList<>();
	
	public void init() {
		this.add(property1, attrList);
		this.add(property2, attrList);
		this.add(property3, attrList);
		this.add(property4, attrList);
		this.add(property5, attrList);
		this.add(property6, attrList);
		
		this.checkGoods(needGoodsId1, goodsNum1);
		this.checkGoods(needGoodsId2, goodsNum2);
		this.checkGoods(needGoodsId3, goodsNum3);
		this.checkGoods(needGoodsId4, goodsNum4);
		this.checkGoods(needGoodsId5, goodsNum5);
		this.checkGoods(needGoodsId6, goodsNum6);
		this.checkGoods(returnGoodsId, returnNum);
	}
	
	private void add(int property, List<Integer> list){
		if (property <= 0) {
			return;
		}
		if (AttrType.valueOf(property) == null) {
			throw new IllegalArgumentException(String.format("成员属性填写错误 templateId=%d, attrType=%d", id, property));
		}
		
		list.add(property);
	}
	
	
	private void checkGoods(int goodsId, int num){
		if(goodsId <= 0 || num <= 0){
			return ;
		}
		GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if(template == null){
			throw new NullPointerException("物品不存在,成员ID = " + id + "  goodsId = " + goodsId);
		}
	}
	
	
	public List<Integer> getAttrList() {
		return attrList;
	}
	
	
	/** 开孔所需的物品 */
	public int[] openHoleNeedGoods(int index){
		switch(index){
			case 0 : return new int[]{this.needGoodsId1, this.goodsNum1};
			case 1 : return new int[]{this.needGoodsId2, this.goodsNum2};
			case 2 : return new int[]{this.needGoodsId3, this.goodsNum3};
			case 3 : return new int[]{this.needGoodsId4, this.goodsNum4};
			case 4 : return new int[]{this.needGoodsId5, this.goodsNum5};
			case 5 : return new int[]{this.needGoodsId6, this.goodsNum6};
		}
		return new int[]{this.needGoodsId6, this.goodsNum6};
	}
	

	@Override
	public Integer getKey() {
		return this.id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getCompositeWeight() {
		return compositeWeight;
	}
	public void setCompositeWeight(int compositeWeight) {
		this.compositeWeight = compositeWeight;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getAptitudeMin() {
		return aptitudeMin;
	}
	public void setAptitudeMin(int aptitudeMin) {
		this.aptitudeMin = aptitudeMin;
	}
	public int getAptitudeMax() {
		return aptitudeMax;
	}
	public void setAptitudeMax(int aptitudeMax) {
		this.aptitudeMax = aptitudeMax;
	}
	public int getAptitudeBase() {
		return aptitudeBase;
	}
	public void setAptitudeBase(int aptitudeBase) {
		this.aptitudeBase = aptitudeBase;
	}
	public int getSpell() {
		return spell;
	}
	public void setSpell(int spell) {
		this.spell = spell;
	}
	public int getProperty1() {
		return property1;
	}
	public void setProperty1(int property1) {
		this.property1 = property1;
	}
	public int getProperty2() {
		return property2;
	}
	public void setProperty2(int property2) {
		this.property2 = property2;
	}
	public int getProperty3() {
		return property3;
	}
	public void setProperty3(int property3) {
		this.property3 = property3;
	}
	public int getProperty4() {
		return property4;
	}
	public void setProperty4(int property4) {
		this.property4 = property4;
	}
	public int getProperty5() {
		return property5;
	}
	public void setProperty5(int property5) {
		this.property5 = property5;
	}
	public int getProperty6() {
		return property6;
	}
	public void setProperty6(int property6) {
		this.property6 = property6;
	}
	public int getMaxLvl() {
		return maxLvl;
	}
	public void setMaxLvl(int maxLvl) {
		this.maxLvl = maxLvl;
	}
	public boolean isExp() {
		return exp;
	}
	public void setExp(boolean exp) {
		this.exp = exp;
	}

	public int getInitHole() {
		return initHole;
	}

	public void setInitHole(int initHole) {
		this.initHole = initHole;
	}

	public int getNeedGoodsId1() {
		return needGoodsId1;
	}

	public void setNeedGoodsId1(int needGoodsId1) {
		this.needGoodsId1 = needGoodsId1;
	}

	public int getGoodsNum1() {
		return goodsNum1;
	}

	public void setGoodsNum1(int goodsNum1) {
		this.goodsNum1 = goodsNum1;
	}

	public int getNeedGoodsId2() {
		return needGoodsId2;
	}

	public void setNeedGoodsId2(int needGoodsId2) {
		this.needGoodsId2 = needGoodsId2;
	}

	public int getGoodsNum2() {
		return goodsNum2;
	}

	public void setGoodsNum2(int goodsNum2) {
		this.goodsNum2 = goodsNum2;
	}

	public int getNeedGoodsId3() {
		return needGoodsId3;
	}

	public void setNeedGoodsId3(int needGoodsId3) {
		this.needGoodsId3 = needGoodsId3;
	}

	public int getGoodsNum3() {
		return goodsNum3;
	}

	public void setGoodsNum3(int goodsNum3) {
		this.goodsNum3 = goodsNum3;
	}

	public int getNeedGoodsId4() {
		return needGoodsId4;
	}

	public void setNeedGoodsId4(int needGoodsId4) {
		this.needGoodsId4 = needGoodsId4;
	}

	public int getGoodsNum4() {
		return goodsNum4;
	}

	public void setGoodsNum4(int goodsNum4) {
		this.goodsNum4 = goodsNum4;
	}

	public int getNeedGoodsId5() {
		return needGoodsId5;
	}

	public void setNeedGoodsId5(int needGoodsId5) {
		this.needGoodsId5 = needGoodsId5;
	}

	public int getGoodsNum5() {
		return goodsNum5;
	}

	public void setGoodsNum5(int goodsNum5) {
		this.goodsNum5 = goodsNum5;
	}

	public int getNeedGoodsId6() {
		return needGoodsId6;
	}

	public void setNeedGoodsId6(int needGoodsId6) {
		this.needGoodsId6 = needGoodsId6;
	}

	public int getGoodsNum6() {
		return goodsNum6;
	}

	public void setGoodsNum6(int goodsNum6) {
		this.goodsNum6 = goodsNum6;
	}

	public int getReturnGoodsId() {
		return returnGoodsId;
	}

	public void setReturnGoodsId(int returnGoodsId) {
		this.returnGoodsId = returnGoodsId;
	}

	public int getReturnNum() {
		return returnNum;
	}

	public void setReturnNum(int returnNum) {
		this.returnNum = returnNum;
	}
}
