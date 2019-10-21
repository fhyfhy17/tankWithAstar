package com.ourpalm.tank.action;

import java.util.Collection;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RolePay;
import com.ourpalm.tank.domain.VipInfo;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.GoodsItem;
import com.ourpalm.tank.message.ROLE_MSG.RewardState;
import com.ourpalm.tank.message.ROLE_MSG.STC_VIP_LIST_MSG;
import com.ourpalm.tank.message.ROLE_MSG.VipItem;
import com.ourpalm.tank.template.VipTemplate;

@Command(
		type = ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE,
		id = ROLE_MSG.CMD_ID.CTS_VIP_LIST_VALUE
	)
public class VipListAction implements Action<Object>{

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		Collection<VipTemplate> templates = GameContext.getVipApp().getTemplates();
		VipInfo info = GameContext.getVipApp().getVipInfo(connect.getRoleId());
		if(info == null) {
			info = new VipInfo();
		}
		
		STC_VIP_LIST_MSG.Builder builder = STC_VIP_LIST_MSG.newBuilder();
		for(VipTemplate template : templates) {
			VipItem.Builder vipBuilder = VipItem.newBuilder();
			vipBuilder.setLevel(template.getLevel());
			vipBuilder.setLimit(template.getLimit());
			vipBuilder.setState(RewardState.valueOf(info.getState(template.getLevel())));
			
			vipBuilder.setGold(template.getGold());
			vipBuilder.setIron(template.getIron());
			vipBuilder.setHonor(template.getHonor());
			vipBuilder.setRoleTankExp(template.getTankExp());
			
			Map<Integer, Integer> goodsMap = template.getGoodsMap();
			for(Integer id : goodsMap.keySet()) {
				GoodsItem.Builder itemBuilder = GoodsItem.newBuilder();
				itemBuilder.setId(id);
				itemBuilder.setNum(goodsMap.get(id));
				vipBuilder.addGoodsList(itemBuilder);
			}
			
			vipBuilder.setPrivilegeRoleTankExp(template.getPrivilegeRoleTankExp() * 100);
			vipBuilder.setPrivilegeIron(template.getPrivilegeIron() * 100);
			vipBuilder.setPrivilegeIronMax(template.getPrivilegeIronMax() * 100);
			vipBuilder.setPrivilegeTankExp(template.getPrivilegeTankExp() * 100);
			vipBuilder.setPrivilegeAlive(template.getPrivilegeAlive());
			
			builder.addVips(vipBuilder);
		}
		
		builder.setRmb(0);
		RolePay pay = GameContext.getPayApp().getRolePay(connect.getRoleId());
		if(pay != null) {
			builder.setRmb(pay.getRmb());
		}
		return builder.build();
	}

}
