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
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_MDF_DEC_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_MDF_DEC_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_MDF_DEC_VALUE
)
public class CorpsModifyDecAction extends AbstractCorpAction implements Action<CTS_CORPS_MDF_DEC_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_MDF_DEC_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_MDF_DEC_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}
		
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);

		STC_CORPS_MDF_DEC_MSG.Builder msgBuilder = STC_CORPS_MDF_DEC_MSG.newBuilder();
		//非法字符
		//验证是否存在非法字符
		Set<String> sensitives = GameContext.getMutilDfaApp().matchSensitiveWord(reqMsg.getDec());
		if(!Util.isEmpty(sensitives)){
			return msgBuilder
				.setResult(Result.FAILURE)
				.setInfo(Tips.CORPS_DESC_ILLEGAL)
				.build();
		}

		GameContext.getCorpsApp().lock(corpsId);
		try{
			//修改军团说明
			Result result = GameContext.getCorpsApp().modifyDec(corpsId, roleId, reqMsg.getDec());

			msgBuilder.setResult(result.getResult());
			msgBuilder.setInfo(result.getInfo());
			msgBuilder.setDec(reqMsg.getDec());

			return msgBuilder.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
