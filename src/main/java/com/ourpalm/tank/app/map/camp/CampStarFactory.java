package com.ourpalm.tank.app.map.camp;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.tank.template.CampaignMapTemplate;

public class CampStarFactory {

	
	public static List<CampStar> createCampStar(CampaignMapTemplate mapTemplate){
		List<CampStar> list = new ArrayList<>();
		buildCampStar(list, mapTemplate.getStar1(), mapTemplate.getStarParam1());
		buildCampStar(list, mapTemplate.getStar2(), mapTemplate.getStarParam2());
		return list;
	}
	
	
	private static void buildCampStar(List<CampStar> list, int starType, int param){
		CampStar campStar ;
		switch(starType){
			case 1 : campStar = new CampKillTankStar(param); break;
			case 2 : campStar = new CampTimeStar(param); break;
			case 3 : campStar = new CampUseTypeTankStar(param); break;
			case 4 : campStar = new CampKillPatrolStar(param == 1); break;
			case 5 : campStar = new CampDeathStar(param); break;
			default : campStar = null;
		}
		if(campStar != null){
			list.add(campStar);
		}
		
	}
}
