package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.CTS_ONE_VIEW_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_ONE_VIEW_MSG;
import com.ourpalm.tank.vo.AITankViewDeal;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;

/**
 * 传递 视野
 * 
 * @author fhy
 *
 */
@Command(type = BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, id = BATTLE_MSG.CMD_ID.CTS_ONE_VIEW_VALUE)
public class OneViewAction implements Action<CTS_ONE_VIEW_MSG> {

	@Override
	public MessageLite execute(ActionContext context, CTS_ONE_VIEW_MSG reqMsg) {

		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		int roleId = connect.getRoleId();
		boolean inclueDate = reqMsg.getInclueDate();
		List<Integer> list = null;
		if (inclueDate) {
			list = reqMsg.getTankIdsList();
		} else {
			list = new ArrayList<>();
		}

		AbstractInstance self = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if (self == null) {
			LogCore.runtime.error("视野  找不到 roleId={} 的Instance", roleId);
			return null;
		}
		MapInstance map = GameContext.getMapApp().getMapInstance(self.getMapInstanceId());
		if (map == null) {
			LogCore.runtime.error("视野  找不到 MapInstanceId {} 的map", self.getMapInstanceId());
			return null;
		}
		// 更新同组AI视野
		AITankViewDeal.INSTANCE.roleViewChange(self, inclueDate, list);
		
		Collection<AbstractInstance> tanks = map.getAllTank();
		for (AbstractInstance t : tanks) {
			if (t.getRoleId() > 0 && t.getTeam().ordinal() == self.getTeam().ordinal()) {
				STC_ONE_VIEW_MSG.Builder builder = STC_ONE_VIEW_MSG.newBuilder();
				builder.setTankId(self.getId());
				if (list.size() > 0) {
					builder.addAllTankIds(list);
					builder.setInclueDate(true);
				} else {
					builder.addAllTankIds(list);
					builder.setInclueDate(false);
				}
				RoleConnect _connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(t.getRoleId());
				if (_connect != null) {
					_connect.sendMsg(builder.build());
				}
			}
		}
		return null;
	}
}