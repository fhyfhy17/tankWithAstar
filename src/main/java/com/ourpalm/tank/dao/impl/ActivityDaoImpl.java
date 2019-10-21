package com.ourpalm.tank.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.domain.ActivityDailyGrabInfo;
import com.ourpalm.tank.domain.ActivityDailyInfo;
import com.ourpalm.tank.domain.ActivityKeepOnlineInfo;
import com.ourpalm.tank.domain.ActivityMonthCardInfo;
import com.ourpalm.tank.domain.ActivityMonthLoginInfo;
import com.ourpalm.tank.domain.ActivitySevenLoginInfo;
import com.ourpalm.tank.domain.HallFirstPayInfo;
import com.ourpalm.tank.domain.RedMoneyInfo;
import com.ourpalm.tank.domain.RedPacketInfo;

public class ActivityDaoImpl extends AbstractJedisDao implements ActivityDao {
	private static final String DAILY_KEY = "ROLE_DAILY_KEY_";
	private static final String MONTH_CARD_KEY = "ROLE_MONTH_CARD_KEY_";
	private static final String RED_PACKET_KEY = "ROLE_RED_PACKET_KEY_";

	private static final String DAILY_GRAB_KEY = "ROLE_DAILY_GRAB_KEY_";
	private static final String DAILY_GRAB_SERVER_PACKET_KEY = "SERVER_DAILY_GRAB_PACKET_KEY_";

	private static final String SEVEN_LOGIN_KEY = "ROLE_SEVEN_LOGIN_KEY_";
	private static final String MONTH_LOGIN_KEY = "ROLE_MONTH_LOGIN_KEY_";

	private static final String HALL_FIRST_PAY_KEY = "ROLE_FIRST_PAY_KEY_";
	private static final String HALL_KEEP_ONLINE_KEY = "ROLE_KEEP_ONLINE_KEY_";

	private static final String RED_MONEY_KEY = "RED_MONEY_KEY_";
	private static final String RED_MONEY_KEY_LOCK = "RED_MONEY_KEY_LOCK";

	@Override
	public void saveDailyInfo(int roleId, int logicId, ActivityDailyInfo info) {
		if (info == null)
			return;

		getClient().hset(DAILY_KEY + roleId, logicId + "", JSON.toJSONString(info));
	}

	@Override
	public ActivityDailyInfo getDailyInfo(int roleId, int logicId) {
		String json = getClient().hget(DAILY_KEY + roleId, logicId + "");
		if (Util.isEmpty(json))
			return null;

		return JSON.parseObject(json, ActivityDailyInfo.class);
	}

	@Override
	public void deleteDailyInfo(int roleId, int logicId) {
		getClient().hdel(DAILY_KEY + roleId, logicId + "");
	}

	@Override
	public ActivityMonthCardInfo getMonthCardInfo(int roleId, int logicId) {
		String json = getClient().hget(MONTH_CARD_KEY + roleId, logicId + "");
		if (Util.isEmpty(json))
			return null;

		return JSON.parseObject(json, ActivityMonthCardInfo.class);
	}

	@Override
	public void saveMonthCardInfo(int roleId, int logicId, ActivityMonthCardInfo info) {
		if (info == null)
			return;

		getClient().hset(MONTH_CARD_KEY + roleId, logicId + "", JSON.toJSONString(info));
	}

	@Override
	public void saveRedPacketInfo(int roleId, int logicId, RedPacketInfo info) {
		if (info == null)
			return;

		getClient().hset(RED_PACKET_KEY + roleId, logicId + "", JSON.toJSONString(info));
	}

	@Override
	public RedPacketInfo getRedPacketInfo(int roleId, int logicId) {
		String json = getClient().hget(RED_PACKET_KEY + roleId, logicId + "");
		if (Util.isEmpty(json))
			return null;

		return JSON.parseObject(json, RedPacketInfo.class);
	}

	@Override
	public void saveDailyGrabInfo(int roleId, int logicId, ActivityDailyGrabInfo info) {
		if (info == null)
			return;

		getClient().hset(DAILY_GRAB_KEY + roleId, logicId + "", JSON.toJSONString(info));
	}

	@Override
	public ActivityDailyGrabInfo getDailyGrabInfo(int roleId, int logicId) {
		String json = getClient().hget(DAILY_GRAB_KEY + roleId, logicId + "");
		if (Util.isEmpty(json))
			return null;

		return JSON.parseObject(json, ActivityDailyGrabInfo.class);
	}

	@Override
	public void saveDailyGrabServerPacket(int count) {
		getClient().set(DAILY_GRAB_SERVER_PACKET_KEY, count + "");
	}

	@Override
	public int getDailyGrabServerPacket() {
		String str = getClient().get(DAILY_GRAB_SERVER_PACKET_KEY);
		if (Util.isEmpty(str))
			return 0;

		return Integer.parseInt(str);
	}

	@Override
	public void saveSevenLoginInfo(int roleId, ActivitySevenLoginInfo day) {
		if (day == null)
			return;

		getClient().set(SEVEN_LOGIN_KEY + roleId, JSON.toJSONString(day));
	}

	@Override
	public ActivitySevenLoginInfo getSevenLoginInfo(int roleId) {
		String json = getClient().get(SEVEN_LOGIN_KEY + roleId);
		return JSON.parseObject(json, ActivitySevenLoginInfo.class);
	}

	@Override
	public void saveMonthLoginInfo(int roleId, ActivityMonthLoginInfo info) {
		if (info == null)
			return;

		getClient().set(MONTH_LOGIN_KEY + roleId, JSON.toJSONString(info));
	}

	@Override
	public ActivityMonthLoginInfo getMonthLoginInfo(int roleId) {
		String json = getClient().get(MONTH_LOGIN_KEY + roleId);
		return JSON.parseObject(json, ActivityMonthLoginInfo.class);
	}

	@Override
	public void saveFirstPayInfo(int roleId, int logicId, HallFirstPayInfo info) {
		if (info == null)
			return;
		getClient().hset(HALL_FIRST_PAY_KEY + roleId, logicId + "", JSON.toJSONString(info));
	}

	@Override
	public HallFirstPayInfo getFirstPayInfo(int roleId, int logicId) {
		String json = getClient().hget(HALL_FIRST_PAY_KEY + roleId, logicId + "");
		return JSON.parseObject(json, HallFirstPayInfo.class);
	}

	@Override
	public void saveKeepOnlineInfo(int roleId, ActivityKeepOnlineInfo info) {
		if (info == null)
			return;
		getClient().set(HALL_KEEP_ONLINE_KEY + roleId, JSON.toJSONString(info));
	}

	@Override
	public ActivityKeepOnlineInfo getKeepOnlineInfo(int roleId) {
		String json = getClient().get(HALL_KEEP_ONLINE_KEY + roleId);
		return JSON.parseObject(json, ActivityKeepOnlineInfo.class);
	}

	@Override
	public void saveRedMoneyList(String uniqueId, List<RedMoneyInfo> infos) {
		if (infos == null)
			return;
		for (RedMoneyInfo redMoneyInfo : infos) {
			getClient().sadd(RED_MONEY_KEY + uniqueId, JSON.toJSONString(redMoneyInfo));
		}
	}

	@Override
	public List<RedMoneyInfo> getRedMoneyList(String uniqueId) {
		Set<String> lists = getClient().smembers(RED_MONEY_KEY + uniqueId);
		List<RedMoneyInfo> list = new ArrayList<RedMoneyInfo>();
		for (String str : lists) {
			list.add(JSON.parseObject(str, RedMoneyInfo.class));
		}
		return list;
	}

	@Override
	public void updateRedMoneyList(String uniqueId, List<RedMoneyInfo> infos) {
		if (infos == null)
			return;
		String key = RED_MONEY_KEY + uniqueId;
	
		// Set<String> lists = getClient().smembers(key);
		// for (String string : lists) {
		// getClient().srem(key, string);
		// }
		getClient().del(key);
		for (RedMoneyInfo redMoneyInfo : infos) {
			getClient().sadd(key, JSON.toJSONString(redMoneyInfo));
		}
	}
}
