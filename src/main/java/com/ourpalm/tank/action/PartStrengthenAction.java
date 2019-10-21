package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.TANK_MSG;
import com.ourpalm.tank.message.TANK_MSG.CTS_STRENGTHEN_MSG;
import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;
import com.ourpalm.tank.message.TANK_MSG.STC_STRENGTHEN_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 配件强化
 * 
 * @author fhy
 *
 */
@Command(type = TANK_MSG.CMD_TYPE.CMD_TYPE_TANK_VALUE, id = TANK_MSG.CMD_ID.CTS_STRENGTHEN_VALUE)
public class PartStrengthenAction implements Action<CTS_STRENGTHEN_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_STRENGTHEN_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		int type = reqMsg.getType();// 1银币强化2金币保成强化，3一键金币保成无CD强化
		int tankTemplateId = reqMsg.getTankId(); // 坦克模板ID
		PART_INDEX index = reqMsg.getPartIndex();// 配件位置
		int upLevel = reqMsg.getLevel();// 3type下的 要强化到的等级
		STC_STRENGTHEN_MSG.Builder builder = STC_STRENGTHEN_MSG.newBuilder();
		Result r = GameContext.getTankApp().strengthen(builder, roleId, tankTemplateId, index, type, upLevel);
		builder.setResult(r.getResult() == (byte) 1 ? 1 : 0);
		builder.setInfo(r.getInfo());
		return builder.build();
	}

}
