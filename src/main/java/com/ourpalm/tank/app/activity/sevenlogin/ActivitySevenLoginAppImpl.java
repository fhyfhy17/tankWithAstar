package com.ourpalm.tank.app.activity.sevenlogin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.ActivityDao;
import com.ourpalm.tank.domain.ActivitySevenLoginInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardItem;
import com.ourpalm.tank.message.ACTIVITY_MSG.RewardType;
import com.ourpalm.tank.message.ACTIVITY_MSG.SevenLoginDay;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.ActivitySevenLoginTemplate;
import com.ourpalm.tank.template.MemberTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.ValueResult;

public class ActivitySevenLoginAppImpl implements ActivitySevenLoginApp{
	private static Logger logger = LogCore.runtime;
	
	private Map<Integer, List<ActivitySevenLoginTemplate>> sevenDayTemplateMap;
	
	private ActivityDao activityDao;

	@Override
	public void start() {
		loadTemplate();
	}

	@Override
	public void stop() {
	}
	
	@Override
	public void create(int roleId) {
		ActivitySevenLoginInfo day = new ActivitySevenLoginInfo();
		day.increaseDay();	//初始第一天
		activityDao.saveSevenLoginInfo(roleId, day);
		
		if(logger.isDebugEnabled()){
			logger.debug("角色:{}-创建-初始化七日签到第一天可领取", roleId);
		}
	}
	
	public void loadTemplate() {
		String sourceFile = XlsSheetType.HallSevenLogin.getXlsFileName();
		String sheetName = XlsSheetType.HallSevenLogin.getSheetName();
		try {
			List<ActivitySevenLoginTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivitySevenLoginTemplate.class);
			sevenDayTemplateMap = new HashMap<>();
			for(ActivitySevenLoginTemplate t : list) {
				t.init();
				
				if(sevenDayTemplateMap.containsKey(t.getDay())) {
					sevenDayTemplateMap.get(t.getDay()).add(t);
				} else {
					List<ActivitySevenLoginTemplate> dayList = new ArrayList<>();
					dayList.add(t);
					sevenDayTemplateMap.put(t.getDay(), dayList);
				}
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public List<SevenLoginDay> getPageList(int roleId) {
		ActivitySevenLoginInfo day = activityDao.getSevenLoginInfo(roleId);
		if(day == null) {
			LogCore.runtime.info("roleId:{} 获取七日签到信息失败", roleId);
			return new ArrayList<>();
		} 
		
		//已经领取
		if(day.isHadDraw()) {
			boolean isSameDay = DateUtil.isSameDay(day.getDrawTime(), System.currentTimeMillis());
			if(!isSameDay) {
				day.increaseDay();	//下一天可领取
				if(sevenDayTemplateMap.containsKey(day.getDay())) {
					activityDao.saveSevenLoginInfo(roleId, day);
					LogCore.runtime.info("初始化第{}天可领取", day.getDay());
				}
			}
		}
		
		List<SevenLoginDay> dayList = new ArrayList<>();
		for(Integer key : sevenDayTemplateMap.keySet()) {
			SevenLoginDay.Builder dayBuilder = SevenLoginDay.newBuilder();
			List<ActivitySevenLoginTemplate> rewardList = sevenDayTemplateMap.get(key);
			for(ActivitySevenLoginTemplate t : rewardList) {
				RewardItem.Builder rewardBuilder = RewardItem.newBuilder();
				rewardBuilder.setId(t.getRewardId());
				rewardBuilder.setCount(t.getCount());
				rewardBuilder.setType(RewardType.valueOf(t.getType()));
				
				dayBuilder.addReward(rewardBuilder);
			}
			dayBuilder.setDay(key);
			dayBuilder.setCanDraw(false);
			dayBuilder.setHadDraw(false);
			
			
			if(key == day.getDay()) {
				dayBuilder.setHadDraw(day.isHadDraw());
				dayBuilder.setCanDraw(day.isHadDraw() ? false : true);
			}
			
			if(key < day.getDay())
				dayBuilder.setHadDraw(true);
			
			dayList.add(dayBuilder.build());
		}
		
		return dayList;
	}

	@Override
	public ValueResult<Integer> draw(int roleId) {
	
		ValueResult<Integer> result = new ValueResult<Integer>();
		result.setValue(0);
		ActivitySevenLoginInfo day = activityDao.getSevenLoginInfo(roleId);
		if(day == null) {
			result.failure("奖励不存在");
			return result;
		}
	

		
		if(day.isHadDraw()) {
			result.failure("奖励已领取");
			return result;
		}
		
		List<ActivitySevenLoginTemplate> rewardList = sevenDayTemplateMap.get(day.getDay());
		if(rewardList == null) {
			result.failure("奖励已过期");
			return result;
		}
		for (ActivitySevenLoginTemplate t : rewardList) {
			//如果奖励中有坦克
			if(t.getType()==1){
				RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
				Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(roleId);
				if (role.getPark() < allTankList.size() + 1) {
					result.failure("车位不足，请先购买车位");
					return result;
				}
			}
		}
		day.setHadDraw(true);
		day.setDrawTime(System.currentTimeMillis());
		
		String origin = "领取七日登录奖励第" + day.getDay() + "天";
		for(ActivitySevenLoginTemplate t : rewardList) {
			switch(t.getType()) {
			case 1:
				for(int i = 0, l = t.getCount(); i < l; i++) {
					RoleTank tank = GameContext.getTankApp().tankAdd(roleId, t.getRewardId(), origin);
					GameContext.getTankApp().tankPush(tank);
				}
				break;
			case 2:
				for(int i = 0, l = t.getCount(); i < l; i++) {
					//已拥有的成员转换成碎片
					int memberTemplateId = t.getRewardId();
					RoleMember roleMember = GameContext.getMemberApp().getRoleMember(roleId, memberTemplateId);
					if(roleMember != null){
						MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(memberTemplateId);
						GameContext.getGoodsApp().addGoods(roleId, template.getReturnGoodsId(), template.getReturnNum(), origin);
						continue;
					}
					GameContext.getMemberApp().addMember(roleId, t.getRewardId(), origin);
				}
				break;
			case 3:
				GameContext.getGoodsApp().addGoods(roleId, t.getRewardId(), t.getCount(), origin);
				break;
			case 4:
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.add, t.getCount()), OutputType.sevenLoginInc.type(), origin);
				break;
			case 5:
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, t.getCount()), OutputType.sevenLoginInc.type(), origin);
				break;
			case 6:
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.honor, Operation.add, t.getCount()), OutputType.sevenLoginInc.type(), origin);
				break;
			}
		}
		LogCore.runtime.info("今日{}", origin);
		
		activityDao.saveSevenLoginInfo(roleId, day);
		
		result.setValue(day.getDay());
		result.success();
		return result;
	}

	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

}
