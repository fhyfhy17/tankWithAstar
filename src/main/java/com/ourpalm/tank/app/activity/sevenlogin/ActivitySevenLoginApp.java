package com.ourpalm.tank.app.activity.sevenlogin;

import java.util.List;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.message.ACTIVITY_MSG.SevenLoginDay;
import com.ourpalm.tank.vo.result.ValueResult;

public interface ActivitySevenLoginApp extends Service{

	public List<SevenLoginDay> getPageList(int roleId);
	
	public ValueResult<Integer> draw(int roleId);
	
	public void create(int roleId);
}
