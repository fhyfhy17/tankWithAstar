package com.ourpalm.tank.action;

import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._201_RedPacketLogic;
import com.ourpalm.tank.domain.RedPacketInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_RED_PACKET_DRAW_MSG;
import com.ourpalm.tank.template.ActivityRedPacketDrawTemplate;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.ValueResult;

@Command(
		type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
		id = ACTIVITY_MSG.CMD_ID.CTS_RED_PACKET_DRAW_VALUE
	)
public class ActivityRedPacketDrawAction  implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		_201_RedPacketLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.RedPacket);
		if(logic == null)
			return null;
		
		ActivityTemplate template = logic.getActivityTemplate();
		if(template == null)
			return null;
		
		STC_RED_PACKET_DRAW_MSG.Builder builder = STC_RED_PACKET_DRAW_MSG.newBuilder();
		ValueResult<Integer> result = logic.draw(connect.getRoleId());
		if(result.isSuccess()) {
			RedPacketInfo info = logic.getInfo(connect.getRoleId());
			builder.setDrawCount(info.getDrawCount());
			
			ActivityRedPacketDrawTemplate rewardTemplate = logic.getDrawTemplate(result.getValue());
			if(rewardTemplate != null) {
				List<AttrUnit> attrList = rewardTemplate.getAttrList();
				if(!Util.isEmpty(attrList)) {
					for(AttrUnit attr : attrList) {
						RewardItem.Builder itemBuilder = RewardItem.newBuilder();
						itemBuilder.setId(0);
						itemBuilder.setCount(attr.getValue());
						switch(attr.getType()) {
						case gold:
							itemBuilder.setType(RewardType.GOLD);
							break;
						case iron:
							itemBuilder.setType(RewardType.IRON);
							break;
						case honor:
							itemBuilder.setType(RewardType.HONOR);
							break;
						case tankExp:
							itemBuilder.setType(RewardType.TANKEXP);
						case diamonds:
							itemBuilder.setType(RewardType.DIAMONDS);
						default:
							break;
						}
						builder.addRewards(itemBuilder);
					}
					
				}
				Map<Integer, Integer> goodsMap = rewardTemplate.getGoodsMap();
				if(!Util.isEmpty(goodsMap)) {
					for(Integer id : goodsMap.keySet()) {
						RewardItem.Builder itemBuilder = RewardItem.newBuilder();
						itemBuilder.setId(id);
						itemBuilder.setCount(goodsMap.get(id));
						itemBuilder.setType(RewardType.GOODS);
						builder.addRewards(itemBuilder);
					}
				}
			}
			
		} else {
			if(template == null || !template.isOpen()) {
				result.setInfo("活动已结束");
			}
		}
		
		builder.setSuccess(result.isSuccess());
		builder.setInfo(result.getInfo());
		return builder.build();
	}
	
}
