package com.ourpalm.tank.app.quest.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.template.MedalTemplate;

/**
 * 获得N级勋章
 * @author Administrator
 *
 */
public class _54_RoleLevelMedalPhase extends QuestPhase {
	private int level;
	
	public _54_RoleLevelMedalPhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建任务 [获得N级勋章 ] 失败，参数错误");
		}
		
		level = Integer.parseInt(param1);
	}

	@Override
	public boolean initProgress() {
		return this.RoleBodyMedal() || this.RoleGoodsMedal();
	}
	
	//遍历成员身上的勋章
	private boolean RoleBodyMedal(){
		Map<String, RoleMember> memberMap = GameContext.getMemberApp().getRoleMembers(roleId);
		if(Util.isEmpty(memberMap)){
			return false;
		}
		Map<Integer, Integer> allBodyMedalMap = new HashMap<>();
		for(RoleMember member : memberMap.values()) {
			List<Integer> medalList = member.getMedals();
			if(Util.isEmpty(medalList)){
				continue;
			}
			
			for(Integer medalId : medalList){
				if(medalId <= 0){
					continue;
				}
				if(allBodyMedalMap.containsKey(medalId)){
					int value = allBodyMedalMap.get(medalId) + 1;
					allBodyMedalMap.put(medalId, value);
					continue;
				}
				allBodyMedalMap.put(medalId, 1);
			}
		}
		return this.hadConditionAndUpdateProgree(allBodyMedalMap);
	}
	
	
	//遍历背包中所有勋章
	private boolean RoleGoodsMedal(){
		Map<Integer, Integer> goodsMap = GameContext.getGoodsApp().getRoleGoods(roleId);
		if(Util.isEmpty(goodsMap)){
			return false;
		}
		
		return this.hadConditionAndUpdateProgree(goodsMap);
	}
	
	
	
	
	//判断勋章列表是否满足条件，并更新任务进度
	private boolean hadConditionAndUpdateProgree(Map<Integer, Integer> medalMap){
		if(Util.isEmpty(medalMap)){
			return false;
		}
		boolean hadCondition = false;
		for(Entry<Integer, Integer> entry : medalMap.entrySet()){
			int medalId = entry.getKey();
			int value = entry.getValue();
			MedalTemplate medalTemplate = GameContext.getGoodsApp().getMedalTemplate(medalId);
			if(medalTemplate == null){
				continue;
			}
			if(medalTemplate.getLevel() == this.level) {
				this.progress += value;
				hadCondition = true;
			}
		}
		
		return hadCondition;
	}
	


	@Override
	public boolean roleLevelMedal(int medalId, int count) {
		MedalTemplate medalTemplate = GameContext.getGoodsApp().getMedalTemplate(medalId);
		if(medalTemplate == null)
			return false;
		
		if(medalTemplate.getLevel() != this.level) {
			return false;
		}
		
		this.progress += count;
		return true;
	}


}
