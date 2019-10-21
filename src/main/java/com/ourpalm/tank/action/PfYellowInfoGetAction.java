package com.ourpalm.tank.action;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_GetYellowPfinfoStr_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_GetYellowPfinfoStr_MSG;

@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_GetYellowPfinfoStr_VALUE)
public class PfYellowInfoGetAction implements Action<CTS_GetYellowPfinfoStr_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_GetYellowPfinfoStr_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		System.err.println("收到前端 请求刷新最新 黄钻pf 请求 roleId=" + roleId);
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;
		STC_GetYellowPfinfoStr_MSG.Builder builder = STC_GetYellowPfinfoStr_MSG.newBuilder();
		String pfInfoStr = "";
		PFYellowUserInfo pfInfo = GameContext.getUserApp().getRefreshedPfYellow(roleId);
		if (pfInfo != null) {
			pfInfoStr = JSONObject.toJSONString(pfInfo);
		}
		builder.setInfo(pfInfoStr);
		return builder.build();
	}

}
