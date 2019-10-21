package com.ourpalm.tank.template;

import java.util.HashMap;
import java.util.Map;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.parser.FelNode;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;

public class MemberPropertyFormular {
	private String aptitude;
	private String hp;
	private String fdef;
	private String idef;
	private String bdef;
	private String fdodge;
	private String idodge;
	private String bdodge;
	private String atk;
	private String chuanjia;
	private String mul_chuanjia;
	private String danjia_time;
	private String acceleration_time_end;
	
	private String can_pre;
	private String strength;
	private String matchScore;
	

	private String cat_damed_rat;
	private String cat_bre_rat;
	private String speed_slow_rat;
	private String fire_rat;
	private String cd_slow_rat;
	private Map<AttrType, FelNode> attrNodes = new HashMap<>();
	private FelNode aptitudeFelNode;
	private FelNode strengthFelNede;
	private FelNode matchScoreFelNode;
	
	public void init() {
		FelEngine engine = new FelEngineImpl();
		
		this.aptitudeFelNode = parse(engine, aptitude);
		this.strengthFelNede = parse(engine, strength);
		this.matchScoreFelNode = parse(engine, matchScore);
		
		attrNodes.put(AttrType.maxHp, parse(engine, hp));              
		attrNodes.put(AttrType.fdef, parse(engine, fdef));
		attrNodes.put(AttrType.idef, parse(engine, idef));
		attrNodes.put(AttrType.bdef, parse(engine, bdef));
		attrNodes.put(AttrType.fdodge, parse(engine, fdodge));
		attrNodes.put(AttrType.idodge, parse(engine, idodge));
		attrNodes.put(AttrType.bdodge, parse(engine, bdodge));
		attrNodes.put(AttrType.atk, parse(engine, atk));
		attrNodes.put(AttrType.chuanjia, parse(engine, chuanjia));
		attrNodes.put(AttrType.mul_chuanjia, parse(engine, mul_chuanjia));
		attrNodes.put(AttrType.danjia_time, parse(engine, danjia_time));
		attrNodes.put(AttrType.acceleration_time_end, parse(engine, acceleration_time_end));
		attrNodes.put(AttrType.can_pre, parse(engine, can_pre));
		attrNodes.put(AttrType.cat_damed_rat, parse(engine, cat_damed_rat));
		attrNodes.put(AttrType.cat_bre_rat, parse(engine, cat_bre_rat));
		attrNodes.put(AttrType.speed_slow_rat, parse(engine, speed_slow_rat));
		attrNodes.put(AttrType.fire_rat, parse(engine, fire_rat));
		attrNodes.put(AttrType.cd_slow_rat, parse(engine, cd_slow_rat));
	}
	
	private FelNode parse(FelEngine engine, String formular){
		if (Util.isEmpty(formular)) {
			throw new IllegalArgumentException(String.format("属性公式填写错误 , %s", formular));
		}
		
		try {
			return engine.parse(formular);
		} catch(Exception e) {
			throw new IllegalArgumentException(String.format("属性公式填写错误,  %s", formular));
		}	
	}
	
	public FelNode getAttrNode(AttrType type) {
		return this.attrNodes.get(type);
	}

	public FelNode getAptitudeFelNode() {
		return aptitudeFelNode;
	}

	public FelNode getStrengthFelNede() {
		return strengthFelNede;
	}

	public String getAptitude() {
		return aptitude;
	}

	public void setAptitude(String aptitude) {
		this.aptitude = aptitude;
	}

	public String getHp() {
		return hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

	public String getFdef() {
		return fdef;
	}

	public void setFdef(String fdef) {
		this.fdef = fdef;
	}

	public String getIdef() {
		return idef;
	}

	public void setIdef(String idef) {
		this.idef = idef;
	}

	public String getBdef() {
		return bdef;
	}

	public void setBdef(String bdef) {
		this.bdef = bdef;
	}

	public String getFdodge() {
		return fdodge;
	}

	public void setFdodge(String fdodge) {
		this.fdodge = fdodge;
	}

	public String getIdodge() {
		return idodge;
	}

	public void setIdodge(String idodge) {
		this.idodge = idodge;
	}

	public String getBdodge() {
		return bdodge;
	}

	public void setBdodge(String bdodge) {
		this.bdodge = bdodge;
	}

	public String getAtk() {
		return atk;
	}

	public void setAtk(String atk) {
		this.atk = atk;
	}

	public String getChuanjia() {
		return chuanjia;
	}

	public void setChuanjia(String chuanjia) {
		this.chuanjia = chuanjia;
	}

	public String getMul_chuanjia() {
		return mul_chuanjia;
	}

	public void setMul_chuanjia(String mul_chuanjia) {
		this.mul_chuanjia = mul_chuanjia;
	}

	public String getDanjia_time() {
		return danjia_time;
	}

	public void setDanjia_time(String danjia_time) {
		this.danjia_time = danjia_time;
	}

	public String getAcceleration_time_end() {
		return acceleration_time_end;
	}

	public void setAcceleration_time_end(String acceleration_time_end) {
		this.acceleration_time_end = acceleration_time_end;
	}

	public String getCan_pre() {
		return can_pre;
	}
	public void setCan_pre(String can_pre) {
		this.can_pre = can_pre;
	}

	public String getCat_damed_rat() {
		return cat_damed_rat;
	}

	public void setCat_damed_rat(String cat_damed_rat) {
		this.cat_damed_rat = cat_damed_rat;
	}

	public String getCat_bre_rat() {
		return cat_bre_rat;
	}

	public void setCat_bre_rat(String cat_bre_rat) {
		this.cat_bre_rat = cat_bre_rat;
	}

	public String getSpeed_slow_rat() {
		return speed_slow_rat;
	}

	public void setSpeed_slow_rat(String speed_slow_rat) {
		this.speed_slow_rat = speed_slow_rat;
	}

	public String getFire_rat() {
		return fire_rat;
	}

	public void setFire_rat(String fire_rat) {
		this.fire_rat = fire_rat;
	}

	public String getCd_slow_rat() {
		return cd_slow_rat;
	}

	public void setCd_slow_rat(String cd_slow_rat) {
		this.cd_slow_rat = cd_slow_rat;
	}

	public Map<AttrType, FelNode> getExpression() {
		return attrNodes;
	}

	public void setExpression(Map<AttrType, FelNode> expression) {
		this.attrNodes = expression;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public FelNode getMatchScoreFelNode() {
		return matchScoreFelNode;
	}

	public String getMatchScore() {
		return matchScore;
	}

	public void setMatchScore(String matchScore) {
		this.matchScore = matchScore;
	}
}
