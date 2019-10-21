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
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_TIREN_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_TIREN_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_TIREN_VALUE
)
public class CorpsDriveAction extends AbstractCorpAction implements Action<CTS_CORPS_TIREN_MSG>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_TIREN_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_TIREN_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}
		
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		final int driveRoleId = reqMsg.getRoleId();
		GameContext.getCorpsApp().lock(corpsId);
		try{
			Result result = GameContext.getCorpsApp().corspDrive(corpsId, roleId, driveRoleId);
			
			if(logger.isDebugEnabled()){
				logger.debug("军团踢人 roleId = {} driveRoleId = {}  result = {}", roleId, driveRoleId, JSON.toJSONString(result));
			}

			if (result.isSuccess()) {
				// 发邮件
				CorpsInfo corps = GameContext.getCorpsApp().getCorpsInfo(corpsId);
				String mailContent = String.format(Tips.CORPS_DRIVE_ROLE_MAIL, corps.getName());
				GameContext.getMailApp().sendMail(
						driveRoleId,
						Tips.CORPS_DRIVE_ROLE,
						mailContent,
						0, 0, 0, 0,
						null);
				RoleConnect rc = GameContext.getUserApp().getRoleConnect(driveRoleId);
				if (rc != null) {
					GameContext.getMailApp().promit(rc);
				}
			}

			return STC_CORPS_TIREN_MSG.newBuilder()
						.setResult(result.getResult())
						.setInfo(result.getInfo())
						.setRoleId(driveRoleId)
						.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
