package com.ourpalm.tank.app.member;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSON;
import com.greenpineyu.fel.FelEngineImpl;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.context.MapContext;
import com.greenpineyu.fel.parser.FelNode;
import com.ourpalm.core.log.LogCore;
import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.tank.AttrBuffer;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.template.MedalTemplate;
import com.ourpalm.tank.template.MemberProperty;
import com.ourpalm.tank.template.MemberPropertyFormular;
import com.ourpalm.tank.template.MemberTemplate;
import com.ourpalm.tank.type.XlsSheetType;
import com.ourpalm.tank.util.XlsPojoUtil;

public class MemberAttrEvaluateImpl implements MemberAttrEvaluate{
	private Logger logger = LogCore.runtime;
	private Map<AttrType, AttrType> attrAdditions = new HashMap<>();
	private Map<Integer, MemberProperty> levelProperties; 
	private MemberPropertyFormular formular;
	private LocalMath localMath = new LocalMath();

	@Override
	public void start() {
		this.loadProperty();
		this.loadFormular();
		this.initAdditionAttrMap();
	}
	
	@Override
	public void stop() {}
	
	private void initAdditionAttrMap() {
		attrAdditions.put(AttrType.maxHp_add_rat , AttrType.crew_maxHp_add_rat);
		attrAdditions.put(AttrType.atk_add_rat , AttrType.crew_atk_add_rat);
		attrAdditions.put(AttrType.danjia_time , AttrType.crew_danjia_add_rat);
		attrAdditions.put(AttrType.speed_add_rat , AttrType.crew_speed_add_rat);
		attrAdditions.put(AttrType.chuanjia_add_rat , AttrType.crew_chuanjia_add_rat);
		attrAdditions.put(AttrType.fdef_add_rat , AttrType.crew_fdef_add_rat);
		attrAdditions.put(AttrType.idef_add_rat , AttrType.crew_idef_add_rat);
		attrAdditions.put(AttrType.bdef_add_rat , AttrType.crew_bdef_add_rat);
	}
	
	
	private void loadProperty() {
		String fileName = XlsSheetType.memberProperty.getXlsFileName();
		String sheetName = XlsSheetType.memberProperty.getSheetName();
		try {
			this.levelProperties = XlsPojoUtil.sheetToGenericMap(fileName, sheetName, MemberProperty.class);
			for(MemberProperty property : levelProperties.values()){
				property.init();
			}
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}

	private void loadFormular() {
		String fileName = XlsSheetType.memberFormular.getXlsFileName();
		String sheetName = XlsSheetType.memberFormular.getSheetName();
		try {
			List<MemberPropertyFormular> list = XlsPojoUtil.sheetToList(fileName, sheetName, MemberPropertyFormular.class);
			this.formular = list.get(0);
			this.formular.init();
		} catch (Exception e) {
			LogCore.startup.error(String.format("加载配置表%s-%s发生异常...", fileName, sheetName), e);
		}
	}
	
	public Map<AttrType, Float> getMemberAttr(int roleId) {
		AttrBuffer buffer = new AttrBuffer();
		//获取成员对象
		Map<String, RoleMember> roleMembers = GameContext.getMemberApp().getUseMember(roleId);
		Set<Integer> memberTemplateIds = buildMemberTemplateIds(roleMembers);
		for(String instanceId : roleMembers.keySet()) {
			RoleMember member = roleMembers.get(instanceId);
			//累加计算成员属性
			buffer.append(getMemberAttr(member, memberTemplateIds));
			//计算成员佩戴的勋章属性加成
			buffer.append(getMedalAttr(member));
		}
		return calcAttrRate(buffer.getAttrMap());
	}
	
	
	//计算成员自身属性
	private Map<AttrType, Float> getMemberAttr(RoleMember member, Set<Integer> memberIds){
		MemberProperty property = this.levelProperties.get(member.getLevel());
		MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(member.getTemplateId());
		if (property == null) {
			return new HashMap<>();
		}
		
		AttrBuffer buffer = new AttrBuffer();
		for(Integer attrType : template.getAttrList()){
			AttrType attr = AttrType.valueOf(attrType);
			float value = property.getAttrValue(attr);
			float rate = this.getRate(member, template, attr);
			float realValue = value*rate;
			buffer.append(attr, realValue);
		}
		return buffer.getAttrMap();
	}
	
	/*最小值，最大值，参考值  x,y,z 对应随机值为a */
	private float getRate(RoleMember member, MemberTemplate template, AttrType type) {
		FelNode aptitudeNode = formular.getAptitudeFelNode();
		FelNode attrNode = formular.getAttrNode(type);
		if (attrNode == null) {
			LogCore.runtime.error("成员属性没有对应计算公式 attrType=" + type, new NullPointerException());
			return 0;
		}

		FelContext context = getContext();
		context.set("x", template.getAptitudeMin());
		context.set("y", template.getAptitudeMax());
		context.set("z", template.getAptitudeBase());
		context.set("a", member.getAptitude());
		Number realAptitude = (Number)(aptitudeNode.eval(context));
		if(logger.isDebugEnabled()){
			logger.debug("计算资质 参数={} 结果={}", JSON.toJSONString(context), realAptitude);
		}
		
		context.set("aptitude", realAptitude);
		Number rate = (Number)(attrNode.eval(context));
		if(logger.isDebugEnabled()){
			logger.debug("属性={}，参数={}， 结果={}", type, JSON.toJSONString(context), rate);
		}
		return rate.floatValue();
	}
	
	private FelContext getContext(){
		FelContext context = new MapContext();
		context.set("Math", localMath);
		return context;
	}
	
	public static void main(String[] args){
//		String str = "aptitude>2000?(1.5*((aptitude-1900)/1000+1)):(aptitude>1700?(1*((aptitude-1900)/1000+1)):(aptitude>1400?(0.5*((aptitude-1900)/1000+1)):(0.25*((aptitude-1900)/1000+1))))";
		//String str = "a<(x+y-z)?(x+(z-x)*Math.pow(1-Math.pow(a+z-x-y,2)/Math.pow(y-z,2),0.5)):(y-(y-z)*Math.pow(1-Math.pow(a+z-x-y,2)/Math.pow(z-x,2),0.5))";
//		String str = "Math.pow(10,10)";
		String str = "z";
		MemberAttrEvaluateImpl i = new MemberAttrEvaluateImpl();
		FelNode node = new FelEngineImpl().parse(str);
		FelContext context = new MapContext();
//		context.set("aptitude", 3000);
		context.set("Math", i.localMath);
		context.set("x", 1900);
		context.set("y", 1900);
		context.set("z", 1900);
		context.set("a", 1900);
		Number result = (Number)(node.eval(context));
		System.out.println("result = " + result.floatValue());
	}
	
	/** 计算成员所佩戴的勋章属性加成 */
	private Map<AttrType, Float> getMedalAttr(RoleMember member) {
		if (Util.isEmpty(member.getMedals())) {
			return new HashMap<>();
		}
		
		AttrBuffer buffer = new AttrBuffer();
		for(Integer medalId : member.getMedals()){
			MedalTemplate template = GameContext.getGoodsApp().getMedalTemplate(medalId);
			if(template == null){
				continue;
			}
			buffer.append(template.getAttrMap());
		}
		if(logger.isDebugEnabled()){
			logger.debug("勋章ID = {} 所加属性 : {}", member.getTemplateId(), JSON.toJSONString(buffer.getAttrMap()));
		}
		return buffer.getAttrMap();
	}
	
	/** 最后计算属性百分比对应的属性加成 */
	private Map<AttrType, Float> calcAttrRate(Map<AttrType, Float> attrMap) {
		for(Map.Entry<AttrType, AttrType> entry : attrAdditions.entrySet()){
			Float value = attrMap.get(entry.getKey());
			Float addRate = attrMap.remove(entry.getValue());
			if (value == null || addRate == null) {
				continue;
			}
			attrMap.put(entry.getKey(), value*(1+addRate));
		}
		return attrMap;
	}

	private Set<Integer> buildMemberTemplateIds(Map<String, RoleMember> roleMembers) {
		Set<Integer> result = new HashSet<>();
		for(RoleMember member : roleMembers.values()){
			MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(member.getTemplateId());
			result.add(template.getMemberId());
		}
		return result;
	}
	
	
	/** 计算成员战斗力 */
	public int calcMemberStrength(RoleMember member){
		FelNode aptitudeNode = formular.getAptitudeFelNode();
		MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(member.getTemplateId());
		MemberProperty property = this.levelProperties.get(member.getLevel());
		if(template == null){
			return 0;
		}
		FelNode attrNode = formular.getStrengthFelNede();
		if(attrNode == null){
			LogCore.runtime.error("成员战斗力计算公式异常...", new NullPointerException());
			return 0;
		}
		FelContext context = getContext();
		context.set("x", template.getAptitudeMin());
		context.set("y", template.getAptitudeMax());
		context.set("z", template.getAptitudeBase());
		context.set("a", member.getAptitude());
		Number realAptitude = (Number)(aptitudeNode.eval(context));
		context.set("aptitude", realAptitude);
		Number rate = (Number)(attrNode.eval(context));
		//战斗力 = 自身等级战斗力属性 * rate
		int strength = (int)(property.getStrength() * rate.floatValue());
		
		return strength;
	}
	
	
	
	/** 计算成员匹配分 */
	public int calcMemberMatchScore(RoleMember member){
		FelNode aptitudeNode = formular.getAptitudeFelNode();
		MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(member.getTemplateId());
		MemberProperty property = this.levelProperties.get(member.getLevel());
		if(template == null){
			return 0;
		}
		FelNode attrNode = formular.getMatchScoreFelNode();
		if(attrNode == null){
			LogCore.runtime.error("成员匹配分计算公式异常...", new NullPointerException());
			return 0;
		}
		FelContext context = getContext();
		context.set("x", template.getAptitudeMin());
		context.set("y", template.getAptitudeMax());
		context.set("z", template.getAptitudeBase());
		context.set("a", member.getAptitude());
		Number realAptitude = (Number)(aptitudeNode.eval(context));
		context.set("aptitude", realAptitude);
		Number rate = (Number)(attrNode.eval(context));
		//匹配分 = 自身等级匹配分属性 * rate
		int strength = (int)(property.getMatchScore() * rate.floatValue());
		
		return strength;
	}
	
	
	
	public class LocalMath {
		
		public Double sqrt(Number num){
			if (num == null){
				return 0d;
			}
			return Math.sqrt(num.doubleValue());
		}
		
		public Double pow(Number a, Number b){
			return Math.pow(a.doubleValue(), b.doubleValue());
		}
	}
}

