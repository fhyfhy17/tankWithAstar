package com.ourpalm.tank.action.inside;

import java.util.Collection;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.STC_NOTICE_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.GMTS_NOTICE_MSG;

@Command(
		type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
		id = SERV_MSG.CMD_ID.GMTS_NOTICE_VALUE
)
public class GmNoticeAction implements Action<GMTS_NOTICE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, GMTS_NOTICE_MSG reqMsg) {
		int areaId = reqMsg.getAreaId();
		int playCount = reqMsg.getPlayCount();
		int gap = reqMsg.getGap();
		String notice = reqMsg.getNotice();
		if(Util.isEmpty(notice))
			return null;
		
		if(playCount <= 0)
			return null;
		
		if(gap < 0)
			return null;
		
		STC_NOTICE_MSG.Builder builder = STC_NOTICE_MSG.newBuilder();
		builder.setNotice(notice);
		builder.setPlayCount(playCount);
		builder.setGap(gap);
		
		Collection<RoleConnect> connectList = GameContext.getOnlineCenter().getAllRoleConnect();
		for(RoleConnect connect : connectList) {
			if(connect != null && connect.getAreaId() == areaId) {
				connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_NOTICE_VALUE, builder.build().toByteArray());
			}
		}
		
		return null;
	}

}
