package com.ourpalm.tank.app.member;

import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.template.MedalTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.MedalCompositeResult;
import com.ourpalm.tank.vo.result.Result;

public class MedalAppImpl implements MedalApp {
	
	
	@Override
	public void start() {
		
	}

	@Override
	public void stop() {}
	
	
	@Override
	public MedalCompositeResult composite(int roleId, int medalId, int count) {
		MedalCompositeResult result = new MedalCompositeResult();
		result.setResult(Result.FAILURE);
		MedalTemplate template = GameContext.getGoodsApp().getMedalTemplate(medalId);
		if (template == null) {
			result.setInfo("勋章ID错误");
			return result;
		}
		int nextId = template.getNextId();
		if (nextId <= 0) {
			result.setInfo("该勋章已经达到顶级");
			return result;
		}
		
		int curCount = GameContext.getGoodsApp().getCount(roleId, medalId);
		int needCount = count * 3;
		if(needCount < 0 || needCount > curCount) {
			result.setInfo("勋章数量不足，无法合成");
			return result;
		}
		
		int needIron = template.getIron() * count;
		boolean isDone = GameContext.getUserAttrApp().consumIron(roleId, needIron, OutputType.medalComposeDec.type(), StringUtil.buildLogOrigin(template.getName(), OutputType.medalComposeDec.getInfo()));
		if(!isDone) {
			result.setInfo(Tips.NEED_GOLD);
			return result;
		}
		
		GameContext.getGoodsApp().removeGoods(roleId, medalId, count * 3, StringUtil.buildLogOrigin(template.getName(), OutputType.medalComposeDec.getInfo()));
		GameContext.getGoodsApp().addGoods(roleId, nextId, count, StringUtil.buildLogOrigin(template.getName(), OutputType.medalComposeInc.getInfo()));
		GameContext.getQuestTriggerApp().roleLevelMedal(roleId, nextId, count);
		
		result.setResult(Result.SUCCESS);
		result.setNextMedalId(template.getNextId());
		result.setCount(count);
		
		return result;
	}

}
