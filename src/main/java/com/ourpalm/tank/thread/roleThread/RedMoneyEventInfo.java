package com.ourpalm.tank.thread.roleThread;

import com.ourpalm.tank.app.GameContext;

public class RedMoneyEventInfo extends RoleEventAbs {
	private String uniqueId;
	private int roleId;

	@Override
	public boolean excute() {
		if (super.excute()) {
			GameContext.getRedMoneyApp().updateByRobotSys(uniqueId, roleId);
			return true;
		}
		return false;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

}
