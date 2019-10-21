package com.ourpalm.tank.action;

import java.util.Set;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_CREATE_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_CREATE_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsResult;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_CREATE_VALUE
)
public class CorpsCreateAction implements Action<CTS_CORPS_CREATE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_CREATE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		final int areaId = connect.getAreaId();
		final String name = reqMsg.getName();

		//非法字符
		//验证是否存在非法字符
		Set<String> sensitives = GameContext.getMutilDfaApp().matchSensitiveWord(name);
		if(!Util.isEmpty(sensitives)){
			return STC_CORPS_CREATE_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NAME_ILLEGAL)
					.setId(0)
					.build();
		}

		GameContext.getCorpsApp().lock(areaId);
		try{
			CorpsResult result = GameContext.getCorpsApp().createCorps(areaId, roleId, name);
			STC_CORPS_CREATE_MSG resp = STC_CORPS_CREATE_MSG.newBuilder()
					.setResult(result.getResult())
					.setInfo(result.getInfo())
					.setId(result.getCorpsId())
					.build();

			LogCore.runtime.debug("创建军团 areaId = {}, name = {}, result = {}, info = {}, corpsId = {}",
					areaId, name, result.getResult(), result.getInfo(), result.getCorpsId());

			return resp;
		}finally{
			GameContext.getCorpsApp().unlock(areaId);
		}
	}

}
