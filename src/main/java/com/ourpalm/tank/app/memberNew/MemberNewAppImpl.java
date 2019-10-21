package com.ourpalm.tank.app.memberNew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.app.tank.AttrBuffer;
import com.ourpalm.tank.dao.RoleMemberNewDao;
import com.ourpalm.tank.domain.MemberColumn;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleMemberNew;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.MEMBERNEW_MSG.Column_Info;
import com.ourpalm.tank.message.MEMBERNEW_MSG.MEMBER_NEW_INFO;
import com.ourpalm.tank.message.MEMBERNEW_MSG.MEMBER_NEW_QUALITY;
import com.ourpalm.tank.message.MEMBERNEW_MSG.MemberNewUpdateType;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_COLUMN_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_LIST_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_STAR_UP_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMBER_NEW_UP_DOWN_MSG;
import com.ourpalm.tank.message.MEMBERNEW_MSG.STC_MEMEBER_NEW_ADD_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.MemberComposite;
import com.ourpalm.tank.template.MemberNewCombinePropertyTemplate;
import com.ourpalm.tank.template.MemberNewCombineTemplate;
import com.ourpalm.tank.template.MemberNewPropertyIdTemplate;
import com.ourpalm.tank.template.MemberNewPropertyTemplate;
import com.ourpalm.tank.template.MemberNewStarTemplate;
import com.ourpalm.tank.template.MemberNewTankLevelLimitTemplate;
import com.ourpalm.tank.template.MemberNewTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.vo.result.Result;

/**
 * 新乘员
 * 
 * @author fhy
 *
 */
public class MemberNewAppImpl implements MemberNewApp {

	/** 乘员map */
	private Map<Integer, MemberNewTemplate> memberMap = new HashMap<>();
	/** 乘员升星map */
	private Map<Integer, MemberNewStarTemplate> memberStarMap = new HashMap<>();
	/** 乘员属性map */
	private Map<Integer, MemberNewPropertyTemplate> memberPropertyMap = new HashMap<>();
	/** 乘员组合技map */
	private Map<Integer, MemberNewCombineTemplate> memberCombineMap = new HashMap<>();
	/** 乘员组合技属性map */
	private Map<Integer, MemberNewCombinePropertyTemplate> memberCombinePropertyMap = new HashMap<>();
	/** 乘员坦克限制 */
	private Map<Integer, MemberNewTankLevelLimitTemplate> memberTankLevelLimitMap = new HashMap<>();

	/** 乘员属性ID map */
	private Map<Integer, MemberNewPropertyIdTemplate> memberPropertyIdMap = new HashMap<>();
	private RoleMemberNewDao roleMemberNewDao;

	/** 品质_等级 property Map */
	private Map<String, MemberNewPropertyTemplate> qualityLevelMap = new HashMap<>();

	/** 品质_等级 star Map */
	private Map<String, MemberNewStarTemplate> qualitystarMap = new HashMap<>();
	/** 品质，经验 Map */
	private Map<Integer, List<MemberNewPropertyTemplate>> qualityExpMap = new HashMap<>();
	/** 多少经验 */
	private Map<Integer, MemberComposite> compositeCosts = new HashMap<>();

	/** 组合 */
	private Map<Integer, Set<Integer>> combineMemberMap = new HashMap<>();

	@Override
	public void start() {
		this.loadMemberNewTemplate();
		this.loadMemberNewStarTemplate();
		this.loadMemberNewPropertyTemplate();
		this.loadMemberNewCombineTemplate();
		this.loadMemberNewCombinePropertyTemplate();
		this.loadMemberNewTankLevelLimit();
		this.loadMemberNewPropertyId();
		this.loadComposite();
	}

	@Override
	public void stop() {

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

	private void loadMemberNewTemplate() {
		String fileName = XlsSheetType.MemberNew.getXlsFileName();
		String sheetName = XlsSheetType.MemberNew.getSheetName();
		try {
			memberMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadMemberNewStarTemplate() {
		String fileName = XlsSheetType.MemberNewStar.getXlsFileName();
		String sheetName = XlsSheetType.MemberNewStar.getSheetName();
		try {
			memberStarMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewStarTemplate.class);
			for (MemberNewStarTemplate template : memberStarMap.values()) {
				qualitystarMap.put(template.getQualilty() + "_" + template.getStar(), template);
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadMemberNewPropertyTemplate() {
		String fileName = XlsSheetType.MemberNewProperty.getXlsFileName();
		String sheetName = XlsSheetType.MemberNewProperty.getSheetName();
		try {
			memberPropertyMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewPropertyTemplate.class);

			for (MemberNewPropertyTemplate template : memberPropertyMap.values()) {
				qualityLevelMap.put(template.getQuality() + "_" + template.getLevel(), template);

				if (qualityExpMap.containsKey(template.getQuality())) {
					qualityExpMap.get(template.getQuality()).add(template);
				} else {
					List<MemberNewPropertyTemplate> list = new ArrayList<>();
					list.add(template);
					qualityExpMap.put(template.getQuality(), list);
				}
			}

		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadMemberNewCombineTemplate() {
		String fileName = XlsSheetType.MemberNewCombine.getXlsFileName();
		String sheetName = XlsSheetType.MemberNewCombine.getSheetName();
		try {
			memberCombineMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewCombineTemplate.class);
			for (Entry<Integer, MemberNewCombineTemplate> entry : memberCombineMap.entrySet()) {
				entry.getValue().init();
				combineMemberMap.put(entry.getKey(), entry.getValue().getCombineSet());
			}

		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadMemberNewCombinePropertyTemplate() {
		String fileName = XlsSheetType.MemberNewCombineProperty.getXlsFileName();
		String sheetName = XlsSheetType.MemberNewCombineProperty.getSheetName();
		try {
			memberCombinePropertyMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewCombinePropertyTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadMemberNewTankLevelLimit() {
		String fileName = XlsSheetType.MemberNewCombineProperty.getXlsFileName();
		String sheetName = XlsSheetType.MemberNewCombineProperty.getSheetName();
		try {
			memberTankLevelLimitMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewTankLevelLimitTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadMemberNewPropertyId() {
		String fileName = XlsSheetType.MemberNewTankPropertyId.getXlsFileName();
		String sheetName = XlsSheetType.MemberNewTankPropertyId.getSheetName();
		try {
			memberPropertyIdMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberNewPropertyIdTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	/**
	 * 乘员列表
	 * 
	 * @param roleId
	 * @param builder
	 */
	public void getMemberList(int roleId, STC_MEMBER_NEW_LIST_MSG.Builder builder) {
		Map<String, RoleMemberNew> allMem = roleMemberNewDao.getAll(roleId);
		for (RoleMemberNew roleMemberNew : allMem.values()) {
			MEMBER_NEW_INFO.Builder infoBuilder = MEMBER_NEW_INFO.newBuilder();
			infoBuilder.setUniqueId(roleMemberNew.getUniqueId());
			infoBuilder.setRoleId(roleMemberNew.getRoleId());
			infoBuilder.setTemplateId(roleMemberNew.getTemplateId());
			infoBuilder.setLevel(roleMemberNew.getLevel());
			infoBuilder.setExp(roleMemberNew.getExp());
			infoBuilder.setActive(roleMemberNew.isActive());
			infoBuilder.setQuality(MEMBER_NEW_QUALITY.values()[memberMap.get(roleMemberNew.getTemplateId()).getQuality()]);
			infoBuilder.setStar(roleMemberNew.getStar());
			infoBuilder.addAllAttr(roleMemberNew.getAttr());
			infoBuilder.setType(MemberNewUpdateType.SHOW);

			builder.addInfos(infoBuilder);
		}
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		// 推送栏位
		columnUpdateSend(roleId, account.getCurrMemberColumn());
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(builder.build());
		}
	}

	/**
	 * 添加成员
	 * 
	 * @param roleId
	 *            角色ID
	 * 
	 * @param memberTemplateId
	 *            模板ID
	 */
	public void addMember(int roleId, int memberTemplateId) {
		MemberNewTemplate memberNewTemplate = memberMap.get(memberTemplateId);
		if (memberNewTemplate == null) {
			LogCore.runtime.error("发送乘员时，找不到templateId ={}", memberTemplateId);
			return;
		}
		RoleMemberNew member = new RoleMemberNew();
		member.setUniqueId(GameContext.getIdFactory().nextStr());
		member.setRoleId(roleId);
		member.setTemplateId(memberTemplateId);
		member.setLevel(1);
		member.setExp(0);
		member.setActive(false);
		member.getAttr().add(memberNewTemplate.getInitProperty());
		roleMemberNewDao.save(member);

		memberUpdateSend(roleId, member, MemberNewUpdateType.ADD);
	}

	private MEMBER_NEW_INFO.Builder builderMemberInfo(RoleMemberNew member, MemberNewUpdateType type) {
		MEMBER_NEW_INFO.Builder builder = MEMBER_NEW_INFO.newBuilder();
		builder.setActive(member.isActive());
		builder.addAllAttr(member.getAttr());
		builder.setExp(member.getExp());
		builder.setLevel(member.getLevel());
		builder.setQuality(MEMBER_NEW_QUALITY.values()[memberMap.get(member.getTemplateId()).getQuality()]);
		builder.setRoleId(member.getRoleId());
		builder.setStar(member.getStar());
		builder.setType(type);
		builder.setTemplateId(member.getTemplateId());
		builder.setUniqueId(member.getUniqueId());
		return builder;
	}

	/**
	 * 上阵下阵
	 * 
	 * @param roleId
	 *            角色ID
	 * @param uniqueId
	 *            乘员唯一ID
	 * @param builder
	 * @param type
	 *            0下阵1上阵
	 * @param column
	 *            对应栏
	 * @return
	 */
	public Result memerUpAndDown(int roleId, String uniqueId, STC_MEMBER_NEW_UP_DOWN_MSG.Builder builder, int type, int column) {
		RoleMemberNew member = roleMemberNewDao.get(roleId, uniqueId);
		if (member == null) {
			return Result.newFailure("不存在该乘员！");
		}
		MemberNewTemplate memberTemplate = memberMap.get(member.getTemplateId());

		if (memberTemplate == null) {
			LogCore.runtime.error("玩家的乘员，找不到模板  templateId={}", member.getTemplateId());
			return Result.newFailure("乘员数据错误！");
		}

		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null) {
			return Result.newFailure("数据错误！");
		}
		if (column != 1 && column != 2 && column != 3) {
			return Result.newFailure("选择的栏位错误！");
		}

		if (column != account.getCurrMemberColumn()) {
			return Result.newFailure("请选择正确的栏位！");
		}

		if (type != 0 || type != 1) {
			return Result.newFailure("请正确输入！");
		}

		MemberColumn memberColumn = getMemberColumnById(account, column);
		if (type == 0) {
			int position = -1;
			for (int i = 0; i < memberColumn.getMemberUniqueIds().length; i++) {
				String id = memberColumn.getMemberUniqueIds()[i];
				if (uniqueId.equals(id)) {
					position = i;
				}
			}
			if (position == -1) {
				return Result.newFailure("不存在该乘员！");
			}
			member.setActive(false);
			memberColumn.getMemberUniqueIds()[position] = "";
			roleMemberNewDao.save(member);
			GameContext.getUserApp().saveRoleAccount(account);
			// 更新乘员
			memberUpdateSend(roleId, member, MemberNewUpdateType.UPATE);
			// 更新栏位
			columnUpdateSend(roleId, column);
		} else {
			boolean hasOn = false;
			boolean sameTemplate = false;
			for (int i = 0; i < memberColumn.getMemberUniqueIds().length; i++) {
				String id = memberColumn.getMemberUniqueIds()[i];
				if (uniqueId.equals(id)) {
					hasOn = true;
				}
				RoleMemberNew m = roleMemberNewDao.get(roleId, id);
				if (m != null && member.getTemplateId() == m.getTemplateId()) {
					sameTemplate = true;
				}
			}
			if (hasOn) {
				return Result.newFailure("该乘员已上阵，不可重复上阵！");
			}
			if (sameTemplate) {
				return Result.newFailure("该类乘员已上阵，不可重复上阵！");
			}
			int position = -1;
			for (int i = 0; i < memberColumn.getMemberUniqueIds().length; i++) {
				String id = memberColumn.getMemberUniqueIds()[i];
				if (id == null || "".equals(id)) {
					position = i;
					break;
				}
			}
			if (position == -1) {
				return Result.newFailure("没有空位！");
			}
			member.setActive(true);
			memberColumn.getMemberUniqueIds()[position] = uniqueId;
			roleMemberNewDao.save(member);
			GameContext.getUserApp().saveRoleAccount(account);
			// 更新乘员
			memberUpdateSend(roleId, member, MemberNewUpdateType.UPATE);
			// 更新栏位
			columnUpdateSend(roleId, column);
		}

		return Result.newSuccess();
	}

	/**
	 * 乘员更新主推
	 * 
	 * @param roleId
	 * @param member
	 * @param updateType
	 */
	private void memberUpdateSend(int roleId, RoleMemberNew member, MemberNewUpdateType updateType) {
		STC_MEMEBER_NEW_ADD_MSG.Builder builder = STC_MEMEBER_NEW_ADD_MSG.newBuilder();
		builder.addInfos(builderMemberInfo(member, updateType));
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(builder.build());
		}
	}

	/**
	 * 多乘员更新主推
	 * 
	 * @param roleId
	 * @param member
	 * @param updateType
	 */
	private void memberUpdateSend(int roleId, MemberNewUpdateType updateType, Set<RoleMemberNew> members) {
		STC_MEMEBER_NEW_ADD_MSG.Builder builder = STC_MEMEBER_NEW_ADD_MSG.newBuilder();
		for (RoleMemberNew roleMemberNew : members) {
			builder.addInfos(builderMemberInfo(roleMemberNew, updateType));
		}
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(builder.build());
		}
	}

	/**
	 * 触发的技能
	 * 
	 * @param roleId
	 * @return
	 */
	private Set<Integer> caclCombine(int roleId) {
		// 触发的技能
		Set<Integer> skillSet = new HashSet<>();

		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null) {
			return skillSet;
		}
		List<RoleMemberNew> currColumnMember = new ArrayList<>();
		MemberColumn memberColumn = getMemberColumnById(account, account.getCurrMemberColumn());
		for (String memberId : memberColumn.getMemberUniqueIds()) {
			if (Util.isEmpty(memberId)) {
				continue;
			}
			currColumnMember.add(roleMemberNewDao.get(roleId, memberId));
		}

		// <模板ID,玩家身上有的乘员>
		Map<Integer, Set<Integer>> combinIdContainsMemberIdMap = new HashMap<>();
		for (MemberNewCombineTemplate combinTemplate : memberCombineMap.values()) {
			for (RoleMemberNew roleMemberNew : currColumnMember) {
				if (combinTemplate.getCombineSet().contains(roleMemberNew.getTemplateId())) {
					if (combinIdContainsMemberIdMap.containsKey(combinTemplate.getId())) {
						combinIdContainsMemberIdMap.get(combinTemplate.getId()).add(roleMemberNew.getTemplateId());

					} else {
						Set<Integer> set = new HashSet<>();
						set.add(roleMemberNew.getTemplateId());
						combinIdContainsMemberIdMap.put(combinTemplate.getId(), set);
					}
				}
			}
		}

		for (Entry<Integer, Set<Integer>> entry : combinIdContainsMemberIdMap.entrySet()) {
			MemberNewCombineTemplate template = memberCombineMap.get(entry.getKey());
			if (entry.getValue().size() >= template.getAllInfo().getNum()) {
				// 触发满技能
				// 三个都有
				skillSet.add(template.getAllInfo().getId());
				skillSet.add(template.getHalfInfo().getId());
				skillSet.add(template.getLittleHalfInfo().getId());
			} else if (entry.getValue().size() >= template.getHalfInfo().getNum()) {
				// 触发3 和2的技能
				skillSet.add(template.getHalfInfo().getId());
				skillSet.add(template.getLittleHalfInfo().getId());
			} else if (entry.getValue().size() >= template.getLittleHalfInfo().getNum()) {
				// 触发2的技能
				skillSet.add(template.getLittleHalfInfo().getId());
			} else {
				// 没触发
			}

		}
		return skillSet;
	}

	/**
	 * 栏位更新主推
	 * 
	 * @param roleId
	 * @param column
	 */
	@Override
	public void columnUpdateSend(int roleId, int column) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null) {
			return;
		}
		STC_MEMBER_NEW_COLUMN_MSG.Builder builder = STC_MEMBER_NEW_COLUMN_MSG.newBuilder();
		MemberColumn memberColumn = getMemberColumnById(account, column);
		Column_Info.Builder info = Column_Info.newBuilder();
		info.setColumn(column);
		info.addAllMemberUniqueIds(Arrays.asList(memberColumn.getMemberUniqueIds()));
		info.addAllSkillIds(caclCombine(roleId));
		builder.setInfos(info);
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if (connect != null) {
			connect.sendMsg(builder.build());
		}
	}

	private boolean ifMemberOn(int roleId, String memberId) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		for (String uniqueId : account.getColumn1().getMemberUniqueIds()) {
			if (memberId.equals(uniqueId)) {
				return true;
			}
		}
		for (String uniqueId : account.getColumn1().getMemberUniqueIds()) {
			if (memberId.equals(uniqueId)) {
				return true;
			}
		}

		for (String uniqueId : account.getColumn1().getMemberUniqueIds()) {
			if (memberId.equals(uniqueId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 乘员升级
	 * 
	 * @param roleId
	 *            角色ID
	 * @param uniqueId
	 *            要升级的唯一ID
	 * @param eatUniqueIdIds
	 *            要吃掉的IDS
	 * @param goods
	 *            要用的经验卡
	 * @return
	 */
	public Result upLevel(int roleId, String uniqueId, List<String> eatUniqueIdIds, Map<Integer, Integer> goodsMap) {
		if (Util.isEmpty(uniqueId) && Util.isEmpty(goodsMap)) {
			return Result.newFailure("请选择乘员或经验卡！");
		}
		RoleMemberNew mainMember = roleMemberNewDao.get(roleId, uniqueId);
		if (mainMember == null) {
			return Result.newFailure("没有该乘员！");
		}
		Set<String> eatUniqueIdIdSet = new HashSet<>(eatUniqueIdIds);
		Set<RoleMemberNew> set = new HashSet<>();
		for (String eatMemberId : eatUniqueIdIdSet) {
			RoleMemberNew eatMember = roleMemberNewDao.get(roleId, eatMemberId);
			if (eatMember == null) {
				return Result.newFailure("选择的乘员不存在！");
			}
			if (ifMemberOn(roleId, eatMemberId)) {
				return Result.newFailure("不可选择已上阵乘员！");
			}
			set.add(eatMember);
		}
		int totalAddExp = 0;
		// 计算经验
		for (RoleMemberNew roleMemberNew : set) {
			totalAddExp += roleMemberNew.getExp();
		}

		for (Entry<Integer, Integer> entry : goodsMap.entrySet()) {
			GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(entry.getKey());
			if (template == null || template.getType_i() != GOODS_TYPE.member_part_VALUE || entry.getValue() < 0) {
				Result.newFailure("非经验卡，乘员不能升级乘员");
			}
			MemberComposite composite = this.compositeCosts.get(entry.getKey());
			if (composite != null) {
				totalAddExp += composite.getExp() * entry.getValue();
			}
		}
		int totalExp = mainMember.getExp() + totalAddExp;
		int newLevel = getNewLevel(mainMember, totalExp);
		mainMember.setExp(totalExp);
		if (newLevel > mainMember.getLevel()) {
			mainMember.setLevel(newLevel);
		}
		// 删除经验卡
		if (!Util.isEmpty(goodsMap)) {
			// 扣除经验卡物品
			boolean success = GameContext.getGoodsApp().removeGoods(roleId, goodsMap, StringUtil.buildLogOrigin(String.valueOf(mainMember.getTemplateId()), OutputType.memberLevelUpDec.getInfo()));
			if (!success) {
				return Result.newFailure("物品不足");
			}
		}
		// 删除旧乘员
		if (!Util.isEmpty(eatUniqueIdIdSet)) {
			roleMemberNewDao.delete(roleId, eatUniqueIdIdSet);
		}
		memberUpdateSend(roleId, MemberNewUpdateType.DELETE, set);
		memberUpdateSend(roleId, mainMember, MemberNewUpdateType.UPATE);
		roleMemberNewDao.save(mainMember);

		return Result.newSuccess();
	}

	private int getNewLevel(RoleMemberNew mainMember, int totalExp) {
		MemberNewPropertyTemplate oldTemplate = getPropertyByMember(mainMember.getTemplateId(), mainMember.getLevel());
		int quality = getQualityByMemberTemplateId(mainMember.getTemplateId());
		MemberNewPropertyTemplate chooseTemplate = getPropertyTemplateByQualityAndExp(quality, totalExp);
		if (chooseTemplate.getId() > oldTemplate.getId()) {
			return chooseTemplate.getLevel();
		} else {
			return mainMember.getLevel();
		}
	}

	/**
	 * 根据经验和品质 取得新的MemberNewPropertyTemplate
	 * 
	 * @param quality
	 * @param totalExp
	 * @return
	 */
	private MemberNewPropertyTemplate getPropertyTemplateByQualityAndExp(int quality, int totalExp) {
		List<MemberNewPropertyTemplate> expList = qualityExpMap.get(quality);
		MemberNewPropertyTemplate chooseTemplate = null;
		for (MemberNewPropertyTemplate memberNewPropertyTemplate : expList) {
			if (totalExp < memberNewPropertyTemplate.getExp()) {
				chooseTemplate = memberNewPropertyTemplate;
			}
		}
		if (chooseTemplate == null) {
			chooseTemplate = expList.get(expList.size() - 1);
		}
		return chooseTemplate;
	}

	/**
	 * 根据memberTemplateId 得到品质
	 * 
	 * @param memberTemplateId
	 * @return
	 */
	private int getQualityByMemberTemplateId(int memberTemplateId) {
		MemberNewTemplate template = memberMap.get(memberTemplateId);
		if (template == null) {
			return 0;
		}
		return template.getQuality();
	}

	/**
	 * 根据memberTemplateId和level取得 propertyTemplate
	 * 
	 * @param memberTemplateId
	 * @param level
	 * @return
	 */
	private MemberNewPropertyTemplate getPropertyByMember(int memberTemplateId, int level) {
		MemberNewTemplate memberNewTemplate = memberMap.get(memberTemplateId);
		if (memberNewTemplate == null) {
			return null;
		}
		int quality = memberNewTemplate.getQuality();
		String key = quality + "_" + level;
		MemberNewPropertyTemplate memberNewPropertyTemplate = qualityLevelMap.get(key);
		return memberNewPropertyTemplate;
	}

	/**
	 * 根据memberTemplateId和star取得 starTemplate
	 * 
	 * @param memberTemplateId
	 * @param level
	 * @return
	 */
	private MemberNewStarTemplate getStarByMember(int memberTemplateId, int star) {
		MemberNewTemplate memberNewTemplate = memberMap.get(memberTemplateId);
		if (memberNewTemplate == null) {
			return null;
		}
		int quality = memberNewTemplate.getQuality();
		String key = quality + "_" + star;
		MemberNewStarTemplate memberNewStartTemplate = qualitystarMap.get(key);
		return memberNewStartTemplate;
	}

	/**
	 * 乘员升星
	 * 
	 * @param roleId
	 *            角色ID
	 * @param uniqueId
	 *            要升星的乘员ID
	 * @param eatUniqueIdIds
	 *            被吃掉的乘员ID
	 * @return
	 */
	public Result upStar(int roleId, String uniqueId, List<String> eatUniqueIdIds, STC_MEMBER_NEW_STAR_UP_MSG.Builder builder) {
		if (Util.isEmpty(eatUniqueIdIds) || Util.isEmpty(uniqueId)) {
			return Result.newFailure("请选择乘员！");
		}
		RoleMemberNew mainMember = roleMemberNewDao.get(roleId, uniqueId);
		if (mainMember == null) {
			return Result.newFailure("没有该乘员！");
		}
		Set<String> eatUniqueIdIdSet = new HashSet<>(eatUniqueIdIds);
		Set<RoleMemberNew> set = new HashSet<>();
		for (String eatMemberId : eatUniqueIdIdSet) {
			RoleMemberNew eatMember = roleMemberNewDao.get(roleId, eatMemberId);
			if (eatMember == null) {
				return Result.newFailure("选择的乘员不存在！");
			}
			if (ifMemberOn(roleId, eatMemberId)) {
				return Result.newFailure("不可选择已上阵乘员！");
			}
			set.add(eatMember);
		}

		MemberNewStarTemplate starTemplate = getStarByMember(mainMember.getTemplateId(), mainMember.getStar());
		if (starTemplate.getNextId() == 0) {
			return Result.newFailure("已满星，无需升星！");
		}
		if (mainMember.getLevel() < starTemplate.getLevelLimit()) {
			return Result.newFailure("要升星的乘员等级不足！");
		}

		if (set.size() < starTemplate.getNum()) {
			return Result.newFailure("所选的乘员数量不足，不可升星！");
		}

		for (RoleMemberNew roleMemberNew : set) {
			if (roleMemberNew.getTemplateId() != mainMember.getTemplateId()) {
				return Result.newFailure("所选的乘员与要升星的乘员不同，不可升星！");
			}
		}
		// 删除旧乘员
		if (!Util.isEmpty(eatUniqueIdIdSet)) {
			roleMemberNewDao.delete(roleId, eatUniqueIdIdSet);
		}
		MemberNewStarTemplate nextStartTemplate = memberStarMap.get(starTemplate.getNextId());
		// 加星级
		mainMember.setStar(nextStartTemplate.getStar());
		// 加一个随机属性
		int newAttr = upStarAttr(roleId, mainMember);
		roleMemberNewDao.save(mainMember);
		// 更新主member和被吃member
		memberUpdateSend(roleId, mainMember, MemberNewUpdateType.UPATE);
		memberUpdateSend(roleId, MemberNewUpdateType.DELETE, set);

		builder.setNewAttr(newAttr);
		return Result.newSuccess();
	}

	/**
	 * 升星随机加一个属性
	 * 
	 * @param roleId
	 * @param member
	 */
	private int upStarAttr(int roleId, RoleMemberNew member) {
		List<Integer> list = member.getAttr();
		int chooseId = 0;
		Set<Integer> set = memberPropertyIdMap.keySet();
		while (!list.contains(chooseId)) {
			chooseId = RandomUtil.getRandomElement(set);
		}
		list.add(chooseId);
		return chooseId;
	}

	/**
	 * 根据column 返回 memberColumn
	 * 
	 * @param account
	 * @param column
	 * @return
	 */
	private MemberColumn getMemberColumnById(RoleAccount account, int column) {
		MemberColumn memberColumn = null;
		switch (column) {
		case 1:
			memberColumn = account.getColumn1();
			break;
		case 2:
			memberColumn = account.getColumn2();
			break;
		case 3:
			memberColumn = account.getColumn3();
			break;
		default:
			return null;
		}
		return memberColumn;
	}

	private List<RoleMemberNew> getRoleNowMemberList(int roleId) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		int currColumn = account.getCurrMemberColumn();
		MemberColumn memberColumn = getMemberColumnById(account, currColumn);
		List<RoleMemberNew> memberList = new ArrayList<>();
		for (String uniqueId : memberColumn.getMemberUniqueIds()) {
			if (!Util.isEmpty(uniqueId)) {
				RoleMemberNew member = roleMemberNewDao.get(roleId, uniqueId);
				if (member != null) {
					memberList.add(member);
				} else {
					LogCore.runtime.error("找角色身上的乘员，栏位里有的在身上找不到   roleId ={} cloumn={} uniqueId={}", roleId, currColumn, uniqueId);
				}
			}
		}
		return memberList;
	}

	/**
	 * 取得 乘员身上的attr 的值
	 * 
	 * @param roleMemberNew
	 * @return
	 */
	private Map<AttrType, Float> getMemberAttrByMember(RoleMemberNew roleMemberNew) {
		Map<AttrType, Float> map = new HashMap<>();
		List<Integer> attrList = roleMemberNew.getAttr();
		MemberNewPropertyTemplate template = getPropertyByMember(roleMemberNew.getTemplateId(), roleMemberNew.getLevel());
		for (Entry<AttrType, Float> entry : template.getNewAttr().entrySet()) {
			if (attrList.contains(entry.getKey().getNumber())) {
				map.put(entry.getKey(), entry.getValue());
			}
		}
		// for (Integer attr : attrList) {
		// map.put(AttrType.valueOf(attr), getAttrById(template, attr));
		// }
		return map;
	}

	/**
	 * 取得 已上阵的所有乘员的加成 值
	 * 
	 * @param roleId
	 * 
	 * @return
	 */
	@Override
	public Map<AttrType, Float> getMemberAttr(int roleId) {
		AttrBuffer onMemberAttrBuff = new AttrBuffer();
		// 乘员的属性值
		List<RoleMemberNew> list = getRoleNowMemberList(roleId);
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		for (RoleMemberNew roleMemberNew : list) {
			Map<AttrType, Float> memberAttrMap = getMemberAttrByMember(roleMemberNew);
			onMemberAttrBuff.append(GameContext.getTankApp().tankRawAddAttr(memberAttrMap, account.getMainTankId()));
		}

		return onMemberAttrBuff.getAttrMap();
	}

	/**
	 * 取得 已上阵的所有乘员技能的加成值
	 * 
	 * @param roleId
	 * 
	 * @return
	 */
	public Map<AttrType, Float> getMemberSkillAttr(int roleId) {
		AttrBuffer onMemberAttrBuff = new AttrBuffer();
		Set<Integer> skillSet = caclCombine(roleId);
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		for (Integer skillId : skillSet) {
			MemberNewCombinePropertyTemplate template = memberCombinePropertyMap.get(skillId);
			Map<AttrType, Float> skillAttrMap = template.getNewAttr();
			onMemberAttrBuff.append(GameContext.getTankApp().tankRawAddAttr(skillAttrMap, account.getMainTankId()));
		}
		return onMemberAttrBuff.getAttrMap();
	}

	@Override
	public int getMemberBattleNum(int roleId) {
		int battleNum = 0;
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		if (account == null) {
			return battleNum;
		}
		MemberColumn column = getMemberColumnById(account, account.getCurrMemberColumn());
		if (column == null) {
			LogCore.runtime.error("玩家身上的column 未找到 column ={}", account.getCurrMemberColumn());
			return battleNum;
		}
		String[] s = column.getMemberUniqueIds();
		for (int i = 0; i < s.length; i++) {
			if (!Util.isEmpty(s[i])) {
				RoleMemberNew member = roleMemberNewDao.get(roleId, s[i]);
				MemberNewPropertyTemplate template = getPropertyByMember(member.getTemplateId(), member.getLevel());
				battleNum += template.getBattleNum();
			}
		}

		return battleNum;
	}

	public Map<Integer, MemberNewTemplate> getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map<Integer, MemberNewTemplate> memberMap) {
		this.memberMap = memberMap;
	}

	public Map<Integer, MemberNewStarTemplate> getMemberStarMap() {
		return memberStarMap;
	}

	public void setMemberStarMap(Map<Integer, MemberNewStarTemplate> memberStarMap) {
		this.memberStarMap = memberStarMap;
	}

	public Map<Integer, MemberNewPropertyTemplate> getMemberPropertyMap() {
		return memberPropertyMap;
	}

	public void setMemberPropertyMap(Map<Integer, MemberNewPropertyTemplate> memberPropertyMap) {
		this.memberPropertyMap = memberPropertyMap;
	}

	public Map<Integer, MemberNewCombineTemplate> getMemberCombineMap() {
		return memberCombineMap;
	}

	public void setMemberCombineMap(Map<Integer, MemberNewCombineTemplate> memberCombineMap) {
		this.memberCombineMap = memberCombineMap;
	}

	public Map<Integer, MemberNewCombinePropertyTemplate> getMemberCombinePropertyMap() {
		return memberCombinePropertyMap;
	}

	public void setMemberCombinePropertyMap(Map<Integer, MemberNewCombinePropertyTemplate> memberCombinePropertyMap) {
		this.memberCombinePropertyMap = memberCombinePropertyMap;
	}

	public Map<Integer, MemberNewTankLevelLimitTemplate> getMemberTankLevelLimitMap() {
		return memberTankLevelLimitMap;
	}

	public void setMemberTankLevelLimitMap(Map<Integer, MemberNewTankLevelLimitTemplate> memberTankLevelLimitMap) {
		this.memberTankLevelLimitMap = memberTankLevelLimitMap;
	}

	public RoleMemberNewDao getRoleMemberNewDao() {
		return roleMemberNewDao;
	}

	public void setRoleMemberNewDao(RoleMemberNewDao roleMemberNewDao) {
		this.roleMemberNewDao = roleMemberNewDao;
	}

	public Map<Integer, MemberNewPropertyIdTemplate> getMemberPropertyIdMap() {
		return memberPropertyIdMap;
	}

	public void setMemberPropertyIdMap(Map<Integer, MemberNewPropertyIdTemplate> memberPropertyIdMap) {
		this.memberPropertyIdMap = memberPropertyIdMap;
	}

	public Map<String, MemberNewPropertyTemplate> getQualityLevelMap() {
		return qualityLevelMap;
	}

	public void setQualityLevelMap(Map<String, MemberNewPropertyTemplate> qualityLevelMap) {
		this.qualityLevelMap = qualityLevelMap;
	}

	public Map<String, MemberNewStarTemplate> getQualitystarMap() {
		return qualitystarMap;
	}

	public void setQualitystarMap(Map<String, MemberNewStarTemplate> qualitystarMap) {
		this.qualitystarMap = qualitystarMap;
	}

	public Map<Integer, List<MemberNewPropertyTemplate>> getQualityExpMap() {
		return qualityExpMap;
	}

	public void setQualityExpMap(Map<Integer, List<MemberNewPropertyTemplate>> qualityExpMap) {
		this.qualityExpMap = qualityExpMap;
	}

	public Map<Integer, MemberComposite> getCompositeCosts() {
		return compositeCosts;
	}

	public void setCompositeCosts(Map<Integer, MemberComposite> compositeCosts) {
		this.compositeCosts = compositeCosts;
	}

	// private float getAttrById(MemberNewPropertyTemplate template, int attr) {
	// float value = 0;
	// switch (attr) {
	// case 132:
	// value = template.getN_minHit();
	// break;
	// case 133:
	// value = template.getN_maxHit();
	// break;
	// case 134:
	// value = template.getN_crit();
	// break;
	// case 135:
	// value = template.getN_isNear();
	// break;
	// case 136:
	// value = template.getN_isFar();
	// break;
	//
	// case 137:
	// value = template.getN_fireSpeed();
	// break;
	// case 138:
	// value = template.getN_stab();
	// break;
	// case 139:
	// value = template.getN_minPrecision();
	// break;
	// case 140:
	// value = template.getN_maxPrecision();
	// break;
	// case 141:
	// value = template.getN_aimSpeed();
	// break;
	// case 142:
	// value = template.getN_hpMax();
	// break;
	// case 143:
	// value = template.getN_dr();
	// break;
	// case 144:
	// value = template.getN_ArmorTurretFront();
	// break;
	// case 145:
	// value = template.getN_ArmorTurretMiddle();
	// break;
	// case 146:
	// value = template.getN_ArmorTurretbehind();
	// break;
	// case 147:
	// value = template.getN_ArmorBodyFront();
	// break;
	// case 148:
	// value = template.getN_ArmorBodyMiddle();
	// break;
	// case 149:
	// value = template.getN_ArmorBodybehind();
	// break;
	// case 150:
	// value = template.getN_turrentSpeep();
	// break;
	// case 151:
	// value = template.getN_bodySpeed();
	// break;
	// case 152:
	// value = template.getN_nimble();
	// break;
	// case 153:
	// value = template.getN_speed();
	// break;
	// case 154:
	// value = template.getN_speedUp();
	// break;
	// case 155:
	// value = template.getN_view();
	// break;
	// case 156:
	// value = template.getN_camouflage();
	// break;
	//
	// default:
	// break;
	// }
	//
	// return value;
	// }

	public Map<Integer, Set<Integer>> getCombineMemberMap() {
		return combineMemberMap;
	}

	public void setCombineMemberMap(Map<Integer, Set<Integer>> combineMemberMap) {
		this.combineMemberMap = combineMemberMap;
	}
}
