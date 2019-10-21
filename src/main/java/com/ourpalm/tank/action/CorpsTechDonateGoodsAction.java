package com.ourpalm.tank.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_DONATE_GOODS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CorpsGoodsItem;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_DONATE_GOODS_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsTechDonateResult;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_DONATE_GOODS_VALUE
)
public class CorpsTechDonateGoodsAction extends AbstractCorpAction implements Action<CTS_CORPS_DONATE_GOODS_MSG>{

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_DONATE_GOODS_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());  //判断用户是否掉线
		if(connect == null){
			return null;
		}
		int techId = reqMsg.getTechId();
		int roleId = connect.getRoleId();
		if(checkNotInCorp(roleId)){
			return STC_CORPS_DONATE_GOODS_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		Map<Integer, Integer> goodsMap = new HashMap<>();
		for(CorpsGoodsItem item : reqMsg.getGoodsItemList()){
			goodsMap.put(item.getGoodsId(), item.getGoodsNum());
		}
		int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		GameContext.getCorpsApp().lock(corpsId);
		try{
			CorpsTechDonateResult result = GameContext.getCorpsApp().techGoodsDonate(corpsId, roleId, techId, goodsMap);

			if(logger.isDebugEnabled()){
				logger.debug("捐献物品结果: {}", JSON.toJSONString(result));
			}

			return STC_CORPS_DONATE_GOODS_MSG.newBuilder()
					.setResult(result.getResult())
					.setInfo(result.getInfo())
					.setState(result.getState())
					.setCoolTime(result.getCoolTime())
					.build();
		}finally{
			GameContext.getCorpsApp().unlock(corpsId);
		}
	}

}
