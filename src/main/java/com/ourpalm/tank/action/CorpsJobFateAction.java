package com.ourpalm.tank.action;

import java.util.Collections;

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
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_JOB_FATE_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CorpsLevel;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_JOB_FATE_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsFateResult;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_JOB_FATE_VALUE
)
public class CorpsJobFateAction extends AbstractCorpAction implements Action<CTS_CORPS_JOB_FATE_MSG>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_JOB_FATE_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_JOB_FATE_MSG.newBuilder()
					.setResult(false)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsInfo corps = GameContext.getCorpsApp().getCorpsInfo(corpsId);

		STC_CORPS_JOB_FATE_MSG.Builder builder = STC_CORPS_JOB_FATE_MSG.newBuilder();
		GameContext.getCorpsApp().lock(corpsId);
		try{
			CorpsFateResult result = GameContext.getCorpsApp().corpFate(reqMsg.getType(), corpsId, roleId, reqMsg.getRoleId());
			builder.setResult(result.isSuccess());
			builder.setInfo(result.getInfo());
			builder.setRoleId(result.getFateRoleId());
			builder.setLvl(CorpsLevel.valueOf(result.getCorpsLvl()));

			if(logger.isDebugEnabled()){
				logger.debug("军团职位变化结果: {}", JSON.toJSONString(result));
			}

			if (result.isSuccess()) {
				// 发邮件
				CorpsRole cr = GameContext.getCorpsApp().getCorpsRole(corpsId, reqMsg.getRoleId());
				String jobTitle = mapJobTitle(corps, cr.getJob());
				String mailContent = String.format(Tips.CORPS_JOB_CHANGE_MAIL, corps.getName(), jobTitle);
				GameContext.getMailApp().sendMail(
						reqMsg.getRoleId(),
						Tips.CORPS_JOB_CHANGE,
						mailContent,
						0, 0, 0, 0,
						Collections.<Integer, Integer>emptyMap());
				RoleConnect rc = GameContext.getUserApp().getRoleConnect(reqMsg.getRoleId());
				if (rc != null) {
					GameContext.getMailApp().promit(rc);
				}
			}

			return builder.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

	private String mapJobTitle(CorpsInfo corps, int jobLevel) {
		switch (jobLevel) {
		case 1:
			return corps.getS1Name();
		case 2:
			return corps.getS2Name();
		case 3:
			return corps.getS3Name();
		case 4:
			return corps.getS4Name();
		case 5:
			return corps.getS5Name();
		default:
			return "";
		}
	}
}
