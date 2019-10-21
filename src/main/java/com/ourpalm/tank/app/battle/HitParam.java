package com.ourpalm.tank.app.battle;

import com.ourpalm.tank.message.BATTLE_MSG.FireType;
import com.ourpalm.tank.vo.AbstractInstance;

public class HitParam {
	
	private AbstractInstance source;
	private AbstractInstance target;
	private boolean hadDodge;		//是否跳弹
	private int hitPart;
	
	private boolean hadChuanjia;	//是否穿甲
	private FireType fireType;
	private int itemId;
	private float chuanjiaAddRat;
	private float atkAddRat;
	
	private HitParamCallBack callBack;//攻击回调，目前用在临时增加属性后恢复属性
	
	public AbstractInstance getSource() {
		return source;
	}
	public void setSource(AbstractInstance source) {
		this.source = source;
	}
	public AbstractInstance getTarget() {
		return target;
	}
	public void setTarget(AbstractInstance target) {
		this.target = target;
	}
	public boolean isHadDodge() {
		return hadDodge;
	}
	public void setHadDodge(boolean hadDodge) {
		this.hadDodge = hadDodge;
	}
	public int getHitPart() {
		return hitPart;
	}
	public void setHitPart(int hitPart) {
		this.hitPart = hitPart;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public float getChuanjiaAddRat() {
		return chuanjiaAddRat;
	}
	public void setChuanjiaAddRat(float chuanjiaAddRat) {
		this.chuanjiaAddRat = chuanjiaAddRat;
	}
	public float getAtkAddRat() {
		return atkAddRat;
	}
	public void setAtkAddRat(float atkAddRat) {
		this.atkAddRat = atkAddRat;
	}
	public FireType getFireType() {
		return fireType;
	}
	public void setFireType(FireType fireType) {
		this.fireType = fireType;
	}
	public boolean isHadChuanjia() {
		return hadChuanjia;
	}
	public void setHadChuanjia(boolean hadChuanjia) {
		this.hadChuanjia = hadChuanjia;
	}
	public HitParamCallBack getCallBack() {
		return callBack;
	}
	public void setCallBack(HitParamCallBack callBack) {
		this.callBack = callBack;
	}
	
}
