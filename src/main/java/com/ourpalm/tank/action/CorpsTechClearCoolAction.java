package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_DONATE_CD_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_DONATE_CD_VALUE
)
public class CorpsTechClearCoolAction extends AbstractCorpAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());  //判断用户是否掉线
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_DONATE_CD_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		STC_CORPS_DONATE_CD_MSG.Builder builder = STC_CORPS_DONATE_CD_MSG.newBuilder();
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsRole corpsRole = GameContext.getCorpsApp().getCorpsRole(corpsId, roleId);
		CorpsInfo crops = GameContext.getCorpsApp().getCorpsInfo(corpsId);
		if(corpsRole == null || crops == null){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.CORPS_NO_EXIST);
			return builder.build();
		}

		//计算所需金币
		int downTime = corpsRole.donateCountdownTime();
		int gold = (int)(downTime * GameContext.getCorpsApp().getInitTemplate().getTimeGoldValue());
		boolean consumeFlag = GameContext.getUserAttrApp().changeAttribute(roleId,
				AttrUnit.build(RoleAttr.gold, Operation.decrease, gold), true, OutputType.corpsDonateCdDec.type(), StringUtil.buildLogOrigin(crops.getName(),  OutputType.corpsDonateCdDec.getInfo()));
		if(!consumeFlag){
			builder.setResult(Result.FAILURE);
			builder.setInfo(Tips.NEED_GOLD);
			return builder.build();
		}

		//清除冷却
		corpsRole.setTechDonateTime(0);
		GameContext.getCorpsApp().saveCorpsRole(corpsRole);

		builder.setResult(Result.SUCCESS);
		builder.setInfo("");

		return builder.build();
	}

}
