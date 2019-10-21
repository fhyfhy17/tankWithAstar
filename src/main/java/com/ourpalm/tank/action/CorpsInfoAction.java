package com.ourpalm.tank.action;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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
import com.ourpalm.tank.message.CORPS_MSG.CTS_CORPS_INFO_MSG;
import com.ourpalm.tank.message.CORPS_MSG.CorpsLevel;
import com.ourpalm.tank.message.CORPS_MSG.CorpsPlayerItem;
import com.ourpalm.tank.message.CORPS_MSG.RecruitType;
import com.ourpalm.tank.message.CORPS_MSG.STC_CORPS_INFO_MSG;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.vo.result.Result;

@Command(
	type = CORPS_MSG.CMD_TYPE.CMD_TYPE_CORPS_VALUE,
	id = CORPS_MSG.CMD_ID.CTS_CORPS_INFO_VALUE
)
public class CorpsInfoAction extends AbstractCorpAction implements Action<CTS_CORPS_INFO_MSG>{

	@Override
	public MessageLite execute(ActionContext context, CTS_CORPS_INFO_MSG reqMsg) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnect(context.getIoId());
		if(connect == null){
			return null;
		}
		
		final int roleId = connect.getRoleId();
		final int corpsId = reqMsg.getId();
		final CorpsInfo corps = GameContext.getCorpsApp().getCorpsInfo(corpsId);
		// 如果不在军团内
		if(checkNotInCorp(roleId) || corps == null){
			return STC_CORPS_INFO_MSG.newBuilder()
					.setResult(Result.FAILURE)
					.setInfo(Tips.CORPS_NO_HAD)
					.build();
		}

		STC_CORPS_INFO_MSG.Builder builder = STC_CORPS_INFO_MSG.newBuilder();
		builder.setResult(Result.SUCCESS);
		builder.setInfo("");
		builder.setDec(corps.getDec());
		builder.setName(corps.getName());
		builder.setId(corpsId);
		builder.setLevel(corps.getLevel());
		builder.setCurrPlayer(this.getCurrPlayer(corpsId));
		builder.setMaxPlayer(corps.getPlayerLimit());
		builder.setS1Name(corps.getS1Name());
		builder.setS2Name(corps.getS2Name());
		builder.setS3Name(corps.getS3Name());
		builder.setS4Name(corps.getS4Name());
		builder.setS5Name(corps.getS5Name());
		builder.setRecType(RecruitType.valueOf(corps.getRecruitType()));

		int allActive = 0;
		int allBattle = 0;
		List<CorpsRole> list = GameContext.getCorpsApp().getCorpsRoleList(corpsId);
		for(CorpsRole corpsRole : list){
			int corpsRoleId = corpsRole.getRoleId();
			if(roleId == corpsRoleId){
				builder.setSelfCorpsLvl(CorpsLevel.valueOf(corpsRole.getJob()));
				builder.setSalute(this.hadSalute(corpsRole));
			}
			RoleAccount role = GameContext.getUserApp().getRoleAccount(corpsRoleId);
			allActive += this.calcActive(corpsRole);
			allBattle += role.getBattleScore();
			
			
			CorpsPlayerItem item = CorpsPlayerItem.newBuilder()
					.setRoleId(role.getRoleId())
					.setRoleName(role.getRoleName())
					.setLevel(role.getLevel())
					.setVipLvl(role.getVipLevel())
					.setBattleScore(role.getBattleScore())
					.setOffTime(this.calcOfflineTime(role))
					.setCorpsLvl(CorpsLevel.valueOf(corpsRole.getJob()))
					.setContribution(corpsRole.getContribution())
					.setWeekContribution(this.getWeekContribution(corpsRole))
					.setEnterCorpsTime((int)corpsRole.getEnterTime())
					.setTankId(role.getMainTankId())
					.setTitleId(role.getCurrTitleId())
					.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(role.getRoleId()))
					.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(role.getRoleId()))
					.build();
			
			builder.addCorps(item);
		}
		builder.setActiveScore(allActive);
		builder.setBattleScore(allBattle);

		return builder.build();
	}

	//返回周贡献度
	private int getWeekContribution(CorpsRole corpsRole){
		if(DateUtil.isSameWeek(corpsRole.getTechDonateTime(), System.currentTimeMillis())){
			return corpsRole.getWeekContribution();
		}
		return 0;
	}

	//是否已经领取敬礼奖励
	private boolean hadSalute(CorpsRole corpsRole){
		return DateUtil.isSameDay(corpsRole.getSaluteTime(), System.currentTimeMillis());
	}


	private int getCurrPlayer(int corpsId){
		return GameContext.getCorpsApp().getCorpsRoleSize(corpsId);
	}

	//计算活跃度
	private int calcActive(CorpsRole corpsRole){
		int active = 0;
		final int nowTime = (int) TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
		for(Entry<Integer, Integer> entry : corpsRole.getActiveMap().entrySet()){
			int dateTime = entry.getKey();
			if(dateTime == nowTime || dateTime < (nowTime - 8)){
				continue;
			}
			active += entry.getValue();
		}
		return active;
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
}
