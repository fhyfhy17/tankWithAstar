package com.ourpalm.tank.domain;

import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;

public class PartInfo {
	private PART_INDEX index;// 索引
	private int partId = -1;// 配件ID
	private boolean active;// 是否激活

	public PART_INDEX getIndex() {
		return index;
	}

	public void setIndex(PART_INDEX index) {
		this.index = index;
	}

	public int getPartId() {
		return partId;
	}

	public void setPartId(int partId) {
		this.partId = partId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


}
