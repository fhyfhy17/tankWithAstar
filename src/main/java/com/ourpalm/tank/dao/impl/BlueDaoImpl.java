package com.ourpalm.tank.dao.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.BlueDao;
import com.ourpalm.tank.domain.BlueExpireInfo;
import com.ourpalm.tank.domain.BlueInfo;
import com.ourpalm.tank.domain.PFUserInfo;

public class BlueDaoImpl extends AbstractJedisDao implements BlueDao {
	private final static String KEY = "BLUE_";

	@Override
	public void insert(BlueInfo blueInfo) {
		update(blueInfo);
	}

	@Override
	public void update(BlueInfo blueInfo) {
		getClient().set(KEY + blueInfo.getRoleId(), JSON.toJSONString(blueInfo));
	}

	@Override
	public BlueInfo getBlueInfo(int roleId) {
		String jsonStr = getClient().get(KEY + roleId);
		if (Util.isEmpty(jsonStr)) {
			BlueInfo blueInfo = new BlueInfo();
			blueInfo.setRoleId(roleId);
			update(blueInfo);
			jsonStr = getClient().get(KEY + roleId);
		}
		BlueInfo blueInfo = JSON.parseObject(jsonStr, BlueInfo.class);
		PFUserInfo pfi = GameContext.getUserApp().getPfUserInfo(roleId);
		blueInfo = convertPfInfo(pfi, blueInfo);
		if (blueInfo != null) {
			update(blueInfo);
		}
		return blueInfo;
	}

	@Override
	public boolean isBlue(int roleId) {
		PFUserInfo pfi = GameContext.getUserApp().getPfUserInfo(roleId);
		if (pfi == null) {
			return false;
		}
		if (pfi.getIs_valid() == 0) {
			return false;
		}
		int blue = pfi.getIs_blue_vip();
		int year = pfi.getIs_blue_year_vip();
		int superBlue = pfi.getIs_super_blue_vip();
		int expand = pfi.getIs_expand_blue_vip();
		int mobile = pfi.getIs_mobile_blue_vip();
		if (blue == 1 || year == 1 || superBlue == 1 || expand == 1 || mobile == 1) {
			return true;
		}
		return false;
	}

	public BlueInfo convertPfInfo(PFUserInfo pf, BlueInfo blueInfo) {
		if (pf == null) {
			return null;
		}
		int blue = pf.getIs_blue_vip();
		int year = pf.getIs_blue_year_vip();
		int superBlue = pf.getIs_super_blue_vip();
		int expand = pf.getIs_expand_blue_vip();
		int mobile = pf.getIs_mobile_blue_vip();
		int level = pf.getBlue_vip_level();
		if (blueInfo.getSuperGift() != 2 && superBlue == 1) {
			blueInfo.setSuperGift(1);
		}

		if (blueInfo.getYearGift() != 2 && year == 1) {
			blueInfo.setYearGift(1);
		}
		if (blueInfo.getNewBieGift() != 2 && isBlue(blueInfo.getRoleId())) {
			blueInfo.setNewBieGift(1);
		}
		blueInfo.setBlueLevel(level);
		return blueInfo;
	}

	@Override
	public BlueExpireInfo getBlueExpireInfo(int roleId) {
		PFUserInfo pf = GameContext.getUserApp().getPfUserInfo(roleId);
		// 测试代码
		if (GameContext.getTxTestUserInfoOpen() == 1) {
			String s = GameContext.getTxTestBlueUserInfo();
			pf = JSONObject.parseObject(s, PFUserInfo.class);
		}
		if (pf == null) {
			return null;
		}
		BlueExpireInfo be = new BlueExpireInfo();
		int serverTime = (int) pf.getServer_time();
		double vip = (pf.getVip_valid_time() - serverTime) * 1.0 / DateUtil.SECOND_TO_DAY;
		double year = (pf.getYear_vip_valid_time() - serverTime) * 1.0 / DateUtil.SECOND_TO_DAY;
		double sper = (pf.getSuper_vip_valid_time() - serverTime) * 1.0 / DateUtil.SECOND_TO_DAY;
		double expand = (pf.getExpand_vip_valid_time() - serverTime) * 1.0 / DateUtil.SECOND_TO_DAY;

		if (Math.abs(sper) < 3) {
			be.setType(1);
			if (sper < 0) {
				be.setDay((int) Math.floor(sper));
			} else {
				be.setDay((int) Math.ceil(sper));
			}
			return be;
		}

		if (Math.abs(year) < 3) {
			be.setType(1);
			if (year < 0) {
				be.setDay((int) Math.floor(year));
			} else {
				be.setDay((int) Math.ceil(year));
			}
			return be;
		}

		if (Math.abs(vip) < 3) {
			be.setType(1);

			if (vip < 0) {
				be.setDay((int) Math.floor(vip));
			} else {
				be.setDay((int) Math.ceil(vip));
			}
			return be;
		}

		// if (Math.abs(expand) < 3) {
		// be.setType(1);
		// if (expand < 0) {
		// be.setDay((int) Math.floor(expand));
		// } else {
		// be.setDay((int) Math.ceil(expand));
		// }
		// return be;
		// }

		// private int server_time;// 服务器时间，用于比较蓝钻开通时间和到期时间，unix时间戳，单位为秒
		// private int vip_valid_time;// 蓝钻到期时间，unix时间戳，单位为秒
		// private int year_vip_valid_time;// 年费蓝钻到期时间，unix时间戳，单位为秒
		// private int super_vip_valid_time;// 豪华蓝钻到期时间，unix时间戳，单位为秒
		// private int expand_vip_valid_time;// 超级蓝钻到期时间，unix时间戳，单位为秒

		return null;
	}

	public static void main(String[] args) {
		System.out.println(Math.floor(-1.1));
		System.out.println(Math.ceil(1.1));
	}
}
