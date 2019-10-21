package com.ourpalm.tank.app.activity.logic;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.template.ActivityTemplate;
import com.ourpalm.tank.vo.result.Result;

public class _107_BindTank1AccountLogic extends DailyActivityLogicAdapter {

	@Override
	public void start() {
	}
	

	public Result bind(int roleId, String area, String accountName) {
		ActivityTemplate template = getActivityTemplate();
		if(!isValid(template)) {
			return Result.newFailure("活动已结束");
		}
		
		if(Util.isEmpty(area)) {
			return Result.newFailure("分区错误");
		}
		
		if(Util.isEmpty(accountName)) {
			return Result.newFailure("角色名错误");
		}
		
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if(account == null) {
			return Result.newFailure("账号不存在");
		}
		
		if(!Util.isEmpty(account.getTank1Area()) || !Util.isEmpty(account.getTank1AccountName())) {
			return Result.newFailure("账号已绑定");
		}
		
		account.setTank1Area(area);
		account.setTank1AccountName(accountName);
		GameContext.getUserApp().saveRoleAccount(account);
		
		return Result.newSuccess();
	}
	
}
