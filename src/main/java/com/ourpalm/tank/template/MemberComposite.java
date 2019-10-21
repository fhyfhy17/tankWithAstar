package com.ourpalm.tank.template;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.app.GameContext;

public class MemberComposite implements KeySupport<Integer> {

	private int goodsId;
	private int compose;
	private int num;
	private int memberId;
	private int exp;
	
	
	public void init(){
		GoodsBaseTemplate goodsTemplate = GameContext.getGoodsApp().getGoodsBaseTemplate(goodsId);
		if(goodsTemplate == null){
			LogCore.startup.error(String.format("goodsId = %s 不存在", goodsId), new NullPointerException());
		}
		
		MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(memberId);
		if(compose == 1 && template == null){
			LogCore.startup.error(String.format("memberId = %s 不存在  goodsId = %s", memberId, goodsId), new NullPointerException());
		}
	}
	
	
	public boolean hadCompose(){
		return compose == 1;
	}
	
	
	@Override
	public Integer getKey(){
		return this.goodsId;
	}
	
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getCompose() {
		return compose;
	}

	public void setCompose(int compose) {
		this.compose = compose;
	}
}
