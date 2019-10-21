package com.ourpalm.tank.action;

import org.slf4j.Logger;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.CTS_TEAM_REQ_MSG;
import com.ourpalm.tank.message.MATCH_MSG.STC_TEAM_REQ_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM_REQ_FRIEND;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.message.ROLE_MSG.STC_SHOW_TIP_MSG;
import com.ourpalm.tank.template.ArmyTitleTemplate;

@Command(type = MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, id = MATCH_MSG.CMD_ID.CTS_TEAM_REQ_VALUE)
public class BattleTeamInviteAction implements Action<CTS_TEAM_REQ_MSG> {

	private Logger logger = LogCore.runtime;

	@Override
	public MessageLite execute(ActionContext context, CTS_TEAM_REQ_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if (connect == null) {
			return null;
		}

		String teamId = reqMsg.getTeamId();
		int friendRoleId = reqMsg.getFriendRoleId();
		WAR_TYPE warType = reqMsg.getWarType();
		TEAM_REQ_FRIEND  teamReqType=reqMsg.getTeamReqFriend();
		String key = BATTLE_TEAM_LOCK + teamId;
		GameContext.getLock().lock(key);
		try {
			RoleConnect friendConnect = GameContext.getUserApp().getRoleConnect(friendRoleId);
			if (friendConnect == null) {
				logger.debug("组队邀请，好友不在线 friendId = {}", friendRoleId);
				return null;
			}

			if (GameContext.getMatchApp().hasMatchQueue(friendConnect.getIoId())) {
				logger.debug("组队邀请，好友在匹配队列中 friendId = {}", friendRoleId);
				return null;
			}

			RoleAccount friendRole = GameContext.getUserApp().getRoleAccount(friendRoleId);
			if (friendRole == null || !Util.isEmpty(friendRole.getTeamId())) {
				return null;
			}

			RoleAccount role = GameContext.getUserApp().getRoleAccount(connect.getRoleId());
			if (role == null) {
				return null;
			}
			int roleTankLevel = GameContext.getTankApp().getTankTemplate(role.getMainTankId()).getLevel_i();
			int friendTankLevel = GameContext.getTankApp().getTankTemplate(friendRole.getMainTankId()).getLevel_i();
			if (!GameContext.getMatchApp().ifLevelMate(roleTankLevel, friendTankLevel)) {
				STC_SHOW_TIP_MSG.Builder builder = STC_SHOW_TIP_MSG.newBuilder();
				builder.setInfo("被邀请的玩家坦克级别不符合");
				connect.sendMsg(builder.build());
				return null;
			}
			if (warType == WAR_TYPE.RANK) {
				Integer roleTitleGrade = null;
				ArmyTitleTemplate template = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(role.getCurrTitleId());
				if (template != null) {
					roleTitleGrade = template.getTitleGrade();
				}
				Integer friendTitleGrade = null;
				ArmyTitleTemplate friendTemplate = GameContext.getRoleArmyTitleApp().getArmyTitleTemplate(friendRole.getCurrTitleId());
				if (friendTemplate != null) {
					friendTitleGrade = friendTemplate.getTitleGrade();
				}
//				if (roleTitleGrade == null || friendTitleGrade == null||roleTitleGrade!=friendTitleGrade) {
//					STC_SHOW_TIP_MSG.Builder builder = STC_SHOW_TIP_MSG.newBuilder();
//					builder.setInfo("被邀请的玩家军衔不符合");
//					connect.sendMsg(builder.build());
//					return null;
//				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("roleId = {} 邀请 friends = {} 加入队伍 warType = {} teamId = {}", connect.getRoleId(), friendRoleId, warType, teamId);
			}

			STC_TEAM_REQ_MSG msg = STC_TEAM_REQ_MSG.newBuilder().setTeamId(teamId).setReqRoleId(role.getRoleId()).setReqName(role.getRoleName()).setWarType(warType).setTeamReqFriend(teamReqType).build();
			friendConnect.sendMsg(msg);
		} finally {
			GameContext.getLock().unlock(key);
		}

		return null;
	}

}
