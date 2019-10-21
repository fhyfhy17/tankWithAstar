package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_USE_SKILL_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_SKILL_MSG;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
	id = BATTLE_MSG.CMD_ID.CTS_USE_SKILL_VALUE
)
public class BattleUseSkillAction implements Action<CTS_USE_SKILL_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_USE_SKILL_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		final int skillId = reqMsg.getSkillId();
		final int targetId = reqMsg.getTargetId();
		
		AbstractInstance self = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(self == null){
			return null;
		}
		
		if(self.isDeath()){
			return null;
		}
		
		STC_USE_SKILL_MSG.Builder builder = STC_USE_SKILL_MSG.newBuilder();
		AbstractInstance target = GameContext.getTankApp().getInstance(targetId);
		if(target == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.SKILL_NO_TARGET);
			return builder.build();
		}
		
		Skill skill = self.getSkill(skillId);
		if(skill == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.SKILL_NO_EXIST);
			return builder.build();
		}
		
		if(! skill.finishCoolTime()){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.COOL_TIME_NOT_OVER);
			return builder.build();
		}
		
		LogCore.runtime.debug("使用技能 skillId = {}, targetId = {}", skillId, targetId);
		
		skill.use(target);
		
		
		
		builder.setResult(Result.SUCCESS);
		builder.setInfo("");
		return builder.build();
	}

}
