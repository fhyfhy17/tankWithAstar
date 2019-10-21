package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_TECH_DEV_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_TECH_DEV_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsTechDevResult;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_TECH_DEV_VALUE
)

public class CorpsTechDevAction extends AbstractCorpAction implements Action<CTS_CORPS_TECH_DEV_MSG>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_TECH_DEV_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());  //判断用户是否掉线
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_TECH_DEV_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}
		
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		GameContext.getCorpsApp().lock(corpsId);
		try{
			CorpsTechDevResult result = GameContext.getCorpsApp().techDev(corpsId, roleId, reqMsg.getTechId());

			if(logger.isDebugEnabled()){
				logger.debug("研发科技 techId = {} {}", reqMsg.getTechId(), JSON.toJSONString(result));
			}

			return STC_CORPS_TECH_DEV_MSG.newBuilder()
					.setResult(result.getResult())
					.setInfo(result.getInfo())
					.setDevTime(result.getDevTime())
					.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}
}
