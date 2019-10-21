package com.ourpalm.tank.app.goods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Cat;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.PackageDao;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_EFFECT_MSG;
import com.ourpalm.tank.message.BATTLE_MSG.STC_USE_WAR_GOODS_MSG;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.message.PACKAGE_MSG.GoodsItem;
import com.ourpalm.tank.message.PACKAGE_MSG.STC_GOODS_CHANGE_MSG;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.GoodsBoxGoldGroup;
import com.ourpalm.tank.template.GoodsBoxGoodsGroup;
import com.ourpalm.tank.template.GoodsBoxIronGroup;
import com.ourpalm.tank.template.GoodsBoxTankGroup;
import com.ourpalm.tank.template.GoodsBoxTemplate;
import com.ourpalm.tank.template.GoodsChangeTemplate;
import com.ourpalm.tank.template.GoodsShellTemplate;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.MedalTemplate;
import com.ourpalm.tank.template.TankPartMaterialTemplate;
import com.ourpalm.tank.tip.Tips;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.GoodsPeshe;
import com.ourpalm.tank.util.peshe.IronPheshe;
import com.ourpalm.tank.util.peshe.MoneyPeshe;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.MapInstance;
import com.ourpalm.tank.vo.result.GoodsBoxOpenResult;
import com.ourpalm.tank.vo.result.Result;

public class GoodsAppImpl implements GoodsApp {
	private static final Logger logger = LogCore.runtime;
	private static final List<Integer> specialList = Arrays.asList(GOODS_TYPE.rename_VALUE, GOODS_TYPE.vip_time_VALUE,
			GOODS_TYPE.broadcast_VALUE, GOODS_TYPE.corps_gold_VALUE, GOODS_TYPE.battle_item_VALUE, GOODS_TYPE.small_shell_VALUE, 
			GOODS_TYPE.middle_shell_VALUE, GOODS_TYPE.heavy_shell_VALUE, GOODS_TYPE.back_shell_VALUE);
	
	private PackageDao packageDao;
	//需要更改的物品
	private List<GoodsChangeTemplate> goodsChangeList ;
	// 所有物品的基础信息
	private Map<Integer, GoodsBaseTemplate> goodsBaseMap = new HashMap<>();
	// 特殊类型物品<物品类型, 物品id>
	// 同一个类型只有一种物品
	private Map<Integer, Integer> specialGoodsMap = new HashMap<>();
	
	// 宝箱配置信息（key为物品ID）
	private Map<Integer, GoodsBoxTemplate> goodsBoxMap = new HashMap<>();
	private Map<Integer, List<GoodsPeshe>> goodsGroup = new HashMap<>();
	private Map<Integer, List<MoneyPeshe>> goldGroup = new HashMap<>();
	private Map<Integer, List<IronPheshe>> ironGroup = new HashMap<>();
	private Map<Integer, List<GoodsPeshe>> tankGroup = new HashMap<>();
	
	//战场道具(key为物品id)
	private Map<Integer, GoodsShellTemplate> goodsShellMap = new HashMap<>();	// 炮弹配置信息
	private Map<Integer, GoodsWarTemplate> goodsWarMap = new HashMap<>();  		// 战场道具信息
	private Map<Integer, TankPartMaterialTemplate> materialMap = new HashMap<>(); //配件材料
	
	private Map<Integer, MedalTemplate> medals = new HashMap<>();	//勋章
	
	@Override
	public void start() {
		this.loadGoods();
		this.loadBoxGoods();
		this.loadBoxGoodsGroup();
		this.loadBoxGoldGroup();
		this.loadGoodsShell();
		this.loadGoodsWar();
		this.loadMaterial();
		this.loadMedal();
		this.loadBoxIronGroup();
		this.loadBoxTankGroup();
		this.loadGoodsChange();
	}

	@Override
	public void stop() {
		this.goodsBaseMap.clear();
		this.goodsBoxMap.clear();
	}

	private void loadGoods() {
		String fileName = XlsSheetType.goodsBaseTemplate.getXlsFileName();
		String sheetName = XlsSheetType.goodsBaseTemplate.getSheetName();
		try {
			this.goodsBaseMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, GoodsBaseTemplate.class);
			for(GoodsBaseTemplate template : goodsBaseMap.values()){
				if (specialList.contains(template.getType_i())) {
					specialGoodsMap.put(template.getType_i(), template.getId_i());
				}
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadBoxGoods() {
		String fileName = XlsSheetType.goodsBoxTemplate.getXlsFileName();
		String sheetName = XlsSheetType.goodsBoxTemplate.getSheetName();
		try {
			this.goodsBoxMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, GoodsBoxTemplate.class);
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	private void loadBoxTankGroup() {
		String fileName = XlsSheetType.goodsBoxTankGroup.getXlsFileName();
		String sheetName = XlsSheetType.goodsBoxTankGroup.getSheetName();
		try {
			List<GoodsBoxTankGroup> list = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsBoxTankGroup.class);
			for(GoodsBoxTankGroup group : list){
				List<GoodsPeshe> pesheList = tankGroup.get(group.getGroup());
				if (pesheList == null) {
					pesheList = new ArrayList<>();
					tankGroup.put(group.getGroup(), pesheList);
				}
				pesheList.add(group.build());
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
		
	}

	private void loadBoxGoodsGroup() {
		String fileName = XlsSheetType.goodsBoxGoodsGroup.getXlsFileName();
		String sheetName = XlsSheetType.goodsBoxGoodsGroup.getSheetName();
		try {
			List<GoodsBoxGoodsGroup> list = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsBoxGoodsGroup.class);
			for(GoodsBoxGoodsGroup group : list){
				List<GoodsPeshe> pesheList = goodsGroup.get(group.getGroup());
				if (pesheList == null) {
					pesheList = new ArrayList<>();
					goodsGroup.put(group.getGroup(), pesheList);
				}
				pesheList.add(group.build());
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadBoxGoldGroup() {
		String fileName = XlsSheetType.goodsBoxGoldGroup.getXlsFileName();
		String sheetName = XlsSheetType.goodsBoxGoldGroup.getSheetName();
		try {
			List<GoodsBoxGoldGroup> list = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsBoxGoldGroup.class);
			for(GoodsBoxGoldGroup group : list){
				List<MoneyPeshe> pesheList = goldGroup.get(group.getGroup());
				if (pesheList == null) {
					pesheList = new ArrayList<>();
					goldGroup.put(group.getGroup(), pesheList);
				}
				pesheList.add(group.build());
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	private void loadBoxIronGroup() {
		String fileName = XlsSheetType.goodsBoxIronGroup.getXlsFileName();
		String sheetName = XlsSheetType.goodsBoxIronGroup.getSheetName();
		try {
			List<GoodsBoxIronGroup> list = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsBoxIronGroup.class);
			for(GoodsBoxIronGroup group : list){
				List<IronPheshe> pesheList = ironGroup.get(group.getGroup());
				if (pesheList == null) {
					pesheList = new ArrayList<>();
					ironGroup.put(group.getGroup(), pesheList);
				}
				pesheList.add(group.build());
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	

	
	private void loadGoodsShell(){
		String fileName = XlsSheetType.goodsShellTemplate.getXlsFileName();
		String sheetName = XlsSheetType.goodsShellTemplate.getSheetName();
		try {
			List<GoodsShellTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsShellTemplate.class);
			for(GoodsShellTemplate template : list){
				int id = template.getId();
				if( !this.goodsBaseMap.containsKey(id) ){
					LogCore.startup.error("炮弹 id={}在基本物品信息中不存在...", id);
					continue;
				}
				this.goodsShellMap.put(id, template);
			}
		} catch(Exception e){
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	
	private void loadGoodsWar(){
		String fileName = XlsSheetType.goodsWarTemplate.getXlsFileName();
		String sheetName = XlsSheetType.goodsWarTemplate.getSheetName();
		try {
			List<GoodsWarTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsWarTemplate.class);
			for(GoodsWarTemplate template : list){
				int id = template.getId();
				if( !this.goodsBaseMap.containsKey(id) ){
					LogCore.startup.error("战场道具 id={}在基本物品信息中不存在...", id);
					continue;
				}
				this.goodsWarMap.put(id, template);
			}
		} catch(Exception e){
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	private void loadMaterial(){
		String fileName = XlsSheetType.goodsMaterialTemplate.getXlsFileName();
		String sheetName = XlsSheetType.goodsMaterialTemplate.getSheetName();
		try {
			List<TankPartMaterialTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, TankPartMaterialTemplate.class);
			for(TankPartMaterialTemplate template : list){
				int id = template.getId();
				if( !this.goodsBaseMap.containsKey(id) ){
					LogCore.startup.error("配件材料id={}在基本物品信息中不存在...", id);
					continue;
				}
				this.materialMap.put(id, template);
			}
		} catch(Exception e){
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	private void loadMedal() {
		String fileName = XlsSheetType.medalTemplate.getXlsFileName();
		String sheetName = XlsSheetType.medalTemplate.getSheetName();
		try {
			medals = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MedalTemplate.class);
			for(MedalTemplate template : medals.values()){
				if( !this.goodsBaseMap.containsKey(template.getId()) ){
					LogCore.startup.error("勋章id={}在基本物品信息中不存在...", template.getId());
					continue;
				}
				template.init();
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	private void loadGoodsChange(){
		String fileName = XlsSheetType.goodsChangeTemplate.getXlsFileName();
		String sheetName = XlsSheetType.goodsChangeTemplate.getSheetName();
		try{
			this.goodsChangeList = XlsPojoUtil.sheetToList(fileName, sheetName, GoodsChangeTemplate.class);
		}catch(Exception e){
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	
	@Override
	public Integer getSpecialGoodsId(GOODS_TYPE type){
		return this.specialGoodsMap.get(type.getNumber());
	}
	
	@Override
	public GoodsBaseTemplate getGoodsBaseTemplate(int goodsId) {
		return goodsBaseMap.get(goodsId);
	}

	@Override
	public GoodsBoxTemplate getGoodsBoxTemplate(int goodsId) {
		return goodsBoxMap.get(goodsId);
	}

	@Override
	public TankPartMaterialTemplate getMaterialTemplate(int goodsId) {
		return this.materialMap.get(goodsId);
	}
	
	@Override
	public Map<Integer, Integer> getRoleGoods(Integer roleId) {
		return packageDao.getAll(roleId);
	}
	
	@Override
	public GoodsWarTemplate getGoodsWarTemplate(int goodsId){
		return this.goodsWarMap.get(goodsId);
	}
	
	
	@Override
	public GoodsBoxOpenResult openBox(int roleId, int goodsId, int count){
		GoodsBoxOpenResult result = new GoodsBoxOpenResult();
		GoodsBoxTemplate boxTemplate = GameContext.getGoodsApp().getGoodsBoxTemplate(goodsId);
		GoodsBaseTemplate template = getGoodsBaseTemplate(goodsId);
		if(boxTemplate == null || template == null){
			result.setInfo("该物品不是宝箱");
			return result;
		}
		boolean success = GameContext.getGoodsApp().removeGoods(roleId, goodsId, count, OutputType.goodsOpenBoxDec.getInfo());
		if(!success){
			result.setInfo("物品数量不足");
			return result;
		}
		
		int gold = this.randomGold(roleId, boxTemplate.getGold(), boxTemplate.getRandomGoldGroup(), count);
		int iron = this.randomIron(roleId, boxTemplate.getIron(), boxTemplate.getRandomIronGroup(), count);
		int honor = boxTemplate.getHonor();
		GameContext.getUserAttrApp().changeAttribute(roleId, OutputType.goodsOpenBoxInc.type(), StringUtil.buildLogOrigin(template.getName_s(), OutputType.goodsOpenBoxInc.getInfo()), 
				AttrUnit.build(RoleAttr.gold, Operation.add, gold),
				AttrUnit.build(RoleAttr.iron, Operation.add, iron),
				AttrUnit.build(RoleAttr.honor, Operation.add, honor));
		
		Map<Integer, Integer> goodsMap = this.randomGoods(roleId, boxTemplate, count);
		
		this.addGoods(roleId, goodsMap, StringUtil.buildLogOrigin(template.getName_s(), OutputType.goodsOpenBoxInc.getInfo()));
		
		result.setGold(gold);
		result.setIron(iron);
		result.setHonor(honor);
		result.setGoodsMap(goodsMap);
		result.setResult(Result.SUCCESS);
		return result;
	}

	
	private Map<Integer, Integer> randomGoods(int roleId, GoodsBoxTemplate template, int count){
		List<GoodsPeshe> goodsList = new ArrayList<>();
		//必给物品
		int mustGoodsGroup = template.getGoodsGroup();
		List<GoodsPeshe> musthGoods = this.goodsGroup.get(mustGoodsGroup);
		if(!Util.isEmpty(musthGoods)){
			goodsList.addAll(musthGoods);
		}
		//randomType 1 可重复， 2不可重复
		if(template.getRandomGoodsGroup() > 0 && goodsGroup.containsKey(template.getRandomGoodsGroup())) {
			List<GoodsPeshe> list = goodsGroup.get(template.getRandomGoodsGroup());
			List<GoodsPeshe> randomList = template.getRandomType() == 2 ? new ArrayList<>(list) : list;
			if(template.getRanCount() > 0){
				count *= template.getRanCount();
			}
			while(count-- > 0 && !Util.isEmpty(randomList)){
				GoodsPeshe randomPeshe = RandomUtil.getPeshe(randomList);
				goodsList.add(randomPeshe);
				if(template.getRandomType() == 2){
					randomList.remove(randomPeshe);
				}
			}
		}
		
		Map<Integer, Integer> result = new HashMap<>();
		for(GoodsPeshe peshe : goodsList){
			count = peshe.getNum();
			if (result.containsKey(peshe.getGoodsId())){
				count += result.get(peshe.getGoodsId());
			}
			result.put(peshe.getGoodsId(), count);
		}
		return result;
	}
	
	
	private int randomGold(int roleId, int mustGold, int randomGoldGroupId, int count){
		int gold = mustGold * count;
		if(randomGoldGroupId <= 0 || !goldGroup.containsKey(randomGoldGroupId)){
			return gold;
		}
		for(int i = 0; i < count; i++){
			MoneyPeshe peshe = RandomUtil.getPeshe(goldGroup.get(randomGoldGroupId));
			gold += peshe.getCount();
		}
		return gold;
	}

	
	private int randomIron(int roleId, int mustIron, int randomIronGroup, int count){
		int iron = mustIron * count;
		if(randomIronGroup <= 0 || !ironGroup.containsKey(randomIronGroup)){
			return iron;
		}
		for(int i = 0; i < count; i++){
			IronPheshe peshe =  RandomUtil.getPeshe(ironGroup.get(randomIronGroup));
			iron += peshe.getCount();
		}
		return iron;
	}
	
	
	@Override
	public boolean removeGoods(Integer roleId, Integer goodsId, Integer count, String origin) {
		return this.removeGoods(roleId, Collections.singletonMap(goodsId, count), origin);
	}

	/**
	 * return true成功
	 */
	@Override
	public boolean removeGoods(Integer roleId, Map<Integer, Integer> goodsMap, String origin) {
		if (Util.isEmpty(goodsMap)) {
			return true;
		}
		
		Map<Integer, Integer> deleteMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> updateMap = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : goodsMap.entrySet()) {
			int goodsId = entry.getKey();
			int goodsCount = packageDao.getCount(roleId, goodsId);
			int remain = goodsCount - entry.getValue();
			if (entry.getValue() < 0 || remain < 0) {
				return false;
			}
			if (remain == 0) {
				deleteMap.put(entry.getKey(), remain);
			} else {
				updateMap.put(entry.getKey(), remain);
			}
			//发送日志
			GoodsBaseTemplate template = this.getGoodsBaseTemplate(goodsId);
			if(template != null){
				//GameContext.getGameLogApp().sendItemChangeLog(roleId, Operation.decrease, String.valueOf(goodsId),
				//		template.getName_s(), entry.getValue(), origin);
			}
		}

		packageDao.delete(roleId, deleteMap.keySet());
		packageDao.save(roleId, updateMap);

		updateMap.putAll(deleteMap);
		
		if(logger.isDebugEnabled()){
			logger.debug("goods remove result : {}", JSON.toJSONString(updateMap));
		}
		
		this.syncGoods(roleId, updateMap, origin);
		return true;
	}

	@Override
	public void addGoods(Integer roleId, Integer goodsId, Integer count, String origin) {
		this.addGoods(roleId, Collections.singletonMap(goodsId, count), origin);
	}

	@Override
	public void addGoods(Integer roleId, Map<Integer, Integer> goodsMap, String origin) {
		Map<Integer, Integer> addMap = filterGoods(goodsMap);
		if (Util.isEmpty(addMap)) {
			return;
		}
		Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : addMap.entrySet()) {
			int goodsId = entry.getKey();
			GoodsBaseTemplate template = getGoodsBaseTemplate(goodsId);
			if(template == null){
				continue;
			}
			int count = packageDao.getCount(roleId, goodsId) + entry.getValue();
			resultMap.put(entry.getKey(), count);
			//发送日志
			//GameContext.getGameLogApp().sendItemChangeLog(roleId, Operation.add, String.valueOf(entry.getKey()),
			//		template.getName_s(), entry.getValue(), origin);
		}

		if(logger.isDebugEnabled()){
			logger.debug("goods add result : {}", JSON.toJSONString(resultMap));
		}
		
		packageDao.save(roleId, resultMap);
		this.syncGoods(roleId, resultMap, origin);
	}

	private Map<Integer, Integer> filterGoods(Map<Integer, Integer> goodsMap) {
		if (Util.isEmpty(goodsMap)) {
			return new HashMap<>();
		}
		
		Map<Integer, Integer> result = new HashMap<>();
		for(Map.Entry<Integer, Integer> entry : goodsMap.entrySet()){
			if ( !goodsBaseMap.containsKey(entry.getKey()) ) {
				logger.error("物品不存在 ID = {}", entry.getKey());
				continue;
			}
			if ( entry.getValue() <= 0 ) {
				continue;
			}
			
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

	private void syncGoods(Integer roleId, Map<Integer, Integer> goodsMap, String origin) {
		if(Util.isEmpty(goodsMap)){
			return;
		}
		RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if(connect == null){
			return;
		}
		
		STC_GOODS_CHANGE_MSG.Builder builder = STC_GOODS_CHANGE_MSG.newBuilder();
		builder.setSource(origin);
		for(Map.Entry<Integer, Integer> entry : goodsMap.entrySet()){
			GoodsItem item = buildItem(entry.getKey(), entry.getValue());
			builder.addChangeItem(item);
		}
		connect.sendMsg(builder.build());
	}
	
	private GoodsItem buildItem(Integer goodsId, Integer count){
		return GoodsItem.newBuilder()
					.setId(goodsId)
					.setCount(count)
					.build();
	}
	
	
	@Override
	public void use(int roleId, int goodsId){
		GoodsBaseTemplate template = this.goodsBaseMap.get(goodsId);
		if(template == null){
			return ;
		}
		GOODS_TYPE goodsType = GOODS_TYPE.valueOf(template.getType_i());
		switch( goodsType ){
			//战场道具
			case battle_item: this.useBattleGoods(roleId, goodsId); break;
			
			default : break;
		}
		//扣除物品
		this.removeGoods(roleId, goodsId, 1, OutputType.goodsUseDec.getInfo());
	}
	
	@Override
	public boolean quickBuy(int roleId, int goodsId, int num) {
		GoodsBaseTemplate template = this.goodsBaseMap.get(goodsId);
		if(template == null){
			return false;
		}
		LogCore.runtime.debug("快速购买={}, goodsId={}, num={}", roleId, goodsId, num);
		
		boolean canBuy = GameContext.getUserAttrApp().changeAttribute(roleId, template.getBuyMoney(num), OutputType.goodsQuickBuyDec);
		if(!canBuy) {
			LogCore.runtime.debug("货币不足");
			return false;
		}
		
		addGoods(roleId, goodsId, num, OutputType.goodsQuickBuyInc.getInfo());
		LogCore.runtime.debug("购买成功");
		return true;
	}
	
	
	@Override
	public GoodsShellTemplate getGoodsShellTemplate(int goodsId){
		return this.goodsShellMap.get(goodsId);
	}
	
	
	/** 使用战场道具 */
	private void useBattleGoods(int roleId, int goodsId){
		GoodsWarTemplate template = this.goodsWarMap.get(goodsId);
		if(template == null){
			logger.warn("战场道具没有配置 goodsId="+goodsId);
			return ;
		}
		final RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
		if(connect == null){
			return ;
		}
		final AbstractInstance tank = GameContext.getTankApp().getTankInstanceByRoleId(roleId);
		if(tank == null){
			return ;
		}
		//死亡判断
		if(tank.isDeath()){
			return ;
		}
		
		final int count = tank.getGoods(goodsId);
		if(count <= 0){
			STC_USE_WAR_GOODS_MSG resp = STC_USE_WAR_GOODS_MSG.newBuilder()
					.setResult(0)
					.setInfo(Tips.GOODS_NOT_ENOUGH)
					.build();
			connect.sendMsg(resp);
			return ;
		}
		//广播使用效果
		STC_USE_WAR_EFFECT_MSG efffectMsg = STC_USE_WAR_EFFECT_MSG.newBuilder()
				.setId(tank.getId())
				.setGoodsId(goodsId)
				.build();
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(tank.getMapInstanceId());
		if(mapInstance != null){
			mapInstance.brodcastMsg(BATTLE_MSG.CMD_TYPE.CMD_TYPE_BATTLE_VALUE, 
					BATTLE_MSG.CMD_ID.STC_USE_WAR_EFFECT_VALUE, efffectMsg.toByteArray());
		}
		
		//添加buff
		GameContext.getBuffApp().putBuff(tank, tank, template.getAddBuff());
		//删除buff
		for(String strId : template.getDelBuff().split(Cat.comma)){
			if(Util.isEmpty(strId)){
				continue;
			}
			int delBuffId = Integer.parseInt(strId);
			GameContext.getBuffApp().remove(tank, delBuffId);
		}
		//扣除坦克道具
		int currCount = count - 1;
		tank.putGoods(goodsId, currCount);
		STC_USE_WAR_GOODS_MSG resp = STC_USE_WAR_GOODS_MSG.newBuilder()
				.setResult(1)
				.setInfo("")
				.setId(goodsId)
				.setNum(currCount)
				.build();
		connect.sendMsg(resp);
	}

	@Override
	public MedalTemplate getMedalTemplate(int medalId) {
		return medals.get(medalId);
	}
	
	@Override
	public int getCount(int roleId, int goodsId) {
		return packageDao.getCount(roleId, goodsId);
	}
	
	@Override
	public void addGoods(Integer roleId, List<Integer> goodsList, int count, String origin) {
		if(count <= 0){
			return;
		}
		Map<Integer, Integer> goodsMap = new HashMap<>();
		for(Integer goodsId : goodsList){
			if(!this.goodsBaseMap.containsKey(goodsId)){
				continue;
			}
			int num = count;
			if(goodsMap.containsKey(goodsId)){
				 num += goodsMap.get(goodsId);
			}
			goodsMap.put(goodsId, num);
		}
		
		this.addGoods(roleId, goodsMap, origin);
	}

	
	
	/** 返回需要更新的物品信息列表 */
	@Override
	public List<GoodsChangeTemplate> getGoodsChangeList(){
		return this.goodsChangeList;
	}
	
	public void setPackageDao(PackageDao packageDao) {
		this.packageDao = packageDao;
	}

	
	
}
