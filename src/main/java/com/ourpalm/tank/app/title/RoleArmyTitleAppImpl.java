package com.ourpalm.tank.app.title;

import static com.ourpalm.tank.app.GameContext.getTankApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.app.tank.TankAppImpl.AiCondition;
import com.ourpalm.tank.constant.RankEnum;
import com.ourpalm.tank.dao.SeasonMatchDao;
import com.ourpalm.tank.domain.Mail;
import com.ourpalm.tank.domain.MatchTeam;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.SeasonMatch;
import com.ourpalm.tank.message.ARMY_TITLE_MSG.STC_DRAW_DAY_REWARD_MSG;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.MATCH_MSG.WAR_TYPE;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.ArmyTitleTemplate;
import com.ourpalm.tank.template.BattleTeamWeightTemplate;
import com.ourpalm.tank.template.RankBattleMatchTemplate;
import com.ourpalm.tank.template.TankAIPathTemplate;
import com.ourpalm.tank.template.TankAIPointTypeMoveTemplate;
import com.ourpalm.tank.template.TankAiActionTemplate;
import com.ourpalm.tank.template.TankAiGroupTemplate;
import com.ourpalm.tank.template.TankAiTemplate;
import com.ourpalm.tank.template.TankAiTestTemplate;
import com.ourpalm.tank.template.TankMatchAiTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.MatchTank;

public class RoleArmyTitleAppImpl implements RoleArmyTitleApp {

	private final static Logger logger = LogCore.runtime;

	private Map<Integer, ArmyTitleTemplate> titles = new HashMap<>();
	//AI携带<组ID, List>
	private Map<Integer, List<TankAiGroupTemplate>> aiGroupMap = new HashMap<>();
	private Map<Integer, TankAiActionTemplate> aiActionMap = new HashMap<>();
	private Map<Integer, TankAIPathTemplate> aiPathPointMap = new HashMap<>();
	private Map<String, TankAIPointTypeMoveTemplate> aiPointMoveMap = new HashMap<>();  //AI点类型跃迁关系
	private Map<Integer, TankMatchAiTemplate> matchMapTemplateMap = new HashMap<>();	//匹配AI配置
	private Map<Integer, RankBattleMatchTemplate> rankBattleMatchMap = new HashMap<>(); //军衔排位赛配置
	private BattleTeamWeightTemplate battleTeamWeightTemplate;		//组队匹配，队伍分数权重
	private ArmyTitleTemplate maxTitle;
//	private RoleArmyTitleDao roleArmyTitleDao;
	private SeasonMatchDao seasonMatchDao;

	@Override
	public void start() {
		loadTitles();
		loadTankAiGroupTemplate();
		loadTankAiActionTemplate();
		loadTankAIPathTemplate();
		loadTankAIPointTypeMoveTemplate();
		loadTankMatchMapTemplate();
		loadRankBattleMatchTemplate();
		loadBattleTeamWeightTemplate();
		//赛季更新
		this.seasonMathUpdate();
	}

	@Override
	public ArmyTitleTemplate getArmyTitleTemplate(int titleId){
		return titles.get(titleId);
	}


	private ArmyTitleTemplate getArmyTitleTemplateByScore(int score){
		ArmyTitleTemplate currTemplate = null;
		for(ArmyTitleTemplate template : titles.values()){
			if(score >= template.getScore()){
				currTemplate = template;
				continue;
			}
			break;
		}
		return currTemplate;
	}



	/** 随机返回下一个路径类型 */
	@Override
	public int nextAiPointType(int pointType){
		//随机一次判断是否迁移点类型
		int random = RandomUtil.randomInt(100);
		//暂且70%概率迁移
		if(random < 70){
			TankAIPointTypeMoveTemplate template = aiPointMoveMap.get(pointType);
			if(template == null){
				return pointType;
			}
			return template.getNextPointType();
		}
		return pointType;
	}
	
	@Override
	public AbstractInstance buildNpcTank(int titleId, AiCondition ac, int preseasonId) {

		AbstractInstance tank = GameContext.getTankApp().createAiTank(ac, preseasonId);

		//随机战斗力
		// tank.setBattleScore(template.randomBattleScore());
		tank.setBattleScore(getTankApp().calcShowBattleScore(tank));
		tank.setActualBattleScore(getTankApp().calcAllBattleScore(tank));

		//获取AI坦克的行为几率配置
		TankAiActionTemplate actionTemplate = aiActionMap.get(titleId);

		//获取AI坦克所携带的物品和技能
		List<TankAiGroupTemplate> list = aiGroupMap.get(actionTemplate.getGroup());
		//初始化携带物品和技能
		TankAiGroupTemplate groupTemplate = list.get(RandomUtil.randomInt(list.size()));
		groupTemplate.initGoods(tank);
		groupTemplate.initSkill(tank);

		//AI行径类型
		TankAIPathTemplate pathTemplate = aiPathPointMap.get(actionTemplate.getPathId());

		//设置AI行为
		tank.setAiAction(actionTemplate);

		//设置AI行径类型
		tank.setAiPathPoint(pathTemplate);

		return tank;
	}
	@Override
	public AbstractInstance buildSpecificNpcTank(int titleId, int tankId, int preseasonId) {
		
		AbstractInstance tank = GameContext.getTankApp().createNpcTank(tankId, preseasonId);
		
		//获取AI坦克的行为几率配置
		TankAiActionTemplate actionTemplate = aiActionMap.get(titleId);
		
		//获取AI坦克所携带的物品和技能
		List<TankAiGroupTemplate> list = aiGroupMap.get(actionTemplate.getGroup());
		//初始化携带物品和技能
		TankAiGroupTemplate groupTemplate = list.get(RandomUtil.randomInt(list.size()));
		groupTemplate.initGoods(tank);
		groupTemplate.initSkill(tank);
		
		//AI行径类型
		TankAIPathTemplate pathTemplate = aiPathPointMap.get(actionTemplate.getPathId());
		
		//设置AI行为
		tank.setAiAction(actionTemplate);
		
		//设置AI行径类型
		tank.setAiPathPoint(pathTemplate);
		
		return tank;
	}
	
	@Override
	public AbstractInstance buildNpcTankOld(int titleId, int averageScore, int preseasonId) {

		TankAiTemplate template = GameContext.getTankApp().getTankAiTemplate2(averageScore);
		List<Integer> aiTanks = template.getTankList();
		int index = RandomUtil.randomInt() % aiTanks.size();
		int tankId = aiTanks.get(index);
		
		AbstractInstance tank = buildSpecificNpcTank(titleId, tankId, preseasonId);
		
		//随机战斗力
		tank.setBattleScore(template.randomBattleScore());
		tank.setActualBattleScore(tank.getBattleScore());
		
		return tank;
	}

	/** 构造AI坦克实例 */
	@Override
	public AbstractInstance buildNpcTankTest(int titleId, int camp, int index){
		TankAiTestTemplate template = GameContext.getTankApp().getTankAiTestTemplate(camp, index);
		int tankId = template.getTankId();

		AbstractInstance tank = GameContext.getTankApp().createNpcTank(tankId, 0);

		//随机战斗力
		tank.setBattleScore(template.getBattleScore());

		//获取AI坦克的行为几率配置
		TankAiActionTemplate actionTemplate = aiActionMap.get(titleId);

		//获取AI坦克所携带的物品和技能
		List<TankAiGroupTemplate> list = aiGroupMap.get(actionTemplate.getGroup());
		//初始化携带物品和技能
		TankAiGroupTemplate groupTemplate = list.get(RandomUtil.randomInt(list.size()));
		groupTemplate.initGoods(tank);
		groupTemplate.initSkill(tank);

		//AI行径类型
		TankAIPathTemplate pathTemplate = aiPathPointMap.get(actionTemplate.getPathId());

		//设置AI行为
		tank.setAiAction(actionTemplate);

		//设置AI行径类型
		tank.setAiPathPoint(pathTemplate);

		return tank;
	}

	private ArmyTitleTemplate getTitle(int id) {
		return this.titles.get(id);
	}


	@Override
	public void rankBattleResult(int roleId, int camp, boolean hadWin, float blueHopeValue, float redHopeValue) {
		float hopeValue = blueHopeValue;
		if(camp == TEAM.RED_VALUE){
			hopeValue = redHopeValue;
		}

		int winScore = hadWin ? 1 : 0;

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		ArmyTitleTemplate template = titles.get(role.getCurrTitleId());

		float multi = hadWin ? template.getWinMultiple() : template.getLostMultiple();
		multi *= 32;

		int score = (int)(role.getScore() + multi * (winScore - hopeValue));

		ArmyTitleTemplate newTemplate = this.getArmyTitleTemplateByScore(score);

		int currTitleId = newTemplate.getId_i();
		role.setCurrTitleId(currTitleId);
		role.setScore(score);
		role.setSeasonMaxTitleId(Math.max(currTitleId, role.getSeasonMaxTitleId()));
		role.setMaxTitleId(Math.max(currTitleId, role.getMaxTitleId()));

		GameContext.getUserApp().saveRoleAccount(role);

		//触发活动
		GameContext.getActivityApp().armyTitleLevel(roleId, currTitleId);
		//排行榜
//		GameContext.getSeasonRankApp().addRank(roleId, currTitleId, score);
		GameContext.getRankApp().addRank(roleId, score, RankEnum.SeasonRankKey, -1,currTitleId);
	}

	@Override
	public void createInit(int roleId, int titleId) {
		ArmyTitleTemplate template = this.getTitle(titleId);

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		role.setCurrTitleId(titleId);
		role.setSeasonMaxTitleId(titleId);
		role.setMaxTitleId(titleId);
		role.setScore(template.getScore());
		//第一天不可领取
		role.setDayDrawRewardsTime(System.currentTimeMillis());
		role.setLastSeansonMaxTitleId(titleId);
		//设置当前赛季结束时间
		SeasonMatch seasonMatch = this.seasonMatchDao.get();
		role.setSeasonEndDate(seasonMatch.getEndTime());

		GameContext.getUserApp().saveRoleAccount(role);
	}



	@Override
	public int getMatchScore(int roleId, int tankId, WAR_TYPE type) {
		//排行模式为排行积分
		if(type == WAR_TYPE.RANK){
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			return role.getScore();
		}

		//其他模式为对战积分
		return GameContext.getTankApp().calcAllMatchScore(roleId, tankId);
	}



	@Override
	public MatchScore getTeamMatchScore(MatchTeam matchTeam){
		List<MatchTank> tanks = new ArrayList<>(matchTeam.allTeamTank());
		Collections.sort(tanks);

		int index = 0;
		float allScore = 0;
		float allWeight = 0;
		List<Float> weights = this.battleTeamWeightTemplate.getWeights();
		for(MatchTank tank : tanks){
			allScore += tank.getScore() * weights.get(index);
			allWeight += weights.get(index);
			index ++;
		}
		allScore /= allWeight;

		MatchScore matchScore = new MatchScore();
		matchScore.setScore((int)allScore);

		if(matchTeam.getWarType() == WAR_TYPE.RANK_VALUE){
			ArmyTitleTemplate currTemplate = this.getArmyTitleTemplateByScore((int)allScore);
			ArmyTitleTemplate minTemplate = this.getArmyTitleTemplate(currTemplate.getRankMinTitle());
			matchScore.setMinScore(minTemplate.getScore());
		} else {
			matchScore.setMinScore((int)(allScore * 0.8f));
		}

		if(logger.isDebugEnabled()){
			logger.debug("计算队伍匹配分 teamId = {},  warType = {},  matchScore = {}",
					matchTeam.getTeamId(), matchTeam.getWarType(), JSON.toJSONString(matchScore));
		}

		return matchScore;
	}


	private void loadTitles() {
		String fileName = XlsSheetType.armyTitleTemplate.getXlsFileName();
		String sheetName = XlsSheetType.armyTitleTemplate.getSheetName();
		try {
			this.titles = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, ArmyTitleTemplate.class);
			for (ArmyTitleTemplate template : titles.values()) {
				if (maxTitle == null || template.getId_i() > maxTitle.getId_i()) {
					this.maxTitle = template;
				}
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	//加载机器人AI组
	private void loadTankAiGroupTemplate(){
		String sourceFile = XlsSheetType.TankAiGroupTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAiGroupTemplate.getSheetName();
		try{
			List<TankAiGroupTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankAiGroupTemplate.class);
			for(TankAiGroupTemplate template : list){
				template.init();
				int groupId = template.getGroup();
				List<TankAiGroupTemplate> _list = this.aiGroupMap.get(groupId);
				if(_list == null){
					_list = new ArrayList<>();
					this.aiGroupMap.put(groupId, _list);
				}
				_list.add(template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	//加载机器人行为
	private void loadTankAiActionTemplate(){
		String sourceFile = XlsSheetType.TankAiActionTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAiActionTemplate.getSheetName();
		try{
			List<TankAiActionTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankAiActionTemplate.class);
			for(TankAiActionTemplate template : list){
				template.init();
				aiActionMap.put(template.getId(), template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}


	//加载AI行径路径点类型
	private void loadTankAIPathTemplate(){
		String sourceFile = XlsSheetType.TankAIPathTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAIPathTemplate.getSheetName();
		try{
			List<TankAIPathTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankAIPathTemplate.class);
			for(TankAIPathTemplate template : list){
				template.init();
				aiPathPointMap.put(template.getId(), template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	//加载AI行径点类型跃迁关系
	private void loadTankAIPointTypeMoveTemplate(){
		String sourceFile = XlsSheetType.TankAIPointTypeMoveTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAIPointTypeMoveTemplate.getSheetName();
		try{
			aiPointMoveMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TankAIPointTypeMoveTemplate.class);
		}catch(Exception e){
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	//加载随机地图配置
	private void loadTankMatchMapTemplate(){
		String sourceFile = XlsSheetType.TankMatchMapTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankMatchMapTemplate.getSheetName();
		try{
			List<TankMatchAiTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankMatchAiTemplate.class);
			for(TankMatchAiTemplate template : list){
				template.init();
				this.matchMapTemplateMap.put(template.getId(), template);
			}
		}catch(Exception e){
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}


	//加载赛季配置
	private void loadRankBattleMatchTemplate(){
		String sourceFile = XlsSheetType.RankBattleMatchTemplate.getXlsFileName();
		String sheetName = XlsSheetType.RankBattleMatchTemplate.getSheetName();
		try{
			boolean checkFlag = false;
			List<RankBattleMatchTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, RankBattleMatchTemplate.class);
			Date now = new Date();
			for(RankBattleMatchTemplate template : list){
				template.init();
				//检测赛季结束时间是否有效
				if(!checkFlag && template.getEndDate().after(now)){
					checkFlag = true;
				}
				this.rankBattleMatchMap.put(template.getId(), template);
			}
			if(!checkFlag){
				LogCore.startup.error("!!!!!!!!!!!!!!!!! 赛季时间配置错误，所有赛季都已过期.....");
			}
		}catch(Exception e){
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}


	//加载组队分权重
	public void loadBattleTeamWeightTemplate(){
		String sourceFile = XlsSheetType.BattleTeamWeightTemplate.getXlsFileName();
		String sheetName = XlsSheetType.BattleTeamWeightTemplate.getSheetName();
		try{
			List<BattleTeamWeightTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BattleTeamWeightTemplate.class);
			this.battleTeamWeightTemplate = list.get(0);
			this.battleTeamWeightTemplate.init();
		}catch(Exception e){
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}



	/** 返回匹配地图配置 */
	@Override
	public TankMatchAiTemplate getTankMatchMapTemplate(int titleId){
		return this.matchMapTemplateMap.get(titleId);
	}


	@Override
	public void stop() {
	}


	@Override
	public STC_DRAW_DAY_REWARD_MSG drawDayRaward(int roleId) {
		STC_DRAW_DAY_REWARD_MSG.Builder builder = STC_DRAW_DAY_REWARD_MSG.newBuilder();
		builder.setSuccess(false);
		builder.setInfo("");

		//判断领取时间是否可领取奖励
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		if(DateUtil.isSameDay(System.currentTimeMillis(), role.getDayDrawRewardsTime())){
			builder.setInfo(Tips.ARMY_TITLE_REWARD_DRAWED);
			return builder.build();
		}
		//根据本赛季最高军衔，领取对应奖励
		ArmyTitleTemplate template = getTitle(role.getLastSeansonMaxTitleId());
		if(template == null){
			builder.setInfo(Tips.ARMY_TITLE_ID_NOEXIST);
			return builder.build();
		}
		final int gold = template.getGold();
		final int iron = template.getIron();
		final int honor = template.getHonor();
		GameContext.getUserAttrApp().changeAttribute(roleId,
				OutputType.armyTitleDayRewardInc,
				AttrUnit.build(RoleAttr.gold, Operation.add, gold),
				AttrUnit.build(RoleAttr.iron, Operation.add, iron),
				AttrUnit.build(RoleAttr.honor, Operation.add, honor));

		//设置领奖时间
		role.setDayDrawRewardsTime(System.currentTimeMillis());

		//替换昨日赛季军衔为本赛季军衔
		role.setLastSeansonMaxTitleId(role.getSeasonMaxTitleId());

		GameContext.getUserApp().saveRoleAccount(role);

		builder.setSuccess(true);
		builder.setGold(gold);
		builder.setHonor(honor);
		builder.setIron(iron);

		return builder.build();
	}


	@Override
	public void login(int roleId, boolean nextDay) {
		if(!nextDay){
			return ;
		}

		SeasonMatch seasonMath = this.seasonMatchDao.get();
		//判断是否赛季结束
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		Date roleEndDate = role.getSeasonEndDate();
		if(roleEndDate == null){
			roleEndDate = seasonMath.getEndTime();
			role.setSeasonEndDate(seasonMath.getEndTime());
			GameContext.getUserApp().saveRoleAccount(role);
		}

		if(seasonMath.getBeginTime().after(roleEndDate)){
			int seasonMaxTitleId = role.getSeasonMaxTitleId();
			ArmyTitleTemplate template = this.titles.get(seasonMaxTitleId);
			int goodsId = template.getRewardId();
			//邮件发送奖励
			String id = GameContext.getIdFactory().nextStr();
			Mail mail = new Mail(id);
			mail.setTitle(template.getMailTitle());
			mail.setContent(template.getMailContext());
			mail.setAttachget(false);
			mail.addAttach(goodsId, 1);
			mail.setReciverId(roleId);
			GameContext.getMailApp().save(mail);

			//重置赛季军衔
			int nextSeasonTitleId = template.getNextSeasonId();
			role.setCurrTitleId(nextSeasonTitleId);
			role.setSeasonEndDate(seasonMath.getEndTime());
			role.setSeasonMaxTitleId(nextSeasonTitleId);
			role.setLastSeansonMaxTitleId(nextSeasonTitleId);
			//重置赛季积分
			ArmyTitleTemplate nextTemplate = this.titles.get(seasonMaxTitleId);
			role.setScore(nextTemplate.getScore());

			//当天不可领取奖励
			role.setDayDrawRewardsTime(System.currentTimeMillis());
			GameContext.getUserApp().saveRoleAccount(role);

			logger.debug("赛季结束邮件, roleId = {} 重置军衔完毕", roleId);
		}
	}


	/** 赛季定时更新 */
	@Override
	public void seasonMathUpdate(){
		SeasonMatch seasonMatch = seasonMatchDao.get();
		if(seasonMatch == null){
			this.createNewSeasonMath();
			return ;
		}
		//验证是否赛季结束，自动进入下一个赛季
		Date endDate = seasonMatch.getEndTime();
		Date now = new Date();
		if(now.after(endDate)){
			//进入下个赛季
			this.createNewSeasonMath();
		}
	}


	/** 创建新的赛季 */
	private void createNewSeasonMath(){
		logger.debug("开始创建新的赛季...");
		Date now = new Date();
		for(RankBattleMatchTemplate template : rankBattleMatchMap.values()){
			Date endDate = template.getEndDate();
			if(endDate.after(now)){
				SeasonMatch newMatch = new SeasonMatch();
				newMatch.setId(template.getId());
				newMatch.setBeginTime(now);
				newMatch.setEndTime(endDate);
				seasonMatchDao.save(newMatch);
				logger.debug("创建新的赛季, 结束时间 = {}", template.getEndTime());
				//重置排行榜
				GameContext.getSeasonRankApp().resetRank(newMatch);

				return ;
			}
		}
	}


	/** 返回当前赛季信息 */
	@Override
	public String getSeasonMatchContext(){
		SeasonMatch seasonMatch = seasonMatchDao.get();
		RankBattleMatchTemplate template = this.rankBattleMatchMap.get(seasonMatch.getId());
		return template.getTips();
	}

	public void setSeasonMatchDao(SeasonMatchDao seasonMatchDao) {
		this.seasonMatchDao = seasonMatchDao;
	}
}
