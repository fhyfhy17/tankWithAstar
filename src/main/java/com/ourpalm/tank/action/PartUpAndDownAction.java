package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_UP_DOWN_MSG;
import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;
import com.ourpalm.tank.message.TANK_MSG.STC_UP_DOWN_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 装和卸
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_UP_DOWN_VALUE)
public class PartUpAndDownAction implements Action<CTS_UP_DOWN_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_UP_DOWN_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		int type = reqMsg.getType();// 1装 2 卸
		int tankTemplateId = reqMsg.getTankId();// 坦克模板ID
		PART_INDEX index = reqMsg.getPartIndex();// 配件位置
		STC_UP_DOWN_MSG.Builder builder = STC_UP_DOWN_MSG.newBuilder();

		Result r = GameContext.getTankApp().upDownPart(builder, tankTemplateId, index, roleId, type);
		builder.setResult(r.getResult() == (byte) 1 ? 1 : 0);
		builder.setInfo(r.getInfo());
		return builder.build();
	}

}
