package com.ourpalm.tank.app.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.DateUtil;
import com.ourpalm.core.util.StringUtil;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.dao.ShopRecordDao;
import com.ourpalm.tank.domain.RoleAccount;
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.VipShopGroupTemplate;
import com.ourpalm.tank.template.VipShopItemTemplate;
import com.ourpalm.tank.template.VipShopTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.GoodsPeshe;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.ShopBuyResult;

public class VipShopAppImpl implements VipShopApp {
	private VipShopTemplate shopTemplate;
	
	private Map<Integer, VipShopItemTemplate> itemTemplateMap = new HashMap<>();
	private Map<Integer, List<GoodsPeshe>> groupPesheMap = new HashMap<>();
	private Map<Integer, VipShopGroupTemplate> groupTemplateMap = new HashMap<>();
	
	private Map<Integer, RoleShop> shopMap = new ConcurrentHashMap<>();
	
	private ShopRecordDao shopRecordDao;
	
	@Override
	public void start() {
		this.loadShopTemplate();
		this.loadItemTemplate();
		this.loadGroupTemplate();
	}
	
	private void loadShopTemplate(){
		String fileName = XlsSheetType.VipShopTemplate.getXlsFileName();
		String sheetName = XlsSheetType.VipShopTemplate.getSheetName();
		try{
			this.shopTemplate = XlsPojoUtil.sheetToList(fileName, sheetName, VipShopTemplate.class).get(0);
		}catch(Exception e){
			LogCore.startup.error("加载文件出错 file={}, sheet={}", fileName, sheetName);
		}
	}

	private void loadItemTemplate(){
		String fileName = XlsSheetType.VipShopTtemTemplate.getXlsFileName();
		String sheetName = XlsSheetType.VipShopTtemTemplate.getSheetName();
		try{
			this.itemTemplateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, VipShopItemTemplate.class);
			
			for(VipShopItemTemplate tmp : itemTemplateMap.values()) {
				int group = tmp.getGroup();
				if(!groupPesheMap.containsKey(group))
					groupPesheMap.put(group, new ArrayList<GoodsPeshe>());
				
				List<GoodsPeshe> groupList = groupPesheMap.get(group);
				groupList.add(tmp.buildPeshe());
			}
			
		}catch(Exception e){
			LogCore.startup.error("加载文件出错 file={}, sheet={}", fileName, sheetName);
		}
	}
	
	private void loadGroupTemplate(){
		String fileName = XlsSheetType.VipShopGroupTemplate.getXlsFileName();
		String sheetName = XlsSheetType.VipShopGroupTemplate.getSheetName();
		try{
			List<VipShopGroupTemplate> list = XlsPojoUtil.sheetToList(fileName, sheetName, VipShopGroupTemplate.class);
			
			for(VipShopGroupTemplate tmp : list) {
				tmp.init();
				groupTemplateMap.put(tmp.getVipLevel(), tmp);
			}
			
		}catch(Exception e){
			LogCore.startup.error("加载文件出错 file={}, sheet={}", fileName, sheetName);
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public RoleShop getShopList(int roleId) {
		RoleShop roleShop = getRoleShop(roleId);
		if(roleShop == null) 
			return null;
		
		boolean refresh = needCommonRefresh(roleId);
		if(refresh) {
			List<Integer> goodsIds = randomCommonGoods(roleId);
			roleShop.setGoodsIds(goodsIds);
			roleShop.clearBuy();
			roleShop.setLastRefreshTime(clacLastRefreshTime(roleShop.getLastRefreshTime()));
			shopRecordDao.save(roleId, roleShop);
		} 
		
		return roleShop;
	}

	@Override
	public int getLeftRefreshTime(int roleId) {
		RoleShop roleShop = getRoleShop(roleId);
		int refreshCd = shopTemplate.getVipRefreshCd();
		if(roleShop == null)
			return refreshCd;
		
		int cTime = (int)(System.currentTimeMillis()/1000);
		int refreshTime = roleShop.getLastRefreshTime();
		return refreshCd - ((cTime - refreshTime) % refreshCd);
	}

	@Override
	public RoleShop refreshShopList(int roleId) {
		RoleShop roleShop = getRoleShop(roleId);
		if(roleShop == null) {
			return null;
		}
		
		int cost = getRefreshCost(roleId);
		
		
		int gold = GameContext.getUserAttrApp().get(roleId, RoleAttr.gold);
		if(gold < cost) {
			return null;
		}
		
		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.gold, Operation.decrease, cost), OutputType.VipShopRefreshDec);
		roleShop.increaseRefreshCount();
		
		List<Integer> goodsIds = randomCommonGoods(roleId);
		roleShop.setGoodsIds(goodsIds);
		roleShop.clearBuy();
		
		shopRecordDao.save(roleId, roleShop);
		return roleShop;
	}

	@Override
	public ShopBuyResult buy(int roleId, int itemId, int moneyType) {
		ShopBuyResult result = new ShopBuyResult();
		result.setResult(Result.FAILURE);
		
		RoleShop roleShop = getRoleShop(roleId);
		if(roleShop == null) {
			result.setInfo("商店购买失败");
			return result;
		}
		
		VipShopItemTemplate item = itemTemplateMap.get(itemId);
		if(item == null){
			result.setInfo("商品不存在");
			return result;
		}
		final int buyGoodsId = item.getGoodsId();
		final int buyCount = item.getCount();
		GoodsBaseTemplate template = GameContext.getGoodsApp().getGoodsBaseTemplate(buyGoodsId);
		if(template == null) {
			result.setInfo("商品不存在");
			return result;
		}
		
		if(!roleShop.getGoodsIds().contains(itemId)) {
			result.setInfo("商品不存在商品列表");
			return result;
		}
		
		if(roleShop.hadBuy(itemId)) {
			result.setInfo("该商品已经购买");
			return result;
		}
		
		int cost = 0;
		if(moneyType == item.getMoneyType1()) {
			cost = item.getPrice1();
		} else if(moneyType == item.getMoneyType2()) {
			cost = item.getPrice2();
		} else {
			result.setInfo("购买货币不正确");
			return result;
		}
		
		boolean consumeRst = GameContext.getUserAttrApp().changeAttribute(roleId, 
				AttrUnit.build(RoleAttr.valueOf(moneyType), Operation.decrease, cost), OutputType.VipShopBuyDec.type(), StringUtil.buildLogOrigin(template.getName_s(), "VIP商店购买"));
		
		if(!consumeRst){
			result.setInfo("货币不足");
			return result;
		}
		
		roleShop.buy(itemId);
		shopRecordDao.save(roleId, roleShop);
		
		GameContext.getQuestTriggerApp().shopBuy(roleId);
		GameContext.getQuestTriggerApp().roleLevelMedal(roleId, buyGoodsId, buyCount);
		GameContext.getGoodsApp().addGoods(roleId, buyGoodsId, buyCount, "VIP商店购买");
		
		result.setResult(Result.SUCCESS);
		result.setMoneyType(moneyType);
		result.setMoney(cost);
		result.setGoodsName(template.getName_s());
		result.setGoodsNum(item.getCount());
		return result;
	}


	@Override
	public void login(int roleId, boolean first) {
		RoleShop roleShop = shopRecordDao.get(roleId);
		if(roleShop == null) {
			roleShop = new RoleShop();
			shopRecordDao.save(roleId, roleShop);
		}
		shopMap.put(roleId, roleShop);
	}

	@Override
	public void offline(int roleId) {
		shopMap.remove(roleId);
	}

	@Override
	public VipShopTemplate getShopTemplate() {
		return shopTemplate;
	}

	@Override
	public VipShopItemTemplate getShopItemTemplate(int itemId) {
		return itemTemplateMap.get(itemId);
	}

	
	private RoleShop getRoleShop(int roleId){
		RoleShop roleShop = this.shopMap.get(roleId);
		if(roleShop == null){
			return this.shopRecordDao.get(roleId);
		}
		return roleShop;
	}
	
	
	@Override
	public int getRefreshCost(int roleId) {
		RoleShop roleShop = getRoleShop(roleId);
		if(roleShop == null)
			return 0;
		
		initRefreshCount(roleId);
		
		int count = roleShop.getRefreshCount();
		int cost = 0;
		switch(count) {
		case 0:
			cost = shopTemplate.getVipRefreshCost1();
			break;
		case 1:
			cost = shopTemplate.getVipRefreshCost2();
			break;
		case 2:
			cost = shopTemplate.getVipRefreshCost3();
			break;
		default:
			cost = shopTemplate.getVipRefreshCost4();
			break;
		}
		return cost;
	}
	
	private boolean needCommonRefresh(int roleId) {
		RoleShop roleShop = getRoleShop(roleId);
		int refreshTime = roleShop.getLastRefreshTime();
		int cTime = (int)(System.currentTimeMillis()/1000);
		
		int refreshCd = shopTemplate.getVipRefreshCd();
		if(refreshTime == 0) {
			return true;
		}
				
		int gaps = cTime - refreshTime;
		if(refreshCd - gaps > 0) {
			return false;
		}
		return true;
	}
	
	private List<Integer> randomCommonGoods(int roleId) {
		RoleAccount account = GameContext.getUserApp().getRoleAccount(roleId);
		int vipLevel = account.getVipLevel();
		
		VipShopGroupTemplate groupTemplate = groupTemplateMap.get(vipLevel);
		if(groupTemplate == null) {
			return new ArrayList<Integer>();
		}
		
		return randomGroupGoods(groupTemplate.getGroups());
	}
	
	
	//每一组随机抽取一个商品
	private List<Integer> randomGroupGoods(List<Integer> groups) {
		List<Integer> list = new ArrayList<>();
		for(int group : groups){
			if(!groupPesheMap.containsKey(group)){
				continue;
			}
			List<GoodsPeshe> pesheList = groupPesheMap.get(group);
			GoodsPeshe randomPeshe = RandomUtil.getPeshe(pesheList);
			if(randomPeshe == null) {
				continue;
			}
			list.add(randomPeshe.getGoodsId());
		}
		return list;
	}
	
	private int clacLastRefreshTime(int oldTime) {
		int cTime = (int)(System.currentTimeMillis()/1000);
		if(oldTime == 0)
			return cTime;
		
		int refreshCd = shopTemplate.getVipRefreshCd();
		int dis = ((cTime - oldTime) % refreshCd);
		return cTime - dis;
	}

	private void initRefreshCount(int roleId) {
		RoleShop roleShop = getRoleShop(roleId);
		
		int refreshTime = roleShop.getRefreshTime();
		int cTime = (int)(System.currentTimeMillis() / 1000);
		
		if(refreshTime == 0 || !DateUtil.isSameDay(refreshTime * 1000l, cTime * 1000l)) {
			roleShop.setRefreshCount(0);
			roleShop.setRefreshTime(cTime);
		}
	}

	public void setShopRecordDao(ShopRecordDao shopRecordDao) {
		this.shopRecordDao = shopRecordDao;
	}
}
