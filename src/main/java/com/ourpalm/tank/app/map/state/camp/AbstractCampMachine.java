package com.ourpalm.tank.app.map.state.camp;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.app.DAOFactory;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.app.map.camp.CampStar;
import com.ourpalm.tank.domain.RoleCamp;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_TANK_DIE_MSG;
import com.ourpalm.tank.message.CAMP_MSG.CampGoodsItem;
import com.ourpalm.tank.message.CAMP_MSG.STC_CAMP_OVER_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.template.CampaignMapTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.CampaignMapInstance;

public abstract class AbstractCampMachine {
	protected final static Logger logger = LogCore.runtime;
	protected CampaignMapInstance mapInstance;
	protected CampaignMapTemplate mapTemplate;
	protected TEAM winTeam ; 		//获胜方
	
	public AbstractCampMachine(CampaignMapInstance mapInstance){
		this.mapTemplate = mapInstance.getMapTemplate();
		this.mapInstance = mapInstance;
	}
	
	
	
	//判断比赛结束时间
	protected void checkOverTime(AbstractInstance tank){
		//游戏是否结束
		if(this.winTeam != null){
			return ;
		}
		//时间失败
		if((System.currentTimeMillis() - mapInstance.getBeginTime()) > mapTemplate.getOverTime() * 1000){
			this.winTeam = tank.getTeam() == TEAM.RED ? TEAM.BLUE : TEAM.RED;
			logger.debug("比赛超时...");
		}
	}
	
	
	//发放奖励
	protected void reward(AbstractInstance tank){
		//尚未分出胜负,返回
		if(this.winTeam == null){
			return ;
		}
		
		//比赛失败
		if(tank.getTeam() != this.winTeam){
			STC_CAMP_OVER_MSG.Builder builder = STC_CAMP_OVER_MSG.newBuilder();
			builder.setWin(false);
			tank.sendMsg(builder.build());
			logger.debug("发送比赛失败消息... ");
			return ;
		}
		
		int star = 1;
		//计算获得星级
		for(CampStar campStar : mapInstance.getCampStars()){
			if(campStar.condition(tank)){
				star += 1;
			}
		}
		logger.debug("比赛胜利, 获得星级 star = {}", star);
		//判断是否首次通关
		int id = mapTemplate.getId();
		RoleCamp roleCamp = DAOFactory.getRoleCampDao().get(tank.getRoleId(), id);
		if(roleCamp == null){
			this.firstRewards(tank, star);
			return ;
		}
		this.commonRewards(tank, star, roleCamp);
	}
	
	
	//首次奖励
	private void firstRewards(AbstractInstance tank, int star){
		STC_CAMP_OVER_MSG.Builder builder = STC_CAMP_OVER_MSG.newBuilder();
		int roleId = tank.getRoleId();
		Map<Integer, Integer> goodsMap = mapTemplate.getFirstGoodsMap();
		for(Entry<Integer, Integer> entry : goodsMap.entrySet()){
			CampGoodsItem item = CampGoodsItem.newBuilder()
					.setGoodsId(entry.getKey())
					.setNum(entry.getValue())
					.build();
			builder.addItems(item);
		}
		builder.setIron(mapTemplate.getFiron());
		builder.setStar(star);
		builder.setWin(true);
		builder.setFirst(true);
		
		//给奖励
		GameContext.getGoodsApp().addGoods(roleId, goodsMap, OutputType.passCommonBarrierInc.getInfo());
		
		//保存记录
		RoleCamp roleCamp = new RoleCamp();
		roleCamp.setRoleId(roleId);
		roleCamp.setId(mapTemplate.getId());
		roleCamp.setStar(star);
		DAOFactory.getRoleCampDao().save(roleCamp);
		
		tank.sendMsg(builder.build());
	}
	
	//通关普通奖励
	private void commonRewards(AbstractInstance tank, int star, RoleCamp roleCamp){
		STC_CAMP_OVER_MSG.Builder builder = STC_CAMP_OVER_MSG.newBuilder();
		int roleId = tank.getRoleId();
		Map<Integer, Integer> goodsMap = mapTemplate.getGoodsMap();
		for(Entry<Integer, Integer> entry : goodsMap.entrySet()){
			CampGoodsItem item = CampGoodsItem.newBuilder()
					.setGoodsId(entry.getKey())
					.setNum(entry.getValue())
					.build();
			builder.addItems(item);
		}
		builder.setIron(mapTemplate.getFiron());
		builder.setStar(star);
		builder.setWin(true);
		builder.setFirst(false);
		
		//给奖励
		GameContext.getGoodsApp().addGoods(roleId, goodsMap, OutputType.passCommonBarrierInc.getInfo());
		
		if(star > roleCamp.getStar()){
			roleCamp.setStar(star);
			DAOFactory.getRoleCampDao().save(roleCamp);
		}
		
		tank.sendMsg(builder.build());
	}
	
	
	//释放资源
	protected void release(AbstractInstance tank){
		if(this.winTeam == null){
			return ;
		}
		mapInstance.leave(tank.getId());
	}
	
	
	//死亡逻辑
	protected void playerDeath(AbstractInstance tank){
		int failDeath = mapTemplate.getDeathCount();
		int deathCount = tank.getDeathCount();
		//到达设定死亡次数限制，比赛结束
		if(failDeath != -1 && deathCount > failDeath){
			//比赛结束
			this.winTeam = tank.getTeam() == TEAM.RED ? TEAM.BLUE : TEAM.RED;
			logger.debug("玩家死亡超过最大限制, 比赛失败");
			return ;
		}
		
		//加入死亡列表
		mapInstance.putDeath(tank.getId());
		
		//设置坦克死亡时间
		tank.setDeathTime(System.currentTimeMillis());
		
		//广播死亡消息
		this.brodcastDeathMsg(tank.getId(), true);
	}
	
	//广播死亡消息
	protected void brodcastDeathMsg(int tankInstanceId, boolean hasRevive){
		STC_TANK_DIE_MSG deathNofityMsg = STC_TANK_DIE_MSG.newBuilder()
				.setId(tankInstanceId)
				.setReliveTime(mapTemplate.getReviveTime())
				.setHasRelive(hasRevive)
				.build();
		mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
				BATTLE_MSG.CMD_ID.STC_TANK_DIE_VALUE, 
				deathNofityMsg.toByteArray());
	}
}
