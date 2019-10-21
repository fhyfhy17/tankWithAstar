package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_DONATE_GOLD_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_DONATE_GOLD_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsTechDonateResult;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_DONATE_GOLD_VALUE
)

public class CorpsTechDonateGoldAction extends AbstractCorpAction implements Action<CTS_CORPS_DONATE_GOLD_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_DONATE_GOLD_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());  //判断用户是否掉线
		if(connect == null){
			return null;
		}
		int techId = reqMsg.getTechId();

		final int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_DONATE_GOLD_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		GameContext.getCorpsApp().lock(corpsId);
		try{
			CorpsTechDonateResult result = GameContext.getCorpsApp().techGoldDonate(corpsId, roleId, techId);

			return STC_CORPS_DONATE_GOLD_MSG.newBuilder()
						.setResult(result.getResult())
						.setInfo(result.getInfo())
						.setState(result.getState())
						.setCoolTime(result.getCoolTime())
						.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
