package com.ourpalm.tank.app.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.RoleLotteryDao;
import com.ourpalm.tank.dao.RoleMemberDao;
import com.ourpalm.tank.dao.RoleUseMemberDao;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleLottery;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MEMBER_MSG.LotteryRewardType;
import com.ourpalm.tank.message.MEMBER_MSG.LotteryType;
import com.ourpalm.tank.message.MEMBER_MSG.MemberItem;
import com.ourpalm.tank.message.MEMBER_MSG.MemberUpdateType;
import com.ourpalm.tank.message.MEMBER_MSG.STC_MEMBER_UPDATE_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.message.ROLE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.ROLE_MSG.STC_PROMPT_MSG;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.MedalTemplate;
import com.ourpalm.tank.template.MemberComposite;
import com.ourpalm.tank.template.MemberLevel;
import com.ourpalm.tank.template.MemberLottery;
import com.ourpalm.tank.template.MemberLotteryConfig;
import com.ourpalm.tank.template.MemberTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.MemberLotteryPeshe;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.LotteryResult;
import com.ourpalm.tank.vo.result.MemberCompositeResult;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.Reward;

public class MemberAppImpl implements MemberApp {

	private RoleMemberDao roleMemberDao;
	private RoleLotteryDao roleLotteryDao;
	private RoleUseMemberDao roleUseMemberDao;
	private MemberAttrEvaluate memberAttrEvaluate;

	// <id, template>
	private Map<Integer, MemberTemplate> members = new HashMap<>();
	// <goodsId, template>
	private Map<Integer, MemberComposite> compositeCosts = new HashMap<>();
	// <quality, <level, template>>
	private Map<Integer, Map<Integer, MemberLevel>> levels = new HashMap<>();

	private MemberLotteryConfig lotteryConfig;
	private List<MemberLotteryPeshe> diamondsOncelottery = new ArrayList<>();
	private List<MemberLotteryPeshe> diamondsMultiLottery = new ArrayList<>();// 金币五连抽特殊一抽列表
	private List<MemberLotteryPeshe> diamondsFloorLottery = new ArrayList<>();

	private List<MemberLotteryPeshe> ironOncelottery = new ArrayList<>();
	private List<MemberLotteryPeshe> ironMultiLottery = new ArrayList<>();// 银币五连抽特殊一抽列表
	private List<MemberLotteryPeshe> ironFloorLottery = new ArrayList<>();

	// 首次金币单抽概率
	private List<MemberLotteryPeshe> firstDiamondsOnceLottery = new ArrayList<>();
	// 首次金币五连抽
	private List<MemberLotteryPeshe> firstDiamondsMultiLottery = new ArrayList<>();

	public static int DIAMOND_FREEZE_TIME; // 分钟
	public static int IRON_FREEZE_TIME; // 分钟

	@Override
	public void start() {
		this.loadMember();
		this.loadLotteryConfig();
		this.loadLottery();
		this.loadComposite();
		this.loadLevel();
		DIAMOND_FREEZE_TIME = SysConfig.get(1);
		IRON_FREEZE_TIME = SysConfig.get(2);
	}

	@Override
	public void stop() {
	}

	private void loadMember() {
		String fileName = XlsSheetType.memberTemplate.getXlsFileName();
		String sheetName = XlsSheetType.memberTemplate.getSheetName();
		try {
			this.members = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberTemplate.class);
			for (MemberTemplate template : members.values()) {
				if (template.getSpell() > 0 && GameContext.getSkillApp().getSkillTemplate(template.getSpell()) == null) {
					throw new IllegalArgumentException(String.format("成员模板配置的技能不存在 templateId=%d, skillId=%d", template.getId(), template.getSpell()));
				}
				// List<SimplePeshe> pesheList =
				// compositeRandoms.get(template.getMemberId());
				// if (pesheList == null) {
				// pesheList = new ArrayList<>();
				// compositeRandoms.put(template.getMemberId(), pesheList);
				// }
				template.init();
				// pesheList.add(buildCompositePeshe(template));
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadLotteryConfig() {
		String fileName = XlsSheetType.memberLotteryConfig.getXlsFileName();
		String sheetName = XlsSheetType.memberLotteryConfig.getSheetName();
		try {
			List<MemberLotteryConfig> list = XlsPojoUtil.sheetToList(fileName, sheetName, MemberLotteryConfig.class);
			this.lotteryConfig = list.get(0);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadLottery() {
		String fileName = XlsSheetType.memberLottery.getXlsFileName();
		String sheetName = XlsSheetType.memberLottery.getSheetName();
		try {
			List<MemberLottery> list = XlsPojoUtil.sheetToList(fileName, sheetName, MemberLottery.class);
			for (MemberLottery template : list) {
				if (template.getDiamondsWeight() > 0) {
					diamondsOncelottery.add(buildLotteryPeshe(template, template.getDiamondsWeight()));
				}
				if (template.getDiamondsMultiWeight() > 0) {
					diamondsMultiLottery.add(buildLotteryPeshe(template, template.getDiamondsMultiWeight()));
				}
				if (template.getDiamondsFloorWeight() > 0) {
					diamondsFloorLottery.add(buildLotteryPeshe(template, template.getDiamondsFloorWeight()));
				}
				if (template.getIronWeight() > 0) {
					ironOncelottery.add(buildLotteryPeshe(template, template.getIronWeight()));
				}
				if (template.getIronMultiWeight() > 0) {
					ironMultiLottery.add(buildLotteryPeshe(template, template.getIronMultiWeight()));
				}
				if (template.getIronFloorWeight() > 0) {
					ironFloorLottery.add(buildLotteryPeshe(template, template.getIronFloorWeight()));
				}
				if (template.getFirstOnceDiamondsWeight() > 0) {
					firstDiamondsOnceLottery.add(buildLotteryPeshe(template, template.getFirstOnceDiamondsWeight()));
				}
				if (template.getFirstMultiDiamondsWeight() > 0) {
					firstDiamondsMultiLottery.add(buildLotteryPeshe(template, template.getFirstMultiDiamondsWeight()));
				}
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadComposite() {
		String fileName = XlsSheetType.memberComposite.getXlsFileName();
		String sheetName = XlsSheetType.memberComposite.getSheetName();
		try {
			this.compositeCosts = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberComposite.class);
			for (MemberComposite template : this.compositeCosts.values()) {
				template.init();
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadLevel() {
		String fileName = XlsSheetType.memberLevel.getXlsFileName();
		String sheetName = XlsSheetType.memberLevel.getSheetName();
		try {
			List<MemberLevel> list = XlsPojoUtil.sheetToList(fileName, sheetName, MemberLevel.class);
			for (MemberLevel template : list) {
				Map<Integer, MemberLevel> levelMap = levels.get(template.getQuality());
				if (levelMap == null) {
					levelMap = new HashMap<>();
					levels.put(template.getQuality(), levelMap);
				}
				levelMap.put(template.getLevel(), template);
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	// private SimplePeshe buildCompositePeshe(MemberTemplate template){
	// SimplePeshe peshe = new SimplePeshe();
	// peshe.setGon(template.getCompositeWeight());
	// peshe.setId(template.getId());
	// peshe.setNum(1);
	// return peshe;
	// }

	private MemberLotteryPeshe buildLotteryPeshe(MemberLottery template, int weight) {
		MemberLotteryPeshe peshe = new MemberLotteryPeshe();
		peshe.setType(template.getRewardType());
		peshe.setId(template.getRewardId());
		peshe.setNum(template.getNum());
		peshe.setGon(weight);
		return peshe;
	}

	public MemberLevel getMemberLevel(int quality, int level) {
		Map<Integer, MemberLevel> levelMap = this.levels.get(quality);
		if (Util.isEmpty(levelMap)) {
			return null;
		}
		return levelMap.get(level);
	}

	public MemberTemplate getMemberTemplate(int templateId) {
		return this.members.get(templateId);
	}

	public MemberLotteryConfig getLotteryConfig() {
		return this.lotteryConfig;
	}

	public RoleLottery getRoleLottery(int roleId) {
		RoleLottery roleLottery = this.roleLotteryDao.get(roleId);
		if (roleLottery == null) {
			roleLottery = new RoleLottery();
			roleLottery.setRoleId(roleId);
			roleLotteryDao.save(roleLottery);
		}
		return roleLottery;
	}

	public Map<String, RoleMember> getRoleMembers(int roleId) {
		return roleMemberDao.getAll(roleId);
	}

	public Map<Integer, String> getUseMemberId(int roleId) {
		return roleUseMemberDao.getAll(roleId);
	}

	public Map<String, RoleMember> getUseMember(int roleId) {
		Map<Integer, String> idMap = getUseMemberId(roleId);
		return roleMemberDao.get(roleId, idMap.values());
	}

	public void login(int roleId, boolean nextDay) {
		RoleLottery lottery = getRoleLottery(roleId);
		if (nextDay) {
			lottery.setDiamondsFreeCount(this.lotteryConfig.getDiamondsFreeCount());
			lottery.setIronFreeCount(this.lotteryConfig.getIronFreeCount());
			roleLotteryDao.save(lottery);
		}

		if (lottery.getDiamondsFreeCount() > 0 || lottery.getIronFreeCount() > 0) {
			RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
			if (connect != null) {
				STC_PROMPT_MSG.Builder builder = STC_PROMPT_MSG.newBuilder();
				builder.setPrompt(PROMPT.MEMBER_LOTTERY);
				connect.sendMsg(ROLE_MSG.CMD_TYPE.CMD_TYPE_ROLE_VALUE, ROLE_MSG.CMD_ID.STC_PROMPT_VALUE, builder.build().toByteArray());
			}
		}

	}

	public LotteryResult lottery(int roleId, LotteryType type) {

		RoleLottery roleLottery = this.getRoleLottery(roleId);
		boolean isReachTime = isReachTime(roleLottery, type);
		if (!isReachTime) {
			return LotteryResult.newFailure(Tips.LOTTERY_FREEZE);
		}
		if (type == LotteryType.freeDiamond) {
			if (roleLottery.getDiamondsFreeCount() <= 0) {
				return LotteryResult.newFailure("次数不足");
			}
		}
		if (type == LotteryType.freeIron) {
			if (roleLottery.getIronFreeCount() <= 0) {
				return LotteryResult.newFailure("次数不足");
			}
		}

		int result = this.lotteryCost(roleLottery, type);
		switch (result) {
		case 1:
			return LotteryResult.newFailure(Tips.NEDD_DIAMONDS);
		case 2:
			return LotteryResult.newFailure(Tips.NEED_IRON);
		}

		int count = LotteryType.diamondsOnce.equals(type) || LotteryType.ironOnce.equals(type) ? 1 : MULTI_COUNT;
		GameContext.getGoodsApp().addGoods(roleId, lotteryConfig.getRewardId(type), count, OutputType.lotteryInc.getInfo());

		switch (type) {
		case diamondsOnce:
		case freeDiamond:
			return this.goldSingleLottery(roleLottery);
		case diamondsMulti:
			return this.goldMultiLottery(roleLottery);
		case ironOnce:
		case freeIron:
			return this.ironSingleLottery(roleLottery);
		case ironMulti:
			return this.ironMultiLottery(roleLottery);
		default:
			return null;
		}
	}

	private LotteryResult goldSingleLottery(RoleLottery roleLottery) {
		LotteryResult result = new LotteryResult();
		int roleId = roleLottery.getRoleId();
		int count = roleLottery.getDiamondsCount();// 金币单抽次数
		List<MemberLotteryPeshe> list = diamondsOncelottery;
		if (count >= MULTI_COUNT) {
			list = diamondsMultiLottery;
			roleLottery.setDiamondsCount(0);
		} else {
			roleLottery.setDiamondsCount(count + 1);
		}
		// 首次单抽概率不同
		if (roleLottery.isFirstOneDiamonds()) {
			list = this.firstDiamondsOnceLottery;
			roleLottery.setFirstOneDiamonds(false);
		}
		MemberLotteryPeshe lotteryResult = RandomUtil.getPeshe(list);
		if (lotteryResult == null) {
			result.success();
			return result;
		}
		Reward reward = this.goldFloorLottery(roleLottery, lotteryResult);

		result.addReward(reward);
		this.lotteryReward(roleId, reward, OutputType.lotteryDiamondsOnceInc.getInfo());

		roleLotteryDao.save(roleLottery);

		result.success();
		return result;
	}

	private Reward goldFloorLottery(RoleLottery roleLottery, MemberLotteryPeshe peshe) {
		Reward reward = new Reward();
		reward.setType(peshe.getType());
		reward.setId(peshe.getId());
		reward.setNum(peshe.getNum());

		int totalCount = roleLottery.getDiamondsFloorCount() + 1;
		if (LotteryRewardType.member_VALUE == peshe.getType()) {
			MemberTemplate template = this.members.get(peshe.getId());
			if (lotteryConfig.getFloorAptitudeMax() >= template.getAptitudeMax()) {
				roleLottery.setDiamondsFloorCount(0);
				return reward;
			}
		}
		if (totalCount >= lotteryConfig.getDiamondsFloorCount()) {
			MemberLotteryPeshe lotteryResult = RandomUtil.getPeshe(this.diamondsFloorLottery);
			roleLottery.setDiamondsFloorCount(0);

			reward.setType(lotteryResult.getType());
			reward.setId(lotteryResult.getId());
			reward.setNum(lotteryResult.getType());
			return reward;
		}

		roleLottery.setDiamondsFloorCount(totalCount);

		return reward;
	}

	private LotteryResult goldMultiLottery(RoleLottery roleLottery) {
		LotteryResult result = new LotteryResult();
		int roleId = roleLottery.getRoleId();
		for (int i = 0; i < MULTI_COUNT; i++) {
			List<MemberLotteryPeshe> list = diamondsOncelottery;
			if (i == MULTI_COUNT - 1) {
				list = diamondsMultiLottery;
				if (roleLottery.isFirstMultDiamonds()) {
					list = firstDiamondsMultiLottery;
					roleLottery.setFirstMultDiamonds(false);
				}
			}
			MemberLotteryPeshe lotteryResult = RandomUtil.getPeshe(list);
			Reward reward = this.goldFloorLottery(roleLottery, lotteryResult);
			result.addReward(reward);
			this.lotteryReward(roleId, reward, OutputType.lotteryDiamondsMutilInc.getInfo());
		}
		roleLotteryDao.save(roleLottery);
		result.success();
		return result;
	}

	private LotteryResult ironSingleLottery(RoleLottery roleLottery) {
		LotteryResult result = new LotteryResult();
		int roleId = roleLottery.getRoleId();
		int count = roleLottery.getIronCount();
		List<MemberLotteryPeshe> list = ironOncelottery;
		if (count >= MULTI_COUNT) {
			list = ironMultiLottery;
			roleLottery.setIronCount(0);
		} else {
			roleLottery.setIronCount(count + 1);
		}
		MemberLotteryPeshe lotteryResult = RandomUtil.getPeshe(list);
		Reward reward = this.ironFloorLottery(roleLottery, lotteryResult);

		result.addReward(reward);
		this.lotteryReward(roleId, reward, OutputType.lotteryIronOnceInc.getInfo());

		roleLotteryDao.save(roleLottery);

		result.success();
		return result;
	}

	private Reward ironFloorLottery(RoleLottery roleLottery, MemberLotteryPeshe result) {
		Reward reward = new Reward();
		reward.setType(result.getType());
		reward.setId(result.getId());
		reward.setNum(result.getNum());

		int totalCount = roleLottery.getIronFloorCount() + 1;
		if (LotteryRewardType.member_VALUE == result.getType()) {
			MemberTemplate template = this.members.get(result.getId());
			if (lotteryConfig.getIronFloorAptitudeMax() >= template.getAptitudeMax()) {
				roleLottery.setIronFloorCount(0);
				return reward;
			}
		}
		if (totalCount >= lotteryConfig.getIronFloorCount()) {
			MemberLotteryPeshe lotteryResult = RandomUtil.getPeshe(this.ironFloorLottery);
			roleLottery.setIronFloorCount(0);

			reward.setType(lotteryResult.getType());
			reward.setId(lotteryResult.getId());
			reward.setNum(lotteryResult.getNum());
			return reward;
		}
		roleLottery.setIronFloorCount(totalCount);

		return reward;
	}

	private LotteryResult ironMultiLottery(RoleLottery roleLottery) {
		LotteryResult result = new LotteryResult();
		int roleId = roleLottery.getRoleId();
		for (int i = 0; i < MULTI_COUNT; i++) {
			List<MemberLotteryPeshe> list = i == (MULTI_COUNT - 1) ? ironMultiLottery : ironOncelottery;
			MemberLotteryPeshe lotteryResult = RandomUtil.getPeshe(list);
			Reward reward = this.ironFloorLottery(roleLottery, lotteryResult);
			result.addReward(reward);
			this.lotteryReward(roleId, reward, OutputType.lotteryIronMutilInc.getInfo());
		}
		roleLotteryDao.save(roleLottery);
		result.success();
		return result;
	}

	private int lotteryCost(RoleLottery roleLottery, LotteryType type) {
		int roleId = roleLottery.getRoleId();
		switch (type) {
		case freeDiamond:
			int goldFreeCount = roleLottery.getDiamondsFreeCount();
			roleLottery.setDiamondsFreeCount(goldFreeCount - 1);

			roleLottery.setLastFreeDiamondTime(System.currentTimeMillis());
			return 0;

		case freeIron:
			int ironFreeCount = roleLottery.getIronFreeCount();
			roleLottery.setIronFreeCount(ironFreeCount - 1);
			roleLottery.setLastFreeIronTime(System.currentTimeMillis());
			return 0;

		case diamondsOnce:

			return GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.diamonds, Operation.decrease, lotteryConfig.getSingleDiamonds()),
					OutputType.lotteryGoldOnceDec) ? 0 : 1;
		case diamondsMulti:
			return GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.diamonds, Operation.decrease, lotteryConfig.getMultiDiamonds()),
					OutputType.lotteryGoldMutilDec) ? 0 : 1;
		case ironOnce:

			return GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.decrease, lotteryConfig.getSigalIron()), OutputType.lotteryIronOnceDec) ? 0
					: 2;
		case ironMulti:
			return GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.decrease, lotteryConfig.getMultiIron()), OutputType.lotteryIronMutilDec) ? 0
					: 2;
		default:
			return 0;
		}
	}

	private boolean isReachTime(RoleLottery roleLottery, LotteryType type) {
		long now = System.currentTimeMillis();
		switch (type) {
		case freeIron:
			if (now - roleLottery.getLastFreeIronTime() > IRON_FREEZE_TIME * DateUtil.MIN) {
				return true;
			}
			break;
		case freeDiamond:
			if (now - roleLottery.getLastFreeDiamondTime() > DIAMOND_FREEZE_TIME * DateUtil.MIN) {
				return true;
			}
			break;

		case diamondsOnce:
			return true;
		case diamondsMulti:

			return true;
		case ironOnce:

			return true;
		case ironMulti:
			return true;
		default:
			return true;
		}
		return false;
	}

	private void lotteryReward(int roleId, Reward reward, String origin) {
		switch (reward.getType()) {
		case LotteryRewardType.goods_VALUE:
		case LotteryRewardType.medal_VALUE:
			GameContext.getGoodsApp().addGoods(roleId, reward.getId(), reward.getNum(), origin);
			GameContext.getQuestTriggerApp().roleLevelMedal(roleId, reward.getId(), reward.getNum());
			break;
		case LotteryRewardType.member_VALUE:
			int memberId = reward.getId();
			RoleMember roleMember = this.roleMemberDao.get(roleId, memberId);
			if (roleMember != null) {
				reward.setHadExist(true);
				// 转换成碎片
				MemberTemplate template = this.members.get(memberId);
				GameContext.getGoodsApp().addGoods(roleId, template.getReturnGoodsId(), template.getReturnNum(), origin);
				break;
			}
			this.addMember(roleId, reward.getId(), origin);
			break;
		default:
			LogCore.runtime.error("roleId={} lottery rewardType={} is wrong", roleId, reward.getType());
		}
	}

	/** 增加成员 */
	public RoleMember addMember(int roleId, int templateId, String origin) {
		MemberTemplate template = this.getMemberTemplate(templateId);
		if (template == null) {
			LogCore.runtime.error(String.format("roleId=%d add not exist Member=%d", roleId, templateId), new NullPointerException());
			return null;
		}
		// 随机资质
		double range = template.getAptitudeMax() - template.getAptitudeMin();
		Double randomAptitude = Math.random() * range + template.getAptitudeMin();

		RoleMember member = new RoleMember();
		member.setRoleId(roleId);
		member.setTemplateId(templateId);
		member.setLevel(1);
		member.setAptitude(randomAptitude.intValue());

		// 判断出生解锁空数目
		int holeCount = template.getInitHole();
		for (int i = 0; i < holeCount; i++) {
			member.openHole();
		}

		roleMemberDao.save(member);

		// GameContext.getLogApp().printMemberLog(roleId, templateId, 1,
		// origin);

		this.syncMember(member, MemberUpdateType.ADD);

		GameContext.getQuestTriggerApp().roleStartLevelMember(roleId, template.getQuality(), member.getLevel());
		GameContext.getQuestTriggerApp().roleStartMember(roleId, template.getQuality());

		return member;
	}

	private void syncMember(RoleMember member, MemberUpdateType type) {
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(member.getRoleId());
		if (connect == null) {
			return;
		}

		STC_MEMBER_UPDATE_MSG.Builder builder = STC_MEMBER_UPDATE_MSG.newBuilder();
		builder.setMember(buildMemberItem(member));
		builder.setType(type);
		connect.sendMsg(builder.build());
	}

	private void syncMember(Collection<RoleMember> members, MemberUpdateType type) {
		for (RoleMember m : members) {
			syncMember(m, type);
		}
	}

	public MemberItem buildMemberItem(RoleMember member) {
		if (member == null) {
			return null;
		}
		MemberItem.Builder builder = MemberItem.newBuilder();
		builder.setInstanceId(member.getTemplateId() + "");
		builder.setTemplateId(member.getTemplateId());
		builder.setLevel(member.getLevel());
		builder.setExp(member.getExp());
		builder.setAptitude(member.getAptitude());
		builder.addAllMedalIds(member.getMedals());
		builder.setPower(memberAttrEvaluate.calcMemberStrength(member)); // 成员战斗力
		return builder.build();
	}

	@Override
	public MemberCompositeResult composite(int roleId, int goodsId) {
		MemberComposite composite = this.compositeCosts.get(goodsId);
		if (composite == null || !composite.hadCompose() || composite.getMemberId() <= 0) {
			return MemberCompositeResult.newFailure("该物品无法合成成员");
		}

		final int memberId = composite.getMemberId();

		// 判断是否已经拥有此成员
		RoleMember roleMember = this.roleMemberDao.get(roleId, memberId);
		if (roleMember != null) {
			return MemberCompositeResult.newFailure("您已拥有该成员");
		}

		MemberTemplate template = members.get(memberId);
		if (!GameContext.getGoodsApp().removeGoods(roleId, goodsId, composite.getNum(), StringUtil.buildLogOrigin(template.getName(), OutputType.memberComposeDec.getInfo()))) {
			return MemberCompositeResult.newFailure("物品不足，无法合成");
		}
		RoleMember member = addMember(roleId, memberId, StringUtil.buildLogOrigin(template.getName(), OutputType.memberComposeInc.getInfo()));

		MemberCompositeResult result = new MemberCompositeResult();
		result.success();
		result.setMember(member);
		return result;
	}

	@Override
	public Result exchangeUseMember(int roleId, boolean dumpMedal, int type, String instanceId) {
		RoleMember roleMember = roleMemberDao.get(roleId, Integer.parseInt(instanceId));
		if (roleMember == null) {
			return Result.newFailure("选择的成员不存在");
		}
		MemberTemplate template = getMemberTemplate(roleMember.getTemplateId());
		if (template.getType() != type) {
			return Result.newFailure("所选成员职业不符");
		}
		if (dumpMedal) {
			String oldInstanceId = roleUseMemberDao.get(roleId, type);
			RoleMember oldMember = roleMemberDao.get(roleId, Integer.parseInt(oldInstanceId));
			if (oldMember != null) {
				// 卸下该成员身上的所有勋章
				List<Integer> medals = oldMember.getMedals();
				GameContext.getGoodsApp().addGoods(roleId, medals, 1, OutputType.medalExchangeInc.getInfo());
				// 清空勋章列表
				oldMember.clearMedals();
				this.syncMember(oldMember, MemberUpdateType.UPATE);
				roleMemberDao.save(oldMember);
			}
		}

		roleUseMemberDao.save(roleId, type, instanceId);
		return Result.newSuccess();
	}

	public Result memberLevelUp(int roleId, String instanceId, List<String> eatInstanceIds, Map<Integer, Integer> goodsMap) {
		if (Util.isEmpty(eatInstanceIds) && Util.isEmpty(goodsMap)) {
			return Result.newFailure("请选择升级使用的碎片或者成员");
		}
		// 把目标自身添加进去，访问一次全取出来，节省连接性能
		Collection<String> selectIds = new ArrayList<>(eatInstanceIds);
		selectIds.add(instanceId);
		Map<String, RoleMember> members = roleMemberDao.get(roleId, selectIds);
		if (members.size() < (eatInstanceIds.size() + 1)) {
			return Result.newFailure("成员ID错误");
		}
		// 删除目标自身
		RoleMember member = members.remove(instanceId);
		MemberTemplate template = getMemberTemplate(member.getTemplateId());
		if (member.getLevel() >= template.getMaxLvl()) {
			return Result.newFailure("成员已经满级，无法继续升级");
		}
		// if (getMemberLevel(template.getQuality(), member.getLevel() + 1) ==
		// null) {
		// return Result.newFailure("成员已经满级，无法继续升级");
		// }
		// 检验并消耗碎片
		String goodsInfo = this.checkMemberParts(roleId, goodsMap, template.getName());
		if (goodsInfo != null) {
			return Result.newFailure(goodsInfo);
		}

		int totalExp = getEatExp(roleId, members.values(), goodsMap);
		this.addExp(member, totalExp);
		this.syncMember(member, MemberUpdateType.UPATE);
		roleMemberDao.save(member);

		// 删除被吃的成员
		roleMemberDao.delete(roleId, members.values());
		this.syncMember(members.values(), MemberUpdateType.DELETE);
		for (RoleMember m : members.values()) {
			// 拆除被吃成员身上所佩戴的勋章
			GameContext.getGoodsApp().addGoods(roleId, m.getMedals(), 1, StringUtil.buildLogOrigin(template.getName(), OutputType.memberLevelUpInc.getInfo()));
			// GameContext.getLogApp().printMemberLog(roleId, m.getTemplateId(),
			// 1, OutputType.memberLevelUpDec.getInfo());
		}

		LogCore.runtime.info("member level up role={} result={}", roleId, JSON.toJSONString(member));
		GameContext.getQuestTriggerApp().memberUpLevel(roleId);
		GameContext.getQuestTriggerApp().roleStartLevelMember(roleId, template.getQuality(), member.getLevel());
		return Result.newSuccess();
	}

	/** 检查并消耗碎片 */
	private String checkMemberParts(int roleId, Map<Integer, Integer> goodsMap, String memberName) {
		if (Util.isEmpty(goodsMap)) {
			return null;
		}
		for (Map.Entry<Integer, Integer> entry : goodsMap.entrySet()) {
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(entry.getKey());
			if (template == null || template.getType_i() != GOODS_TYPE.member_part_VALUE || entry.getValue() < 0) {
				return "非碎片物品不能升级成员";
			}
		}
		// 扣除碎片物品
		boolean success = GameContext.getGoodsApp().removeGoods(roleId, goodsMap, StringUtil.buildLogOrigin(memberName, OutputType.memberLevelUpDec.getInfo()));
		return !success ? "物品不足" : null;
	}

	private int getEatExp(int roleId, Collection<RoleMember> members, Map<Integer, Integer> goodsMap) {
		Float result = 0f;
		for (RoleMember member : members) {
			MemberTemplate template = getMemberTemplate(member.getTemplateId());
			MemberLevel level = getMemberLevel(template.getQuality(), member.getLevel());
			result += level.getEatExp();
			result += member.getExp() * level.getExpRate() / 100;
		}
		for (Map.Entry<Integer, Integer> entry : goodsMap.entrySet()) {
			MemberComposite composite = this.compositeCosts.get(entry.getKey());
			if (composite != null) {
				result += composite.getExp() * entry.getValue();
			}
		}

		return result.intValue();
	}

	private void addExp(RoleMember member, int exp) {
		MemberTemplate template = getMemberTemplate(member.getTemplateId());
		MemberLevel curr = getMemberLevel(template.getQuality(), member.getLevel());
		MemberLevel next = getMemberLevel(template.getQuality(), member.getLevel() + 1);

		member.setExp(member.getExp() + exp);
		while (member.getExp() >= curr.getNeedExp() && next != null) {
			member.setExp(member.getExp() - curr.getNeedExp());
			member.setLevel(next.getLevel());
			curr = next;
			next = getMemberLevel(template.getQuality(), next.getLevel() + 1);
		}
		if (member.getExp() >= curr.getNeedExp()) {
			member.setExp(curr.getNeedExp() - 1);
		}
	}

	@Override
	public Result memberExchangeMedal(int roleId, String instanceId, List<Integer> medalIds) {
		// protobuf list 会被释放得重新引用
		List<Integer> medalList = new ArrayList<>();
		if (!Util.isEmpty(medalIds)) {
			medalList.addAll(medalIds);
		}

		RoleMember member = roleMemberDao.get(roleId, Integer.valueOf(instanceId));
		if (member == null) {
			return Result.newFailure("此成员不存在");
		}
		if (member.medalHoleCount() < medalList.size()) {
			return Result.newFailure("佩戴的勋章数量超过上限");
		}

		// 成员身上的勋章
		List<Integer> bodyList = member.getMedals();
		Map<Integer, Integer> bodyMap = new HashMap<Integer, Integer>();
		for (int medalId : bodyList) {
			if (bodyMap.containsKey(medalId)) {
				int num = bodyMap.get(medalId);
				num += 1;
				bodyMap.put(medalId, num);
				continue;
			}
			bodyMap.put(medalId, 1);
		}
		// 放回背包中
		GameContext.getGoodsApp().addGoods(roleId, bodyMap, OutputType.medalExchangeInc.getInfo());

		// 整理需要佩戴的勋章
		Map<Integer, Integer> wearMap = new HashMap<Integer, Integer>();
		for (int medalId : medalList) {
			if (wearMap.containsKey(medalId)) {
				int num = wearMap.get(medalId);
				num += 1;
				wearMap.put(medalId, num);
				continue;
			}
			wearMap.put(medalId, 1);
		}

		// 验证勋章数是否足够
		boolean removeFlag = GameContext.getGoodsApp().removeGoods(roleId, wearMap, OutputType.medalExchangeDec.getInfo());
		if (!removeFlag) {
			return Result.newFailure("勋章数量不足");
		}

		// 放入身上勋章
		member.putMedals(medalList);
		roleMemberDao.save(member);
		this.syncMember(member, MemberUpdateType.UPATE);
		return Result.newSuccess();
	}

	@Override
	public Map<AttrType, Float> getMemberAttr(int roleId) {
		return memberAttrEvaluate.getMemberAttr(roleId);
	}

	/** 计算所使用的成员战斗力 */
	@Override
	public int calcUseMemberStrength(int roleId) {
		Map<Integer, String> roleUseMembers = roleUseMemberDao.getAll(roleId);
		if (Util.isEmpty(roleUseMembers)) {
			return 0;
		}
		int strength = 0;
		Map<String, RoleMember> roleMemberMap = roleMemberDao.get(roleId, roleUseMembers.values());
		for (RoleMember member : roleMemberMap.values()) {
			strength += memberAttrEvaluate.calcMemberStrength(member);
		}
		return strength;
	}

	/** 计算所使用的成员匹配分 */
	@Override
	public int calcUseMemberMatchScore(int roleId) {
		Map<Integer, String> roleUseMembers = roleUseMemberDao.getAll(roleId);
		if (Util.isEmpty(roleUseMembers)) {
			return 0;
		}
		int matchScore = 0;
		Map<String, RoleMember> roleMemberMap = roleMemberDao.get(roleId, roleUseMembers.values());
		for (RoleMember member : roleMemberMap.values()) {
			matchScore += memberAttrEvaluate.calcMemberMatchScore(member);
		}
		return matchScore;
	}

	public void setMemberAttrEvaluate(MemberAttrEvaluate memberAttrEvaluate) {
		this.memberAttrEvaluate = memberAttrEvaluate;
	}

	/** 使用成员的勋章战斗力加成比 */
	@Override
	public float calcUseMedalStrengthRat(int roleId) {
		Map<Integer, String> roleUseMembers = roleUseMemberDao.getAll(roleId);
		if (Util.isEmpty(roleUseMembers)) {
			return 0;
		}
		float strength = 0;
		Map<String, RoleMember> roleMemberMap = roleMemberDao.get(roleId, roleUseMembers.values());
		for (RoleMember member : roleMemberMap.values()) {
			List<Integer> medalList = member.getMedals();
			for (Integer medialId : medalList) {
				MedalTemplate template = GameContext.getGoodsApp().getMedalTemplate(medialId);
				if (template == null) {
					continue;
				}
				strength += template.getStrengthRat();
			}
		}
		return strength;
	}

	/** 使用成员的勋章匹配分加成比 */
	@Override
	public float calcUseMedalMatchScoreRat(int roleId) {
		Map<Integer, String> roleUseMembers = roleUseMemberDao.getAll(roleId);
		if (Util.isEmpty(roleUseMembers)) {
			return 0;
		}
		float strength = 0;
		Map<String, RoleMember> roleMemberMap = roleMemberDao.get(roleId, roleUseMembers.values());
		for (RoleMember member : roleMemberMap.values()) {
			List<Integer> medalList = member.getMedals();
			for (Integer medialId : medalList) {
				MedalTemplate template = GameContext.getGoodsApp().getMedalTemplate(medialId);
				if (template == null) {
					continue;
				}
				strength += template.getMatchScoreRat();
			}
		}
		return strength;
	}

	/** 成员勋章开孔 */
	@Override
	public Result memberOpenMedalHole(int roleId, int templateId, int index) {
		RoleMember member = roleMemberDao.get(roleId, templateId);
		if (member == null) {
			return Result.newFailure("成员不存在");
		}
		// 以下标判断是否顺序开孔
		if (member.medalHoleCount() != index) {
			return Result.newFailure("前一个勋章空未开启");
		}
		MemberTemplate template = this.getMemberTemplate(member.getTemplateId());
		if (template == null) {
			return Result.newFailure("成员不存在");
		}

		// 判断所需道具
		int[] goods = template.openHoleNeedGoods(index);
		int goodsId = goods[0];
		int num = goods[1];
		boolean cosumeResult = GameContext.getGoodsApp().removeGoods(roleId, goodsId, num, OutputType.memberOpenHoleDec.getInfo());
		if (!cosumeResult) {
			return Result.newFailure("所需物品不足");
		}

		// 开孔
		member.openHole();
		roleMemberDao.save(member);

		return Result.newSuccess();
	}

	public void setRoleMemberDao(RoleMemberDao roleMemberDao) {
		this.roleMemberDao = roleMemberDao;
	}

	public void setRoleLotteryDao(RoleLotteryDao roleLotteryDao) {
		this.roleLotteryDao = roleLotteryDao;
	}

	public void setRoleUseMemberDao(RoleUseMemberDao roleUseMemberDao) {
		this.roleUseMemberDao = roleUseMemberDao;
	}

	@Override
	public RoleMember getRoleMember(int roleId, int templateId) {
		return this.roleMemberDao.get(roleId, templateId);
	}
}
