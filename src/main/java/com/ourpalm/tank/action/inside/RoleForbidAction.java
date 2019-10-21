package com.ourpalm.tank.action.inside;

import java.util.List;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ROLE_MSG.STC_EXCEP_OFFLINE_MSG;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.BanItem;
import com.ourpalm.tank.message.SERV_MSG.PTS_BAN_ROLE_MSG;

@Command(
		type = SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE,
		id = SERV_MSG.CMD_ID.PTS_BAN_ROLE_VALUE
)
public class RoleForbidAction implements Action<PTS_BAN_ROLE_MSG>{

	@Override
	public MessageLite execute(ActionContext context, PTS_BAN_ROLE_MSG reqMsg) {
		int type = reqMsg.getType();
		
		List<BanItem> items = reqMsg.getItemsList();
		for(BanItem item: items) {
			int roleId = item.getRoleId();
			RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
			if(account == null) {
				continue;
			}
			
			String beginDate = item.getBeginTime();
			String endDate = item.getEndTime();
			if(Util.isEmpty(beginDate) || Util.isEmpty(endDate))
				continue;
			
			long beginTime = DateUtil.convertDateToMillis(DateUtil.str2Date(beginDate));
			long endTime = DateUtil.convertDateToMillis(DateUtil.str2Date(endDate));
			switch(type) {
				case 1://封停角色
					account.setLoginForbidBeginTime(beginTime);
					account.setLoginForbidEndTime(endTime);
					if(account.hadLoginForbid()) {
						RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
						if(connect != null) {
							STC_EXCEP_OFFLINE_MSG.Builder builder = STC_EXCEP_OFFLINE_MSG.newBuilder();
							builder.setTips("您已被封号");
							connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_EXCEP_OFFLINE_VALUE, builder.build().toByteArray());
						}
					}
					break;
				case 2:	//禁言
					account.setTalkForbidBeginTime(beginTime);
					account.setTalkForbidEndTime(endTime);
					if(account.hadTalkForbid()){
						RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
						if(connect != null) {
							STC_EXCEP_OFFLINE_MSG.Builder builder = STC_EXCEP_OFFLINE_MSG.newBuilder();
							builder.setTips("您已被禁言");
							connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_EXCEP_OFFLINE_VALUE, builder.build().toByteArray());
						}
					}
					break;
			}
			GameContext.getUserApp().saveRoleAccount(account);
		}
		
		return null;
	}

}
