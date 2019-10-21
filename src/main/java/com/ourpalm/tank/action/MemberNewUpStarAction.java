package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBERNEW_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.CTS_MEMBER_NEW_STAR_UP_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_STAR_UP_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 新乘员，升星
 * 
 * @author fhy
 *
 */
@Command(type = MEMBERNEW_MSG.CMD_TYPE.CMD_TYPE_MEMBERNEW_VALUE, id = MEMBERNEW_MSG.CMD_ID.CTS_MEMBER_NEW_STAR_UP_VALUE)
public class MemberNewUpStarAction implements Action<CTS_MEMBER_NEW_STAR_UP_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_NEW_STAR_UP_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		String uniqueId = reqMsg.getUniqueId();

		STC_MEMBER_NEW_STAR_UP_MSG.Builder builder = STC_MEMBER_NEW_STAR_UP_MSG.newBuilder();

		Result r = GameContext.getMemberNewApp().upStar(connect.getRoleId(), uniqueId, getEatList(reqMsg), builder);
		builder.setSuccess(r.getResult());
		builder.setInfo(r.getInfo());
		return null;
	}

	private List<String> getEatList(CTS_MEMBER_NEW_STAR_UP_MSG msg) {
		List<String> result = new ArrayList<>();
		if (Util.isEmpty(msg.getEatUniqueIdIdsList())) {
			return result;
		}

		for (String item : msg.getEatUniqueIdIdsList()) {
			result.add(item);
		}
		return result;
	}

}