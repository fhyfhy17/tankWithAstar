package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.YellowDao;
import com.ourpalm.tank.domain.PFYellowUserInfo;
import com.ourpalm.tank.domain.YellowInfo;

public class YellowDaoImpl extends AbstractJedisDao implements YellowDao {
	private final static String KEY = "YELLOW_";

	@Override
	public void insert(YellowInfo blueInfo) {
		update(blueInfo);
	}

	@Override
	public void update(YellowInfo blueInfo) {
		getClient().set(KEY + blueInfo.getRoleId(), JSON.toJSONString(blueInfo));
	}

	@Override
	public YellowInfo getYellowInfo(int roleId) {
		String jsonStr = getClient().get(KEY + roleId);
		if (Util.isEmpty(jsonStr)) {
			YellowInfo yellowInfo = new YellowInfo();
			yellowInfo.setRoleId(roleId);
			update(yellowInfo);
			jsonStr = getClient().get(KEY + roleId);
		}
		YellowInfo yellowInfo = JSON.parseObject(jsonStr, YellowInfo.class);
		PFYellowUserInfo pfi = GameContext.getUserApp().getPfYellowUserInfo(roleId);
		yellowInfo = convertPfInfo(pfi, yellowInfo);
		if (yellowInfo != null) {
			update(yellowInfo);
		}
		return yellowInfo;
	}

	@Override
	public boolean isYellow(int roleId) {
		PFYellowUserInfo pfi = GameContext.getUserApp().getPfYellowUserInfo(roleId);
		if (pfi == null) {
			return false;
		}
		int blue = pfi.getIs_yellow_vip();
		int year = pfi.getIs_yellow_year_vip();
		int superBlue = pfi.getIs_yellow_high_vip();
		if (blue == 1 || year == 1 || superBlue == 1) {
			return true;
		}
		return false;
	}

	public YellowInfo convertPfInfo(PFYellowUserInfo pf, YellowInfo yellowInfo) {
		if (pf == null) {
			return null;
		}
		int yellow = pf.getIs_yellow_vip();
		int year = pf.getIs_yellow_year_vip();
		int superBlue = pf.getIs_yellow_high_vip();
		int level = pf.getYellow_vip_level();
		if (yellowInfo.getSuperGift() != 2 && superBlue == 1) {
			yellowInfo.setSuperGift(1);
		}

		if (yellowInfo.getYearGift() != 2 && year == 1) {
			yellowInfo.setYearGift(1);
		}
		if (yellowInfo.getNewBieGift() != 2 && isYellow(yellowInfo.getRoleId())) {
			yellowInfo.setNewBieGift(1);
		}
		yellowInfo.setYellowLevel(level);
		return yellowInfo;
	}

	public static void main(String[] args) {
		System.out.println(Math.floor(-1.1));
		System.out.println(Math.ceil(1.1));
	}
}
