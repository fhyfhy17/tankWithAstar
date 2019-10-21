package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.KeySupport;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class MasterQuestTemplate implements KeySupport<Integer>{
	private int id;
	private String title;
	private int condition;
	private String param1;
	private String param2;
	private int limit_i;
	private int branch1;
	private int branch2;
	private int branch3;

	private int exp;
	private int gold;
	private int iron;
	private int diamond;
	private int goodsId1;	
	private int num1;
	private int goodsId2;
	private int num2;
	private int goodsId3;
	private int num3;
	private int goodsId4;
	private int num4;
	private int goodsId5;
	private int num5;
	
	private Map<Integer, Integer> goodsMap = new HashMap<>();
	private List<AttrUnit> attrList = new ArrayList<>();
	private List<Integer> branchList = new ArrayList<>();
	private List<Set<Integer>> paramList = new ArrayList<>();
	
	public void init(){
		putGoods(goodsId1, num1, goodsMap);
		putGoods(goodsId2, num2, goodsMap);
		putGoods(goodsId3, num3, goodsMap);
		putGoods(goodsId4, num4, goodsMap);
		putGoods(goodsId5, num5, goodsMap);
		
		add(RoleAttr.gold, gold, attrList);
		add(RoleAttr.exp, exp, attrList);
		add(RoleAttr.iron, iron, attrList);
		add(RoleAttr.diamonds, diamond, attrList);
		
		addBranch(branch1);
		addBranch(branch2);
		addBranch(branch3);
		
		paramList.add(split(param1));
		paramList.add(split(param2));
	}
	
	private void addBranch(int branchId) {
		if(branchId <= 0)
			return;
		
		branchList.add(branchId);
	}
	
	private void putGoods(int goodsId, int num, Map<Integer, Integer> map){
		if(goodsId <= 0 || num <= 0){
			return;
		}
		
		GoodsBaseTemplate t = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if(t == null)
			throw new NullPointerException("初始化主线任务表失败，goodsId = " + goodsId);
		
		if(map.containsKey(goodsId)){
			num += map.get(goodsId);
		}
		map.put(goodsId, num);
	}
	
	private Set<Integer> split(String param){
		if(Util.isEmpty(param) || "0".equalsIgnoreCase(param)){
			return new HashSet<>();
		}
		
		Set<Integer> result = new HashSet<>();
		try{
			String[] params = param.split(Cat.comma);
			for(String paramStr : params){
				result.add(Integer.valueOf(paramStr));
			}
			return result;
		}catch(Exception e){
			String errorStr = String.format("主线任务条件错误, questId=%d, param=%s", id, param);
			LogCore.startup.error(errorStr, e);
			throw new IllegalArgumentException(errorStr, e);
		}
	}
	
	private void add(RoleAttr type, int value, List<AttrUnit> list){
		if(value <= 0){
			return;
		}
		
		list.add(AttrUnit.build(type, Operation.add, value));
	}
	
	
	public List<AttrUnit> getAttrList() {
		return new ArrayList<AttrUnit>(attrList);
	}
	
	public Map<Integer, Integer> getGoodsMap(){
		return new HashMap<>(this.goodsMap);
	}
	
	public List<Integer> getBranchList() {
		return Collections.unmodifiableList(this.branchList);
	}
	
	public boolean isMatched(int conditionType, int... params) {
		if (this.condition != conditionType) {
			return false;
		}
		int index = 0;
		for(Set<Integer> paramSet : paramList){
			if(Util.isEmpty(paramSet)){
				index++;
				continue;
			}
			
			//任务有限定条件，但是限定条件列表又不包括传入的参数条件
			if(index >= params.length || !paramSet.contains(params[index++])) {
				return false;
			}
		}
		return true;
	}
	
	
	@Override
	public Integer getKey(){
		return this.id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCondition() {
		return condition;
	}

	public void setCondition(int condition) {
		this.condition = condition;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLimit_i() {
		return limit_i;
	}

	public void setLimit_i(int limit_i) {
		this.limit_i = limit_i;
	}

	public int getBranch1() {
		return branch1;
	}

	public void setBranch1(int branch1) {
		this.branch1 = branch1;
	}

	public int getBranch2() {
		return branch2;
	}

	public void setBranch2(int branch2) {
		this.branch2 = branch2;
	}

	public int getBranch3() {
		return branch3;
	}

	public void setBranch3(int branch3) {
		this.branch3 = branch3;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
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

	public int getGoodsId5() {
		return goodsId5;
	}

	public void setGoodsId5(int goodsId5) {
		this.goodsId5 = goodsId5;
	}

	public int getNum5() {
		return num5;
	}

	public void setNum5(int num5) {
		this.num5 = num5;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

}