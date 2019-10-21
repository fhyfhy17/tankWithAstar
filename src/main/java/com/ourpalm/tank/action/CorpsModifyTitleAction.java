package com.ourpalm.tank.action;

import java.util.Set;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_MDF_LVL_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_MDF_LVL_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_MDF_LVL_VALUE
)
public class CorpsModifyTitleAction extends AbstractCorpAction implements Action<CTS_CORPS_MDF_LVL_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_MDF_LVL_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_MDF_LVL_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		//非法字符
		//验证是否存在非法字符
		Set<String> sensitives = GameContext.getMutilDfaApp().matchSensitiveWord(reqMsg.getName());
		if(!Util.isEmpty(sensitives)){
			return STC_CORPS_MDF_LVL_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_TITLE_ILLEGAL)
					.build();
		}
		
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		GameContext.getCorpsApp().lock(corpsId);
		try{
			Result result = GameContext.getCorpsApp().modifyTitleName(corpsId,
					connect.getRoleId(), reqMsg.getCorpsLvl(), reqMsg.getName());

			return STC_CORPS_MDF_LVL_MSG.newBuilder()
					.setCorpsLvl(reqMsg.getCorpsLvl())
					.setInfo(result.getInfo())
					.setResult(result.getResult())
					.setName(reqMsg.getName())
					.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
