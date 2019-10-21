package com.ourpalm.tank.vo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.battle.BattleApp;
import com.ourpalm.tank.app.map.camp.CampStar;
import com.ourpalm.tank.app.map.camp.CampStarFactory;
import com.ourpalm.tank.app.map.state.NotifyState;
import com.ourpalm.tank.app.map.state.StateMachine;
import com.ourpalm.tank.app.quest.BattleResultRecord;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.Coordinate3D;
import com.ourpalm.tank.message.BATTLE_MSG.STC_TANK_RELIVE_MSG;
import com.ourpalm.tank.message.MATCH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.AiType;
import com.ourpalm.tank.message.MATCH_MSG.Location;
import com.ourpalm.tank.message.MATCH_MSG.STC_TANK_BIRTH_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.TankItem;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.template.CampaignMapTankTemplate;
import com.ourpalm.tank.template.CampaignMapTemplate;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.util.LoopCount;

/**
 * 战役地图实例
 * @author wangkun
 *
 */
public class CampaignMapInstance extends MapInstance{
	
	private static final Logger logger = LogCore.runtime;
	private CampaignMapTemplate mapTemplate;
	private long beginTime = System.currentTimeMillis();			//比赛开始时间
	private List<LoopBirthTank> loopBirthTanks = new ArrayList<>(); 	//循环定时出生坦克列表
	private List<CampStar> campStars ;				//星级条件
	
	public CampaignMapInstance(CampaignMapTemplate mapTemplate, int instanceId) {
		super(instanceId);
		this.mapTemplate = mapTemplate;
		this.init();
	}
	
	private void init(){
		//初始化循环出生坦克
		this.initLoopBirthTank(mapTemplate.getId());
		//初始化星级条件
		this.campStars = CampStarFactory.createCampStar(mapTemplate);
	}
	
	//初始化循环出生坦克
	private void initLoopBirthTank(int campId){
		List<CampaignMapTankTemplate> list = GameContext.getMapApp().getCampaignMapTankTemplates(campId);
		if(Util.isEmpty(list)){
			logger.warn("此战役没有出战的npc坦克 campId = " + campId);
			return ;
		}
		//初始化循环出生坦克
		for(CampaignMapTankTemplate template : list){
			if(template.getType() == BodyType.tank.getType()
					&& template.getLoopTime() > 0){
				LoopBirthTank loopTank = new LoopBirthTank(template);
				this.loopBirthTanks.add(loopTank);
			}
		}
	}
	

	@Override
	public void death(AbstractInstance killer, AbstractInstance target) {
		synchronized (lock) {
			if(! target.isDeath()){
				return ;
			}
			//已经死亡
			if(this.deathMap.containsKey(target.getId())){
				return ;
			}
			
			//统计击杀数目
			this.calcuKillCount(killer, target);
			
			//击杀广播提示
//			this.killTipsNotify(killer, target);
			
			//判断是否为巡逻坦克,设置击杀巡逻坦克标识
			if(!killer.isRobot()){
				if(target.getAiType() == AiType.patrol_VALUE){
					BattleResultRecord record = killer.getQuestRecord();
					if(record != null){
//						record.setKillPatrolTank(true);
					}
				}
			}
			
			//任务统计
//			this.deathQuest(killer, target);
			
			//死亡者为玩家，调用死亡方法
			this.death(target);
			
			//广播战绩表
			int overTime = this.mapTemplate.getOverTime() + this.mapTemplate.getReadyTime(); 
			overTime -= this.durationTime();
			if(overTime <= 0){
				overTime = 0;
			}
			this.brodcastWarScore(overTime);
		}
	}

	@Override
	public void death(AbstractInstance tank) {
		
		//清空统计记录
		tank.clearCount();
		
		//删除所有buff
		for(IBuff buff : tank.getBuffers()){
			buff.clear();
		}
		
		//通知状态机玩家死亡，由状态机决定比赛是否结束或者让玩家复活
		stateMachine.notify(NotifyState.death, tank.getId());
	}
	
	/** 离开地图 */
	@Override
	public void leave(int tankInstanceId){
		super.leave(tankInstanceId);
		//回收地图
		GameContext.getMapApp().removeMapInstance(this.instanceId);
		
		for(int id : this.getAllTanksId()){
			//回收坦克
			GameContext.getTankApp().removeTankInstance(id);
		}
		logger.debug("离开战役地图, 回收资源...");
	}
	
	
	//坦克复活逻辑
	private void revive(){
		if(Util.isEmpty(deathMap)){
			return ;
		}
		long reviveTime = mapTemplate.getReviveTime() * 1000;
		for(int deathInstanceId : this.deathMap.keySet()){
			AbstractInstance tank = this.tanks.get(deathInstanceId);
			if(tank == null){
				continue;
			}
			if((System.currentTimeMillis() - tank.getDeathTime()) > reviveTime){
				tank.changeAttr(AttrType.hp, tank.get(AttrType.n_hpMax));
				
				Location location = tank.getBirthLocation();
				STC_TANK_RELIVE_MSG msg = STC_TANK_RELIVE_MSG.newBuilder()
						.setId(deathInstanceId)
						.setDir(location.getDir())
						.setPosition(Coordinate3D.newBuilder()
								.setPx(location.getX())
								.setPy(location.getY())
								.setPz(location.getZ())
								.build())
						.build();
				//复活消息
				this.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
						BATTLE_MSG.CMD_ID.STC_TANK_RELIVE_VALUE, msg.toByteArray());
				//属性同步
				tank.synchChangeAttr();
				
				//队列中删除
				deathMap.remove(deathInstanceId);
			}
		}
	}

	@Override
	public void update() {
		//执行状态机逻辑，负责比赛规则
		try{
			this.stateMachine.update();
		}catch(Exception e){
			logger.error("", e);
		}
		
		//循环出坦克
		try{
			this.loopBirthTankLogic();
		}catch(Exception e){
			logger.error("", e);
		}
		
		synchronized (lock) {
			try{
				//复活逻辑
				this.revive();
			}catch(Exception e){
				logger.error("", e);
			}
			
			//处理buff
			try{
				this.runBuff();
			}catch(Exception e){
				LogCore.runtime.error("", e);
			}
			
			//坦克状态
			try{
				this.runUpate();
			}catch(Exception e){
				LogCore.runtime.error("", e);
			}
		}
	}
	
	
	//循环出坦克
	private void loopBirthTankLogic(){
		for(LoopBirthTank loop : this.loopBirthTanks){
			AbstractInstance tank = loop.createTankInstance();
			if(tank == null){
				continue;
			}
			//放入地图
			this.enter(tank);
			//通知客户端
			TankItem item = GameContext.getBattleApp().buildTankItem(tank, BattleApp.otherTankAttr);
			STC_TANK_BIRTH_MSG msg = STC_TANK_BIRTH_MSG.newBuilder()
					.setTankItem(item)
					.build();
			this.brodcastMsg(MATCH_MSG.CMD_TYPE.CMD_TYPE_MATCH_VALUE, 
					MATCH_MSG.CMD_ID.STC_TANK_BIRTH_VALUE, msg.toByteArray());
		}
	}
	
	

	@Override
	public int durationTime() {
		int time = (int)((System.currentTimeMillis() - this.beginTime) / 1000) - mapTemplate.getReadyTime();
		if(time < 0){
			time = 0;
		}
		return time;
	}

	@Override
	public String getMapId() {
		return this.mapTemplate.getMapId();
	}

	public CampaignMapTemplate getMapTemplate(){
		return this.mapTemplate;
	}

	@Override
	public void notify(NotifyState state, int roleId) {
		if(state == NotifyState.enter){
			//开始计时
			this.beginTime = System.currentTimeMillis();
		}
	}

	public List<CampStar> getCampStars() {
		return campStars;
	}

	@Override
	public void setStateMachine(StateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public long getBeginTime() {
		return beginTime;
	}
	
	public void putDeath(int tankInstanceId){
		this.deathMap.put(tankInstanceId, tankInstanceId);
	}
	
	
	class LoopBirthTank{
		private CampaignMapTankTemplate template;
		private LoopCount loopCount;
		
		LoopBirthTank(CampaignMapTankTemplate template){
			this.template = template;
			this.loopCount = new LoopCount(template.getLoopTime() * 1000, 1000);
			logger.debug("初始化循环出生坦克 tankId = {}, time = {}", template.getItemId(), template.getLoopTime());
		}
		
		public AbstractInstance createTankInstance(){
			if(loopCount.isReachCycle()){
				AbstractInstance tank = GameContext.getTankApp().createNpcTank(template.getItemId(), 0);
				//设置出生点
				String[] locationes = template.getLocation().split(Cat.comma);
				tank.setBirthLocation(Location.newBuilder()
						.setDir(template.getBirthDir())
						.setX(Float.valueOf(locationes[0]))
						.setY(Float.valueOf(locationes[1]))
						.setZ(Float.valueOf(locationes[2]))
						.build());
				tank.setRoleName(template.getName());
				tank.setTeam(TEAM.valueOf(template.getTeam()));
				tank.setAiType(template.getAiType());
				tank.setAiInt(template.getAiInt());
				tank.setAiStr(template.getAiStr());
				logger.debug("循环坦克出生 tankId = {}, location = {}", tank.getTemplateId(), JSON.toJSONString(locationes));
				return tank;
			}
			
			return null;
		}
	}


	@Override
	protected void deathQuest(AbstractInstance killer, AbstractInstance target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deathNotifyMsg(AbstractInstance killer, AbstractInstance target) {
		// TODO Auto-generated method stub
		
	}
}
