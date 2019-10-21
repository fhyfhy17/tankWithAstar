package com.ourpalm.tank.action;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_JOIN_MSG;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_JOIN_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.CorpsResult;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_JOIN_VALUE
)
public class CorpsJoinAction extends AbstractCorpAction implements Action<CTS_CORPS_JOIN_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_JOIN_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		final int areaId = connect.getAreaId();
		final int roleId = connect.getRoleId();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);

		GameContext.getCorpsApp().lock(areaId);
		try{
			CorpsResult result = GameContext.getCorpsApp().joinCorps(reqMsg.getId(), roleId);
			STC_CORPS_JOIN_MSG msgBuilder = STC_CORPS_JOIN_MSG.newBuilder()
					.setResult(result.getResult())
					.setInfo(result.getInfo())
					.setId(result.getCorpsId())
					.build();

			boolean inApplyList = GameContext.getCorpsApp().hasExistCorpsApply(reqMsg.getId(), roleId);
			if (inApplyList) {
				// 自己进军团申请列表了，发消息给管理员
				Set<Integer> admins = new HashSet<>();
				for (CorpsRole corpsRole : GameContext.getCorpsApp().getCorpsRoleList(reqMsg.getId())) {
					if (corpsRole.getJob() < 3) {
						admins.add(corpsRole.getRoleId());
					}
				}

				String mailContent = String.format(Tips.CORPS_SOMEONE_APPLY, role.getRoleName());

				RoleConnect adminConnect;
				for (Integer adminId : admins) {
					// 发邮件
					GameContext.getMailApp().sendMail(
							adminId,
							mailContent,
							mailContent,
							0, 0, 0, 0,
							Collections.<Integer, Integer>emptyMap());
					adminConnect = GameContext.getUserApp().getRoleConnect(adminId);
					if (adminConnect != null) {
						GameContext.getMailApp().promit(adminConnect);
					}
				}
			}

			return msgBuilder;
		}finally{
			GameContext.getCorpsApp().unlock(areaId);
		}
	}

}
