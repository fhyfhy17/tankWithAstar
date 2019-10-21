package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_ONE_KEY_STRENGTHEN_MSG;
import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;
import com.ourpalm.tank.message.TANK_MSG.STC_ONE_KEY_STRENGTHEN_MSG;

/**
 * 一键强化需要金币数
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_ONE_KEY_STRENGTHEN_VALUE)
public class PartOneKeyGoldAction implements Action<CTS_ONE_KEY_STRENGTHEN_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_ONE_KEY_STRENGTHEN_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		int upLevel = reqMsg.getLevel();// 目标等级
		int tankId = reqMsg.getTankId();
		PART_INDEX partIndex = reqMsg.getPartIndex();
		STC_ONE_KEY_STRENGTHEN_MSG.Builder builder = STC_ONE_KEY_STRENGTHEN_MSG.newBuilder();
		GameContext.getTankApp().getOneKeyAllGold(roleId, builder, upLevel, tankId, partIndex);
		return builder.build();
	}

}
