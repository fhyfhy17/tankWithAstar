package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MEMBERNEW_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.CTS_MEMBER_NEW_LEVEL_UP_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_LEVEL_UP_MSG;
import com.ourpalm.tank.vo.result.Result;

/**
 * 新乘员，升级
 * 
 * @author fhy
 *
 */
@Command(type = MEMBERNEW_MSG.CMD_TYPE.CMD_TYPE_MEMBERNEW_VALUE, id = MEMBERNEW_MSG.CMD_ID.CTS_MEMBER_NEW_LEVEL_UP_VALUE)
public class MemberNewUpLevelAction implements Action<CTS_MEMBER_NEW_LEVEL_UP_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_MEMBER_NEW_LEVEL_UP_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		String uniqueId = reqMsg.getUniqueId();

		STC_MEMBER_NEW_LEVEL_UP_MSG.Builder builder = STC_MEMBER_NEW_LEVEL_UP_MSG.newBuilder();

		Result r = GameContext.getMemberNewApp().upLevel(connect.getRoleId(), uniqueId, getEatList(reqMsg), getGoodsMap(reqMsg));
		builder.setSuccess(r.getResult());
		builder.setInfo(r.getInfo());
		return null;
	}

	private List<String> getEatList(CTS_MEMBER_NEW_LEVEL_UP_MSG msg) {
		List<String> result = new ArrayList<>();
		if (Util.isEmpty(msg.getEatUniqueIdIdsList())) {
			return result;
		}

		for (String item : msg.getEatUniqueIdIdsList()) {
			result.add(item);
		}
		return result;
	}

	private Map<Integer, Integer> getGoodsMap(CTS_MEMBER_NEW_LEVEL_UP_MSG msg) {
		Map<Integer, Integer> result = new HashMap<>();
		if (Util.isEmpty(msg.getGoodsItemsList())) {
			return result;
		}

		for (com.ourpalm.tank.message.MEMBERNEW_MSG.GoodsItem item : msg.getGoodsItemsList()) {
			result.put(item.getGoodsId(), item.getNum());
		}
		return result;
	}
}