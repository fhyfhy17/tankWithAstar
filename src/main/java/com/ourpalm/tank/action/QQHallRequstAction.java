package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.QQHallDao;
import com.ourpalm.tank.domain.QQHallInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.CTS_QQHall_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_QQHall_MSG;

@Command(type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, id = ROLE_MSG.CMD_ID.CTS_QQHall_VALUE)
public class QQHallRequstAction implements Action<CTS_QQHall_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_QQHall_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		int roleId = connect.getRoleId();
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null)
			return null;
		String isQQHallStr = reqMsg.getIsQQHall();
		int isQQHall = Integer.parseInt(isQQHallStr);
		STC_QQHall_MSG.Builder builder = STC_QQHall_MSG.newBuilder();
		System.err.println("收到 QQHall 大厅 push  isQQHall = " + isQQHall);
		if (isQQHall == 0) {
			QQHallDao qqHallDao = GameContext.getQqHallApp().getQQHallDao();
			QQHallInfo qqHallInfo = qqHallDao.getQQHallInfo(roleId);
			qqHallInfo.setIsQQHall(0);
			qqHallDao.update(qqHallInfo);
			connect.sendMsg(builder.build());
			return builder.build();
		} else {
			GameContext.getQqHallApp().push(roleId);
		}

		return null;
	}

}
