package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.QQZoneDao;
import com.ourpalm.tank.domain.QQZoneInfo;

public class QQZoneDaoImpl extends AbstractJedisDao implements QQZoneDao {
	private final static String KEY = "QQ_ZONE_";

	@Override
	public void insert(QQZoneInfo qqZoneInfo) {
		update(qqZoneInfo);
	}

	@Override
	public void update(QQZoneInfo qqZoneInfo) {
		getClient().set(KEY + qqZoneInfo.getRoleId(), JSON.toJSONString(qqZoneInfo));
	}

	@Override
	public QQZoneInfo getQQZoneInfo(int roleId) {
		String jsonStr = getClient().get(KEY + roleId);
		QQZoneInfo qqZoneInfo = null;
		if (Util.isEmpty(jsonStr)) {
			qqZoneInfo = new QQZoneInfo();
			qqZoneInfo.setRoleId(roleId);
			qqZoneInfo.setNewBieGift(1);
			qqZoneInfo.setEveryDayGift(1);
			update(qqZoneInfo);
			jsonStr = getClient().get(KEY + roleId);
		} else {
			qqZoneInfo = JSONObject.parseObject(jsonStr, QQZoneInfo.class);
		}

		return qqZoneInfo;
	}

}
