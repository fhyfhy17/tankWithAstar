package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.QQHallDao;
import com.ourpalm.tank.dao.QQZoneDao;
import com.ourpalm.tank.domain.QQHallInfo;
import com.ourpalm.tank.domain.QQZoneInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_QQZone_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQHall_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQZone_MSG;

@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_QQZone_VALUE)
public class QQZoneRequstAction implements Action<CTS_QQZone_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_QQZone_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;
		String isQQZoneStr = reqMsg.getIsQQZone();
		int isQQZone = Integer.parseInt(isQQZoneStr);
		STC_QQZone_MSG.Builder builder = STC_QQZone_MSG.newBuilder();
		System.err.println("收到 QQZone 空间 push  isQQZone = " + isQQZone);
		if (isQQZone == 0) {
			QQZoneDao qqZoneDao = GameContext.getQqZoneApp().getQQZoneDao();
			QQZoneInfo qqZoneInfo = qqZoneDao.getQQZoneInfo(roleId);
			qqZoneInfo.setIsQQZone(0);
			qqZoneDao.update(qqZoneInfo);
			connect.sendMsg(builder.build());
			return builder.build();
		} else {
			GameContext.getQqZoneApp().push(roleId);
		}

		return null;
	}

}
