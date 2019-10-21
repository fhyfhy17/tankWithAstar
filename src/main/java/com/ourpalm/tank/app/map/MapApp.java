package com.ourpalm.tank.app.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.app.map.astar.Point;
import com.ourpalm.tank.template.BattleHandUpTemplate;
import com.ourpalm.tank.template.BattleTeachTankTemplate;
import com.ourpalm.tank.template.BattleTeachTemplate;
import com.ourpalm.tank.template.CampaignBoxTemplate;
import com.ourpalm.tank.template.CampaignMapTankTemplate;
import com.ourpalm.tank.template.CampaignMapTemplate;
import com.ourpalm.tank.template.ExtraIncomeTemplate;
import com.ourpalm.tank.template.ExtraIncomeTextTemplate;
import com.ourpalm.tank.template.IncomeTemplate;
import com.ourpalm.tank.template.MapDataTemplate;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.type.BattleRewardType;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.result.CampEnterResult;

public interface MapApp extends Service{
	
	/** 返回地图实例对象 */
	MapInstance getMapInstance(int mapInstanceId);
	
	/** 返回战役(战区)下的所有关卡配置 */
	List<CampaignMapTemplate> getAllCampaignMapTemplate(int warId);
	CampaignMapTemplate getCampaignMapTemplate(int campId);
	
	/** 战役坦克配置 */
	List<CampaignMapTankTemplate> getCampaignMapTankTemplates(int campId);
	
	/** 战役星级宝箱配置 */
	List<CampaignBoxTemplate> getAllCampaignBoxTemplate(int warId);
	
	/** 回收地图实例 */
	void removeMapInstance(int mapInstance);
	
	/** 创建战场,通知进入 */
	void createBattleNotifyEnter(List<AbstractInstance> tanks, int warType, int mapIndex, int preseason);
	
	/** 进入战役 */
	CampEnterResult enterCampaign(int campId, int roleId);
	
	MapTemplate getMapTemplate(int id);
	
	/** 返回地图网格信息 */
	MapDataTemplate getMapDataTemplate(String mapId);
	
	/** 
	 * 返回AI寻路目标点信息 
	 */
	List<Point> getAIPoint(String mapId, int pointLvl, int team);
	
	/**
	 * 获取教学战场配置
	 * @return
	 */
	BattleTeachTemplate getTeachTemplate();
	
	/**
	 * 获取教学坦克配置
	 * @return
	 */
	List<BattleTeachTankTemplate> getBattleTeachTankList();
	
	Map<Integer, MapInstance> getAllInstanceMap();

	void mapForceClose();

	/** 坦克收益 */
	Map<Integer, IncomeTemplate> getIncomeTemplateMap();

	/** 坦克额外收益 */
	ExtraIncomeTemplate getExtraIncomeTemplate();
	/** 坦克额外收益文字 */
	Map<Integer, ExtraIncomeTextTemplate>  getExtraIncomeTextTemplateMap();
	/**挂机map*/
	Map<Integer, BattleHandUpTemplate> getBattleHandupMap();

	void loadHotUpdateMap();
}
