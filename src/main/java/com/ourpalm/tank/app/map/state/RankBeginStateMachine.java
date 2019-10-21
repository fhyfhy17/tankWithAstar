package com.ourpalm.tank.app.map.state;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.SportMapInstance;

/**
 * 军衔排行比赛开始状态机
 * 
 * @author wangkun
 *
 */
public class RankBeginStateMachine extends BeginStateMachine{

	public RankBeginStateMachine(SportMapInstance mapInstance) {
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
			
		}catch(Exception e){
			logger.error("", e);
		}
		
		// 判断比赛结束时间
		try {
			this.checkOverTime();
		} catch (Exception e) {
			logger.error("", e);
		}

		// 判断占领旗帜逻辑
		try {
			this.checkCaptureFlag();
		} catch (Exception e) {
			logger.error("", e);
		}

		// 计算MVP
		try {
			this.checkMVP();
		} catch (Exception e) {
			logger.error("", e);
		}

		// 统计战斗军衔结果
		try {
			this.rankBattleResultAndArmyTitle();
		} catch (Exception e) {
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

		// 切换比赛结束状态
		try {
			this.changeEndStateMachine();
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	
	
	/** 统计排位赛结果 */
	private void rankBattleResultAndArmyTitle(){
		final TEAM winTeam = this.winTeam ;
		if(winTeam == null){
			return ;
		}
		//计算双方期望值
		float blueAve = 0;
		float redAve = 0;
		for(int instanceId : mapInstance.getAllTanksId()){
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if(ist == null){
				continue;
			}
			if(ist.getTeam() == TEAM.BLUE){
				blueAve += ist.getMatchScore();
				continue;
			}
			redAve += ist.getMatchScore();
		}
		blueAve /= 5;
		redAve /= 5;
		// 期望得分 = 1 / ( 1 + 10 ^ (对方平均分 - 本方平均分) / 400)
		float redHopeValue = (float)(1 / (1 + Math.pow(10, (blueAve - redAve) / 400)));
		float blueHopeValue = (float)(1 / (1 + Math.pow(10, (redAve - blueAve) / 400)));
		
		if(logger.isDebugEnabled()){
			logger.debug("双方期望得分为 blue = {}, red = {}", blueHopeValue, redHopeValue);
		}
		
		for(int instanceId : mapInstance.getAllTanksId()){
			AbstractInstance ist = GameContext.getTankApp().getInstance(instanceId);
			if(ist == null || ist.isRobot()){
				continue;
			}
			final int roleId = ist.getRoleId();
			RoleWarInfo roleWarInfo = GameContext.getBattleApp().getRoleWarInfo(roleId);
			//累计击杀数
			roleWarInfo.accumuRankAllKillCount(ist.getKillCount());
			//累计死亡数
			roleWarInfo.accumuRankAllDedCount(ist.getDeathCount());
			//累计一次生命周期内最大击杀数
			roleWarInfo.accumuRankMaxOneLifeKillCount(ist.getLifeKillCount());
			//累计单场最大击杀数
			roleWarInfo.accumuRankMaxKillCount(ist.getKillCount());
			//累计最高助攻数
			roleWarInfo.accumuRankMaxHelperCount(ist.getHelpKill());
			//累计最大伤害输出
			roleWarInfo.accumuRankMaxOutputHurt(ist.getHitAllDamage());
			//累计最大承受伤害数
			roleWarInfo.accumuRankMaxInputHurt(ist.getReceiveAllDamage());
			
			//累计坦克胜率
			RoleTank roleTank = GameContext.getTankApp().getRoleTank(roleId, ist.getTemplateId());
			
			//累计胜负
			if(ist.getTeam() == winTeam){
				roleWarInfo.accumuRankWinCount();
				roleWarInfo.accumuRankLoopWinCount();
				roleTank.accumuWinCount();
				//排行榜加胜场
				GameContext.getRankApp().rankAddWin(roleWarInfo.getRoleId());
			}else{
				roleWarInfo.accumuRankLostCount();
				roleWarInfo.setRankLoopWinCount(0);
				roleTank.accumuLostCount();
			}
			//MVP
			if(roleId == this.mvpRoleId){
				roleWarInfo.accumuRankMvp();
				this.mvpCount = roleWarInfo.getRankMvp();
			}
			
			
			GameContext.getRoleArmyTitleApp().rankBattleResult(roleId, ist.getTeam().getNumber(), 
					ist.getTeam() == winTeam, blueHopeValue, redHopeValue);
			
			GameContext.getTankApp().saveRoleTank(roleTank);
			GameContext.getBattleApp().saveRoleWarInfo(roleWarInfo);
		}
	}
}
