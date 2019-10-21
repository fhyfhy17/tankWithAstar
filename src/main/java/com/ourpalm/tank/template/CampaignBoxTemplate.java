package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;

public class CampaignBoxTemplate {
	private int warId;
	private int hard;
	private int star;
	private int goodsId1;
	private int num1;
	private int goodsId2;
	private int num2;
	private int goodsId3;
	private int num3;
	private int goodsId4;
	private int num4;
	
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	
	public void init(){
		this.buildMap(goodsId1, num1);
		this.buildMap(goodsId2, num2);
		this.buildMap(goodsId3, num3);
		this.buildMap(goodsId4, num4);
	}
	
	private void buildMap(int goodsId, int num){
		if(goodsId <= 0 || num <= 0){
			return ;
		}
		if(GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId) == null){
			LogCore.startup.error("goodsId = {} 此物品不存在", goodsId);
			return ;
		}
		Integer count = goodsMap.get(goodsId);
		if(count != null){
			num += count;
		}
		goodsMap.put(goodsId, num);
	}
	
	
	public Map<Integer, Integer> getGoodsMap(){
		return this.goodsMap;
	}
	
	
	public int getWarId() {
		return warId;
	}
	public void setWarId(int warId) {
		this.warId = warId;
	}
	public int getHard() {
		return hard;
	}
	public void setHard(int hard) {
		this.hard = hard;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public int getGoodsId1() {
		return goodsId1;
	}
	public void setGoodsId1(int goodsId1) {
		this.goodsId1 = goodsId1;
	}
	public int getNum1() {
		return num1;
	}
	public void setNum1(int num1) {
		this.num1 = num1;
	}
	public int getGoodsId2() {
		return goodsId2;
	}
	public void setGoodsId2(int goodsId2) {
		this.goodsId2 = goodsId2;
	}
	public int getNum2() {
		return num2;
	}
	public void setNum2(int num2) {
		this.num2 = num2;
	}
	public int getGoodsId3() {
		return goodsId3;
	}
	public void setGoodsId3(int goodsId3) {
		this.goodsId3 = goodsId3;
	}
	public int getNum3() {
		return num3;
	}
	public void setNum3(int num3) {
		this.num3 = num3;
	}
	public int getGoodsId4() {
		return goodsId4;
	}
	public void setGoodsId4(int goodsId4) {
		this.goodsId4 = goodsId4;
	}
	public int getNum4() {
		return num4;
	}
	public void setNum4(int num4) {
		this.num4 = num4;
	}
}
