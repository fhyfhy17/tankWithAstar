package com.ourpalm.tank.vo.result;

import com.ourpalm.tank.vo.CampaignMapInstance;
import com.ourpalm.tank.vo.AbstractInstance;

public class CampEnterResult extends Result{

	private CampaignMapInstance mapInstance;
	private AbstractInstance selfTank;

	public CampaignMapInstance getMapInstance() {
		return mapInstance;
	}

	public void setMapInstance(CampaignMapInstance mapInstance) {
		this.mapInstance = mapInstance;
	}

	public AbstractInstance getSelfTank() {
		return selfTank;
	}

	public void setSelfTank(AbstractInstance selfTank) {
		this.selfTank = selfTank;
	}

	
}
