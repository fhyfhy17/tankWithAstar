package com.ourpalm.tank.app.scheduler;

import com.ourpalm.core.service.Service;


public interface SchedulerApp extends Service{

//	public void addToScheduler(JobDetail jobDetail,Trigger trigger) throws Exception;
	
	public void update();
	
	public void updateWeek();
}
