package com.ourpalm.tank.app.goods;

import java.util.List;
import java.util.Map;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.message.PACKAGE_MSG.GOODS_TYPE;
import com.ourpalm.tank.template.GoodsBaseTemplate;
import com.ourpalm.tank.template.GoodsBoxTemplate;
import com.ourpalm.tank.template.GoodsChangeTemplate;
import com.ourpalm.tank.template.GoodsShellTemplate;
import com.ourpalm.tank.template.GoodsWarTemplate;
import com.ourpalm.tank.template.MedalTemplate;
import com.ourpalm.tank.template.TankPartMaterialTemplate;
import com.ourpalm.tank.vo.result.GoodsBoxOpenResult;

public interface GoodsApp extends Service{

	/**
	 * 返回物品基础对象
	 */
	GoodsBaseTemplate getGoodsBaseTemplate(int goodsId);
	
	GoodsShellTemplate getGoodsShellTemplate(int goodsId);
	
	TankPartMaterialTemplate getMaterialTemplate(int goodsId);
	
	GoodsWarTemplate getGoodsWarTemplate(int goodsId);
	/**
	 * 返回宝箱属性
	 */
	GoodsBoxTemplate getGoodsBoxTemplate(int goodsId);
	
	public Integer getSpecialGoodsId(GOODS_TYPE type);
	
	Map<Integer, Integer> getRoleGoods(Integer roleId);
	
	boolean removeGoods(Integer roleId, Integer goodsId, Integer count, String origin);
	
	boolean removeGoods(Integer roleId, Map<Integer, Integer> goodsMap, String origin);
	
	void addGoods(Integer roleId, Integer goodsId, Integer count, String origin);
	
	void addGoods(Integer roleId, Map<Integer, Integer> goodsMap, String origin);
	
	/**
	 * 所有物品添加同一个数量
	 * 
	 * @param roleId
	 * @param goodsList  物品id列表
	 * @param count		 添加数量（列表中的每个物品数量）
	 * @param type
	 */
	void addGoods(Integer roleId, List<Integer> goodsList, int count, String origin);
	
	GoodsBoxOpenResult openBox(int roleId, int goodsId, int count);
	
	void use(int roleId, int goodsId);

	boolean quickBuy(int roleId, int goodsId, int num);

	MedalTemplate getMedalTemplate(int medalId);
	
	//获取物品数量
	public int getCount(int roleId, int goodsId);
	
	/** 返回需要更新的物品信息列表 */
	List<GoodsChangeTemplate> getGoodsChangeList();
}
