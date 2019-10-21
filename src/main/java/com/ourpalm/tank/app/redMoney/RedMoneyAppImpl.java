package com.ourpalm.tank.app.redMoney;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.domain.LeftMoneyPackage;
import com.ourpalm.tank.domain.RedMoneyInfo;
import com.ourpalm.tank.domain.RedMoneyRole;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.ACTIVITY_MSG.REDMONEY_INFO;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_REDMONEY_GET_INFO_MSG;
import com.ourpalm.tank.message.ACTIVITY_MSG.STC_REDMONEY_RECEIVE_MSG.Builder;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.RedMoneyTemplate;
import com.ourpalm.tank.thread.ThreadId;
import com.ourpalm.tank.thread.ThreadUtil;
import com.ourpalm.tank.thread.roleThread.RedMoneyEventInfo;
import com.ourpalm.tank.thread.roleThread.RoleThread;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.RedMoneyLevelPeshe;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

public class RedMoneyAppImpl implements RedMoneyApp {
	private final static Logger logger = LogCore.runtime;
	private ActivityDao activityDao;

	private Map<Integer, RedMoneyTemplate> templateMap = new HashMap<>();
	private List<RedMoneyLevelPeshe> redMoneyLevelPesheList = new ArrayList<>();
	private static final String RED_MONEY_KEY_LOCK = "RED_MONEY_KEY_LOCK";
	
	@Override
	public void start() {
		loadShopTemplate();
	}

	@Override
	public void stop() {

	}

	private void loadShopTemplate() {
		String fileName = XlsSheetType.redMoney.getXlsFileName();
		String sheetName = XlsSheetType.redMoney.getSheetName();
		try {
			this.templateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, RedMoneyTemplate.class);
			for (RedMoneyTemplate redMoneyTemplate : templateMap.values()) {
				RedMoneyLevelPeshe peshe = new RedMoneyLevelPeshe();
				peshe.setId(redMoneyTemplate.getId());
				peshe.setGon(redMoneyTemplate.getRate());
				redMoneyLevelPesheList.add(peshe);
			}
		} catch (Exception e) {
			LogCore.startup.error("加载文件出错 file={}, sheet={}", fileName, sheetName);
		}
	}

	public ActivityDao getActivityDao() {
		return activityDao;
	}

	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	@Override
	public void get(int roleId, String uniqueId) {

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if (role == null)
			return;
		STC_REDMONEY_GET_INFO_MSG.Builder builder = STC_REDMONEY_GET_INFO_MSG.newBuilder();
		List<RedMoneyInfo> list = activityDao.getRedMoneyList(uniqueId);
		Integer bestRoleId =calcBest(list);
		for (RedMoneyInfo redMoneyInfo : list) {
			if (redMoneyInfo.getState() == 2) {
				REDMONEY_INFO.Builder infoBuilder = REDMONEY_INFO.newBuilder();
				infoBuilder.setRoleId(redMoneyInfo.getRoleId());
				infoBuilder.setRoleName(redMoneyInfo.getName());
				infoBuilder.setDia(redMoneyInfo.getDia());
				infoBuilder.setGold(redMoneyInfo.getGold());
				infoBuilder.setIron(redMoneyInfo.getIron());
				if(bestRoleId==null){
					infoBuilder.setBest(0);
				}else{
					infoBuilder.setBest(redMoneyInfo.getRoleId() == bestRoleId ? 1 : 0);
				}
				infoBuilder.setTankId(redMoneyInfo.getTankId());
				builder.addInfos(infoBuilder);
			}
		}
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		connect.sendMsg(builder.build());
	}

	private Integer calcBest(List<RedMoneyInfo> list){
		boolean ifOver =true;
		for (RedMoneyInfo r : list) {
			if(r.getState()!=2){
				ifOver=false;
			}
		}
		if(!ifOver){
			return null;
		}
		sortRedMoney(list);
		return list.get(0).getRoleId();
	}
	
	@Override
	public void updateByRobotSys(String uniqueId, int roleId) {
		GameContext.getLock().lock(RED_MONEY_KEY_LOCK + uniqueId);
		List<RedMoneyInfo> list = activityDao.getRedMoneyList(uniqueId);
	
		if (list == null) {
			return;
		}
		for (RedMoneyInfo redMoneyInfo : list) {
			if (redMoneyInfo.getRoleId() == roleId) {
				redMoneyInfo.setState(2);
				activityDao.updateRedMoneyList(uniqueId, list);
			}
		}
		GameContext.getLock().unlock(RED_MONEY_KEY_LOCK + uniqueId);
	}	
	
	@Override
	public Result getGift(int roleId, String uniqueId, Builder builder) {

			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			if (role == null)
				return Result.newFailure("角色为空");
			List<RedMoneyInfo> list = activityDao.getRedMoneyList(uniqueId);
			if (list == null) {
				return Result.newFailure("不存在该奖励");
			}
			RedMoneyInfo self = null;
			for (RedMoneyInfo redMoneyInfo : list) {
				if (redMoneyInfo.getRoleId() == roleId) {
					self = redMoneyInfo;
				}
			}
			if (self == null) {
				return Result.newFailure("不存在该奖励");
			}
			if (self.getState() == 2) {
				return Result.newFailure("已领取过");
			}
			if (self.getState() == 0) {
				return Result.newFailure("不可领取");
			}
			List<AttrUnit> attrList = new ArrayList<>();

			attrList.add(AttrUnit.build(RoleAttr.diamonds, Operation.add, self.getDia()));
			attrList.add(AttrUnit.build(RoleAttr.gold, Operation.add, self.getGold()));
			attrList.add(AttrUnit.build(RoleAttr.iron, Operation.add, self.getIron()));

			boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, attrList, OutputType.redMoney.type(), StringUtil.buildLogOrigin(uniqueId, OutputType.redMoney.getInfo()));
			if (suc) {
				builder.setDia(self.getDia());
				builder.setGold(self.getGold());
				builder.setIron(self.getIron());
				self.setState(2);
				activityDao.updateRedMoneyList(uniqueId, list);
				return Result.newSuccess();
			}

		return Result.newFailure("领取失败");
	}

	@Override
	public boolean saveRedMoney(String uniqueId, List<AbstractInstance> roleIds) {
		// 红包概率
		if (Math.random() > SysConfig.get(17) * 1.0f / 100)
			return false;
		// 随机得一个红包档
		RedMoneyLevelPeshe peshe = RandomUtil.getPeshe(redMoneyLevelPesheList);
		RedMoneyTemplate template = templateMap.get(peshe.getId());
		// 随机分配 红包
		List<RedMoneyInfo> infos = new ArrayList<>();
		LeftMoneyPackage leftMoney = new LeftMoneyPackage();
		leftMoney.setDiaSize(roleIds.size());
		leftMoney.setGoldSize(roleIds.size());
		leftMoney.setIronSize(roleIds.size());

		leftMoney.setDiaMoney(template.getDia());
		leftMoney.setGoldMoney(template.getGold());
		leftMoney.setIronMoney(template.getIron());

		for (AbstractInstance role : roleIds) {
			RedMoneyRole redMoneyRole = getRandomMoney(leftMoney, role.getRoleId());
			RedMoneyInfo info = new RedMoneyInfo();
			info.setRoleId(role.getRoleId());
			info.setDia(redMoneyRole.getDia());
			info.setGold(redMoneyRole.getGold());
			info.setIron(redMoneyRole.getIron());
			info.setTankId(role.getTemplateId());
			info.setName(role.getRoleName());
			if (role.getRoleId() <= 0) {
				RoleThread r = (RoleThread) ThreadUtil.getThread(ThreadId.ROLE);
				RedMoneyEventInfo eventInfo = new RedMoneyEventInfo();
				eventInfo.setUniqueId(uniqueId);
				eventInfo.setRoleId(role.getRoleId());
				eventInfo.setTime(System.currentTimeMillis() + RandomUtil.randomInt(5, 10) * DateUtil.SECOND);
				r.add(eventInfo);
			}else{
				info.setState(1);
			}
		
			infos.add(info);
		}

		activityDao.saveRedMoneyList(uniqueId, infos);
		return true;
	}

	public RedMoneyRole getRandomMoney(LeftMoneyPackage leftMoney, int roleId) {
		int dia = 0;
		int gold = 0;
		int iron = 0;
		// 钻石
		if (leftMoney.getDiaSize() == 1) {
			leftMoney.setDiaSize(0);
			dia = leftMoney.getDiaMoney();
		} else {
			int min = 0; //
			int max = leftMoney.getDiaMoney() / leftMoney.getDiaSize() * 2;
			int money = (int) (Math.random() * max);
			money = money <= min ? 0 : money;
			leftMoney.setDiaSize(leftMoney.getDiaSize() - 1);
			leftMoney.setDiaMoney(leftMoney.getDiaMoney() - money);
			dia = money;
		}
		// 金币
		if (leftMoney.getGoldSize() == 1) {
			leftMoney.setGoldSize(0);
			gold = leftMoney.getGoldMoney();
		} else {
			int min = 0; //
			int max = leftMoney.getGoldMoney() / leftMoney.getGoldSize() * 2;
			int money = (int) (Math.random() * max);
			money = money <= min ? 0 : money;
			leftMoney.setGoldSize(leftMoney.getGoldSize() - 1);
			leftMoney.setGoldMoney(leftMoney.getGoldMoney() - money);
			gold = money;
		}
		// 银币
		if (leftMoney.getIronSize() == 1) {
			leftMoney.setIronSize(0);
			iron = leftMoney.getIronMoney();
		} else {
			int min = 1; //
			int max = leftMoney.getIronMoney() / leftMoney.getIronSize() * 2;
			int money = (int) (Math.random() * max);
			money = money <= min ? 1 : money;
			leftMoney.setIronSize(leftMoney.getIronSize() - 1);
			leftMoney.setIronMoney(leftMoney.getIronMoney() - money);
			iron = money;
		}
		RedMoneyRole redMoneyRole = new RedMoneyRole();
		redMoneyRole.setRoleId(roleId);
		redMoneyRole.setDia(dia);
		redMoneyRole.setGold(gold);
		redMoneyRole.setIron(iron);
		return redMoneyRole;
	}

	// public static void main(String[] args) {
	// for (int i = 0; i < 1000; i++) {
	// LeftMoneyPackage leftMoney = new LeftMoneyPackage();
	// leftMoney.setDiaSize(5);
	// leftMoney.setGoldSize(5);
	// leftMoney.setIronSize(5);
	//
	// leftMoney.setDiaMoney(55);
	// leftMoney.setGoldMoney(100);
	// leftMoney.setIronMoney(1000);
	// for (int j = 0; j < 5; j++) {
	// System.out.println(JSONObject.toJSONString(getRandomMoney(leftMoney,
	// i)));
	// }
	// System.out.println();
	// }
	//
	// }
	Comparator<RedMoneyInfo> comp = new RedMoneyComparator();

	/**
	 * 红包排序
	 */
	public void sortRedMoney(List<RedMoneyInfo> list) {
		Collections.sort(list, comp);
	}


	
}
class RedMoneyComparator implements Comparator<RedMoneyInfo> {

	@Override
	public int compare(RedMoneyInfo g1, RedMoneyInfo g2) {
		if (g1.getDia()< g2.getDia()) {
			return 1;
		} else if (g1.getDia() > g2.getDia()) {
			return -1;
		} else {
			if (g1.getGold()<g2.getGold()) {
				return 1;
			} else if (g1.getGold() > g2.getGold()) {
				return -1;
			} else {
				if (g1.getIron() < g2.getIron()) {
					return 1;
				} else if (g1.getIron() >g2.getIron()) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}