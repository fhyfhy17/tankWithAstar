package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.RANK_MSG;
import com.ourpalm.tank.message.RANK_MSG.CTS_RANK_SEARCH_MSG;
import com.ourpalm.tank.message.RANK_MSG.STC_RANK_SEARCH_MSG;

/**
 * 搜索玩家 排行榜
 * 
 * @author Administrator
 *
 */
@Command(type = RANK_MSG.CMD_TYPE.CMD_TYPE_RANK_VALUE, id = RANK_MSG.CMD_ID.CTS_RANK_SEARCH_VALUE)
public class RankSearchAction implements Action<CTS_RANK_SEARCH_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_RANK_SEARCH_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		String name = reqMsg.getName();
		int type = reqMsg.getType();
		RankEnum rankEnum = RankEnum.values()[type];
		STC_RANK_SEARCH_MSG.Builder builder = STC_RANK_SEARCH_MSG.newBuilder();
		int roleId = GameContext.getUserApp().getRoleId(name);
		if (roleId == -1) {
			builder.setIsFinded("0");
			return builder.build();
		}
		int result = GameContext.getRankApp().searchRole(roleId, builder, rankEnum);
		if (result == 0) {
			builder.setIsFinded("0");
			return builder.build();
		}
		return builder.build();
	}

}
