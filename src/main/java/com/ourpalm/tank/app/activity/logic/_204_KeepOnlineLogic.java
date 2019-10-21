package com.ourpalm.tank.app.activity.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.ActivityKeepOnlineInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.ActivityKeepOnlineTemplate;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

public class _204_KeepOnlineLogic extends HallActivityLogicAdapter {

	private Map<Integer, ActivityKeepOnlineTemplate> vipMapTemplate = new HashMap<>();
	
	@Override
	public void start() {
		loadTemplate();
	}
	
	private void loadTemplate() {
		String sourceFile = XlsSheetType.HallKeepOnline.getXlsFileName();
		String sheetName = XlsSheetType.HallKeepOnline.getSheetName();
		try {
			List<ActivityKeepOnlineTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, ActivityKeepOnlineTemplate.class);
			for(ActivityKeepOnlineTemplate t : list) {
				vipMapTemplate.put(t.getVipLevel(), t);
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public void login(int roleId, boolean online, String serviceId) {
		ActivityTemplate template = getActivityTemplate();
		if(template != null) {
			init(roleId);
		}
	}
	
	@Override
	public void refreshAM0(int roleId, boolean b, String serviceId) {
		login(roleId, b, serviceId);
	}

	private ActivityKeepOnlineInfo init(int roleId) {
		ActivityKeepOnlineInfo info = getInfo(roleId);
		if( info == null ) {
			info = new ActivityKeepOnlineInfo();
			info.setLastDrawTime((int)(System.currentTimeMillis() / 1000));
			saveInfo(roleId, info);
		}
		return info;
	}

	public Result reward(int roleId) {
		ActivityKeepOnlineInfo info = getInfo(roleId);
		if(info == null) {
			return Result.newFailure("活动信息不存在");
		}
		
		int curTime = (int)(System.currentTimeMillis()/1000);
		
		int totalSec = curTime - info.getLastDrawTime();
		if( totalSec < 0 ){
			return Result.newFailure("数据异常");
		}
		
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int vipLevel = role.getVipLevel();
		
		if( !vipMapTemplate.containsKey(vipLevel) ){
			return Result.newFailure("配置异常，相关vip等级读取不到配置");
		}
		
		ActivityKeepOnlineTemplate temp = vipMapTemplate.get(vipLevel);
		
		int totalExp = (int)((float)temp.getAddValueBySec()/1000 * totalSec);
		if( totalExp <= 0 ){
			return Result.newFailure("时间异常");
		}
		
		if( totalExp > temp.getUpValue() ){
			totalExp = temp.getUpValue();
		}
		
		// 保存领取状态
		info.setLastDrawTime(curTime);
		saveInfo(roleId, info);
		
		// 发放奖励
		GameContext.getUserAttrApp().changeAttribute(roleId, 
				AttrUnit.build(RoleAttr.tankExp, Operation.add, totalExp),
				OutputType.KeepOnlineInc.type(), OutputType.KeepOnlineInc.getInfo() + " " + totalExp);
		
		Result ret = Result.newSuccess();
		ret.setInfo(""+totalExp);
		return ret;
	}
	
	public ActivityKeepOnlineInfo getInfo(int roleId) {
		return activityDao.getKeepOnlineInfo(roleId);
	}
	
	private void saveInfo(int roleId, ActivityKeepOnlineInfo info) {
		activityDao.saveKeepOnlineInfo(roleId, info);
	}
	
	public ActivityKeepOnlineTemplate getKeepOnlineTemplate(int vipLevel){
		return vipMapTemplate.get(vipLevel);
	}
}
