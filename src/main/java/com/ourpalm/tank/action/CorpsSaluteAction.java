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
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_SALUTE_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsSaluteResult;
import com.ourpalm.tank.vo.result.Result;


/**
 * 向长官敬礼
 * @author wangkun
 *
 */
@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_SALUTE_VALUE
)
public class CorpsSaluteAction extends AbstractCorpAction implements Action<Object>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_SALUTE_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}
		

		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		
		CorpsSaluteResult result = GameContext.getCorpsApp().corpsSalute(corpsId, roleId);

		if(logger.isDebugEnabled()){
			logger.debug("军团敬礼结果:"+JSON.toJSONString(result));
		}

		return STC_CORPS_SALUTE_MSG.newBuilder()
				.setResult(result.getResult())
				.setInfo(result.getInfo())
				.setGold(result.getGold())
				.build();
	}

}
