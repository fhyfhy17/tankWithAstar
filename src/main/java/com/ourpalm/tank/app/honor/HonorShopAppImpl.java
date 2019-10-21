package com.ourpalm.tank.app.honor;

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
import com.ourpalm.tank.domain.RoleShop;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.HonorShopTemplate;
import com.ourpalm.tank.template.ShopItemTemplate;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.XlsPojoUtil;
import com.ourpalm.tank.util.peshe.GoodsPeshe;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;
import com.ourpalm.tank.vo.result.ShopBuyResult;

public class HonorShopAppImpl implements HonorShopApp {
	//普通商品格子组索引
	private final int[] commonGroups = {1, 2, 3, 4, 5, 6, 7, 8};
	
	private ShopRecordDao shopRecordDao;
	
	private HonorShopTemplate shopTemplate;
	
	private Map<Integer, ShopItemTemplate> itemTemplateMap = new HashMap<Integer, ShopItemTemplate>();
	private Map<Integer, List<GoodsPeshe>> groupPesheMap = new HashMap<>();
	
	private Map<Integer, RoleShop> shopMap = new ConcurrentHashMap<>();
	
	@Override
	public void start() {
		this.loadShopTemplate();
		this.loadItemTemplate();
		
	}
	
	private void loadShopTemplate(){
		String fileName = XlsSheetType.HonorShopTemplate.getXlsFileName();
		String sheetName = XlsSheetType.HonorShopTemplate.getSheetName();
		try{
			this.shopTemplate = XlsPojoUtil.sheetToList(fileName, sheetName, HonorShopTemplate.class).get(0);
		}catch(Exception e){
			LogCore.startup.error("加载文件出错 file={}, sheet={}", fileName, sheetName);
		}
	}

	private void loadItemTemplate(){
		String fileName = XlsSheetType.HonorShopTtemTemplate.getXlsFileName();
		String sheetName = XlsSheetType.HonorShopTtemTemplate.getSheetName();
		try{
			this.itemTemplateMap = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, ShopItemTemplate.class);
			
			for(ShopItemTemplate tmp : itemTemplateMap.values()) {
				if(!groupPesheMap.containsKey(tmp.getGroup()))
					groupPesheMap.put(tmp.getGroup(), new ArrayList<GoodsPeshe>());
				
				List<GoodsPeshe> groupList = groupPesheMap.get(tmp.getGroup());
				groupList.add(tmp.buildPeshe());
			}
			
		}catch(Exception e){
			LogCore.startup.error("加载文件出错 file={}, sheet={}", fileName, sheetName);
		}
	}
	
	@Override
	public void stop() { }
	
	@Override
	public ShopBuyResult buy(int roleId, int itemId,int itemNum){
		ShopBuyResult result = new ShopBuyResult();
		result.setResult(Result.FAILURE);
		
		RoleShop roleShop = shopMap.get(roleId);
		if(roleShop == null) {
			result.setInfo("商店购买失败");
			return result;
		}
		
		ShopItemTemplate item = itemTemplateMap.get(itemId);
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
		
		int cost = item.getDis_price();
		int moneyType = item.getMoneyType();
		
		boolean consumeRst = GameContext.getUserAttrApp().changeAttribute(roleId, 
				AttrUnit.build(RoleAttr.valueOf(moneyType), Operation.decrease, cost), OutputType.shopBuyDec.type(), StringUtil.buildLogOrigin(template.getName_s(), OutputType.shopBuyDec.getInfo()));
		if(!consumeRst){
			result.setInfo("购买条件不足");
			return result;
		}
		
		roleShop.buy(itemId);
		shopRecordDao.save(roleId, roleShop);
		
		GameContext.getQuestTriggerApp().shopBuy(roleId);
		GameContext.getQuestTriggerApp().roleLevelMedal(roleId, buyGoodsId, buyCount);
		GameContext.getGoodsApp().addGoods(roleId, buyGoodsId, buyCount, OutputType.shopBuyInc.getInfo());
		
		result.setResult(Result.SUCCESS);
		result.setMoneyType(moneyType);
		result.setMoney(cost);
		result.setGoodsName(template.getName_s());
		result.setGoodsNum(item.getCount());
		return result;
	}
	
	public Result sell(int roleId, int goodsId, int count){
		GoodsBaseTemplate goods = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if(goods == null){
			return Result.newFailure("物品不存在");
		}
		if(goods.getSellMoney_i() <= 0){
			return Result.newFailure("物品无法出售");
		}
		if(!GameContext.getGoodsApp().removeGoods(roleId, goodsId, count, OutputType.goodsSellDec.getInfo())){
			return Result.newFailure("物品数量不足");
		}
		
		int addGold = goods.getSellMoney_i() * count;
		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.iron, Operation.add, addGold), OutputType.goodsSellInc.type(), StringUtil.buildLogOrigin(goods.getName_s(), OutputType.goodsSellInc.getInfo()));
		return Result.newSuccess();
	}
	
	@Override
	public void login(int roleId, boolean first) {
		RoleShop roleShop = shopRecordDao.get(roleId);
		if(roleShop == null) {
			roleShop = new RoleShop();
		}
		shopMap.put(roleId, roleShop);
	}

	@Override
	public void offline(int roleId) {
		shopMap.remove(roleId);
	}
	
	@Override
	public RoleShop getShopList(int roleId) {
		RoleShop roleShop = shopMap.get(roleId);
		if(roleShop == null) 
			return null;
		
		boolean refresh = needCommonRefresh(roleId);
		if(refresh) {
			List<Integer> goodsIds = randomGroupGoods();
			roleShop.setGoodsIds(goodsIds);
			roleShop.clearBuy();
			
			roleShop.setLastRefreshTime(clacLastRefreshTime(roleShop.getLastRefreshTime()));
			shopRecordDao.save(roleId, roleShop);
		}
		
		return roleShop;
	}
	
	@Override
	public RoleShop refreshShopList(int roleId) {
		RoleShop roleShop = shopMap.get(roleId);
		if(roleShop == null) {
			return null;
		}
		
		int cost = getRefreshCost(roleId);
		
		
		int honor = GameContext.getUserAttrApp().get(roleId, RoleAttr.honor);
		if(honor < cost) {
			return null;
		}
		
		GameContext.getUserAttrApp().changeAttribute(roleId, AttrUnit.build(RoleAttr.honor, Operation.decrease, cost), OutputType.shopRefreshDec);
		roleShop.increaseRefreshCount();
		
		List<Integer> goodsIds = randomGroupGoods();
		roleShop.setGoodsIds(goodsIds);
		roleShop.clearBuy();
		shopRecordDao.save(roleId, roleShop);
		
		return roleShop;
	}

	@Override
	public HonorShopTemplate getShopTemplate() {
		return shopTemplate;
	}

	@Override
	public ShopItemTemplate getShopItemTemplate(int itemId) {
		return itemTemplateMap.get(itemId);
	}

	@Override
	public int getRefreshCost(int roleId) {
		RoleShop roleShop = shopMap.get(roleId);
		if(roleShop == null)
			return 0;
		
		initRefreshCount(roleId);
		
		int count = roleShop.getRefreshCount();
		int cost = 0;
		switch(count) {
		case 0:
			cost = shopTemplate.getHonorRefreshCost1();
			break;
		case 1:
			cost = shopTemplate.getHonorRefreshCost2();
			break;
		case 2:
			cost = shopTemplate.getHonorRefreshCost3();
			break;
		default:
			cost = shopTemplate.getHonorRefreshCost4();
			break;
		}
		return cost;
	}
	
	@Override
	public int getLeftRefreshTime(int roleId) {
		RoleShop roleShop = shopMap.get(roleId);
		int refreshCd = shopTemplate.getHonorRefreshCd();
		if(roleShop == null)
			return refreshCd;
		
		int cTime = (int)(System.currentTimeMillis()/1000);
		int refreshTime = roleShop.getLastRefreshTime();
		return refreshCd - ((cTime - refreshTime) % refreshCd);
	}
	
	private int clacLastRefreshTime(int oldTime) {
		int cTime = (int)(System.currentTimeMillis()/1000);
		if(oldTime == 0)
			return cTime;
		
		int refreshCd = shopTemplate.getHonorRefreshCd();
		int dis = ((cTime - oldTime) % refreshCd);
		return cTime - dis;
	}
	
	//每一组随机抽取一个商品
	private List<Integer> randomGroupGoods() {
		List<Integer> list = new ArrayList<>();
		for(int group : this.commonGroups){
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
	
	private boolean needCommonRefresh(int roleId) {
		RoleShop roleShop = shopMap.get(roleId);
		int refreshTime = roleShop.getLastRefreshTime();
		int cTime = (int)(System.currentTimeMillis()/1000);
		
		int refreshCd = shopTemplate.getHonorRefreshCd();
		if(refreshTime == 0) {
			return true;
		}
				
		int gaps = cTime - refreshTime;
		if(refreshCd - gaps > 0) {
			return false;
		}
		return true;
	}

	private void initRefreshCount(int roleId) {
		RoleShop roleShop = shopMap.get(roleId);
		
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
