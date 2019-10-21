package com.ourpalm.tank.domain;

/**
 * 邮件附件
 * @author admin
 *
 */
public class MailAttach {
	
	private int id;
	
	private int goodsId;
	
	private int count;
	//附件是否领取
	private boolean recive;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}




	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isRecive() {
		return recive;
	}

	public void setRecive(boolean recive) {
		this.recive = recive;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	
	
}
