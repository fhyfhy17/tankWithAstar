package com.ourpalm.tank.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件
 * @author lizengcun
 */
public class Mail {
	public Mail(){
	}
	
	public Mail(String id){
		this.id = id;
		this.creatTime = new Date();
	}

	//邮件保存时间30天
	public static final int SAVETIME = 30*24*3600;
	//邮件ID
	private String id;
	//接收者id
	private int reciverId;
	//创建时间
	private Date creatTime;
	//是否已读
	private boolean read;
	//邮件标题
	private String title;
	//邮件内容
	private String content;
	//金币
	private int gold;
	//钢铁
	private int iron;
	//经验
	private int exp;
	//荣耀
	private int honor;
	//全局经验
	private int tankExp;
	//钻石
	private int diamond;
	
	//附件
	private Map<Integer,MailAttach> attach = new HashMap<Integer,MailAttach>();
	
	//附近是否已领取
	private boolean attachget;

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreatTime() {
		return creatTime;
	}

	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<Integer, MailAttach> getAttach() {
		return attach;
	}

	public void setAttach(Map<Integer, MailAttach> attach) {
		this.attach = attach;
	}
	
	public void addAttach(int goodsId,int count){
		MailAttach mailAttach = new MailAttach();
		mailAttach.setId(attach.size());
		mailAttach.setGoodsId(goodsId);
		mailAttach.setCount(count);
		mailAttach.setRecive(false);
		attach.put(mailAttach.getId(), mailAttach);
	}


	public int getReciverId() {
		return reciverId;
	}

	public void setReciverId(int reciverId) {
		this.reciverId = reciverId;
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

	public boolean isAttachget() {
		return attachget;
	}

	public void setAttachget(boolean attachget) {
		this.attachget = attachget;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	public int getTankExp() {
		return tankExp;
	}

	public void setTankExp(int tankExp) {
		this.tankExp = tankExp;
	}
}
