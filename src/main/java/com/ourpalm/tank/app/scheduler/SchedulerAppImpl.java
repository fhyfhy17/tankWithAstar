package com.ourpalm.tank.app.scheduler;

import java.util.Collection;

import org.quartz.Scheduler;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.constant.ConstantRedis;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.SERV_MSG;

public class SchedulerAppImpl implements SchedulerApp {

	private Scheduler scheduler;

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public void start() {
		try {
			scheduler.start();
		} catch (Exception e) {
			LogCore.startup.error("start SchedulerException ", e);
		}
	}

	@Override
	public void stop() {
		try {
			scheduler.shutdown();
		} catch (Exception e) {
			LogCore.startup.error("", e);
		}
	}

	@Override
	public void update() {
		/******** 执行系统刷新 ********/
		// 商城更新
		// try{
		// GameContext.getShopApp().refreshSpecialItemList();
		// }catch(Exception ex){
		// LogCore.runtime.error("", ex);
		// }

		// 赛季更新
		try {
			GameContext.getRoleArmyTitleApp().seasonMathUpdate();
		} catch (Exception ex) {
			LogCore.runtime.error("", ex);
		}

		// 打印排行榜
		try {
			GameContext.getSeasonRankApp().printRank();
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}

		// 清空排行榜 和 更新发奖信息（每天）
		try {
			boolean ifChangeTheTime = false;
			GameContext.getLock().lock(ConstantRedis.RANK_UPDATE_LOCK);
			try {
				String updateTimeStr = GameContext.getLock().get(ConstantRedis.RANK_UPDATE_TIME);
				long now = System.currentTimeMillis();
				if (updateTimeStr == null || "".equals(updateTimeStr)) {
					GameContext.getLock().set(ConstantRedis.RANK_UPDATE_TIME, String.valueOf(now));
					ifChangeTheTime = true;
				} else {
					long updateTime = Long.parseLong(updateTimeStr);
					if (!DateUtil.isSameDay(now, updateTime)) {
						if (updateTime < now) {
							GameContext.getLock().set(ConstantRedis.RANK_UPDATE_TIME, String.valueOf(now));
							ifChangeTheTime = true;
						}
					}
				}
			} finally {
				GameContext.getLock().unlock(ConstantRedis.RANK_UPDATE_LOCK);
			}
			if (ifChangeTheTime) {
				GameContext.getRankApp().setRoleGiftState(1);
			}
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}

		// 刷新玩家数据
		Collection<RoleConnect> allConnects = GameContext.getOnlineCenter().getAllRoleConnect();
		if (Util.isEmpty(allConnects)) {
			return;
		}

		// 活动自身更新
		try {
			GameContext.getActivityApp().refreshServerAM0();
		} catch (Exception ex) {
			LogCore.runtime.error("", ex);
		}

		for (RoleConnect connect : allConnects) {
			Message msg = new Message();
			msg.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
			msg.setCmdId((byte) SERV_MSG.CMD_ID.ITS_SYS_REFRESH_VALUE);
			msg.setIoId(connect.getIoId());
			msg.setFromNode(GameContext.getLocalNodeName());
			msg.setData(null);

			GameContext.getMessageHandler().messageReceived(msg);
		}
	}

	@Override
	public void updateWeek() {
		System.err.println("每周一触发" + "   " + DateUtil.millsToString(System.currentTimeMillis()));
		// 清空排行榜 和 更新发奖信息（每周）
		try {
			boolean ifChangeTheTime = false;
			GameContext.getLock().lock(ConstantRedis.RANK_UPDATE_WEEK_LOCK);
			try {
				String updateTimeStr = GameContext.getLock().get(ConstantRedis.RANK_UPDATE_WEEK_TIME);
				long now = System.currentTimeMillis();
				if (updateTimeStr == null || "".equals(updateTimeStr)) {
					GameContext.getLock().set(ConstantRedis.RANK_UPDATE_WEEK_TIME, String.valueOf(now));
					ifChangeTheTime = true;
				} else {
					long updateTime = Long.parseLong(updateTimeStr);
					if (!DateUtil.isSameWeek(now, updateTime)) {
						if (updateTime < now) {
							GameContext.getLock().set(ConstantRedis.RANK_UPDATE_WEEK_TIME, String.valueOf(now));
							ifChangeTheTime = true;
						}
					}
				}
			} finally {
				GameContext.getLock().unlock(ConstantRedis.RANK_UPDATE_WEEK_LOCK);
			}
			if (ifChangeTheTime) {
				GameContext.getRankApp().setRoleGiftState(2);
			}
		} catch (Exception e) {
			LogCore.runtime.error("", e);
		}
	}

	// @Override
	// public void addToScheduler(JobDetail jobDetail, Trigger trigger) throws
	// Exception {
	// this.scheduler.scheduleJob(jobDetail, trigger);
	// }

}
