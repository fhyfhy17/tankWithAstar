package com.ourpalm.tank.action.inside;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.PayStateEnum;
import com.ourpalm.tank.domain.PayOrderInfo;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.PTS_PAY_RESULT_MSG;
import com.ourpalm.tank.message.SERV_MSG.STP_TX_ACTIVE_RESULT_MSG;

@Command(type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE, id = SERV_MSG.CMD_ID.PTS_PAY_RESULT_VALUE)
public class PayResultAction implements Action<PTS_PAY_RESULT_MSG> {

	@Override
	public MessageLite execute(ActionContext context, PTS_PAY_RESULT_MSG req) {
		PayOrderInfo order = new PayOrderInfo();
		order.setType(req.getType());
		order.setRoleId(req.getRoleId());
		order.setChannelId(req.getChannelId());
		order.setToken(req.getOrderId());
		order.setLogTime(System.currentTimeMillis());
		order.setItemId(req.getPropId());
		order.setActualPrice(req.getActualPrice());
		order.setRebateType(req.getRebateType());
		order.setRebatePrice(req.getRebatePrice());
		order.setRebateGoodsId(req.getRebateGoodsId());
		order.setPubacctPayAmt(req.getPubacctPayAmt());
		order.setItemNum(req.getItemNum());
		PayStateEnum p = null;
		if (order.getType() == 1) {
			// 蓝钻
			p = GameContext.getPayApp().blueRenew(order);
		} else if (order.getType() == 3) {
			// 充值
			p = GameContext.getPayApp().pay(order);
		}
		if (p != null) {
			STP_TX_ACTIVE_RESULT_MSG.Builder builder = STP_TX_ACTIVE_RESULT_MSG.newBuilder();
			builder.setOrderId(order.getToken());
			builder.setResult(p == PayStateEnum.SUCCESS ? "1" : "0");
			builder.setType(order.getType());
			builder.setFailType(p.ordinal());
		}

		return null;
	}

}
