package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_DISBAND_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_DISBAND_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

/**
 * 军团解散和退出
 * @author wangkun
 *
 */
@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_DISBAND_VALUE
)
public class CorpsDisbandAction extends AbstractCorpAction implements Action<CTS_CORPS_DISBAND_MSG>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_DISBAND_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_DISBAND_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}
		
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsInfo corps = GameContext.getCorpsApp().getCorpsInfo(corpsId);
		
		STC_CORPS_DISBAND_MSG.Builder builder = STC_CORPS_DISBAND_MSG.newBuilder();
		GameContext.getCorpsApp().lock(corpsId);
		try{
			int type = reqMsg.getType();
			//手动解散军团或者军团长退出军团
			if(type == 1 || roleId == corps.getLeaderId()){
				CorpsRole corpsRole = GameContext.getCorpsApp().getCorpsRole(corpsId, roleId);

				Result result = GameContext.getCorpsApp().corpsDisband(corps, corpsRole);
				builder.setExit(false);
				builder.setResult(result.getResult());
				builder.setInfo(result.getInfo());

				if(logger.isDebugEnabled()){
					logger.debug("解散军团结果: {}", JSON.toJSONString(result));
				}
				
				GameContext.getUserApp().saveCorpsId(roleId, 0);
				return builder.build();
			}
			//退出军团
			GameContext.getCorpsApp().removeCorpsRole(corpsId, roleId);
			GameContext.getUserApp().saveCorpsId(roleId, 0);

			builder.setResult(Result.SUCCESS);
			builder.setExit(true);
			return builder.build();

		} finally {
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
