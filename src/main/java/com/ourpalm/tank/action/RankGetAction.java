package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_GET_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_GET_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 排行榜领取
 * 
 * @author Administrator
 *
 */
@Command(type = RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE, id = RANK_MSG.CMD_ID.CTS_RANK_GET_VALUE)
public class RankGetAction implements Action<CTS_RANK_GET_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_RANK_GET_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int type = reqMsg.getType();
		RankEnum rankEnum = RankEnum.values()[type];

		STC_RANK_GET_MSG.Builder builder = STC_RANK_GET_MSG.newBuilder();

		Result r = GameContext.getRankApp().rankGet(connect.getRoleId(), rankEnum, builder);
		builder.setSuccess(r.getResult() == (byte) 1 ? true : false);
		builder.setInfo(r.getInfo());
		return builder.build();
	}

}
