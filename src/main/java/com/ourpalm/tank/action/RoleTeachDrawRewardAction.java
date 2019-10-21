package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.STC_DRAW_TEACH_REWARD_MSG;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

/** 领取新手引导完成奖励 */
@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, 
	id = ROLE_MSG.CMD_ID.CTS_DRAW_TEACH_REWARD_VALUE
)
public class RoleTeachDrawRewardAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		STC_DRAW_TEACH_REWARD_MSG.Builder builder = STC_DRAW_TEACH_REWARD_MSG.newBuilder();
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
		int gold = role.getTeachGold();
		if(gold <= 0){
			builder.setSuccess(false);
			builder.setInfo("奖励已领取");
			return builder.build();
		}
		
		role.setTeachGold(0);
		GameContext.getUserApp().saveRoleAccount(role);
		
		GameContext.getUserAttrApp().changeAttribute(role.getRoleId(),
				AttrUnit.build(RoleAttr.gold, Operation.add, gold), OutputType.newRoleGuideRewardInc );
		
		builder.setSuccess(true);
		builder.setInfo("");
		
		return builder.build();
	}

}
