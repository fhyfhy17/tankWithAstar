package com.ourpalm.tank.app;

import com.ourpalm.tank.dao.RoleCampDao;

public class DAOFactory {

	
	
	private static RoleCampDao roleCampDao;
	
	
	public static RoleCampDao getRoleCampDao() {
		return roleCampDao;
	}

	public void setRoleCampDao(RoleCampDao roleCampDao) {
		DAOFactory.roleCampDao = roleCampDao;
	}

}
