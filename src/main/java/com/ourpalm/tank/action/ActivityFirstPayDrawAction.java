package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._203_FirstPayLogic;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_FIRST_PAY_DRAW_MSG;
import com.ourpalm.tank.template.HallFirstPayRewardTemplate;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

/**
 * 首充奖励
 * @author wangkun
 *
 */
@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_FIRST_PAY_DRAW_VALUE
)
public class ActivityFirstPayDrawAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		int roleId = connect.getRoleId();
		
		_203_FirstPayLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.FirstPay);
		if(logic == null) {
			return null;
		}
		
		HallFirstPayRewardTemplate rewardTemplate = logic.getRewardTemplate();
		Result result = logic.reward(roleId);
		
		STC_FIRST_PAY_DRAW_MSG.Builder builder = STC_FIRST_PAY_DRAW_MSG.newBuilder();
		builder.setInfo(result.getInfo());
		builder.setSuccess(result.isSuccess());
		
		if(result.isSuccess()) {
			if(rewardTemplate.getTankId() > 0) {
				RewardItem.Builder tankItem = RewardItem.newBuilder();
				tankItem.setCount(1);
				tankItem.setId(rewardTemplate.getTankId());
				tankItem.setType(RewardType.TANK);
				builder.addRewards(tankItem);
			}
			
			builder.addAllRewards(buildAttrRewardItem(rewardTemplate.getAttrList()));
			builder.addAllRewards(buildGoodsRewardItem(rewardTemplate.getGoodsMap()));
		}
		
		return builder.build();
	}
	
	private List<RewardItem> buildAttrRewardItem(List<AttrUnit> roleAttrList) {
		List<RewardItem> result = new ArrayList<>();
		for(AttrUnit attr : roleAttrList) {
			RewardItem.Builder itemBuilder = RewardItem.newBuilder();
			itemBuilder.setCount(attr.getValue());
			itemBuilder.setId(0);
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
				break;
			case exp:
				itemBuilder.setType(RewardType.EXP);
				break;
			case diamonds:
				itemBuilder.setType(RewardType.DIAMONDS);
				break;
			default:
				break;
			}
			result.add(itemBuilder.build());
		}
		return result;
	}
	
	private List<RewardItem> buildGoodsRewardItem(Map<Integer, Integer> goodsMap) {
		List<RewardItem> result = new ArrayList<>();
		for(Integer key : goodsMap.keySet()) {
			int value = goodsMap.get(key);
			RewardItem.Builder itemBuilder = RewardItem.newBuilder();
			itemBuilder.setCount(value);
			itemBuilder.setId(key);
			itemBuilder.setType(RewardType.GOODS);
			
			result.add(itemBuilder.build());
		}
		
		return result;
	}

}
