package com.ourpalm.tank.template;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;
import com.ourpalm.tank.vo.AttrUnit;

public class GoodsBaseTemplate implements KeySupport<Integer>{
	private int id_i;
	private String name_s;
	private String desc_s;
	private String icon_s;
	private int quality_i;
	private int sort_i;
	private int type_i;
	private int carryNum_i;
	private int frame_i;
	private int gold;
	private int iron;
	private int sellMoney_i;
	private int donate_i;			//军团捐献值
	private int contribute_i;		//军团贡献值
	private int gangGold_i;			//军团币
	
	public AttrUnit getBuyMoney(int num) {
		if(num <= 0)
			num = 1;
		
		if(gold > 0)
			return AttrUnit.build(RoleAttr.gold, Operation.decrease, gold * num);
		
		return AttrUnit.build(RoleAttr.iron, Operation.decrease, iron * num);
	}
	
	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getIron() {
		return iron;
	}

	public void setIron(int iron) {
		this.iron = iron;
	}

	@Override
	public Integer getKey() {
		return id_i;
	}
	
	public int getId_i() {
		return id_i;
	}
	public void setId_i(int id_i) {
		this.id_i = id_i;
	}
	public String getName_s() {
		return name_s;
	}
	public void setName_s(String name_s) {
		this.name_s = name_s;
	}
	public String getDesc_s() {
		return desc_s;
	}
	public void setDesc_s(String desc_s) {
		this.desc_s = desc_s;
	}
	public String getIcon_s() {
		return icon_s;
	}
	public void setIcon_s(String icon_s) {
		this.icon_s = icon_s;
	}
	public int getQuality_i() {
		return quality_i;
	}
	public void setQuality_i(int quality_i) {
		this.quality_i = quality_i;
	}
	public int getSort_i() {
		return sort_i;
	}
	public void setSort_i(int sort_i) {
		this.sort_i = sort_i;
	}
	public int getType_i() {
		return type_i;
	}
	public void setType_i(int type_i) {
		this.type_i = type_i;
	}
	public int getFrame_i() {
		return frame_i;
	}
	public void setFrame_i(int frame_i) {
		this.frame_i = frame_i;
	}
	public int getSellMoney_i() {
		return sellMoney_i;
	}
	public void setSellMoney_i(int sellMoney_i) {
		this.sellMoney_i = sellMoney_i;
	}
	public int getDonate_i() {
		return donate_i;
	}
	public void setDonate_i(int donate_i) {
		this.donate_i = donate_i;
	}
	public int getCarryNum_i() {
		return carryNum_i;
	}
	public void setCarryNum_i(int carryNum_i) {
		this.carryNum_i = carryNum_i;
	}
	public int getContribute_i() {
		return contribute_i;
	}
	public void setContribute_i(int contribute_i) {
		this.contribute_i = contribute_i;
	}
	public int getGangGold_i() {
		return gangGold_i;
	}
	public void setGangGold_i(int gangGold_i) {
		this.gangGold_i = gangGold_i;
	}
}
