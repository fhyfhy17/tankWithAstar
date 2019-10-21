package com.ourpalm.tank.app.continuelogin;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.dao.ContinueLoginDao;
import com.ourpalm.tank.domain.ContinueLoginInfo;

public class ContinueLoginAppImpl implements ContinueLoginApp{
	private static Logger logger = LogCore.runtime;
	private static final String TITLE = "连续登陆%d天奖励";
	private static final String THREE_CONTENT = "亲爱的指挥官："  +  System.getProperty("line.separator") 
			+"感谢您对游戏的关注与喜爱，请收下我们一点暖暖的心意！"  +  System.getProperty("line.separator") 
			+"激活码：%s"  +  System.getProperty("line.separator")
			+ System.getProperty("line.separator")
			+"凭此激活码，您可以领取：" +  System.getProperty("line.separator") 
			+ "3D坦克争霸1金币388，3D坦克争霸2银币100000"  +  System.getProperty("line.separator")
			+"请注意，该激活码每部游戏每个账号仅限使用一次。有效期为1个月。";
	private static final String SEVEN_CONTENT = "亲爱的指挥官："  +  System.getProperty("line.separator") 
			+"感谢您对游戏的支持与厚爱，请收下您应得的荣誉与褒奖！"  +  System.getProperty("line.separator") 
			+ "激活码：%s"  +  System.getProperty("line.separator")
			+  System.getProperty("line.separator")
			+"凭此激活码，您可以领取：" +  System.getProperty("line.separator") 
			+ "3D坦克争霸1金币888，3D坦克争霸2金币2888"  +  System.getProperty("line.separator")
			+"请注意，该激活码每部游戏每个账号仅限使用一次。有效期为1个月";

	
	private ContinueLoginDao continueLoginDao;

	@Override
	public void login(int roleId) {
		
		ContinueLoginInfo info = continueLoginDao.getInfo(roleId);
		if(info == null) {
			info = new ContinueLoginInfo();
			info.setDay(1);
		}
		
		//已经完成了
		if(info.isHadSevenCode())
			return;
		
		long curTime = System.currentTimeMillis();
		long lastLoginTime = info.getLastLoginTime();
		
		//同一天登录多次
		if(DateUtil.isSameDay(curTime, lastLoginTime)) {
			return;
		}
		
		int dayTime = 24 * 60 * 60 * 1000;
		
		info.setLastLoginTime(curTime);
		if(DateUtil.isSameDay(curTime, lastLoginTime + dayTime)) {
			info.increaseDay();
		} else {
			//重新累计
			info.setDay(1);
		}
		
		//发送激活码
		int day = info.getDay();
		String code = null;
		String content = "";
		if(day == 3 && !info.isHadThreeCode()) {
			code = continueLoginDao.randomThreeDayCode();
			info.setHadThreeCode(true);
			content = THREE_CONTENT;
		} else if(day == 7 && !info.isHadSevenCode()) {
			code = continueLoginDao.randomSevenDayCode();
			info.setHadSevenCode(true);
			content = SEVEN_CONTENT;
		}
		
		continueLoginDao.saveInfo(roleId, info);
		
		if(code != null) {
			GameContext.getMailApp().sendMail(roleId, String.format(TITLE, day), String.format(content, code), 0, 0, 0, 0, null);
			if(logger.isDebugEnabled()) {
				logger.debug("roleId: {}, continueLoginDay: {}, activeCode: {}", roleId, day, code);
			}
		}
	}
	
	public void setContinueLoginDao(ContinueLoginDao continueLoginDao) {
		this.continueLoginDao = continueLoginDao;
	}
	

}
