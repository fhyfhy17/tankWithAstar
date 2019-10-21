package com.ourpalm.tank.action;

import com.google.protobuf.MessageLite;
import com.ourpalm.core.action.Action;
import com.ourpalm.core.action.ActionContext;
import com.ourpalm.core.action.Command;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.CorpsInfo;
import com.ourpalm.tank.domain.CorpsRole;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.CORPS_MSG;
import com.ourpalm.tank.message.CORPS_MSG.ApplyRst;
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_APPLY_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CorpsLevel;
import com.ourpalm.tank.message.CORPS_MSG.CorpsPlayerItem;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_APPLY_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;


@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_APPLY_VALUE
)
public class CorpsApplyAction extends AbstractCorpAction implements Action<CTS_CORPS_APPLY_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_APPLY_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}

		final int areaId = connect.getAreaId();
		final int roleId = connect.getRoleId();
		// 如果不在军团内
		if(checkNotInCorp(roleId)){
			return STC_CORPS_APPLY_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}
		
		final int corpsId = GameContext.getUserApp().getCorpsId(roleId);
		CorpsInfo corps = GameContext.getCorpsApp().getCorpsInfo(corpsId);

		STC_CORPS_APPLY_MSG.Builder builder = STC_CORPS_APPLY_MSG.newBuilder();
		GameContext.getCorpsApp().lock(areaId);
		try{
			RoleConnect rc = GameContext.getUserApp().getRoleConnect(reqMsg.getRoleId());
			// 如果拒绝
			boolean refuse = reqMsg.getRst().equals(ApplyRst.nopass);
			if (refuse) {
				// 发邮件
				String mailContent = String.format(Tips.CORPS_REFUSE_JOIN_MAIL, corps.getName());
				GameContext.getCorpsApp().removeApplyRole(corpsId, reqMsg.getRoleId());
				GameContext.getMailApp().sendMail(
						reqMsg.getRoleId(),
						mailContent,
						mailContent,
						0, 0, 0, 0,
						null);
				if (rc != null) {
					GameContext.getMailApp().promit(rc);
					rc.sendMsg(builder
							.setResult(Result.FAILURE)
							.setInfo(mailContent)
							.build());
				}

				return builder
						.setResult(Result.SUCCESS)
						.build();
			}

			int applyRoleId = reqMsg.getRoleId();
			Result result = GameContext.getCorpsApp().corpsApply(roleId, applyRoleId, corps);
			if(result.isSuccess()){
				CorpsRole corpsRole = GameContext.getCorpsApp().getCorpsRole(corpsId, applyRoleId);
				RoleAccount applyRole = GameContext.getUserApp().getRoleAccount(applyRoleId);
				CorpsPlayerItem item = CorpsPlayerItem.newBuilder()
						.setRoleId(applyRole.getRoleId())
						.setRoleName(applyRole.getRoleName())
						.setLevel(applyRole.getLevel())
						.setVipLvl(applyRole.getVipLevel())
						.setBattleScore(applyRole.getBattleScore())
						.setOffTime(this.calcOfflineTime(applyRole))
						.setCorpsLvl(CorpsLevel.valueOf(corpsRole.getJob()))
						.setContribution(corpsRole.getContribution())
						.setWeekContribution(this.getWeekContribution(corpsRole))
						.setEnterCorpsTime((int)corpsRole.getEnterTime())
						.setTankId(applyRole.getMainTankId())
						.setTitleId(applyRole.getCurrTitleId())
						.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(applyRoleId))
						.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(applyRoleId))
						.build();
				builder.setItem(item);
			}
			builder.setResult(result.getResult());
			builder.setInfo(result.getInfo());
			builder.setRoleId(applyRoleId);

			return builder.build();
		}finally{
			GameContext.getCorpsApp().unlock(areaId);
		}
	}

	//计算离线时常
	private int calcOfflineTime(RoleAccount role){
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(role.getRoleId());
		if(connect != null){
			return 0;
		}
		long offlineTimeMillis = role.getOfflineDate() != null ? role.getOfflineDate().getTime() : role.getLoginDate().getTime();
		return (int) (System.currentTimeMillis() - offlineTimeMillis) / 1000;
	}


	//返回周贡献度
	private int getWeekContribution(CorpsRole corpsRole){
		if(DateUtil.isSameWeek(corpsRole.getTechDonateTime(), System.currentTimeMillis())){
			return corpsRole.getWeekContribution();
		}
		return 0;
	}
}
