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
import com.ourpalm.tank.domain.HallFirstPayInfo;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardState;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_FIRST_PAY_REWS_MSG;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.template.HallFirstPayRewardTemplate;
import com.ourpalm.tank.template.PayTemplate;
import com.ourpalm.tank.vo.AttrUnit;

/**
 * 首充奖励
 * @author wangkun
 *
 */
@Command(
	type = ACTIVITY_MSG.CMD_TYPE.CMD_TYPE_ACTIVITY_VALUE,
	id = ACTIVITY_MSG.CMD_ID.CTS_FIRST_PAY_REWS_VALUE
)
public class ActivityFirstPayAction implements Action<Object>{

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
		
		HallFirstPayInfo info = logic.getInfo(roleId);
		if(info == null) {
			return null;
		}
		
		//活动结束，且领取奖励。则不显示
		ActivityTemplate template = logic.getActivityTemplate();
		if(template == null) {
			if(info.hadReward())
				return null;
		}
		
		PayTemplate payTemplate = GameContext.getPayApp().getFirstPay();
		if(payTemplate == null)
			return null;
		
		STC_FIRST_PAY_REWS_MSG.Builder builder = STC_FIRST_PAY_REWS_MSG.newBuilder();
		
		builder.setProId(payTemplate.getId());
		builder.setName(payTemplate.getName());
		builder.setRmb(payTemplate.getRmb());
		builder.setDesc(payTemplate.getDesc());
		builder.setExtendParam(GameContext.getPayApp().getPayParam(roleId));
		
		builder.setState(RewardState.INIT);
		if(info.canDraw()) {
			builder.setState(RewardState.DRAW);
		}
		
		if(info.hadReward()) {
			builder.setState(RewardState.FINISH);
		}
		
		HallFirstPayRewardTemplate rewardTemplate = logic.getRewardTemplate();
		if(rewardTemplate.getTankId() > 0) {
			RewardItem.Builder tankItem = RewardItem.newBuilder();
			tankItem.setCount(1);
			tankItem.setId(rewardTemplate.getTankId());
			tankItem.setType(RewardType.TANK);
			builder.addRewards(tankItem);
		}
		
		builder.addAllRewards(buildAttrRewardItem(rewardTemplate.getAttrList()));
		builder.addAllRewards(buildGoodsRewardItem(rewardTemplate.getGoodsMap()));
		
		//如果时间到，没有充值，则不显示活动图标
		int remainTime = logic.getRemainTime(info.getCreateTime());
		if(remainTime <= 0) {
			if(builder.getState() == RewardState.INIT) {
				builder.setState(RewardState.FINISH);
			}
		}
		builder.setRemainTime(remainTime);
		
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
