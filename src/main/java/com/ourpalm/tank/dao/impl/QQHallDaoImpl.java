package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.dao.QQHallDao;
import com.ourpalm.tank.domain.QQHallInfo;

public class QQHallDaoImpl extends AbstractJedisDao implements QQHallDao {
	private final static String KEY = "QQ_Hall_";

	@Override
	public void insert(QQHallInfo qqHallInfo) {
		update(qqHallInfo);
	}

	@Override
	public void update(QQHallInfo qqHallInfo) {
		getClient().set(KEY + qqHallInfo.getRoleId(), JSON.toJSONString(qqHallInfo));
	}

	@Override
	public QQHallInfo getQQHallInfo(int roleId) {
		String jsonStr = getClient().get(KEY + roleId);
		QQHallInfo qqHallInfo = null;
		if (Util.isEmpty(jsonStr)) {
			qqHallInfo = new QQHallInfo();
			qqHallInfo.setRoleId(roleId);
			qqHallInfo.setNewBieGift(1);
			qqHallInfo.setEveryDayGift(1);
			update(qqHallInfo);
			jsonStr = getClient().get(KEY + roleId);
		} else {
			qqHallInfo = JSONObject.parseObject(jsonStr, QQHallInfo.class);
		}

		return qqHallInfo;
	}

}
