package com.ourpalm.tank.thread.roleThread;

public abstract class RoleEventAbs implements RoleEvent {
	protected long time;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public boolean excute() {
		if (System.currentTimeMillis() > time) {
			return true;
		}
		return false;
	}

}
