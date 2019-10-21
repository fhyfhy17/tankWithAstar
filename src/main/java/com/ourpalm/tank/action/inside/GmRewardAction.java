package com.ourpalm.tank.action.inside;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.STC_TIPS_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.GMTS_SEND_REWARD_MSG;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

@Command(
	type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, 
	id = SERV_MSG.CMD_ID.GMTS_SEND_REWARD_VALUE
)
public class GmRewardAction implements Action<GMTS_SEND_REWARD_MSG>{

	@Override
	public MessageLite execute(ActionContext context, GMTS_SEND_REWARD_MSG reqMsg) {
		int roleId = reqMsg.getRoleId();
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if(connect == null) {
			return null;
		}
		
		if(reqMsg.getGoodsId() > 0 && reqMsg.getGoodsNum() > 0)
			GameContext.getGoodsApp().addGoods(roleId, reqMsg.getGoodsId(), reqMsg.getGoodsNum(), OutputType.gmRewardInc.getInfo());
		
		if(reqMsg.getGold() > 0 || reqMsg.getIron() > 0) {
			UserAttr userAttr = GameContext.getUserAttrApp().getUserAttr(roleId);
			if(userAttr != null) {
				List<AttrUnit> list = new ArrayList<>();
				list.add(AttrUnit.build(RoleAttr.gold, Operation.add, reqMsg.getGold()));
				list.add(AttrUnit.build(RoleAttr.iron, Operation.add, reqMsg.getIron()));
				GameContext.getUserAttrApp().changeAttribute(roleId, list, OutputType.gmRewardInc);
			}
		}
		LogCore.runtime.info("收到gm发送的奖励, roleid = {}, goodsId={}, goodsNum={}, gold={}, iron={}", 
					roleId, reqMsg.getGoodsId(), reqMsg.getGoodsNum(), reqMsg.getGold(), reqMsg.getIron());
		
		String tips = reqMsg.getGmTip();
		if(Util.isEmpty(tips)){
			return null;
		}
		//通知玩家
		STC_TIPS_MSG tipsMsg = STC_TIPS_MSG.newBuilder()
				.setTips(reqMsg.getGmTip())
				.build();
		connect.sendMsg(tipsMsg);
		
		return null;
	}

}
