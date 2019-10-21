package com.ourpalm.tank.app.map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.map.astar.Grid;
import com.ourpalm.tank.app.map.astar.Node;
import com.ourpalm.tank.app.map.astar.Point;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.app.map.state.camp.CampStateMachineFactory;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.MatchItem;
import com.ourpalm.tank.message.MATCH_MSG.STC_NOTIFY_ENTER_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.template.BattleHandUpTemplate;
import com.ourpalm.tank.template.BattleRewardBaseTempalte;
import com.ourpalm.tank.template.BattleTeachTankTemplate;
import com.ourpalm.tank.template.BattleTeachTemplate;
import com.ourpalm.tank.template.BattleWeakTemplate;
import com.ourpalm.tank.template.CampaignBoxTemplate;
import com.ourpalm.tank.template.CampaignMapTankTemplate;
import com.ourpalm.tank.template.CampaignMapTemplate;
import com.ourpalm.tank.template.ExtraIncomeTemplate;
import com.ourpalm.tank.template.ExtraIncomeTextTemplate;
import com.ourpalm.tank.template.GridTemplate;
import com.ourpalm.tank.template.IncomeTemplate;
import com.ourpalm.tank.template.MapAiPointTemplate;
import com.ourpalm.tank.template.MapBirthTemplate;
import com.ourpalm.tank.template.MapDataTemplate;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.BattleMapInstance;
import com.ourpalm.tank.vo.CampaignMapInstance;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.PerishMapInstance;
import com.ourpalm.tank.vo.SportMapInstance;
import com.ourpalm.tank.vo.result.CampEnterResult;
import com.ourpalm.tank.vo.result.Result;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MapAppImpl implements MapApp{
	
	private final Logger logger = LogCore.runtime;
	private Map<Integer, MapTemplate> allMap = new HashMap<>();
	private Map<Integer, List<MapBirthTemplate>> mapBirthMap = new HashMap<>();
	private Map<String, MapDataTemplate> allDataMap = new HashMap<>();		//地图网格数据
	private Map<String, ReadHightMapData> allHeightMap = new HashMap<>();	//地图高度数据
	//AI寻路目标点<地图ID_点级别_队伍ID, List>
	private Map<String, List<Point>> aiPointMap = new HashMap<>();			//AI路点信息
	private Map<Integer, CampaignMapTemplate> battleMap = new HashMap<>();	//战役地图模版
	private Map<Integer, List<CampaignMapTemplate>> battleWarMap = new HashMap<>();			//战役对应的所有关卡列表(key=warId)
	private Map<Integer, List<CampaignMapTankTemplate>> battleTankMap = new HashMap<>();	//战役坦克列表
	private Map<Integer, List<CampaignBoxTemplate>> battleBoxMap = new HashMap<>();			//战役奖励Box(key=warId)
	private Map<Integer, BattleRewardBaseTempalte> battleRewardMap = new HashMap<>();		//排位赛、对战模式战斗奖励
	private Map<Integer, BattleWeakTemplate> battleWeakMap = new HashMap<>();				//奖励衰弱系数配置
	private Map<Integer, BattleHandUpTemplate> battleHandupMap = new HashMap<>();			//挂机惩罚系数配置
	

	private Map<Integer, MapInstance> allInstanceMap = new ConcurrentHashMap<>();
	
	private BattleTeachTemplate battleTeachTemplate ;
	private List<BattleTeachTankTemplate> battleTeachTankList ;
	private AtomicInteger idFactory = new AtomicInteger(0);
	
	private String mapDataPath;		//地图网格数据路径
	private String aiPointPah;		//AI寻路目标点
	private String mapHeightPath;	//地图高度数据
	
	private int mapThreadHandlerCount = 8;
	private int aiThreadHandlerCount = 16;
	private List<MapThreadHandler>  mapThreadHandlers = new ArrayList<>();		//地图循环执行器
	private List<AIThreadHandler> aiThreadHandlers = new ArrayList<>();			//AI执行器
	
	/** 收益 坦克等级 */
	private Map<Integer, IncomeTemplate> incomeTemplateMap = new HashMap<>();
	/** 坦克额外收益 */
	private ExtraIncomeTemplate extraIncomeTemplate = new ExtraIncomeTemplate();
	
	/**坦克额外收益文字 */
	private Map<Integer, ExtraIncomeTextTemplate> extraIncomeTextTemplateMap = new HashMap<>();
	
	
	public Map<Integer, MapInstance> getAllInstanceMap() {
		return allInstanceMap;
	}
	@Override
	public void start() {
		//加载地图出生点
		this.loadMapBirthTemplate();
		//加载地图配置
		this.loadMapTemplate();
		//加载战役配置
		this.loadBattleMapTemplate();
		//加载战役坦克配置
		this.loadBattleMapTankTemplate();
		//加载战役宝箱配置
		this.loadCampaignBoxTemplate();
		
		//初始化地图执行器
		this.initMapThreadHandler();
		this.initAIThreadHandler();
		
		//战斗教学战场配置
		this.loadTeachTemplate();
		this.loadTeachTankTemplate();
		
		//加载地图网格信息
		this.loadMapDataTemplate();
		//加载地图高度信息
		this.loadMapHeightData();
		//加载AI寻路目标点信息
		this.loadAiPointTemplate();
		//加载主战场战斗结束奖励配置
		this.loadBattleRewardBaseTemplate();
		//加载奖励衰弱配置
		this.loadBattleWeakTemplate();
		//加载挂机惩罚配置
		this.loadBattleHandUpTemplate();
		//加载收益
		this.loadIncomeTemplate();
		//加载额外收益
		this.loadExtraIncomeTemplate();
		//加载额外收益文字 
		this.loadExtraIncomeTextTemplate();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					while(true){
						Thread.sleep(10000);
						LogCore.system.info("当前MapInstance实例数 : {} ", allInstanceMap.size());
					}
				}catch(Exception ex){
					logger.error("", ex);
				}
			}
		}).start();
	}
	
	
	//初始化地图执行器
	private void initMapThreadHandler(){
		for(int i = 0; i < mapThreadHandlerCount; i++){
			MapThreadHandler handler = new MapThreadHandler("地图"+i);
			handler.startup();
			mapThreadHandlers.add(handler);
		}
	}
	
	//初始化AI执行器
	private void initAIThreadHandler(){
		for(int i = 0; i < aiThreadHandlerCount; i++){
			AIThreadHandler handler = new AIThreadHandler("AI"+ i);
			handler.startup();
			aiThreadHandlers.add(handler);
		}
	}
	
	//加载战场结束奖励配置
	private void loadBattleRewardBaseTemplate() {
		String sourceFile = XlsSheetType.battleRewardBase.getXlsFileName();
		String sheetName = XlsSheetType.battleRewardBase.getSheetName();
		
		try {
			this.battleRewardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BattleRewardBaseTempalte.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}
	
	//加载收益配置
	private void loadIncomeTemplate() {
		String sourceFile = XlsSheetType.Income.getXlsFileName();
		String sheetName = XlsSheetType.Income.getSheetName();
		
		try {
			this.incomeTemplateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, IncomeTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}
	
	//加载额外收益配置
	private void loadExtraIncomeTemplate() {
		String sourceFile = XlsSheetType.ExtraIncome.getXlsFileName();
		String sheetName = XlsSheetType.ExtraIncome.getSheetName();
		
		try {
			this.extraIncomeTemplate = XlsPojoUtil.sheetToList(sourceFile, sheetName, ExtraIncomeTemplate.class).get(0);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}
	
	//加载额外收益文字
	private void loadExtraIncomeTextTemplate() {
		String sourceFile = XlsSheetType.ExtraIncomeText.getXlsFileName();
		String sheetName = XlsSheetType.ExtraIncomeText.getSheetName();

		try {
			this.extraIncomeTextTemplateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, ExtraIncomeTextTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}

	//加载战场衰弱系数配置
	private void loadBattleWeakTemplate(){
		String sourceFile = XlsSheetType.battleWeakTemplate.getXlsFileName();
		String sheetName = XlsSheetType.battleWeakTemplate.getSheetName();
		try{
			this.battleWeakMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BattleWeakTemplate.class);
		}catch(Exception e){
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", sourceFile, sheetName), e);
		}
	}
	
	

	@Override
	public CampEnterResult enterCampaign(int campId, int roleId){
		CampEnterResult result = new CampEnterResult();
		//是否解锁
		//燃油是否足够判断
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int tankId = role.getMainTankId();
		
		//主战坦克
		CampaignMapTemplate mapTemplate = this.battleMap.get(campId);
		String[] locations = mapTemplate.getLocation().split(Cat.comma);
		AbstractInstance tank = GameContext.getTankApp().createTankInstance(roleId, tankId);
		//设置队伍，出生点复活坐标
		tank.setTeam(TEAM.RED);
		tank.setAiMain(1);
		tank.setBirthLocation(Location.newBuilder()
				.setX(Float.valueOf(locations[0]))
				.setY(Float.valueOf(locations[1]))
				.setZ(Float.valueOf(locations[2]))
				.setDir(mapTemplate.getDir())
				.build());
		
		//设置复活需要金币数
		tank.setRebirthGold(mapTemplate.getReviveGold());
		
		//创建地图实例对象
		CampaignMapInstance mapInstance = this.createCampaignMapInstance(tank, campId);
		
		//放入地图实例
		mapInstance.enter(tank);
		
		//创建战斗记录
		RoleBattle battle = new RoleBattle();
		battle.setCreateTime(System.currentTimeMillis());
		battle.setNodeName(GameContext.getLocalNodeName());
		battle.setRoleId(roleId);
		battle.setTankInstanceId(tank.getId());
		
		GameContext.getMatchApp().saveRoleBattle(battle);
		
		result.setResult(Result.SUCCESS);
		result.setMapInstance(mapInstance);
		result.setSelfTank(tank);
		return result;
	}
	
	
	@Override
	public void createBattleNotifyEnter(List<AbstractInstance> tanks, int warType, int mapIndex,int preseason){
		logger.debug("通知匹配结果...");
		
		//创建地图实例，通知全体
		SportMapInstance mapInstance = this.createSportMapInstance(mapIndex, warType,preseason);
		
		MapTemplate mapTemplate = mapInstance.getTemplate();
		STC_NOTIFY_ENTER_MSG.Builder builder = this.buildNotifyMsg(tanks);
		builder.setMapId(mapTemplate.getMapId());
		builder.setMapName(mapTemplate.getName());
		builder.setSmallMapOffX(mapTemplate.getOffX());
		builder.setSmallMapOffY(mapTemplate.getOffY());
		builder.setSmallMapHeigh(mapTemplate.getOffHeigh());
		builder.setSmallMapWidth(mapTemplate.getOffWidth());
		builder.setWarType(WAR_TYPE.valueOf(warType));
		builder.setWeather(mapInstance.getWeather());
		
		String roomId = GameContext.getLocalNodeName() + Cat.underline + mapInstance.getInstanceId();
		builder.setRoomId(roomId);
		for (AbstractInstance abstractInstance : tanks) {
			Map<Integer, Integer> scheduleMap =	mapInstance.getTankEnterScheduleMap();
			if(abstractInstance.getRoleId()<=0){
				scheduleMap.put(abstractInstance.getRoleId(), 100);
			}else{
				scheduleMap.put(abstractInstance.getRoleId(), 0);
			}
			 
		}
			
		for(AbstractInstance tank : tanks){
			//分配出生点
			mapTemplate.matchBirthLoaction(tank);
			//设置复活需要金币数
			tank.setRebirthGold(mapTemplate.getReviveGold());
			//放入地图内
			mapInstance.enter(tank);
			
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(tank.getRoleId());
			if(connect != null){
				logger.debug("...通知[{}]进入战场",tank.getId());
				builder.setSelf(tank.getTeam()); //自身所属队伍类型
				builder.setWeakTeam(tank.isWeakTeam());//是否遭遇强敌
				//先主推一条心跳消息
				connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_HEARTBEAT_VALUE, null);
				//通知进入消息
				connect.sendMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, MATCH_MSG.CMD_ID.STC_NOTIFY_ENTER_VALUE, builder.build().toByteArray());
			}
		
		}
		this.addToThread(mapInstance);
	}
	
	//创建通知进入消息
	private STC_NOTIFY_ENTER_MSG.Builder buildNotifyMsg(List<AbstractInstance> tanks){
		STC_NOTIFY_ENTER_MSG.Builder builder = STC_NOTIFY_ENTER_MSG.newBuilder();
		for(AbstractInstance tank : tanks){
			int roleId = tank.getRoleId();
			int tankId = tank.getTemplateId();
			MatchItem item = MatchItem.newBuilder()
					.setRoleName(tank.getRoleName())
					.setTankId(tankId)
					.setTeamType(tank.getTeam())
					.setRoleId(roleId)
					.setTitleId(tank.getTitleId())
					.setAttack(tank.getBattleScore())
					.setHadElite(tank.isEliteTank())
					.setPfUserInfo(GameContext.getUserApp().getPfUserInfoStr(roleId))
					.setPfYellowUserInfo(GameContext.getUserApp().getPfYellowUserInfoStr(roleId))
					.setTankInstanceId(tank.getId())
					.addAllGoodBad(GameContext.getTankApp().getTankTemplate(tank.getTemplateId()).getGoodBadList())
					.build();
			builder.addMatchList(item);
		}
		return builder;
	}
	
	// 所拥有的技能
//	private List<Integer> getSkillItem(AbstractInstance tank) {
//		List<Integer> skillList = new ArrayList<>();
//		for (Skill skill : tank.getAllSkill()) {
//			if (skill.isActive()) {
//				skillList.add(skill.getId());
//			}
//		}
//		return skillList;
//	}
	
	
	//加载地图配置表
	private void loadMapTemplate(){
		String sourceFile = XlsSheetType.MapTemplate.getXlsFileName();
		String sheetName = XlsSheetType.MapTemplate.getSheetName();
		try{
			List<MapTemplate> allMapList = XlsPojoUtil.sheetToList(sourceFile, sheetName, MapTemplate.class);
			for(MapTemplate template : allMapList){
				List<MapBirthTemplate> birthList = this.mapBirthMap.get(template.getId());
				if(Util.isEmpty(birthList)){
					LogCore.startup.error("地图没有配置出生点 id={}, mapId={}", template.getId(), template.getMapId());
					continue;
				}
				template.setBirthList(birthList);
				template.init();
				this.allMap.put(template.getId(), template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	@Override
	public void loadHotUpdateMap() {
		// 加载地图出生点
		this.loadMapBirthTemplate();
		// 加载地图配置
		this.loadMapTemplate();
		// 加载战役配置
		this.loadBattleMapTemplate();
		// 加载战役坦克配置
		this.loadBattleMapTankTemplate();
		// 加载战役宝箱配置
		this.loadCampaignBoxTemplate();
		// 战斗教学战场配置
		this.loadTeachTemplate();
		this.loadTeachTankTemplate();
		// 加载挂机惩罚配置
		this.loadBattleHandUpTemplate();
		// 加载收益
		this.loadIncomeTemplate();
		// 加载额外收益
		this.loadExtraIncomeTemplate();
		// 加载额外收益文字
		this.loadExtraIncomeTextTemplate();
	}
	
	//加载战役地图配置
	private void loadBattleMapTemplate(){
		String sourceFile = XlsSheetType.battleMapTemplate.getXlsFileName();
		String sheetName = XlsSheetType.battleMapTemplate.getSheetName();
		try{
			List<CampaignMapTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CampaignMapTemplate.class);
			for(CampaignMapTemplate template : list){
				template.init();
				battleMap.put(template.getId(), template);
				
				int warId = template.getWarId();
				List<CampaignMapTemplate> _list = battleWarMap.get(warId);
				if(Util.isEmpty(_list)){
					_list = new ArrayList<>();
					battleWarMap.put(warId, _list);
				}
				_list.add(template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	
	//加载战役坦克配置
	private void loadBattleMapTankTemplate(){
		String sourceFile = XlsSheetType.battleMapTankTemplate.getXlsFileName();
		String sheetName = XlsSheetType.battleMapTankTemplate.getSheetName();
		try{
			List<CampaignMapTankTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CampaignMapTankTemplate.class);
			for(CampaignMapTankTemplate template : list){
				int id = template.getId();
				List<CampaignMapTankTemplate> templateList = this.battleTankMap.get(id);
				if(templateList == null){
					templateList = new ArrayList<CampaignMapTankTemplate>();
					templateList.add(template);
					this.battleTankMap.put(id, templateList);
					continue;
				}
				templateList.add(template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	
	//加载战役宝箱配置
	private void loadCampaignBoxTemplate(){
		String sourceFile = XlsSheetType.campaignBoxTemplate.getXlsFileName();
		String sheetName = XlsSheetType.campaignBoxTemplate.getSheetName();
		try{
			List<CampaignBoxTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CampaignBoxTemplate.class);
			for(CampaignBoxTemplate template : list){
				template.init();
				int warId = template.getWarId();
				List<CampaignBoxTemplate> templateList = this.battleBoxMap.get(warId);
				if(templateList == null){
					templateList = new ArrayList<>();
					templateList.add(template);
					this.battleBoxMap.put(warId, templateList);
					continue;
				}
				templateList.add(template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	/** 加载教学地图配置 */
	private void loadTeachTemplate(){
		String sourceFile = XlsSheetType.battleTeachTemplate.getXlsFileName();
		String sheetName = XlsSheetType.battleTeachTemplate.getSheetName();
		try {
			List<BattleTeachTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BattleTeachTemplate.class);
			this.battleTeachTemplate = list.get(0);
			this.battleTeachTemplate.init();
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	/** 加载教学关卡坦克配置 */
	private void loadTeachTankTemplate(){
		String sourceFile = XlsSheetType.battleTeachTankTemplate.getXlsFileName();
		String sheetName = XlsSheetType.battleTeachTankTemplate.getSheetName();
		try {
			this.battleTeachTankList = XlsPojoUtil.sheetToList(sourceFile, sheetName, BattleTeachTankTemplate.class);
			for(BattleTeachTankTemplate template : battleTeachTankList){
				template.init();
			}
		} catch (Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	
	
	/** 加载地图网格数据 */
	private void loadMapDataTemplate(){
		String fileName = "";
		try{
			ClassPathResource res = new ClassPathResource(mapDataPath);
			File file = res.getFile();
			for(File fileData : Objects.requireNonNull(file.listFiles())){
				fileName = fileData.getName();
				if(!fileName.endsWith(".json")){
					continue;
				}
				InputStreamReader read = new InputStreamReader(new FileInputStream(fileData),StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(read);
				StringBuilder strBuf = new StringBuilder();
				String str ;
				while((str = br.readLine()) != null){
					if(Util.isEmpty(str)){
						continue;
					}
					strBuf.append(str);
				}
				br.close();
				MapDataTemplate template = JSON.parseObject(strBuf.toString(), MapDataTemplate.class);
				this.allDataMap.put(template.getMapId(), template);
			}
			
		}catch(Exception e){
			LogCore.startup.error("加载地图网格数据异常...fileName = {}",fileName, e);
		}
	}
	
	
	/** 加载AI目标寻路点数据 */
	private void loadAiPointTemplate(){
		String fileName = "";
		try{
			ClassPathResource res = new ClassPathResource(aiPointPah);
			File file = res.getFile();
			for(File fileData : Objects.requireNonNull(file.listFiles())){
				fileName = fileData.getName();
				if(!fileName.endsWith(".bytes")){
					continue;
				}
				InputStreamReader read = new InputStreamReader(new FileInputStream(fileData),StandardCharsets.UTF_8);
				BufferedReader br = new BufferedReader(read);
				String str = null;
				while((str = br.readLine()) != null){
					if(Util.isEmpty(str)){
						continue;
					}
					MapAiPointTemplate template = JSON.parseObject(str, MapAiPointTemplate.class);
					String key = this.createAIPointKey(template.getMapId(), template.getLevel(), template.getTeam());
					List<Point> list = aiPointMap.get(key);
					if(list == null){
						list = new ArrayList<>();
						aiPointMap.put(key, list);
					}
					list.addAll(template.getPoints());
				}
				br.close();
			}
		}catch(Exception e){
			LogCore.startup.error("加载AI目标寻路点数据异常...fileName ={} ",fileName, e);
		}
	}
	
	
	/** 加载地图高度数据 */
	private void loadMapHeightData(){
		String fileName = "";
		try{
			ClassPathResource res = new ClassPathResource(this.mapHeightPath);
			File file = res.getFile();
			for(File fileData : Objects.requireNonNull(file.listFiles())){
				fileName = fileData.getName();
				if(!fileName.endsWith(".raw")){
					continue;
				}
				//去掉后缀名
				fileName = fileName.substring(0, fileName.indexOf("."));
				String[] params = fileName.split(Cat.underline);
				String mapId = params[0];
				ReadHightMapData  readHightMapData = new ReadHightMapData(
						Integer.parseInt(params[1]), Integer.parseInt(params[2]),
						Integer.parseInt(params[3]), Integer.parseInt(params[4]), 
						fileData, Integer.parseInt(params[5]) == 16);
				this.allHeightMap.put(mapId, readHightMapData);
			}
		}catch(Exception e){
			LogCore.startup.error("加载地图高度数据异常...fileName = {}",fileName, e);
		}
	}
	
	//加载地图出生点
	private void loadMapBirthTemplate(){
		String sourceFile = XlsSheetType.mapBirthTemplate.getXlsFileName();
		String sheetName = XlsSheetType.mapBirthTemplate.getSheetName();
		try {
			List<MapBirthTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, MapBirthTemplate.class);
			for(MapBirthTemplate template : list){
				List<MapBirthTemplate> birthList = this.mapBirthMap.get(template.getId());
				if(birthList == null){
					birthList = new ArrayList<>();
					this.mapBirthMap.put(template.getId(), birthList);
				}
				birthList.add(template);
			}
		} catch (Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	/** 加载挂机惩罚配置 */
	private void loadBattleHandUpTemplate(){
		String sourceFile = XlsSheetType.battleHandUpTemplate.getXlsFileName();
		String sheetName = XlsSheetType.battleHandUpTemplate.getSheetName();
		try{
			this.battleHandupMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, BattleHandUpTemplate.class);
		}catch(Exception e){
			LogCore.startup.error("加载{},{}异常", sourceFile, sheetName, e);
		}
	}
	
	@Override
	public void stop() {
		
	}

	@Override
	public MapInstance getMapInstance(int mapInstanceId) {
		return allInstanceMap.get(mapInstanceId);
	}
	
	
	@Override
	public void removeMapInstance(int mapInstance){
		this.allInstanceMap.remove(mapInstance);
	}
	
	@Override
	public List<CampaignMapTankTemplate> getCampaignMapTankTemplates(int campId){
		return this.battleTankMap.get(campId);
	}
	
	@Override
	public CampaignMapTemplate getCampaignMapTemplate(int campId){
		return this.battleMap.get(campId);
	}
	
	@Override
	public List<CampaignMapTemplate> getAllCampaignMapTemplate(int warId){
		return this.battleWarMap.get(warId);
	}
	
	@Override
	public List<CampaignBoxTemplate> getAllCampaignBoxTemplate(int warId){
		return this.battleBoxMap.get(warId);
	}
	
	@Override
	public MapDataTemplate getMapDataTemplate(String mapId){
		return this.allDataMap.get(mapId);
	}
	
	/** 
	 * 返回AI寻路目标点信息 
	 */
	@Override
	public List<Point> getAIPoint(String mapId, int pointLvl, int team){
		return aiPointMap.get(this.createAIPointKey(mapId, pointLvl, team));
	}
	
	private String createAIPointKey(String mapId, int pointLvl, int team){
		return mapId + Cat.underline + pointLvl + Cat.underline + team;
	}
	

	public SportMapInstance createSportMapInstance(int mapIndex, int warType,int preseason){
		int id = idFactory.incrementAndGet();
		String uniqueId = GameContext.getIdFactory().nextStr();
		MapTemplate template = this.allMap.get(mapIndex);
		SportMapInstance instance = null;
		switch (warType) {
		case WAR_TYPE.BATTLE_VALUE:
			instance = new BattleMapInstance(template, id, warType);
			break;
		case WAR_TYPE.RANK_VALUE:
			instance = new SportMapInstance(template, id, warType);
			break;
		case WAR_TYPE.PERISH_VALUE:
			instance = new PerishMapInstance(template, id, warType);
			break;
		}
		instance.setUniqueId(uniqueId);
		if (preseason > 0 && preseason < 6) {
			instance.setPreseasonTemplate(GameContext.getMatchApp().getPreseasonTemplateMap().get(preseason));
		}
		//创建网格信息
		String mapId = template.getMapId();
		instance.setGrid(this.createGrid(mapId));
		//地图高度数据
		instance.setReadHightMapData(this.allHeightMap.get(mapId));
		
		this.allInstanceMap.put(id, instance);
		
		return instance;
	}
	
	public void addToThread(SportMapInstance instance) {
		int id = instance.getInstanceId();
		// 放入执行器中
		int handlerIndex = Math.abs(id) % this.mapThreadHandlerCount;
		MapThreadHandler mapHandler = this.mapThreadHandlers.get(handlerIndex);
		mapHandler.add(id);

		// AI执行器
		int aiIndex = Math.abs(id) % this.aiThreadHandlerCount;
		AIThreadHandler aiHandler = this.aiThreadHandlers.get(aiIndex);
		aiHandler.add(id);

	}
	
	/** 创建网格信息 */
	private Grid createGrid(String mapId){
		MapDataTemplate template = this.allDataMap.get(mapId);
		if(template == null){
			logger.warn("地图ID = {} 没有网格信息...", mapId);
			return null;
		}
		Grid grid = new Grid();
		grid.setCellSize(template.getCellSize());
		grid.setCols(template.getCols());
		grid.setDeviationX(template.getDeviationX());
		grid.setDeviationZ(template.getDeviationZ());
		grid.setRows(template.getRows());
		
		Map<Integer, List<Node>> nodeMap = new HashMap<>();
		for(GridTemplate gridTmp : template.getNodeList()){
			int x = gridTmp.getX();
			Node node = new Node(gridTmp.getIndex(), x, gridTmp.getZ());
			node.setPx(gridTmp.getPx());
			node.setPy(gridTmp.getPy());
			node.setPz(gridTmp.getPz());
			node.setWalkable(gridTmp.getWalk() == 1);
			
			List<Node> list = nodeMap.get(x);
			if(list == null){
				list = new ArrayList<>();
				nodeMap.put(x, list);
			}
			list.add(node);
		}
		grid.setNodeMap(nodeMap);
		
		return grid;
	}
	
	
	@Override
	public MapTemplate getMapTemplate(int mapId){
		return this.allMap.get(mapId);
	}
	
	@Override
	public BattleTeachTemplate getTeachTemplate(){
		return battleTeachTemplate;
	}

	public void setAiPointPah(String aiPointPah) {
		this.aiPointPah = aiPointPah;
	}

	private CampaignMapInstance createCampaignMapInstance(AbstractInstance tank, int campId){
		int id = idFactory.incrementAndGet();
		CampaignMapTemplate mapTemplate = this.battleMap.get(campId);
		if(mapTemplate == null){
			throw new NullPointerException("关卡id错误，没有对应的关卡 gateId = "+ campId);
		}
		CampaignMapInstance instance = new CampaignMapInstance(mapTemplate, id);
		this.allInstanceMap.put(id, instance);
		
		//创建状态机
		final int roleId = tank.getRoleId();
		StateMachine stateMachine = CampStateMachineFactory.createStateMachine(instance, roleId);
		instance.setStateMachine(stateMachine);
		
		List<CampaignMapTankTemplate> tanks = this.battleTankMap.get(campId);
		if(Util.isEmpty(tanks)){
			return instance;
		}
		//创建地图npc
		for(CampaignMapTankTemplate template : tanks){
			BodyType bodyType = BodyType.valueOf(template.getType());
			AbstractInstance npcTank = this.createCampaignTank(tank, bodyType, template.getItemId());
			//设置出生点
			String[] locationes = template.getLocation().split(Cat.comma);
			Location location = Location.newBuilder()
					.setDir(template.getBirthDir())
					.setX(Float.parseFloat(locationes[0]))
					.setY(Float.parseFloat(locationes[1]))
					.setZ(Float.parseFloat(locationes[2]))
					.build();
			npcTank.setBirthLocation(location);
			npcTank.setRoleName(template.getName());
			npcTank.setTeam(TEAM.valueOf(template.getTeam()));
			npcTank.setAiType(template.getAiType());
			npcTank.setAiInt(template.getAiInt());
			npcTank.setAiStr(template.getAiStr());
			//放入地图
			instance.enter(npcTank);
		}
		return instance;
	}

	/** 创建战役坦克 */
	private AbstractInstance createCampaignTank(AbstractInstance tank, BodyType type, int itemId){
		switch(type){
			case tank : 
				return GameContext.getTankApp().createNpcTank(itemId, 0);
			case build :
				return GameContext.getTankApp().createBuildTank(itemId, type);
			case selfTank :
				AbstractInstance npcTank = GameContext.getTankApp().createNpcTank(tank.getTemplateId(), 0);
				npcTank.setBodyType(type);
				npcTank.setAttribute(tank.getAttribute());
				return npcTank;
			default : return null;
		}
	}
	
	public void setMapDataPath(String mapDataPath) {
		this.mapDataPath = mapDataPath;
	}

	
	@Override
	public List<BattleTeachTankTemplate> getBattleTeachTankList(){
		return this.battleTeachTankList;
	}
	

	public void setMapHeightPath(String mapHeightPath) {
		this.mapHeightPath = mapHeightPath;
	}

	public void setMapThreadHandlerCount(int mapThreadHandlerCount) {
		this.mapThreadHandlerCount = mapThreadHandlerCount;
	}
	
	public void setAiThreadHandlerCount(int aiThreadHandlerCount) {
		this.aiThreadHandlerCount = aiThreadHandlerCount;
	}
	@Override
	public Map<Integer, IncomeTemplate> getIncomeTemplateMap() {
		return incomeTemplateMap;
	}
	public void setIncomeTemplateMap(Map<Integer, IncomeTemplate> incomeTemplateMap) {
		this.incomeTemplateMap = incomeTemplateMap;
	}
	@Override
	public ExtraIncomeTemplate getExtraIncomeTemplate() {
		return extraIncomeTemplate;
	}
	public void setExtraIncomeTemplate(ExtraIncomeTemplate extraIncomeTemplate) {
		this.extraIncomeTemplate = extraIncomeTemplate;
	}
	
	@Override
	public Map<Integer, ExtraIncomeTextTemplate> getExtraIncomeTextTemplateMap() {
		return extraIncomeTextTemplateMap;
	}
	public void setExtraIncomeTextTemplateMap(Map<Integer, ExtraIncomeTextTemplate> extraIncomeTextTemplateMap) {
		this.extraIncomeTextTemplateMap = extraIncomeTextTemplateMap;
	}
	
	@Override
	public Map<Integer, BattleHandUpTemplate> getBattleHandupMap() {
		return battleHandupMap;
	}
	public void setBattleHandupMap(Map<Integer, BattleHandUpTemplate> battleHandupMap) {
		this.battleHandupMap = battleHandupMap;
	}
	@Override
	public void mapForceClose(){
		for(MapThreadHandler threadHandle : this.mapThreadHandlers){
			threadHandle.mapForceClose();
		}
	}
}
