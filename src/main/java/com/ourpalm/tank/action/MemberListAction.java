package com.ourpalm.tank.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.message.MEMBER_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.MemberItem;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEMBER_LIST_MSG;
import com.ourpalm.tank.message.MEMBER_MSG.UseMemberItem;

@Command(
	type = MEMBER_MSG.CMD_TYPE.CMD_TYPE_MEMBER_VALUE,
	id = MEMBER_MSG.CMD_ID.CTS_MEMBER_LIST_VALUE
)
public class MemberListAction implements Action<Object> {

	@Override
	public MessageLite execute(ActionContext context, Object reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}
		
		
		STC_MEMBER_LIST_MSG.Builder builder = STC_MEMBER_LIST_MSG.newBuilder();
		builder.addAllUseMembers(buildUseMembers(connect.getRoleId()));
		builder.addAllMembers(buildMembers(connect.getRoleId()));
		return builder.build();
	}
	
	private List<UseMemberItem> buildUseMembers(int roleId) {
		List<UseMemberItem> result = new ArrayList<>();
		Map<Integer, String> useMembers = GameContext.getMemberApp().getUseMemberId(roleId);
		for(Map.Entry<Integer, String> entry : useMembers.entrySet()){
			UseMemberItem.Builder builder = UseMemberItem.newBuilder();
			builder.setType(entry.getKey());
			builder.setInstanceId(entry.getValue());
			result.add(builder.build());
		}
		return result;
	}
	
	private List<MemberItem> buildMembers(int roleId) {
		List<MemberItem> result = new ArrayList<>();
		Map<String, RoleMember> members = GameContext.getMemberApp().getRoleMembers(roleId);
		for(RoleMember member : members.values()){
			result.add(GameContext.getMemberApp().buildMemberItem(member));
		}
		return result;
	}

}
