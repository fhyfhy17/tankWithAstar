package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_SETTING_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_SETTING_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_SETTING_VALUE
)
public class CorpsSettingAction extends AbstractCorpAction implements Action<CTS_CORPS_SETTING_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_SETTING_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_SETTING_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}		

		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);

		GameContext.getCorpsApp().lock(corpsId);
		try{
			Result result = GameContext.getCorpsApp().corpsSetting(roleId, corpsId, reqMsg.getType());

			return STC_CORPS_SETTING_MSG.newBuilder()
					.setResult(result.getResult())
					.setInfo(result.getInfo())
					.setType(reqMsg.getType())
					.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
