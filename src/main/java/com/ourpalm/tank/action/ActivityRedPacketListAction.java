package com.ourpalm.tank.action;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._201_RedPacketLogic;
import com.ourpalm.tank.domain.RedPacketInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.RedPacketNode;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_RED_PACKET_LIST_MSG;
import com.ourpalm.tank.template.ActivityRedPacketNodeTemplate;
import com.ourpalm.tank.template.ActivityTemplate;

@Command(
		type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
		id = ACTIVITY_MSG.CMD_ID.CTS_RED_PACKET_LIST_VALUE
	)
public class ActivityRedPacketListAction  implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		_201_RedPacketLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.RedPacket);
		if(logic == null)
			return null;
		
		RedPacketInfo info = logic.getInfo(connect.getRoleId());
		if(info == null)
			return null;
		
		ActivityTemplate template = logic.getActivityTemplate();
		if(!info.canDraw() && template == null)
			return null;
		
		List<ActivityRedPacketNodeTemplate> nodeTemplates = logic.getNodeTemplate();
		
		STC_RED_PACKET_LIST_MSG.Builder builder = STC_RED_PACKET_LIST_MSG.newBuilder();
		builder.setRmb(info.getRmb());
		builder.setDrawCount(info.getDrawCount());
		builder.setDesc(template.getDesc());
		builder.setRechargeTip(0);
		
		int remainDay =(int)((template.getEndTime() - System.currentTimeMillis())/1000);
		if(remainDay < 0)
			remainDay = 0;
		builder.setTime(remainDay);
		
		int rmb = info.getRmb();
		boolean tmp = false;
		for(ActivityRedPacketNodeTemplate nodeT : nodeTemplates) {
			if(!tmp && nodeT.getRmbLimit() > rmb) {
				tmp = true;
				builder.setRechargeTip(nodeT.getRmbLimit() - rmb);
			}
			
			RedPacketNode.Builder nodeBuilder = RedPacketNode.newBuilder();
			nodeBuilder.setId(nodeT.getId());
			nodeBuilder.setRmb(nodeT.getRmbLimit());
			nodeBuilder.setDrawCount(nodeT.getDrawCount());
			builder.addNodes(nodeBuilder);
		}
		
		return builder.build();
	}
	
}
