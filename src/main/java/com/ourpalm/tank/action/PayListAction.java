package com.ourpalm.tank.action;

import java.util.Collections;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PayItem;
import com.ourpalm.tank.message.ROLE_MSG.STC_PAY_LIST_MSG;
import com.ourpalm.tank.template.PayTemplate;
import com.ourpalm.tank.template.VipTemplate;
import com.ourpalm.tank.vo.PayExtendParam;

@Command(
	type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
	id = ROLE_MSG.CMD_ID.CTS_PAY_LIST_VALUE
)
public class PayListAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int roleId = connect.getRoleId();
		RolePay rolePay = GameContext.getPayApp().getRolePay(roleId);
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int curVipLvl = role.getVipLevel();
		
		VipTemplate currVipTemplate = GameContext.getVipApp().getVipTemplate(curVipLvl);
		VipTemplate nextVipTemplate = GameContext.getVipApp().getVipTemplate(curVipLvl + 1);
		
		STC_PAY_LIST_MSG.Builder builder = STC_PAY_LIST_MSG.newBuilder();
		builder.setCurVipLvl(role.getVipLevel());
		builder.setNextVipLvl(nextVipTemplate == null ? curVipLvl : nextVipTemplate.getLevel());
		
		int curLimit = currVipTemplate == null ? 0 : currVipTemplate.getLimit();
		builder.setCurPayRmb(rolePay.getRmb() - curLimit);
		builder.setNextPayRmb(nextVipTemplate == null ? curLimit : nextVipTemplate.getLimit() - curLimit);
		
		PayExtendParam param = new PayExtendParam();
		param.setIoId(connect.getIoId());
		param.setNodeName(GameContext.getLocalNodeName());
		String strParam = param.decode();
		
		List<PayTemplate> allTemplates = GameContext.getPayApp().getAllPayTemplate();
		Collections.sort(allTemplates);
		for(PayTemplate template : allTemplates){
			PayItem item = PayItem.newBuilder()
					.setId(template.getId())
					.setName(template.getName())
					.setRmb(template.getRmb())
					.setGold(template.getShowDiamond())
					.setParam(strParam)
					.setIcon(template.getIcon())
					.setDesc(template.getDesc())
					.setFirst(rolePay.hadFirst(template.getId()))
					.build();
			builder.addItems(item);
		}
		
		return builder.build();
	}

}
