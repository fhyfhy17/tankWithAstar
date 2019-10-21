package com.ourpalm.tank.app.map.state;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.template.MapTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.SportMapInstance;

/**
 * 歼灭战状态机
 * 
 * @author wangkun
 *
 */
public class PerishBeginStateMachine extends BeginStateMachine{

	private boolean firstBrodcastWarScore = true;
	
	public PerishBeginStateMachine(SportMapInstance mapInstance) {
		super(mapInstance);
	}

	@Override
	public void update() {
		try{
			//判断是否正式开始
			if(!hadBeginGame()){
				return ;
			}
			//开场添加buff
			this.addBeginBuff();
			
			//广播开局战报
			this.brodcastWarScore();
			
		}catch(Exception e){
			logger.error("", e);
		}
		
		// 判断比赛结束时间
		try {
			this.checkOverTime();
		} catch (Exception e) {
			logger.error("", e);
		}

		//判断胜负
		try {
			this.checkWin();
		} catch(Exception e){
			logger.error("", e);
		}
		
		//计算比赛得分
		try{
			this.calcScore();
		} catch (Exception e){
			logger.error("", e);
		}

		// 通知比赛结束
		try {
			this.notifyGameOver();
		} catch (Exception e) {
			logger.error("", e);
		}
		
		// 触发任务
		try {
			this.completeQuest();
		} catch (Exception e) {
			logger.error("", e);
		}
		
		//累计战斗记录与坦克疲劳度
		try{
			this.countBattleRecordAndTired();
		} catch(Exception e){
			logger.error("", e);
		}

		// 切换比赛结束状态
		try {
			this.changeEndStateMachine();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	
	/** 计算得分 */
	private void calcScore(){
		if(this.winTeam == null){
			return ;
		}
		for(AbstractInstance ist : mapInstance.getAllTank()){
			//MVP得分 = (击杀数 *1.6 + 助攻数 * 0.8 + 协作 * 0.5）* （1 - 死亡数 * 0.05）
			float score = (ist.getKillCount() * 1.6f + ist.getHelpKill() * 0.8f + ist.getAidKill() * 0.5f) * (1f - ist.getDeathCount() * 0.05f);
			ist.setMvpScore(score);
		}
	}
	
	
	
	/** 判断胜负 */
	private void checkWin(){
		//已分出胜负返回
		if(this.winTeam != null){
			return ;
		}
		int blueKill = mapInstance.getBlueKillNum();
		int redKill = mapInstance.getRedKillNum();
		
		if(blueKill <= 0){
			this.winTeam = TEAM.RED;
		}
		else if(redKill <= 0){
			this.winTeam = TEAM.BLUE;
		}
	}
	
	//广播战绩表
	private void brodcastWarScore(){
		if(firstBrodcastWarScore){
			MapTemplate template = mapInstance.getTemplate();
			int overTime = template.getOverTime() + template.getReadyTime(); 
			overTime -= mapInstance.durationTime() ;
			if(overTime <= 0){
				overTime = 0;
			}
			mapInstance.brodcastWarScore(overTime);
			firstBrodcastWarScore = false;
		}
	}
	
	
	/** 累计战斗记录与坦克疲劳度 */
	private void countBattleRecordAndTired(){
		if(this.winTeam == null){
			return ;
		}
		
		for(int instanceId : mapInstance.getAllTanksId()){
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if(ist == null || ist.isRobot()){
				continue;
			}
			final int roleId = ist.getRoleId();
			final int tankId = ist.getTemplateId();
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			if(ist.getTeam() == this.winTeam){
				roleWarInfo.accumuBattleWinCount();
				//排行榜加胜场
				GameContext.getRankApp().rankAddWin(roleWarInfo.getRoleId());
			} else {
				roleWarInfo.accumuBattleLostCount();
			}
			GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
			
			//累计坦克疲劳度
			RoleTank roleTank = GameContext.getTankApp().getRoleTank(roleId, tankId);
			roleTank.setTired(roleTank.getTired() + 1);
			GameContext.getTankApp().saveRoleTank(roleTank);
			//通知客户端更新坦克信息
			GameContext.getTankApp().tankPush(roleTank);
		}
	}
	
}
