package com.ourpalm.tank.app.map.state;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.domain.RoleWarInfo;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.template.PreseasonTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.SportMapInstance;

/**
 * 对战模式比赛开始
 * 
 * @author wangkun
 *
 */
public class BattleBeginStateMachine extends BeginStateMachine{

	
	public BattleBeginStateMachine(SportMapInstance mapInstance) {
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
		
		//计算新手赛AI是否超过玩家方杀人数，要全部降低属性
		try {
			this.aiKillBeyond();
		} catch (Exception e) {
			logger.error("", e);
		}
		
		//计算新手赛第二场 是否比玩家方杀人数多3个，多3个时降低敌方AI，提升我方AI
		
		try {
			this.aiKillBeyond3();
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

	/**
	 * 在新手赛第1，3，4，5局中，如果敌方人头数大于玩家方，降低敌方属性,玩家方追平了或者超过了又把原来的属性赋值回来
	 */
	private void aiKillBeyond() {
		PreseasonTemplate preseasonTemplate = mapInstance.getPreseasonTemplate();
		if (preseasonTemplate == null) {
			return;
		}
		if (preseasonTemplate.getId() != 1 && preseasonTemplate.getId() != 3 && preseasonTemplate.getId() != 4 && preseasonTemplate.getId() != 5) {
			return;
		}
		if (this.winTeam != null) {
			return;
		}
		List<int[]> eKilloList = preseasonTemplate.geteKilloList();
		if (eKilloList.isEmpty()) {
			return;
		}
		int redAllKill = 0;
		int blueAllKill = 0;
		for (AbstractInstance ist : mapInstance.getAllTank()) {
			if (ist.getTeam() == TEAM.RED) {
				redAllKill += ist.getKillCount();
			} else {
				blueAllKill += ist.getKillCount();
			}
		}
		if (redAllKill > blueAllKill) {
			for (AbstractInstance ist : mapInstance.getAllTank()) {
				if (ist.getRoleId() > 0)
					continue;
				if (ist.isAiIsReduced()) {
					continue;
				}
				for (int[] is : eKilloList) {
					int camp = is[0];
					if (camp == 0 && ist.getTeam() == TEAM.RED && ist.get(AttrType.valueOf(is[1])) > 0) {
//						System.err.println("aiKillBeyond敌原攻击力   "+ist.get(AttrType.valueOf(10)) );
						ist.getAiChangeAttribute().put(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) );
						ist.changeAttr(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) + ist.get(AttrType.valueOf(is[1])) * is[2] * 1.0f / 100);
						ist.setAiIsReduced(true);
//						System.err.println("aiKillBeyond敌方现攻击力   "+ist.get(AttrType.valueOf(10)) );
					}
					if (camp == 1 && ist.getTeam() == TEAM.BLUE && ist.get(AttrType.valueOf(is[1])) > 0) {
						ist.getAiChangeAttribute().put(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) );
						ist.changeAttr(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) + ist.get(AttrType.valueOf(is[1])) * is[2] * 1.0f / 100);
						ist.setAiIsReduced(true);
					}
				}
			}
		} else {
			for (AbstractInstance ist : mapInstance.getAllTank()) {
				if (ist.getRoleId() > 0)
					continue;
				if (!ist.isAiIsReduced()) {
					continue;
				}
				Map<AttrType, Float>  oldattr = ist.getAiChangeAttribute();
				for (Entry<AttrType, Float> entry : oldattr.entrySet()) {
					ist.changeAttr(entry.getKey(), entry.getValue());
				}
				ist.getAiChangeAttribute().clear();
				ist.setAiIsReduced(false);
				if(ist.getTeam()==TEAM.RED){
//					System.err.println("aiKillBeyond恢复敌现攻击力   "+ist.get(AttrType.valueOf(10)) );
				}
			}
		}
	}
	
	/**
	 * 在新手赛第2局中，如果敌方人头数大于玩家方3个，恢复敌方属性，加强我方属性，不大于3个时，敌方属性加强 
	 */
	private void aiKillBeyond3() {
		PreseasonTemplate preseasonTemplate = mapInstance.getPreseasonTemplate();
		if (preseasonTemplate == null) {
			return;
		}
		if (preseasonTemplate.getId() != 2) {
			return;
		}
		if (this.winTeam != null) {
			return;
		}
		List<int[]> beyond3List = preseasonTemplate.getBeyond3List();
		if (beyond3List.isEmpty()) {
			return;
		}
		int redAllKill = 0;
		int blueAllKill = 0;
		for (AbstractInstance ist : mapInstance.getAllTank()) {
			if (ist.getTeam() == TEAM.RED) {
				redAllKill += ist.getKillCount();
			} else {
				blueAllKill += ist.getKillCount();
			}
		}
		if (redAllKill -3 >= blueAllKill) {
//		if (redAllKill  >blueAllKill) {
			for (AbstractInstance ist : mapInstance.getAllTank()) {
				if (ist.getRoleId() > 0)
					continue;
				if (ist.isAiIsReduced()) {
					continue;
				}
				for (int[] is : beyond3List) {
					int camp = is[0];
					if (camp == 0 && ist.getTeam() == TEAM.RED && ist.get(AttrType.valueOf(is[1])) > 0) {
//						System.err.println("敌原攻击力   "+ist.get(AttrType.valueOf(10)) );
//						System.err.println("敌原穿甲值    " +ist.get(AttrType.valueOf(11)));
						ist.getAiChangeAttribute().put(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) );
						ist.changeAttr(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) + ist.get(AttrType.valueOf(is[1])) * is[2] * 1.0f / 100);
						ist.setAiIsReduced(true);
//						System.out.println();
//						System.err.println("敌方现攻击力   "+ist.get(AttrType.valueOf(10)) );
//						System.err.println("敌方现穿甲值    " +ist.get(AttrType.valueOf(11)));
					}
					if (camp == 1 && ist.getTeam() == TEAM.BLUE && ist.get(AttrType.valueOf(is[1])) > 0) {
//						System.err.println("我原攻击力   "+ist.get(AttrType.valueOf(10)) );
						ist.getAiChangeAttribute().put(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) );
						ist.changeAttr(AttrType.valueOf(is[1]), ist.get(AttrType.valueOf(is[1])) + ist.get(AttrType.valueOf(is[1])) * is[2] * 1.0f / 100);
						ist.setAiIsReduced(true);
//						System.err.println("我现攻击力   "+ist.get(AttrType.valueOf(10)) );
					}
				}
			}
		} else {
			for (AbstractInstance ist : mapInstance.getAllTank()) {
				if (ist.getRoleId() > 0)
					continue;
				if (!ist.isAiIsReduced()) {
					continue;
				}
				Map<AttrType, Float>  oldattr = ist.getAiChangeAttribute();
				for (Entry<AttrType, Float> entry : oldattr.entrySet()) {
					ist.changeAttr(entry.getKey(), entry.getValue());
				}
				ist.getAiChangeAttribute().clear();
				ist.setAiIsReduced(false);
				if(ist.getTeam()==TEAM.BLUE)
//				System.err.println("恢复我现攻击力   "+ist.get(AttrType.valueOf(10)) );
				if(ist.getTeam()==TEAM.RED){
//					System.err.println("恢复敌现攻击力   "+ist.get(AttrType.valueOf(10)) );
//					System.err.println("恢复敌现穿甲   "+ist.get(AttrType.valueOf(11)) );
				}
			}
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
			TankTemplate tankTemplate = GameContext.getTankApp().getTankTemplate(tankId);
			RoleTank roleTank = GameContext.getTankApp().getRoleTank(roleId, tankId);
			int tired = Math.min(tankTemplate.getTired(), roleTank.getTired() + 1);
			roleTank.setTired(tired);
			GameContext.getTankApp().saveRoleTank(roleTank);
			//通知客户端更新坦克信息
			GameContext.getTankApp().tankPush(roleTank);
		}
	}
}
