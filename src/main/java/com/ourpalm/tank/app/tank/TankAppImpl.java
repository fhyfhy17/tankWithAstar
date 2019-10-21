package com.ourpalm.tank.app.tank;

import static com.ourpalm.tank.app.GameContext.getTankApp;
import static org.springframework.util.ObjectUtils.containsConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.w3c.dom.Attr;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.node.RemoteNode;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.NetUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.app.map.ai.CircleAi;
import com.ourpalm.tank.app.map.ai.DeathAi;
import com.ourpalm.tank.app.map.ai.EscapeAi;
import com.ourpalm.tank.app.map.ai.FireAi;
import com.ourpalm.tank.app.map.ai.FireToFlagAi;
import com.ourpalm.tank.app.map.ai.MoveAi;
import com.ourpalm.tank.app.map.ai.MoveOutFlagAi;
import com.ourpalm.tank.app.map.ai.MoveToEnemyAi;
import com.ourpalm.tank.app.map.ai.MoveToFlagAi;
import com.ourpalm.tank.app.map.ai.PursueAi;
import com.ourpalm.tank.app.map.ai.StayAndFireAi;
import com.ourpalm.tank.dao.RoleTankDao;
import com.ourpalm.tank.dao.TankDevelopDao;
import com.ourpalm.tank.domain.PartInfo;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleBattle;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.domain.RoleTank;
import com.ourpalm.tank.domain.TankDevelop;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.Message;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.message.SERV_MSG;
import com.ourpalm.tank.message.SERV_MSG.GTG_ONLINE_COUNT_MSG;
import com.ourpalm.tank.message.TANK_MSG.Freeze_Info;
import com.ourpalm.tank.message.TANK_MSG.PART_INDEX;
import com.ourpalm.tank.message.TANK_MSG.PartNewInfo;
import com.ourpalm.tank.message.TANK_MSG.RewardItem;
import com.ourpalm.tank.message.TANK_MSG.RewardType;
import com.ourpalm.tank.message.TANK_MSG.STC_BUYPARK_MSG.Builder;
import com.ourpalm.tank.message.TANK_MSG.STC_CLEAR_FREEZE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_ONE_KEY_STRENGTHEN_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_OPEN_GROOVE_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_OPEN_GROOVE_SHOW_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_STRENGTHEN_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_BUY_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_DEVELOP_LIST_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_DEVELOP_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_TANK_UPDATE_PUSH_MSG;
import com.ourpalm.tank.message.TANK_MSG.STC_UP_DOWN_MSG;
import com.ourpalm.tank.message.TANK_MSG.Strengthen_Info;
import com.ourpalm.tank.message.TANK_MSG.TANK_DEVELOP_INFO;
import com.ourpalm.tank.message.TANK_MSG.TankItem;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.template.AiConfig;
import com.ourpalm.tank.template.BuildTemplate;
import com.ourpalm.tank.template.ClearFreezeTemplate;
import com.ourpalm.tank.template.GoldTankTemplate;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.GoodsShellTemplate;
import com.ourpalm.tank.template.GrooveTemplate;
import com.ourpalm.tank.template.MemberTemplate;
import com.ourpalm.tank.template.PartDrawTemplate;
import com.ourpalm.tank.template.StrengthenTemplate;
import com.ourpalm.tank.template.TankAiTemplate;
import com.ourpalm.tank.template.TankAiTestTemplate;
import com.ourpalm.tank.template.TankCoefficientTemplate;
import com.ourpalm.tank.template.TankPartNewTemplate;
import com.ourpalm.tank.template.TankRobotNameTemplate;
import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.template.UserInitTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.SysConfig;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.PartDrawPeshe;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.BuildInstance;
import com.ourpalm.tank.vo.IComponent;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.TankInstance;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.TankBuyResult;
import com.ourpalm.tank.vo.result.ValueResult;
import com.ourpalm.tank.vo.tank.NewPlayer1TankInstance;
import com.ourpalm.tank.vo.tank.NewPlayer2TankInstance;
import com.ourpalm.tank.vo.tank.NewPlayer3TankInstance;
import com.ourpalm.tank.vo.tank.NewPlayer4TankInstance;
import com.ourpalm.tank.vo.tank.NewPlayer5TankInstance;

public class TankAppImpl implements TankApp {
	private final static Logger logger = LogCore.runtime;
	private static final long BROADCASET_LINK_COUNT_INTERVAL_TIME = 5 * 1000;
	private static final int GROOVE_LIMIT = 7;
	private static final int LEVEL_MAX = 100;

	private Map<Integer, TankTemplate> tanks = new TreeMap<Integer, TankTemplate>();
	private Map<Integer, List<TankTemplate>> levelTanks = new HashMap<Integer,  List<TankTemplate>>();
	private Map<Integer, BuildTemplate> buildMap = new HashMap<>();
	private List<TankAiTemplate> aiTanks = new ArrayList<>();
	private Map<Integer, GoldTankTemplate> goldTanks = new HashMap<>();
	private Map<String, TankAiTestTemplate> aiTestTanks = new HashMap<>();
	private AiConfig aiConfig;
	private TankCoefficientTemplate tankCoefficientTemplate = new TankCoefficientTemplate();

	private int localPort;
	private String localIpAddress; // 本机外网IP
	private int startTime;
	private int versionCode;

	private XNameSupplier nameSupplier = new XNameSupplier();

	// KEY为实例ID
	private Map<Integer, AbstractInstance> tankInstances = new ConcurrentHashMap<Integer, AbstractInstance>();
	// 坦克实例ID生成器
	private AtomicInteger idFactory = new AtomicInteger(1);
	// 机器人名称
	@Deprecated
	private List<String> robotNameList = new ArrayList<>();
	// 附加机器人名称
	private Map<Integer, List<String>> xRobotNameList = new HashMap<>();

	private RoleTankDao roleTankDao;

	private TankDevelopDao tankDevelopDao;
	// 新改装PART MAP
	private Map<Integer, TankPartNewTemplate> partNewTemplateMap = new HashMap<>();
	// 新改装 坦克等级和槽 MAP <坦克等级，模板>
	private Map<Integer, GrooveTemplate> grooveTemplateMap = new HashMap<>();
	// 抽奖开槽
	private List<PartDrawPeshe> partDrawPeshe = new ArrayList<>();
	// 消除冷却次数与金币
	private Map<Integer, Integer> clearFreezeMap = new HashMap<>();
	// 强化map<强化等级 ，模板>
	private Map<Integer, StrengthenTemplate> strengthTemplateMap = new HashMap<>();
	// 强化冷却上限 小时
	public static int STRENGTHEN_LIMIT_TIME_HOUR;
	/** 坦克 类型加 等级取得 ID */
	private Map<String, TankPartNewTemplate> partNewLevelMap = new HashMap<>();
	private Map<Integer, List<TankPartNewTemplate>> partNews = new HashMap<>();
	@Override
	public void start() {
		STRENGTHEN_LIMIT_TIME_HOUR = SysConfig.get(19);
		this.loadTankTemplate();
		this.loadBuildTemplate();
		this.loadTankRobotNameTemplate();
		this.loadExtendedTankRobotNameTemplate();
		this.loadTankAiTemplate();
		this.loadTankAiTestTemplate();
		this.loadTankAiConfigTemplate();
		this.loadGoldTankTemplate();
		this.runBroadcastHeartbeatThread();
		this.loadTankCoefficientTemplate();

		this.loadPartNewTemplate();
		this.loadPartDrawTemplate();
		this.loadGrooveTemplate();
		this.loadClearFreezeTemplate();
		this.loadStrengthenTemplate();
	}

	private void loadPartNewTemplate() {
		String sourceFile = XlsSheetType.PartNew.getXlsFileName();
		String sheetName = XlsSheetType.PartNew.getSheetName();
		try {
			partNewTemplateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, TankPartNewTemplate.class);
			for (TankPartNewTemplate t : partNewTemplateMap.values()) {
				t.init();
				partNewLevelMap.put(t.getIndex_type() + "_" + t.getLevel(), t);
				if (!partNews.containsKey(t.getIndex_type())) {
					partNews.put(t.getIndex_type(), new ArrayList<TankPartNewTemplate>());
				}
				partNews.get(t.getIndex_type()).add(t);
				// t.init();
			}
			Comparator<TankPartNewTemplate> c = new Comparator<TankPartNewTemplate>() {

				@Override
				public int compare(TankPartNewTemplate o1, TankPartNewTemplate o2) {
					return o1.getLevel() - o2.getLevel();
				}
			};
			for (Integer idx_type : partNews.keySet()) {
				List<TankPartNewTemplate> list = partNews.get(idx_type);
				Collections.sort(list, c);
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadPartDrawTemplate() {
		String sourceFile = XlsSheetType.PartDraw.getXlsFileName();
		String sheetName = XlsSheetType.PartDraw.getSheetName();
		try {
			Map<Integer, PartDrawTemplate> partDrawMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, PartDrawTemplate.class);
			for (PartDrawTemplate t : partDrawMap.values()) {
				PartDrawPeshe p = new PartDrawPeshe();
				p.setType(t.getType());
				p.setItemId(t.getItemId());
				p.setNum(t.getNum());
				p.setGon(t.getRate());
				partDrawPeshe.add(p);
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadGrooveTemplate() {
		String sourceFile = XlsSheetType.Groove.getXlsFileName();
		String sheetName = XlsSheetType.Groove.getSheetName();
		try {
			grooveTemplateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, GrooveTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadClearFreezeTemplate() {
		String sourceFile = XlsSheetType.ClearFreeze.getXlsFileName();
		String sheetName = XlsSheetType.ClearFreeze.getSheetName();
		try {
			Map<Integer, ClearFreezeTemplate> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, ClearFreezeTemplate.class);
			for (ClearFreezeTemplate c : map.values()) {
				clearFreezeMap.put(c.getNum(), c.getGold());
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadStrengthenTemplate() {
		String sourceFile = XlsSheetType.Strengthen.getXlsFileName();
		String sheetName = XlsSheetType.Strengthen.getSheetName();
		try {
			strengthTemplateMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, StrengthenTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadTankCoefficientTemplate() {
		String sourceFile = XlsSheetType.TankCoefficient.getXlsFileName();
		String sheetName = XlsSheetType.TankCoefficient.getSheetName();
		try {
			List<TankCoefficientTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankCoefficientTemplate.class);
			tankCoefficientTemplate = list.get(0);
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadTankTemplate() {
		this.tanks.clear();
		this.levelTanks.clear();
		String sourceFile = XlsSheetType.TankTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankTemplate.getSheetName();
		try {
			List<TankTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankTemplate.class);
			for (TankTemplate template : list) {
				int id = template.getId_i();
				if (tanks.containsKey(id)) {
					LogCore.startup.error("坦克ID {} 重复", id);
					continue;
				}
				template.init();
				this.tanks.put(id, template);
				if (!this.levelTanks.containsKey(template.getLevel_i())) {
					this.levelTanks.put(template.getLevel_i(), new ArrayList<TankTemplate>());
				}
				this.levelTanks.get(template.getLevel_i()).add(template);
				// 验证配件是否存在
				// for (int partId : template.getParts()) {
				// if (!parts.containsKey(partId)) {
				// LogCore.startup.error("tankTemplate id={} part={} 配件不存在", id,
				// partId);
				// }
				// }
			}
		} catch (Exception e) {
			LogCore.startup.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadBuildTemplate() {
		String sourceFile = XlsSheetType.BuildTemplate.getXlsFileName();
		String sheetName = XlsSheetType.BuildTemplate.getSheetName();
		try {
			List<BuildTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, BuildTemplate.class);
			for (BuildTemplate template : list) {
				template.init();
				buildMap.put(template.getId_i(), template);
			}
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	/** 加载机器人姓名 */
	private void loadTankRobotNameTemplate() {
		String sourceFile = XlsSheetType.TankRobotNameTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankRobotNameTemplate.getSheetName();
		try {
			List<TankRobotNameTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankRobotNameTemplate.class);
			for (TankRobotNameTemplate template : list) {
				this.robotNameList.add(template.getFirst());
				// String firstName = template.getFirst();
				// for(TankRobotNameTemplate lastTemplate : list){
				// this.robotNameList.add(firstName + lastTemplate.getLast());
				// }
			}
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadExtendedTankRobotNameTemplate() {
		String sourceFile = XlsSheetType.XTankRobotNameTemplate.getXlsFileName();
		String sheetName = XlsSheetType.XTankRobotNameTemplate.getSheetName();
		try {
			List<TankRobotNameTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankRobotNameTemplate.class);
			Map<Integer, List<String>> lib = xRobotNameList;
			for (TankRobotNameTemplate template : list) {
				String primitiveName = template.getFirst();
				int length = primitiveName.length();
				if (!lib.containsKey(length)) {
					lib.put(length, new ArrayList<String>());
				}
				lib.get(length).add(primitiveName);
			}
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	// 加载机器人列表
	private void loadTankAiTemplate() {
		String sourceFile = XlsSheetType.TankAiTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAiTemplate.getSheetName();
		try {
			this.aiTanks = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankAiTemplate.class);
			for (TankAiTemplate template : this.aiTanks) {
				template.init();
			}
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	// 加载机器人列表
	private void loadTankAiTestTemplate() {
		String sourceFile = XlsSheetType.TankAiTestTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAiTestTemplate.getSheetName();
		try {
			List<TankAiTestTemplate> aiTestTanks = XlsPojoUtil.sheetToList(sourceFile, sheetName, TankAiTestTemplate.class);
			for (TankAiTestTemplate template : aiTestTanks) {
				template.init();
				this.aiTestTanks.put(template.getCamp() + "_" + template.getIndex(), template);
			}
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadTankAiConfigTemplate() {
		String sourceFile = XlsSheetType.TankAiConfigTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankAiConfigTemplate.getSheetName();
		try {
			AiConfig aiConfig = XlsPojoUtil.sheetToList(sourceFile, sheetName, AiConfig.class).get(0);
			this.aiConfig = aiConfig;
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	private void loadGoldTankTemplate() {
		String sourceFile = XlsSheetType.TankGoldTemplate.getXlsFileName();
		String sheetName = XlsSheetType.TankGoldTemplate.getSheetName();
		try {
			List<GoldTankTemplate> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, GoldTankTemplate.class);
			for (GoldTankTemplate template : list) {
				template.init();
				goldTanks.put(template.getTankId(), template);
			}
		} catch (Exception e) {
			logger.error("加载{},{}时异常", sourceFile, sheetName, e);
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public TankTemplate getTankTemplate(int tankId) {
		return this.tanks.get(tankId);
	}

	@Override
	public TankTemplate getTankAiTemplate(int score, int minLevel, int maxLevel) {
//		, 542, 4210, 4250
		List<Integer> available = Arrays.asList(
				212, 312, 412, 522, 532 // TODO
				);
		
		
		Collections.shuffle(available);
//		for (Integer i : available) {
//			TankTemplate t = getTankTemplate(i);
//			if (t.getLevel_i() >= minLevel
//					&& t.getLevel_i() <= maxLevel) {
//				return t;
//			}
//		}
		int idx = RandomUtil.randomInt() % available.size();
		// int level = getTemplateLevelByScore(score);
		// return randomTemplateByLevel(level);
		int id = available.get(idx);
		TankTemplate template = getTankTemplate(id);
		return template;
	}

	@Override
	public TankAiTemplate getTankAiTemplate2(int score) {
		for (TankAiTemplate template : this.aiTanks) {
			if (score >= template.getMin() && score <= template.getMax()) {
				logger.debug("最高战斗力 = {}  min = {}, max = {}", score, template.getMin(), template.getMax());
				return template;
			}
		}
		return aiTanks.get(0);
	}

	private int getTemplateLevelByScore(int score) {
		for (Map.Entry<Integer, TankTemplate> entry : tanks.entrySet()) {
			TankTemplate template = entry.getValue();
			if (template.getShowBattleNum() == 0) {
				continue;
			}
			if (score <= template.getShowBattleNum()) {
				return template.getLevel_i();
			}
		}
		return 1;
	}

	private TankTemplate randomTemplateByLevel(int level) {
		List<TankTemplate> tanks = levelTanks.get(level);
		int idx = RandomUtil.randomInt() % tanks.size();
		TankTemplate t = tanks.get(idx);
		while (t.getTank_type() != 0) {
			idx = RandomUtil.randomInt() % tanks.size();
			t = tanks.get(idx);
		}
		return tanks.get(idx);
	}

	@Override
	public TankAiTestTemplate getTankAiTestTemplate(int camp, int index) {
		return this.aiTestTanks.get(camp + "_" + index);
	}

	@Override
	public AbstractInstance createTankInstance(int roleId, int tankId) {
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		int tankInstanceId = this.idFactory.incrementAndGet();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		AbstractInstance tankInstance = new TankInstance(tankInstanceId);
		tankInstance.setRoleId(roleId);
		tankInstance.setTemplateId(tankId);
		tankInstance.setRoleName(role.getRoleName());
		tankInstance.setLevel(role.getLevel());

		int attackSocre = this.calcAllBattleScore(roleId, tankId);

		tankInstance.setBattleScore(attackSocre);

		// 设置属性
		Map<AttrType, Float> attrMap = this.tankBornAttr(tankInstance);
		// 赋值到坦克身上
		tankInstance.setAttribute(attrMap);
		// 计算最终属性
		TankFormula.calcAttr(attrMap);
		// 设置当前血量
		tankInstance.set(AttrType.hp, attrMap.get(AttrType.n_hpMax));
		// 初始化坦克
		tankInstance.init();
		// 初始化技能
		this.initSkill(tankInstance);

		this.tankInstances.put(tankInstanceId, tankInstance);

		return tankInstance;
	}

	// 获取坦克+成员所有属性
	@Override
	public Map<AttrType, Float> getAllTankMemberAttr(int roleId, int tankId) {
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (roleTank == null) {
			return null;
		}

		AbstractInstance tankInstance = new TankInstance(0);
		tankInstance.setTemplateId(tankId);
		tankInstance.setRoleId(roleId);

		// 设置属性
		Map<AttrType, Float> attrMap = this.tankBornAttr(tankInstance);
		// 赋值到坦克身上
		tankInstance.setAttribute(attrMap);
		// 计算最终属性
		TankFormula.calcAttr(attrMap);
		// 设置当前血量
		tankInstance.set(AttrType.hp, attrMap.get(AttrType.n_hpMax));

		return tankInstance.getAttribute();
	}

	// 初始化技能
	private void initSkill(AbstractInstance tank) {
		if (tank.isRobot()) {
			return;
		}
		Map<String, RoleMember> memberMap = GameContext.getMemberApp().getUseMember(tank.getRoleId());
		if (Util.isEmpty(memberMap)) {
			return;
		}
		for (RoleMember member : memberMap.values()) {
			int templateId = member.getTemplateId();
			MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(templateId);
			if (template == null) {
				continue;
			}
			int skillId = template.getSpell();
			if (skillId <= 0) {
				continue;
			}
			Skill skill = GameContext.getSkillApp().createSkill(tank, skillId);
			if (skill == null) {
				logger.warn("技能id={} 不存在", skillId);
				continue;
			}
			tank.putSkill(skill);
		}

	}

	public void addPartNewAttr(AttrBuffer attr, AbstractInstance tank) {
		if (tank.getRoleId() <= 0) {
//			TankTemplate tankTemplate = tanks.get(tank.getTemplateId());
//			if (tankTemplate == null) {
//				return;
//			}
//			int part1 = tankTemplate.getParts1_i();
//			int part2 = tankTemplate.getParts2_i();
//			int part3 = tankTemplate.getParts3_i();
//			int part4 = tankTemplate.getParts4_i();
//			int part5 = tankTemplate.getParts5_i();
//			int part6 = tankTemplate.getParts6_i();
//			int part7 = tankTemplate.getParts7_i();
//			partNewAttr(attr, part1);
//			partNewAttr(attr, part2);
//			partNewAttr(attr, part3);
//			partNewAttr(attr, part4);
//			partNewAttr(attr, part5);
//			partNewAttr(attr, part6);
//			partNewAttr(attr, part7);

//			for (Integer part : tank.getAiParts().values()) {
//				partNewAttr(attr, part);
//			}

		} else {
			// 配件新属性
			RoleTank roleTank = GameContext.getTankApp().getRoleTank(tank.getRoleId(), tank.getTemplateId());
			Map<PART_INDEX, PartInfo> map = roleTank.getPartNewMap();
			for (PartInfo info : map.values()) {
				if (info.isActive()) {
					partNewAttr(attr, info.getPartId(),tank.getTemplateId());
				}
			}
		}

	}

	/**
	 * @param attr
	 * @param partId
	 */
	public void partNewAttr(AttrBuffer attr, int partId,int tankTemplateId) {
		if (partId <= 0)
			return;
		TankPartNewTemplate tt = getPartNewTemplateMap().get(partId);
		if (tt == null)
			return;
		Map<AttrType, Float> map = tt.getNewAttr();
//		for (AttrType attrType : map.keySet()) {
//			Float f = attr.getAttr(attrType);
//			if (f != null) {
//				Float partValue = map.get(attrType);
//				if (partValue != null) {
//					attr.put(attrType, f * (1 + partValue / 100));
//				}
//			}
//		}
		attr.append(tankRawAddAttr(map, tankTemplateId));
	}

	/**
	 * 通过 传过 加成百分比map，返回根据 原生 坦克属性算得的，值的map
	 *
	 * @param percentMap
	 *            百分比MAP，
	 * @param tankTemplateId
	 *            主战TANKID
	 */
	public Map<AttrType, Float> tankRawAddAttr(Map<AttrType, Float> percentMap, int tankTemplateId) {
		AttrBuffer attr = new AttrBuffer();
		for (Entry<AttrType, Float> entry : percentMap.entrySet()) {
			float tankRawValue = getTankRawAttr(tankTemplateId).get(entry.getKey());
			float memberValue = tankRawValue * entry.getValue();
			attr.append(entry.getKey(), memberValue);
		}
		return attr.getAttrMap();
	}

	public Map<AttrType, Float> getTankRawAttr(int tankTemplateId) {
		AttrBuffer attr = new AttrBuffer();
		// 坦克新属性
		TankTemplate tankTemplate = getTankTemplate(tankTemplateId);
		Map<AttrType, Float> map = tankTemplate.getNewAttr();
		for (AttrType attrType : map.keySet()) {
			attr.append(attrType, map.get(attrType));
		}
		return attr.getAttrMap();
	}

	// 坦克出生属性
	@Override
	public Map<AttrType, Float> tankBornAttr(AbstractInstance tank) {
		AttrBuffer attr = new AttrBuffer();
		List<IComponent> components = this.getComponents(tank);
		for (IComponent component : components) {
			attr.append(component.getAttr());
		}

		// 坦克新属性
		TankTemplate tankTemplate = getTankTemplate(tank.getTemplateId());
		Map<AttrType, Float> map = tankTemplate.getNewAttr();
		for (AttrType attrType : map.keySet()) {
			attr.append(attrType, map.get(attrType));
		}

		// 配件新属性
		addPartNewAttr(attr, tank);

		// 成员属性
		 attr.append(this.memberAttr(tank));
		// attr.append(GameContext.getMemberNewApp().getMemberAttr(tank.getRoleId()));

		// 成员技能属性
//		 attr.append(GameContext.getMemberNewApp().getMemberSkillAttr(tank.getRoleId()));


		return attr.getAttrMap();
	}

	/** 成员属性加成 */
	private Map<AttrType, Float> memberAttr(AbstractInstance tank) {
		if (tank.isRobot()) {
			return null;
		}
		Map<AttrType, Float> attrMap = GameContext.getMemberApp().getMemberAttr(tank.getRoleId());
		// 百分比属性计算
		TankFormula.calcAttr(attrMap);

		// 根据坦克配置的成员加成比例计算加成值
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tank.getTemplateId());
		if (template == null) {
			return attrMap;
		}

		for (Entry<AttrType, Float> entry : template.getMemberAttrMap().entrySet()) {
			Float value = attrMap.get(entry.getKey());
			if (value == null) {
				continue;
			}
			attrMap.put(entry.getKey(), value * entry.getValue());
		}

		return attrMap;
	}

	@Override
	public void reCalcAttr(AbstractInstance tank, Map<AttrType, Float> attrMap) {
		if (Util.isEmpty(attrMap)) {
			return;
		}
		for (Entry<AttrType, Float> entry : attrMap.entrySet()) {
			Float oldValue = tank.get(entry.getKey());
			if (oldValue == null) {
				oldValue = 0f;
			}
			float newValue = oldValue + entry.getValue();
			// logger.debug("[属性重算]变化属性 {} : {} -> {}", entry.getKey(),
			// oldValue, newValue);
			tank.changeAttr(entry.getKey(), newValue);
		}
		// 大于最大血量处理
		if (tank.get(AttrType.hp) > tank.get(AttrType.n_hpMax)) {
			tank.changeAttr(AttrType.hp, tank.get(AttrType.n_hpMax));
		}
		// 属性同步
		tank.synchChangeAttr();
	}

	/** 属性重算 */
	@Override
	public void reCalcAttr(AbstractInstance tank) {
		if (tank.isRobot()) {
			// 设置当前血量
			tank.changeAttr(AttrType.hp, tank.get(AttrType.n_hpMax));
			// 属性同步
			tank.synchChangeAttr();
			return;
		}
		// 设置属性
		Map<AttrType, Float> attrMap = this.tankBornAttr(tank);
		// 赋值到坦克身上
		tank.setAttribute(attrMap);
		// 计算最终属性
		TankFormula.calcAttr(attrMap);
		// 设置当前血量
		tank.changeAttr(AttrType.hp, attrMap.get(AttrType.n_hpMax));
		//穿甲弹属性重算
		chuanJiaReCalc(tank);
		// 属性同步
		tank.synchChangeAttr();
	}

	private void chuanJiaReCalc(AbstractInstance tank) {
		final GoodsShellTemplate goodsShellTemplate = GameContext.getGoodsApp().getGoodsShellTemplate(tank.getBattleGoods());
		if (goodsShellTemplate != null) {
			tank.changeAttr(AttrType.n_stab, tank.get(AttrType.n_stab) * (1 + goodsShellTemplate.getChuajia()));
		}
	}
	
	/** 创建教学关机器人 */
	@Override
	public AbstractInstance createTeachTank(int instanceId, int tankId) {
		TankTemplate template = this.getTankTemplate(tankId);

		AbstractInstance tank = new TankInstance(instanceId);
		tank.setId(instanceId);
		tank.setRoleId(-1);
		tank.setTemplateId(template.getId_i());
		tank.setRoleName(template.getName_s());
		tank.setLevel(RandomUtil.randomInt(20));

		// 设置属性
		Map<AttrType, Float> attrMap = this.tankBornAttr(tank);
		// 赋值到坦克身上
		tank.setAttribute(attrMap);
		// 计算属性
		TankFormula.calcAttr(attrMap);
		// 设置当前血量
		tank.set(AttrType.hp, attrMap.get(AttrType.n_hpMax));
		// 初始化坦克
		tank.init();

		return tank;
	}

	public static class AiCondition {

		public int battleScore;
		public int minLevel;
		public int maxLevel;
	}

	@Override
	public AbstractInstance createAiTank(AiCondition ac, int preseasonId) {

		int instanceId = this.idFactory.incrementAndGet();

		int maximumChars = RandomUtil.randomInt() % 6 + 1;
		String name = nameSupplier.join(nameSupplier.get(maximumChars, 2), "-");

		int battleScore = ac.battleScore;
		float multiple = (float) ((9 + Math.random() * 3) / 10.0);
		battleScore *= multiple;
		battleScore = (int) battleScore;

		// 1. 随机出一个坦克模板
		TankTemplate template = getTankAiTemplate(battleScore, ac.minLevel, ac.maxLevel);

		AbstractInstance tank = this.createTankInstanceByPreseasonId(preseasonId, instanceId);
		tank.setRoleId(-instanceId);
		tank.setTemplateId(template.getId_i());
		tank.setRoleName(name);
		tank.setLevel(template.getLevel_i());

		// 2. 随机出部件
		int numOfParts = RandomUtil.randomInt() % 8;
		Map<Integer, Integer> parts = new HashMap<>();
		while (numOfParts > 0) {
			int idx = RandomUtil.randomInt() % partNews.keySet().size() + 1;
			if (!parts.containsKey(idx) && idx != 6 && idx != 7) {
				parts.put(idx, randomParts(idx, battleScore));
			}
			numOfParts -= 1;
		}
		tank.setAiParts(parts);

		// 设置属性
		Map<AttrType, Float> attrMap = this.tankBornAttr(tank);

		// 赋值到坦克身上
		tank.setAttribute(attrMap);
		// 计算属性
		TankFormula.calcAttr(attrMap);
		// 设置当前血量
		tank.set(AttrType.hp, attrMap.get(AttrType.maxHp));
		// 初始化坦克
		tank.init();
		// 初始化AI
		tank.putAi(new MoveAi(tank));
		tank.putAi(new FireAi(tank));
		tank.putAi(new EscapeAi(tank));
		tank.putAi(new StayAndFireAi(tank));
		tank.putAi(new PursueAi(tank));
		tank.putAi(new DeathAi(tank));
		tank.putAi(new MoveToFlagAi(tank));
		tank.putAi(new FireToFlagAi(tank));
		tank.putAi(new CircleAi(tank));
		tank.putAi(new MoveToEnemyAi(tank));
		tank.putAi(new MoveOutFlagAi(tank));

		this.tankInstances.put(instanceId, tank);

		return tank;
	}

	private int randomParts(int idx, int battleScore) {
		List<TankPartNewTemplate> list = partNews.get(idx);
		for (TankPartNewTemplate tpnt : list) {
			if (battleScore <= tpnt.getBattleNum()) {
				return tpnt.getId();
			}
		}
		return list.get(0).getId();
	}

	/** 创建机器人 */
	@Override
	public AbstractInstance createNpcTank(int tankId, int preseasonId) {
		int instanceId = this.idFactory.incrementAndGet();

		// int index = RandomUtil.randomInt() % this.robotNameList.size();
		// String name = this.robotNameList.get(index);

		int maximumChars = RandomUtil.randomInt() % 6 + 1;
		String name = nameSupplier.join(nameSupplier.get(maximumChars, 2), "-");

		TankTemplate template = this.getTankTemplate(tankId);

		AbstractInstance tank = this.createTankInstanceByPreseasonId(preseasonId, instanceId);

		tank.setRoleId(-instanceId);
		tank.setTemplateId(template.getId_i());
		tank.setRoleName(name);
		tank.setLevel(template.getLevel_i());

		// 设置属性
		Map<AttrType, Float> attrMap = this.tankBornAttr(tank);
		// 赋值到坦克身上
		tank.setAttribute(attrMap);
		// 计算属性
		TankFormula.calcAttr(attrMap);
		// 设置当前血量
		tank.set(AttrType.hp, attrMap.get(AttrType.n_hpMax));
		// 初始化坦克
		tank.init();
		// 初始化AI
		tank.putAi(new MoveAi(tank));
		tank.putAi(new FireAi(tank));
		tank.putAi(new EscapeAi(tank));
		tank.putAi(new StayAndFireAi(tank));
		tank.putAi(new PursueAi(tank));
		tank.putAi(new DeathAi(tank));
		tank.putAi(new MoveToFlagAi(tank));
		tank.putAi(new FireToFlagAi(tank));
		tank.putAi(new CircleAi(tank));
		tank.putAi(new MoveToEnemyAi(tank));
		tank.putAi(new MoveOutFlagAi(tank));

		this.tankInstances.put(instanceId, tank);

		return tank;
	}

	@Override
	public AbstractInstance createBuildTank(int buildId, BodyType type) {
		// int roleId = npcIdFactory.decrementAndGet();
		int instanceId = this.idFactory.incrementAndGet();

		BuildTemplate template = this.buildMap.get(buildId);

		AbstractInstance tank = new BuildInstance(instanceId);
		tank.setRoleId(-1);
		tank.setTemplateId(buildId);
		tank.setLevel(RandomUtil.randomInt(20));
		tank.setBodyType(type);

		// 设置属性
		Map<AttrType, Float> attrMap = new HashMap<>(template.getAttr());
		// 赋值到坦克身上
		tank.setAttribute(attrMap);
		// 设置当前血量
		tank.set(AttrType.hp, attrMap.get(AttrType.n_hpMax));

		this.tankInstances.put(instanceId, tank);

		return tank;
	}

	private List<IComponent> getComponents(AbstractInstance tank) {
		List<IComponent> result = new ArrayList<IComponent>();
		result.add(getTankTemplate(tank.getTemplateId()));
		return result;
	}

	@Override
	public TankBuyResult tankBuy(int roleId, int tankId, STC_TANK_BUY_MSG.Builder builder) {
		TankBuyResult result = new TankBuyResult();
		result.setResult(Result.FAILURE);
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		if (template == null) {
			result.setInfo(Tips.TANK_NO_EXIST);
			return result;
		}

		// 判断是否已经拥有
		RoleTank tank = this.getRoleTank(roleId, tankId);
		if (tank != null) {
			result.setInfo(Tips.TANK_BUY_EXIST);
			return result;
		}

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(roleId);
		if (role.getPark() < allTankList.size() + 1) {
			result.failure("车位不足，请先购买车位");
			return result;
		}

		TankDevelop tankDevelop = tankDevelopDao.getDevelop(roleId);
		Map<Integer, Integer> developMap = tankDevelop.getDevelopMap();
		Integer tankState = developMap.get(tankId);
		if (tankState == null || tankState != 2) {
			result.setInfo("不符合购买条件！");
			return result;
		}

		// 扣除银币
		int needIron = template.getNeedIron();
		boolean decMoney = GameContext.getUserAttrApp().consumIron(roleId, needIron, OutputType.tankBuyDec.type(), StringUtil.buildLogOrigin(template.getName_s(), OutputType.tankBuyDec.getInfo()));
		if (!decMoney) {
			result.setInfo(Tips.NEED_IRON);
			return result;
		}
		// 设置为已购买
		developMap.put(tankId, 3);
		tankDevelopDao.save(tankDevelop);
		TANK_DEVELOP_INFO.Builder developBuilder = TANK_DEVELOP_INFO.newBuilder();
		developBuilder.setTankTemplateId(tankId);
		developBuilder.setState(3);
		builder.setDevelopInfo(developBuilder);
		RoleTank newTank = this.tankAdd(roleId, tankId, "坦克购买");

		GameContext.getQuestTriggerApp().tankBuy(roleId, tankId);

		result.setResult(Result.SUCCESS);
		result.setRoleTank(newTank);

		return result;
	}

	@Override
	public RoleTank tankAdd(int roleId, int tankId, String origin) {
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		// 添加新坦克
		RoleTank newTank = new RoleTank();
		newTank.setRoleId(roleId);
		newTank.setTankId(tankId);

		UserInitTemplate initTemplate = GameContext.getUserApp().getUserInitTemplate();
		// 初始化新改装配件
		Map<PART_INDEX, PartInfo> partInfoMap = newTank.getPartNewMap();
		initPartInfo(partInfoMap, initTemplate, template.getLevel_i(), newTank);

		this.saveRoleTank(newTank);
		return newTank;
	}

	/**
	 * 计算坦克真实战斗力，匹配用
	 */
	@Override
	public int calcAllBattleScore(RoleTank tank) {
		// int totalValue = (int)
		// this.tanks.get(tank.getTankId()).getStrength();

		// 使用的成员
		int roleId = tank.getRoleId();
		// totalValue +=
		// GameContext.getMemberApp().calcUseMemberStrength(roleId);
		//
		// // 所使用的勋章战斗力加成比
		// float strengthRat =
		// GameContext.getMemberApp().calcUseMedalStrengthRat(roleId);
		// totalValue = (int) ((float) totalValue * (1 + strengthRat));

		Map<AttrType, Float> attrMap = GameContext.getTankApp().getAllTankMemberAttr(roleId, tank.getTankId());
		TankCoefficientTemplate t = GameContext.getTankApp().getTankCoefficientTemplate();
		// 攻击总值
		float fight = (attrMap.get(AttrType.n_minHit) + attrMap.get(AttrType.n_maxHit)) * t.getN_hitR() * (1 + attrMap.get(AttrType.n_crit)) * (1 + (0.02359f + 0) / 2)
				+ attrMap.get(AttrType.n_fireSpeed) * t.getN_fireR() + attrMap.get(AttrType.n_stab) * t.getN_stabR()
				- (attrMap.get(AttrType.n_minPrecision) + attrMap.get(AttrType.n_maxPrecision)) * t.getN_precisionR()
				- (attrMap.get(AttrType.n_maxPrecision) - attrMap.get(AttrType.n_minPrecision)) / attrMap.get(AttrType.n_aimSpeed) * t.getN_aimR();
		// 防御总值
		float def = attrMap.get(AttrType.n_hpMax) * (1 + attrMap.get(AttrType.n_dr)) * t.getN_hpMaxR()
				+ (attrMap.get(AttrType.n_ArmorTurretFront) * t.getN_ArmorTurretFrontR() + attrMap.get(AttrType.n_ArmorTurretMiddle) * t.getN_ArmorTurretMiddleR()
						+ attrMap.get(AttrType.n_ArmorTurretbehind) * t.getN_ArmorTurretbehindR() + attrMap.get(AttrType.n_ArmorBodyFront) * t.getN_ArmorBodyFrontR()
						+ attrMap.get(AttrType.n_ArmorBodyMiddle) * t.getN_ArmorBodytMiddleR() + attrMap.get(AttrType.n_ArmorBodybehind) * t.getN_ArmorBoeybehindR()) / 6;
		// 机动总值
		float flexi = attrMap.get(AttrType.n_turrentSpeep) * t.getN_turrentSpeepR() + attrMap.get(AttrType.n_bodySpeed) * t.getN_bodySpeedR() + attrMap.get(AttrType.n_nimble) * t.getN_nimbleR()
				+ attrMap.get(AttrType.n_speed) * t.getN_speedR() + attrMap.get(AttrType.n_speedUp) * t.getN_speedUpR();
		// 辅助总值
		float assist = attrMap.get(AttrType.n_view) * t.getN_viewR() + attrMap.get(AttrType.n_camouflage) * t.getN_camouflageR();
		// 最终总值
		float finalBattleNum = (def + flexi + assist) * (fight + flexi + attrMap.get(AttrType.n_view) * t.getN_viewRAll());

		return (int) finalBattleNum;
	}

	// 计算坦克匹配分
	@Override
	public int calcAllMatchScore(int roleId, int tankId) {
		RoleTank tank = this.getRoleTank(roleId, tankId);
		if (tank == null) {
			logger.warn("计算坦克匹配分, roleId = {}, tankId = {} 不曾拥有该坦克 ", roleId, tankId);
			return 0;
		}
		return this.calcAllMatchScore(tank);
	}

	// 计算坦克匹配分
	@Override
	public int calcAllMatchScore(RoleTank tank) {
		int roleId = tank.getRoleId();
		int totalValue = (int) this.tanks.get(tank.getTankId()).getMatchScore();

		// 使用的成员
		totalValue += GameContext.getMemberApp().calcUseMemberMatchScore(roleId);

		// 所使用的勋章战斗力加成比
		float strengthRat = GameContext.getMemberApp().calcUseMedalMatchScoreRat(roleId);
		totalValue = (int) (totalValue * (1 + strengthRat));

		return totalValue;
	}

	/***
	 * 计算自身坦克和火炮配件的攻击力
	 */
	@Override
	public int calcTankAndPart2Atk(AbstractInstance tank) {
		if (tank.getBodyType() != BodyType.tank) {
			return 0;
		}
		final int tankId = tank.getTemplateId();
		TankTemplate template = this.tanks.get(tankId);
		if (template == null) {
			return 0;
		}
		// 火炮配件ID

		float atk = template.getAtk();
		return (int) atk;
	}

	// 是否拥有前置坦克
	private boolean hadNeedTankUpLevel(int roleId, List<Integer> needTanks) {
		if (Util.isEmpty(needTanks)) {
			return true;
		}
		for (int needTankId : needTanks) {
			RoleTank roleTank = this.getRoleTank(roleId, needTankId);
			if (roleTank != null) {
				// 判断研发进度
				// 整体都改 TODO
				// if (this.hasTankPartMaxLevel(roleTank)) {
				// return true;
				// }
			}
		}
		return false;
	}

	// 判断消耗材料是否足够
	@Override
	public boolean goodsIsEnough(int roleId, Map<Integer, Integer> consumeMap) {
		for (Entry<Integer, Integer> entry : consumeMap.entrySet()) {
			int goodsId = entry.getKey();
			int num = entry.getValue();
			int count = GameContext.getGoodsApp().getCount(roleId, goodsId);
			if (count < num) {
				GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
				if (template == null) {
					logger.warn("!!!!!! ============ 物品不存在 id{} ===============", goodsId);
					continue;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public AbstractInstance getTankInstanceByRoleId(int roleId) {
		RoleBattle roleBattle = GameContext.getMatchApp().getLocalRoleBattle(roleId);
		if (roleBattle == null) {
			return null;
		}
		return this.getInstance(roleBattle.getTankInstanceId());
	}

	@Override
	public AbstractInstance getInstance(int instanceId) {
		return this.tankInstances.get(instanceId);
	}

	@Override
	public void removeTankInstance(int instanceId) {
		this.tankInstances.remove(instanceId);
	}

	@Override
	public void offline(int roleId) {
		AbstractInstance tank = getTankInstanceByRoleId(roleId);
		if (tank == null) {
			return;
		}
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if (mapInstance != null) {
			mapInstance.leave(tank.getId());
		}
	}

	@Override
	public BuildTemplate getBuildTemplate(int buildId) {
		return buildMap.get(buildId);
	}

	@Override
	public Result changeMainTank(int roleId, int tankId) {
		// 判断是否拥有此坦克
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (roleTank == null) {
			return Result.newFailure(Tips.TANK_NO_EXIST);
		}
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		role.setMainTankId(tankId);
		GameContext.getUserApp().saveRoleAccount(role);

		return Result.newSuccess();
	}

	@Override
	public RoleTank createRoleTank(int roleId, int tankId, UserInitTemplate initTemplate) {
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		RoleTank tank = new RoleTank();
		tank.setRoleId(roleId);
		tank.setTankId(tankId);
		// 初始化新改装配件
		Map<PART_INDEX, PartInfo> partInfoMap = tank.getPartNewMap();
		initPartInfo(partInfoMap, initTemplate, template.getLevel_i(), tank);

		roleTankDao.insert(tank);
		TankDevelop tankDevelop = new TankDevelop();
		tankDevelop.setRoleId(roleId);
		tankDevelop.getDevelopMap().put(tankId, 3);
		tankDevelopDao.save(tankDevelop);

		return tank;
	}

	private void initPartInfo(Map<PART_INDEX, PartInfo> partInfoMap, UserInitTemplate initTemplate, int tankLevel, RoleTank tank) {
		GrooveTemplate template = grooveTemplateMap.get(tankLevel);
		int initGrooveNum = template.getGrooveNum();
		tank.setGrooveNum(initGrooveNum);
		PartInfo partInfo1 = new PartInfo();
		partInfo1.setIndex(PART_INDEX.HIT);
		partInfo1.setActive(initGrooveNum >= 1 ? true : false);
		partInfo1.setPartId(initTemplate.getInitPart1());
		partInfoMap.put(PART_INDEX.HIT, partInfo1);

		PartInfo partInfo2 = new PartInfo();
		partInfo2.setIndex(PART_INDEX.PRECISION);
		partInfo2.setActive(initGrooveNum >= 2 ? true : false);
		partInfo2.setPartId(initTemplate.getInitPart2());
		partInfoMap.put(PART_INDEX.PRECISION, partInfo2);

		PartInfo partInfo3 = new PartInfo();
		partInfo3.setIndex(PART_INDEX.STAB);
		partInfo3.setActive(initGrooveNum >= 3 ? true : false);
		partInfo3.setPartId(initTemplate.getInitPart3());
		partInfoMap.put(PART_INDEX.STAB, partInfo3);

		PartInfo partInfo4 = new PartInfo();
		partInfo4.setIndex(PART_INDEX.ARMOR);
		partInfo4.setActive(initGrooveNum >= 4 ? true : false);
		partInfo4.setPartId(initTemplate.getInitPart4());
		partInfoMap.put(PART_INDEX.ARMOR, partInfo4);

		PartInfo partInfo5 = new PartInfo();
		partInfo5.setIndex(PART_INDEX.HP);
		partInfo5.setActive(initGrooveNum >= 5 ? true : false);
		partInfo5.setPartId(initTemplate.getInitPart5());
		partInfoMap.put(PART_INDEX.HP, partInfo5);

		PartInfo partInfo6 = new PartInfo();
		partInfo6.setIndex(PART_INDEX.SPEED);
		partInfo6.setActive(initGrooveNum >= 6 ? true : false);
		partInfo6.setPartId(initTemplate.getInitPart6());
		partInfoMap.put(PART_INDEX.SPEED, partInfo6);

		PartInfo partInfo7 = new PartInfo();
		partInfo7.setIndex(PART_INDEX.TURN);
		partInfo7.setActive(initGrooveNum >= 7 ? true : false);
		partInfo7.setPartId(initTemplate.getInitPart7());
		partInfoMap.put(PART_INDEX.TURN, partInfo7);
	}

	@Override
	public RoleTank getRoleTank(int roleId, int tankId) {
		return roleTankDao.getRoleTank(roleId, tankId);
	}

	@Override
	public void saveRoleTank(RoleTank roleTank) {
		roleTankDao.insert(roleTank);
	}

	// @Override
	// public TankAiGroupTemplate randomTankAiGroupTemplate(int group){
	// List<TankAiGroupTemplate> list = this.aiGroupMap.get(group);
	// if(Util.isEmpty(list)){
	// return null;
	// }
	// int index = RandomUtil.randomInt(list.size());
	// return list.get(index);
	// }

	@Override
	public Collection<RoleTank> getAllRoleTank(int roleId) {
		Map<Integer, RoleTank> allMap = roleTankDao.getAll(roleId);
		return allMap.values();
	}

	public void setRoleTankDao(RoleTankDao roleTankDao) {
		this.roleTankDao = roleTankDao;
	}

	@Override
	public int calcAllBattleScore(int roleId, int tankId) {
		RoleTank tank = this.roleTankDao.getRoleTank(roleId, tankId);
		if (tank == null) {
			return 0;
		}
		return this.calcAllBattleScore(tank);
	}

	@Override
	public int calcAllBattleScore(AbstractInstance tank) {
		// int totalValue = (int)
		// this.tanks.get(tank.getTankId()).getStrength();

		// 使用的成员
		// totalValue +=
		// GameContext.getMemberApp().calcUseMemberStrength(roleId);
		//
		// // 所使用的勋章战斗力加成比
		// float strengthRat =
		// GameContext.getMemberApp().calcUseMedalStrengthRat(roleId);
		// totalValue = (int) ((float) totalValue * (1 + strengthRat));

		// 设置属性
		Map<AttrType, Float> map = this.tankBornAttr(tank);
		// 赋值到坦克身上
		tank.setAttribute(map);
		// 计算最终属性
		TankFormula.calcAttr(map);
		// 设置当前血量
		tank.set(AttrType.hp, map.get(AttrType.maxHp));

		Map<AttrType, Float> attrMap = tank.getAttribute();

		TankCoefficientTemplate t = GameContext.getTankApp().getTankCoefficientTemplate();
		// 攻击总值
		float fight = (attrMap.get(AttrType.n_minHit) + attrMap.get(AttrType.n_maxHit)) * t.getN_hitR() * (1 + attrMap.get(AttrType.n_crit)) * (1 + (0.02359f + 0) / 2)
				+ attrMap.get(AttrType.n_fireSpeed) * t.getN_fireR() + attrMap.get(AttrType.n_stab) * t.getN_stabR()
				- (attrMap.get(AttrType.n_minPrecision) + attrMap.get(AttrType.n_maxPrecision)) * t.getN_precisionR()
				- (attrMap.get(AttrType.n_maxPrecision) - attrMap.get(AttrType.n_minPrecision)) / attrMap.get(AttrType.n_aimSpeed) * t.getN_aimR();
		// 防御总值
		float def = attrMap.get(AttrType.n_hpMax) * (1 + attrMap.get(AttrType.n_dr)) * t.getN_hpMaxR()
				+ (attrMap.get(AttrType.n_ArmorTurretFront) * t.getN_ArmorTurretFrontR() + attrMap.get(AttrType.n_ArmorTurretMiddle) * t.getN_ArmorTurretMiddleR()
						+ attrMap.get(AttrType.n_ArmorTurretbehind) * t.getN_ArmorTurretbehindR() + attrMap.get(AttrType.n_ArmorBodyFront) * t.getN_ArmorBodyFrontR()
						+ attrMap.get(AttrType.n_ArmorBodyMiddle) * t.getN_ArmorBodytMiddleR() + attrMap.get(AttrType.n_ArmorBodybehind) * t.getN_ArmorBoeybehindR()) / 6;
		// 机动总值
		float flexi = attrMap.get(AttrType.n_turrentSpeep) * t.getN_turrentSpeepR() + attrMap.get(AttrType.n_bodySpeed) * t.getN_bodySpeedR() + attrMap.get(AttrType.n_nimble) * t.getN_nimbleR()
				+ attrMap.get(AttrType.n_speed) * t.getN_speedR() + attrMap.get(AttrType.n_speedUp) * t.getN_speedUpR();
		// 辅助总值
		float assist = attrMap.get(AttrType.n_view) * t.getN_viewR() + attrMap.get(AttrType.n_camouflage) * t.getN_camouflageR();
		// 最终总值
		float finalBattleNum = (def + flexi + assist) * (fight + flexi + attrMap.get(AttrType.n_view) * t.getN_viewRAll());

		return (int) finalBattleNum;
	}

	@Override
	public TankItem buildTankItem(RoleTank tank) {
		if (tank != null) {
			TankItem item = TankItem.newBuilder().setTankId(tank.getTankId()).setTankExp(tank.getExp()).setTired(tank.getTired()).setTiredMax(getTankTemplate(tank.getTankId()).getTired())
					.addAllInfos(builderPartNews(tank)).setGrooveNum(tank.getGrooveNum()).setPartOnNum(calcPartOnNum(tank))

					.build();

			return item;
		}
		return null;
	}

	/**
	 * 计算安上多少个配件了
	 *
	 * @param tank
	 */
	private int calcPartOnNum(RoleTank tank) {
		int num = 0;
		for (PartInfo info : tank.getPartNewMap().values()) {
			if (info.isActive())
				num++;
		}
		return num;
	}

	@Override
	public Freeze_Info buildFreezeInfo(int roleId) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		Freeze_Info info = Freeze_Info.newBuilder().setFreezeTime(calcRestFreezeTime(roleId)).setFreezeTimeAll(STRENGTHEN_LIMIT_TIME_HOUR).setCanFreeze(role.isCanFreeze())
				.setUnFreezeGold(caclUnFreezeGold(role.getClearFreezeNum() + 1)).build();
		return info;
	}

	private int caclUnFreezeGold(int num) {
		Integer gold = clearFreezeMap.get(num);
		if (gold == null) {
			gold = clearFreezeMap.get(clearFreezeMap.size() - 1);
		}
		return gold;
	}

	private long calcRestFreezeTime(int roleId) {
		long now = System.currentTimeMillis();
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		long freezeExpire = role.getFreezeExpire();
		long restTime = 0;
		if (now > freezeExpire) {
			restTime = 0;
			if (!role.isCanFreeze()) {
				role.setCanFreeze(true);
				GameContext.getUserApp().saveRoleAccount(role);
			}
		} else {
			restTime = freezeExpire - now;
		}

		return restTime;
	}

	public static void main(String[] args) {
		long now = 1487761000l;
		long last = 1469761000l;
		int freezeHour = 9;
		long restTime = freezeHour * DateUtil.HOUR - (now - last);
		System.out.println(restTime / DateUtil.SECOND);
	}

	private List<PartNewInfo> builderPartNews(RoleTank roleTank) {
		List<PartNewInfo> list = new ArrayList<>();
		Map<PART_INDEX, PartInfo> parts = roleTank.getPartNewMap();

		for (PartInfo p : parts.values()) {
			TankPartNewTemplate partTemplate = partNewTemplateMap.get(p.getPartId());
			PartNewInfo info = PartNewInfo.newBuilder().setIndex(p.getIndex()).setId(p.getPartId()).setActive(p.isActive()).setLevel(partTemplate.getLevel())
					.setStrengthenInfo(builderStrengthenInfo(p)).setName(partTemplate.getName()).setDesc(partTemplate.getDesc()).build();
			list.add(info);
		}
		return list;
	}

	private PartNewInfo.Builder builderPartNew(RoleTank roleTank, int tankId, PART_INDEX index) {
		Map<PART_INDEX, PartInfo> parts = roleTank.getPartNewMap();
		PartInfo p = parts.get(index);
		TankPartNewTemplate partTemplate = partNewTemplateMap.get(p.getPartId());
		if (partTemplate == null) {
			logger.error("数据错误，partId  = {} 在partNew表里找不到  ", p.getPartId());
			return null;
		}

		PartNewInfo.Builder info = PartNewInfo.newBuilder().setIndex(p.getIndex()).setId(p.getPartId()).setActive(p.isActive()).setLevel(partTemplate.getLevel())
				.setStrengthenInfo(builderStrengthenInfo(p)).setName(partTemplate.getName()).setDesc(partTemplate.getDesc());

		return info;
	}

	private Strengthen_Info.Builder builderStrengthenInfo(PartInfo p) {
		TankPartNewTemplate partTemplate = partNewTemplateMap.get(p.getPartId());
		if (partTemplate == null) {
			logger.error("数据错误，partId  = {} 在partNew表里找不到  ", p.getPartId());
			return null;
		}
		StrengthenTemplate s = strengthTemplateMap.get(partTemplate.getLevel());
		Strengthen_Info.Builder info = Strengthen_Info.newBuilder().setGold(s.getGold()).setIron(s.getIron()).setStrengthenSuccessRate(s.getStrengthenSuccessRate());
		return info;
	}

	@Override
	public void tankPush(RoleTank tank) {
		TankItem item = this.buildTankItem(tank);
		if (item == null)
			return;

		STC_TANK_UPDATE_PUSH_MSG.Builder builder = STC_TANK_UPDATE_PUSH_MSG.newBuilder();
		builder.setTank(item);

		RoleConnect connect = GameContext.getUserApp().getRoleConnect(tank.getRoleId());
		if (connect != null)
			connect.sendMsg(builder.build());
	}

	@Override
	public ValueResult<Map<Integer, Integer>> translateTankExp(int roleId, List<Integer> tankIds, int translateExp, int type) {
		ValueResult<Map<Integer, Integer>> result = new ValueResult<>();

		UserInitTemplate initTemplate = GameContext.getUserApp().getUserInitTemplate();
		List<RoleTank> tankList = new ArrayList<>();

		int totalTankExp = 0;
		for (Integer tankId : tankIds) {
			RoleTank tank = getRoleTank(roleId, tankId);
			if (tank == null) {
				result.failure("坦克不存在");
				return result;
			}

			// if (!hasTankPartMaxLevel(tank)) {
			// result.failure("坦克配件未达到最大级");
			// return result;
			// }

			int tankExp = tank.getExp();
			if (tankExp <= 0) {
				result.failure("坦克经验不足");
				return result;
			}

			totalTankExp += tankExp;

			tankList.add(tank);
		}

		if (totalTankExp < translateExp) {
			result.failure("坦克经验不足");
			return result;
		}

		int money = 0;
		RoleAttr attr = null;
		if (type == 1) {
			double rate = initTemplate.getToTankExpGoldRate();
			money = (int) Math.ceil(translateExp * rate);
			attr = RoleAttr.gold;
		} else if (type == 2) {
			double rate = initTemplate.getToTankExpIronRate();
			money = (int) Math.ceil(translateExp * rate);
			attr = RoleAttr.iron;
		}

		boolean t = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(attr, Operation.decrease, money), OutputType.expToTankExpDec);
		if (!t) {
			result.failure("货币不足");
			return result;
		}

		int translateExpTemp = translateExp;
		Map<Integer, Integer> resultMap = new HashMap<>();
		for (RoleTank tank : tankList) {
			if (translateExpTemp > 0) {
				int tankExp = tank.getExp();
				if (tankExp >= translateExpTemp) {
					tank.setExp(tankExp - translateExpTemp);
					translateExpTemp = 0;
				} else {
					tank.setExp(0);
					translateExpTemp -= tankExp;
				}
				saveRoleTank(tank);
			}

			resultMap.put(tank.getTankId(), tank.getExp());
		}

		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.tankExp, Operation.add, translateExp), OutputType.expToTankExpInc);

		result.setValue(resultMap);
		result.success();
		return result;
	}

	@Override
	public TankBuyResult tankGoldBuy(int roleId, int tankId, int moneyType) {
		TankBuyResult result = new TankBuyResult();
		GoldTankTemplate template = goldTanks.get(tankId);
		if (template == null) {
			result.failure("坦克不存在");
			return result;
		}

		TankTemplate tankTemplate = getTankTemplate(tankId);
		if (tankTemplate == null) {
			result.failure("坦克不存在");
			return result;
		}

		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		Collection<RoleTank> allTankList = GameContext.getTankApp().getAllRoleTank(roleId);
		if (role.getPark() < allTankList.size() + 1) {
			result.failure("车位不足，请先购买车位");
			return result;
		}

		RoleTank tank = getRoleTank(roleId, tankId);
		if (tank != null) {
			result.failure("坦克已存在");
			return result;
		}

		boolean t = false;
		if (moneyType == RoleAttr.gold_VALUE) {
			t = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, template.getGold()), OutputType.buyGoldTankDec.type(),
					StringUtil.buildLogOrigin("金币坦克购买", "购买id： " + tankId));
		} else if (moneyType == RoleAttr.diamonds_VALUE) {
			t = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.diamonds, Operation.decrease, template.getDiamond()), OutputType.buyGoldTankDec.type(),
					StringUtil.buildLogOrigin("金币坦克购买", "购买id： " + tankId));
		} else {
			result.failure("货币类型不正确");
			return result;
		}

		if (!t) {
			result.failure("货币不足");
			return result;
		}

		RoleTank newTank = this.tankAdd(roleId, tankId, "金币坦克购买");
		GameContext.getQuestTriggerApp().tankBuy(roleId, tankId);

		result.setResult(Result.SUCCESS);
		result.setRoleTank(newTank);

		return result;
	}

	@Override
	public Collection<GoldTankTemplate> getGoldTankTemplates() {
		return goldTanks.values();
	}

	@Override
	public void login(int roleId, boolean nextDay) {
		if (!nextDay) {
			return;
		}
		for (RoleTank roleTank : this.getAllRoleTank(roleId)) {
			roleTank.setTired(0);
			this.roleTankDao.update(roleTank);
		}
	}

	@Override
	public void runBroadcastHeartbeatThread() {
		this.startTime = new Long(System.currentTimeMillis() / 1000).intValue();

		LogCore.startup.debug("startTime:" + startTime);
		this.localIpAddress = NetUtil.getRealIp();
		if (Util.isEmpty(localIpAddress)) {
			LogCore.startup.error("无法获取本地IP地址,服务器将停止...");
			System.exit(1);
		}
		LogCore.startup.info("本地外网IP: " + localIpAddress);

		Thread task = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(BROADCASET_LINK_COUNT_INTERVAL_TIME);
						broadcastHeartbeat();
					} catch (Exception e) {
						LogCore.runtime.error("", e);
					}
				}
			}
		});
		task.start();
	}

	@Override
	public void broadcastHeartbeat() {
		GTG_ONLINE_COUNT_MSG onlineMsg = GTG_ONLINE_COUNT_MSG.newBuilder().setIp(localIpAddress).setPort(localPort).setCount(-1).setProcessType(ProcessType.tank).setStartTime(startTime)
				.setVersionCode(versionCode).build();

		String remoteName = GameContext.getGmManagerApp().loopRemoteNodeName();
		RemoteNode remoteNode = GameContext.getGmManagerApp().getRemoteNode(remoteName);
		if (remoteNode != null) {
			Message message = new Message();
			message.setCmdType((byte) SERV_MSG.CMD_TYPE.CMD_TYPE_SERV_VALUE);
			message.setCmdId((byte) SERV_MSG.CMD_ID.GTG_ONLINE_COUNT_VALUE);
			message.setFromNode(GameContext.getLocalNodeName());
			message.setData(onlineMsg.toByteArray());
			remoteNode.sendReqMsg(message);
		}
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public void hotLoadTankTemplate() {
		loadTankTemplate();
		this.loadTankAiTemplate();
		this.loadTankAiTestTemplate();
		this.loadTankAiConfigTemplate();
		this.loadGoldTankTemplate();
		this.loadTankCoefficientTemplate();
	}

	@Override
	public AbstractInstance createTankInstanceByPreseasonId(int preseasonId, int instanceId) {

		if (preseasonId == 1) {
			return new NewPlayer1TankInstance(instanceId);
		} else if (preseasonId == 2) {
			return new NewPlayer2TankInstance(instanceId);
		} else if (preseasonId == 3) {
			return new NewPlayer3TankInstance(instanceId);
		} else if (preseasonId == 4) {
			return new NewPlayer4TankInstance(instanceId);
		} else if (preseasonId == 5) {
			return new NewPlayer5TankInstance(instanceId);
		} else {
			return new TankInstance(instanceId);
		}

	}

	private void addFreeze(RoleAccount role) {
		long now = System.currentTimeMillis();
		if (role.getFreezeExpire() <= 0) {
			role.setFreezeExpire(now + 1 * DateUtil.HOUR);
		} else {
			role.setFreezeExpire(role.getFreezeExpire() + 1 * DateUtil.HOUR);// 加一个小时冷却时间
		}

		if (role.getFreezeExpire() - now > STRENGTHEN_LIMIT_TIME_HOUR * DateUtil.HOUR) {
			role.setCanFreeze(false);
		}
		GameContext.getUserApp().saveRoleAccount(role);
	}

	/**
	 * @param builder
	 * @param roleId
	 *            角色 ID
	 * @param tankTemplateId
	 *            坦克模板ID
	 * @param index
	 *            配件位置
	 * @param type
	 *            强化类型 1银币强化2金币保成强化，3一键金币保成无CD强化
	 * @param upLevel
	 *            3type下的 要强化到的等级
	 * @return
	 */
	@Override
	public Result strengthen(STC_STRENGTHEN_MSG.Builder builder, int roleId, int tankTemplateId, PART_INDEX index, int type, int upLevel) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		// 判断是否拥有此坦克
		RoleTank roleTank = this.getRoleTank(roleId, tankTemplateId);
		if (roleTank == null) {
			Result result = Result.newFailure(Tips.TANK_NO_HAD);
			return result;
		}
		Map<PART_INDEX, PartInfo> partMap = roleTank.getPartNewMap();
		PartInfo partInfo = partMap.get(index);
		TankPartNewTemplate template = partNewTemplateMap.get(partInfo.getPartId());
		if (template == null) {
			Result result = Result.newFailure("参数错误");
			return result;
		}
		// 判断是否为最高等级
		final int newPartId = template.getNextId();
		if (newPartId <= 0) {
			Result result = Result.newFailure(Tips.PART_MAX_LVL);
			return result;
		}
		int partLevel = template.getLevel();

		StrengthenTemplate strengthenTemplate = strengthTemplateMap.get(partLevel);
		int success = 0;
		// 银币强化
		if (type == 1) {
			if (!role.isCanFreeze()) {
				Result result = Result.newFailure("当前正在CD中");
				return result;
			}
			int iron = strengthenTemplate.getIron();
			boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.decrease, iron), OutputType.IronStrengthen.type(),
					StringUtil.buildLogOrigin(String.valueOf(partLevel), OutputType.IronStrengthen.getInfo()));
			if (!suc) {
				Result result = Result.newFailure(Tips.NEED_IRON);
				return result;
			}
			int rate = strengthenTemplate.getStrengthenSuccessRate();
			if (rate >= RandomUtil.randomInt(100) + 1) {
				success = 1;
				partInfo.setPartId(template.getNextId());
			}
			// 加冷却
			addFreeze(role);
			// 任务
			GameContext.getQuestTriggerApp().tankUpPart(roleId);
			roleTankDao.update(roleTank);

		} else if (type == 2) {
			if (!role.isCanFreeze()) {
				Result result = Result.newFailure("当前正在CD中");
				return result;
			}
			// 金币强化
			int gold = strengthenTemplate.getGold();
			boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, gold), OutputType.GoldStrengthen.type(),
					StringUtil.buildLogOrigin(String.valueOf(partLevel), OutputType.GoldStrengthen.getInfo()));
			if (!suc) {
				Result result = Result.newFailure(Tips.NEED_GOLD);
				return result;
			}
			success = 1;
			partInfo.setPartId(template.getNextId());

			// 加冷却
			addFreeze(role);
			// 任务
			GameContext.getQuestTriggerApp().tankUpPart(roleId);
			roleTankDao.update(roleTank);
		} else {
			// 一键强化
			if (upLevel <= partLevel || upLevel > LEVEL_MAX) {
				Result result = Result.newFailure("输入错误");
				return result;
			}
			int totalGold = 0;
			int upTimes = 0;
			for (int i = partLevel; i < upLevel + 1; i++) {
				upTimes++;
				totalGold += strengthTemplateMap.get(i).getGold();
			}
			boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, totalGold), OutputType.OneKeyStrengthen.type(),
					StringUtil.buildLogOrigin(String.valueOf(partLevel), OutputType.OneKeyStrengthen.getInfo()));
			if (!suc) {
				Result result = Result.newFailure(Tips.NEED_GOLD);
				return result;
			}
			String key = index.getNumber() + "_" + upLevel;
			partInfo.setPartId(partNewLevelMap.get(key).getId_i());
			success = 1;
			for (int i = 0; i < upTimes; i++) {
				// 任务
				GameContext.getQuestTriggerApp().tankUpPart(roleId);
			}
			roleTankDao.update(roleTank);
		}

		builder.setTankId(tankTemplateId);
		builder.setPart(builderPartNew(roleTank, tankTemplateId, index));
		builder.setFreezeInfo(buildFreezeInfo(roleId));
		builder.setSuccess(success);
		return Result.newSuccess();
	}

	@Override
	public void openGrooveShow(STC_OPEN_GROOVE_SHOW_MSG.Builder builder, int roleId, int tankId) {
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		if (template == null) {
			return;
		}
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (roleTank == null) {
			return;
		}
		int tankLevel = template.getLevel_i();
		GrooveTemplate grooveTemplate = grooveTemplateMap.get(tankLevel);
		if (grooveTemplate == null) {
			return;
		}
		int drawGold = grooveTemplate.getGold();
		builder.setDrawGold(drawGold);
		int buyGold = grooveTemplate.getBuyGold();
		builder.setBuyGold(buyGold);
	}

	@Override
	public Result openGroove(STC_OPEN_GROOVE_MSG.Builder builder, int type, int tankId, int roleId) {
		TankTemplate template = GameContext.getTankApp().getTankTemplate(tankId);
		builder.setType(type);
		if (template == null) {
			return Result.newFailure("信息错误");
		}
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (roleTank == null) {
			return Result.newFailure("没有该坦克");
		}
		int tankLevel = template.getLevel_i();
		GrooveTemplate grooveTemplate = grooveTemplateMap.get(tankLevel);
		if (grooveTemplate == null) {
			return Result.newFailure("信息错误");
		}
		if (roleTank.getGrooveNum() >= GROOVE_LIMIT) {
			return Result.newFailure("槽位已满");
		}
		int drawGold = grooveTemplate.getGold();
		int buyGold = grooveTemplate.getBuyGold();
		RewardItem.Builder itemBuilder = RewardItem.newBuilder();
		// 抽奖购买
		if (type == 1) {
			boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, drawGold), OutputType.OpenGrooveDraw);
			if (!suc) {
				return Result.newFailure(Tips.NEED_GOLD);
			}
			// 开始抽奖
			PartDrawPeshe peshe = RandomUtil.getPeshe(partDrawPeshe);
			if (peshe != null) {
				if (peshe.getType() == 1) {
					// 物品
					GameContext.getGoodsApp().addGoods(roleId, peshe.getItemId(), peshe.getNum(), "开槽抽奖");
					itemBuilder.setType(RewardType.GOODS);
					itemBuilder.setId(peshe.getItemId());
					itemBuilder.setCount(peshe.getNum());
					builder.setItem(itemBuilder);
				} else if (peshe.getType() == 2) {
					// 银币
					GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, peshe.getNum()), OutputType.OpenGrooveDraw);
					itemBuilder.setType(RewardType.IRON);
					itemBuilder.setId(0);
					itemBuilder.setCount(peshe.getNum());
					builder.setItem(itemBuilder);
				} else {
					// 槽位
					roleTank.setGrooveNum(roleTank.getGrooveNum() + 1);
				}

			} else {
				logger.error("开槽抽奖  peshe为空");
			}

		} else {
			// 直接购买
			boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, buyGold), OutputType.OpenGrooveBuy);
			if (!suc) {
				return Result.newFailure(Tips.NEED_GOLD);
			}
			roleTank.setGrooveNum(roleTank.getGrooveNum() + 1);
			roleTankDao.update(roleTank);
		}
		builder.setGrooveNum(roleTank.getGrooveNum());
		builder.setPartOnNum(calcPartOnNum(roleTank));
		return Result.newSuccess();
	}

	@Override
	public Result clearFreeze(STC_CLEAR_FREEZE_MSG.Builder builder, int roleId) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		int hasClearNum = role.getClearFreezeNum();

		Integer gold = caclUnFreezeGold(hasClearNum + 1);
		boolean suc = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, gold), OutputType.ClearFreezePart);
		if (!suc) {
			return Result.newFailure(Tips.NEED_GOLD);
		}
		role.setClearFreezeNum(role.getClearFreezeNum() + 1);
		role.setFreezeExpire(System.currentTimeMillis());
		role.setCanFreeze(true);
		GameContext.getUserApp().saveRoleAccount(role);
		Freeze_Info info = buildFreezeInfo(roleId);
		builder.setFreezeInfo(info);
		return Result.newSuccess();
	}

	@Override
	public void getOneKeyAllGold(int roleId, STC_ONE_KEY_STRENGTHEN_MSG.Builder builder, int upLevel, int tankId, PART_INDEX partIndex) {
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (roleTank == null) {
			return;
		}
		Map<PART_INDEX, PartInfo> partMap = roleTank.getPartNewMap();
		PartInfo partInfo = partMap.get(partIndex);
		TankPartNewTemplate partTemplate = partNewTemplateMap.get(partInfo.getPartId());
		int partLevel = partTemplate.getLevel();
		builder.setGold(0);
		if (upLevel <= partLevel || upLevel > LEVEL_MAX) {
			return;
		}
		int totalGold = 0;
		for (int i = partLevel; i < upLevel + 1; i++) {
			totalGold += strengthTemplateMap.get(i).getGold();
		}
		builder.setGold(totalGold);
	}

	/**
	 * @param builder
	 * @param tankId
	 * @param partIndex
	 *            配件位置
	 * @param roleId
	 * @param type
	 *            1装 2 卸
	 * @return
	 */
	@Override
	public Result upDownPart(STC_UP_DOWN_MSG.Builder builder, int tankId, PART_INDEX partIndex, int roleId, int type) {
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (roleTank == null) {
			return Result.newFailure("没有此坦克");
		}
		Map<PART_INDEX, PartInfo> partMap = roleTank.getPartNewMap();
		PartInfo partInfo = partMap.get(partIndex);
		if (type == 1) {
			if (partInfo.isActive()) {
				return Result.newFailure("已经装配该配件，无需重复装配");
			}
			int hasOn = calcPartOnNum(roleTank);
			int grooveNum = roleTank.getGrooveNum();
			if (hasOn + 1 > grooveNum) {
				return Result.newFailure("槽位已满");
			}
			partInfo.setActive(true);
		} else {
			if (!partInfo.isActive()) {
				return Result.newFailure("配件未装配");
			}
			partInfo.setActive(false);
		}
		roleTankDao.update(roleTank);
		builder.setTankId(tankId);
		builder.setPart(builderPartNew(roleTank, tankId, partIndex));
		builder.setGrooveNum(roleTank.getGrooveNum());
		builder.setPartOnNum(calcPartOnNum(roleTank));
		return Result.newSuccess();
	}

	@Override
	public void refreshFreezeNum(int roleId, boolean isNextDay) {
		if (isNextDay) {
			RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
			role.setClearFreezeNum(0);
			GameContext.getUserApp().saveRoleAccount(role);
		}
	}

	@Override
	public TankCoefficientTemplate getTankCoefficientTemplate() {
		return tankCoefficientTemplate;
	}

	public void setTankCoefficientTemplate(TankCoefficientTemplate tankCoefficientTemplate) {
		this.tankCoefficientTemplate = tankCoefficientTemplate;
	}

	@Override
	public Result buyPark(Builder builder, int roleId) {
		RoleAccount role = GameContext.getUserApp().getRoleAccount(roleId);
		boolean canBuy = GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, SysConfig.get(18)), OutputType.buyPark);
		if (!canBuy) {
			LogCore.runtime.debug("货币不足");
			return Result.newFailure("货币不足");
		}
		role.setPark(role.getPark() + 1);
		builder.setPark(role.getPark());
		GameContext.getUserApp().saveRoleAccount(role);
		return Result.newSuccess();
	}

	/**
	 * 战斗召唤
	 *
	 * @param roleId
	 * @param tankId
	 */
	public void calcCallUp(int roleId, int tankId) {
		RoleTank roleTank = this.getRoleTank(roleId, tankId);
		if (System.currentTimeMillis() - roleTank.getLastCallUpTime() > GameContext.getMapApp().getExtraIncomeTemplate().getCallUpTime() * DateUtil.HOUR) {
			if (!roleTank.isCallUp()) {
				roleTank.setCallUp(true);
			}
			// TODO 此处刷新战斗召唤开启状态
		} else {
			if (roleTank.isCallUp()) {
				roleTank.setCallUp(false);
			}
			// TODO 此处刷新战斗召唤关闭状态
		}
	}

	/**
	 * 请求研发树
	 *
	 * @param roleId
	 * @param builder
	 */
	public void getTankTree(int roleId, STC_TANK_DEVELOP_LIST_MSG.Builder builder) {
		TankDevelop tankDevelop = tankDevelopDao.getDevelop(roleId);

		Map<Integer, Integer> map = tankDevelop.getDevelopMap();
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			TANK_DEVELOP_INFO.Builder infoBuilder = TANK_DEVELOP_INFO.newBuilder();
			infoBuilder.setTankTemplateId(entry.getKey());
			infoBuilder.setState(entry.getValue());
			builder.addInfos(infoBuilder);
		}
		RoleConnect connect = GameContext.getUserApp().getRoleConnect(roleId);
		if (connect != null) {
			connect.sendMsg(builder.build());
		}
	}

	/**
	 * 研发
	 *
	 * @param roleId
	 * @param tankTemplateId
	 *            要研发的坦克
	 * @param builder
	 * @return
	 */
	public Result developTank(int roleId, int tankTemplateId, STC_TANK_DEVELOP_MSG.Builder builder) {
		TankDevelop tankDevelop = tankDevelopDao.getDevelop(roleId);

		Map<Integer, Integer> map = tankDevelop.getDevelopMap();
		if (map.containsKey(tankTemplateId)) {
			return Result.newFailure("您已研发过该坦克！");
		}
		TankTemplate tankTemplate = getTankTemplate(tankTemplateId);
		if (tankTemplate == null) {
			return Result.newFailure("不存在该坦克！");
		}
		if (tankTemplate.getTank_type() != 0) {
			return Result.newFailure("坦克错误！");
		}
		int needTankId = tankTemplate.getNeedTankId_i();
		if (!map.containsKey(needTankId)) {
			return Result.newFailure("前置坦克未解锁！");
		}
		TankTemplate needTankTemplate = getTankTemplate(needTankId);
		if (needTankTemplate == null) {
			return Result.newFailure("前置坦克发生错误！");
		}

		final RoleTank roleTank = this.getRoleTank(roleId, needTankId);
		int needExp = tankTemplate.getNeedExp();
		boolean success = false;
		if (roleTank == null) {
			// 代表前置车研发了没购买，使用全局经验
			int globalTankExp = GameContext.getUserAttrApp().get(roleId, RoleAttr.tankExp);
			if (needExp > globalTankExp) {
				return Result.newFailure("坦克经验不够");
			} else {
				int globalNeed = globalTankExp - needExp;
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.tankExp, Operation.decrease, globalNeed), OutputType.tankDevelop.type(),
						StringUtil.buildLogOrigin(tankTemplate.getName_s(), OutputType.tankDevelop.getInfo()));
				success = true;
			}

		} else {
			// 先扣单车经验，不足再使用全局经验
			int tankExp = roleTank.getExp();
			int globalNeed = 0; // 全局需要减的
			if (needExp > tankExp) {
				globalNeed = needExp - tankExp;
				int globalTankExp = GameContext.getUserAttrApp().get(roleId, RoleAttr.tankExp);
				if (globalNeed > globalTankExp) {
					return Result.newFailure("坦克经验不够");
				}
			}

			if (globalNeed == 0) {
				roleTank.setExp(tankExp - needExp);
			} else {
				roleTank.setExp(0);
				GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.tankExp, Operation.decrease, globalNeed), OutputType.tankDevelop.type(),
						StringUtil.buildLogOrigin(tankTemplate.getName_s(), OutputType.tankDevelop.getInfo()));
			}
			saveRoleTank(roleTank);
			success = true;

		}
		if (success) {
			// 设置为已研发可购买
			map.put(tankTemplateId, 2);
			tankDevelopDao.save(tankDevelop);
			TANK_DEVELOP_INFO.Builder infoBuilder = TANK_DEVELOP_INFO.newBuilder();
			infoBuilder.setTankTemplateId(tankTemplateId);
			infoBuilder.setState(2);
			builder.setInfo(infoBuilder);
			return Result.newSuccess();
		}
		return Result.newFailure("");
	}

	public Map<Integer, TankPartNewTemplate> getPartNewTemplateMap() {
		return partNewTemplateMap;
	}

	public void setPartNewTemplateMap(Map<Integer, TankPartNewTemplate> partNewTemplateMap) {
		this.partNewTemplateMap = partNewTemplateMap;
	}

	public Map<Integer, GrooveTemplate> getGrooveTemplateMap() {
		return grooveTemplateMap;
	}

	public void setGrooveTemplateMap(Map<Integer, GrooveTemplate> grooveTemplateMap) {
		this.grooveTemplateMap = grooveTemplateMap;
	}

	public List<PartDrawPeshe> getPartDrawPeshe() {
		return partDrawPeshe;
	}

	public void setPartDrawPeshe(List<PartDrawPeshe> partDrawPeshe) {
		this.partDrawPeshe = partDrawPeshe;
	}

	public Map<Integer, Integer> getClearFreezeMap() {
		return clearFreezeMap;
	}

	public void setClearFreezeMap(Map<Integer, Integer> clearFreezeMap) {
		this.clearFreezeMap = clearFreezeMap;
	}

	public Map<Integer, StrengthenTemplate> getStrengthTemplateMap() {
		return strengthTemplateMap;
	}

	public void setStrengthTemplateMap(Map<Integer, StrengthenTemplate> strengthTemplateMap) {
		this.strengthTemplateMap = strengthTemplateMap;
	}

	public TankDevelopDao getTankDevelopDao() {
		return tankDevelopDao;
	}

	public void setTankDevelopDao(TankDevelopDao tankDevelopDao) {
		this.tankDevelopDao = tankDevelopDao;
	}

	@Override
	public AiConfig getAiConfig() {
		return aiConfig;
	}

	class XNameSupplier {

		List<String> get(int maximumChars, int remainCount) {
			if (maximumChars <= 0 || remainCount <= 0) {
				return new ArrayList<>();
			}
			// 获得一个名称
			int key = RandomUtil.randomInt() % maximumChars + 1;
			List<String> lib = xRobotNameList.get(key);

			int idx = RandomUtil.randomInt() % lib.size();
			String name = lib.get(idx);

			List<String> res = get(maximumChars - key, remainCount--);
			res.add(name);

			return res;
		}

		String join(List<String> list, String conjunction) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String item : list) {
				if (first)
					first = false;
				else
					sb.append(conjunction);
				sb.append(item);
			}
			return sb.toString();
		}
	}

	@Override
	public int calcShowBattleScore(AbstractInstance tank) {

		if (!tank.isRobot()) {
			return 0;
		}

		int templateId = tank.getTemplateId();
		int showTemplate = getTankApp().getTankTemplate(templateId).getShowBattleNum();

		int showParts = 0;
		for (Integer part : tank.getAiParts().values()) {
			showParts += partNewTemplateMap.get(part).getBattleNum();
		}

		return showParts + showTemplate;
	}
}
