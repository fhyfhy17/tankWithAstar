package com.ourpalm.tank.vo;

import static com.ourpalm.tank.app.GameContext.getTankApp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.quest.BattleResultRecord;
import com.ourpalm.tank.domain.RoleConnect;
import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.message.BATTLE_MSG.INCOME_TYPE;
import com.ourpalm.tank.message.MATCH_MSG.TEAM;
import com.ourpalm.tank.message.ROLE_MSG.STC_EXCEP_OFFLINE_MSG;
import com.ourpalm.tank.script.buff.IBuff;
import com.ourpalm.tank.script.skill.Skill;
import com.ourpalm.tank.type.BodyType;
import com.ourpalm.tank.type.EffectType;
import com.ourpalm.tank.type.HitPart;
import com.ourpalm.tank.util.RandomUtil;
import com.ourpalm.tank.util.peshe.TankEffectPeshe;

public class TankInstance extends AbstractInstance{
	private final static int allRat = 10000; //攻击效果总概率
	private final static int MAX_ERROR_MOVE = 100; //异常移动最大次数,到达最大次数则踢下线
	private final static int MAX_ERROR_LOCATION = 10; //位置移动异常上限
	private final static int MOVE_RATE = 250;	//移动同步频率(单位:毫秒)
	private final static int MOVE_MAX_DISTANCE = 20; //每次移动同步的最大移动位置距离阀值

	// TODO
	private final static int SHARED_VIEW_BUFF = 129; // 共享视野 BUFF

	private int matchScore;		//匹配分数

	private volatile int killCount; 		//击杀数目
	private int deathCount;		//死亡数目
	private int helpKill;		//助攻次数
	private int aidKill;		//协助次数
	private int dodgeCount;		//跳弹次数
	private int missCount;		//未命中次数
	private int findCount;		//点灯次数

	private float mvpScore; 		//mvp战场得分
	private int receiveAllDamage;	//受到的总伤害
	private int hitAllDamage;		//输出的总伤害
	private int aidpAllDamage;		//协助总伤害
	private int lastDamage;			//受到的最后伤害值

	private volatile int lifeKillCount;			//一生累计击杀数
	private volatile int continueKillCount;		//连续击杀数
	private volatile int maxContinueKillCount;	//最大连杀数
	private long killTime; 				//击杀时间

	private long lastFireTime;			//上次开炮时间(普通开炮、使用金币弹开炮)
	private long lastHitTime;			//上次命中时间
	private long lastMoveTime;			//上次移动时间
	private int errorMoveCount;			//异常移动次数
	private List<Integer> moveTimeList = new ArrayList<>();

	private int handup;			//挂机次数

	private int part1;			//配件1
	private int part2;			//配件2
	private int part3; 			//配件3
	private int part4;			//配件4
	private int elitePart1;		//精英配件1
	private int elitePart2; 	//精英配件2

	private int battleScore;		//战斗力
	private int actualBattleScore;
	private int titleId;			//军衔ID

	private int voiceId;			//语音扬声器ID
	private int voiceState;			//扬声器状态

	// 攻击自身的坦克列表
	private Map<Integer, Long> killerMap = new ConcurrentHashMap<>();

	private Map<Integer, Integer> goodsMap = new HashMap<>();			//道具列表
	private Map<Integer, Skill> skillMap = new HashMap<>();				//技能列表

	private BattleResultRecord questRecord = new BattleResultRecord();	 //任务记录

	//命中前护甲有几率产生的效果
	private List<TankEffectPeshe> fHitPeshe = new ArrayList<>();
	//命中侧面护甲有几率产生的效果
	private List<TankEffectPeshe> iHitPeshe = new ArrayList<>();
	//命中背部护甲有几率产生的效果
	private List<TankEffectPeshe> bHitPesh = new ArrayList<>();

	/**战斗收益数据*/
	private Map<INCOME_TYPE,List<IncomeInfo>> incomeMap = new HashMap<>();
	public TankInstance(int instanceId){
		this.id = instanceId;
		this.bodyType = BodyType.tank;
	}

	@Override
	public void init(){
		//初始化正面受损各种效果概率
		this.initFHitPeshe();

		//初始化侧面受损各种效果概率
		this.initIHitPeshe();

		//初始化背面受损各种效果概率
		this.initBHitPeshe();
	}


	/** 判断开炮是否冷却，并设置重置冷却时间 */
	@Override
	public boolean hadAndSetFireCoolTime(){
		 if(this.hadFireCoolTime()){
			 this.lastFireTime = System.currentTimeMillis();
			 return true;
		 }
		 return false;
	}

	/** 判断开炮是否冷却 */
	@Override
	public boolean hadFireCoolTime(){
		return (System.currentTimeMillis() - this.lastFireTime) >= get(AttrType.danjia_time) * 1000;
	}



	//重置开炮冷却时间
	@Override
	public void clearFireCoolTime(){
		this.lastFireTime = 0;
		this.lastHitTime = 0;
	}

	/** 已命中冷却状态 */
	@Override
	public boolean hadAndSetHitFireCoolTime(){
		 if((System.currentTimeMillis() - this.lastHitTime) >= get(AttrType.danjia_time) * 1000){
			 this.lastHitTime = System.currentTimeMillis();
			 return true;
		 }
		 return false;
	}

	/** 检测坦克移动频率 */
	@Override
	public boolean checkMoveTime() {
		boolean result = true;
		if(this.lastMoveTime <= 0){
			this.lastMoveTime = System.currentTimeMillis();
			return true;
		}
		int timeStamp = (int) (System.currentTimeMillis() - this.lastMoveTime);
		//避免网络卡顿或者服务器消息堆积时，误判定为加速的情况
		if(timeStamp < 5){
			timeStamp = MOVE_RATE;
		}
		moveTimeList.add(timeStamp);

		this.lastMoveTime = System.currentTimeMillis();

		int allStamp = 0;
		int length = moveTimeList.size();
		// 统计记录时间戳
		if (length < MAX_ERROR_MOVE) {
			return true;
		}
		// 计算平均值
		for (int stamp : moveTimeList) {
			allStamp += stamp;
		}
		int checkTimeStamp = allStamp / length;
		if (checkTimeStamp < MOVE_RATE) {
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
			if (connect != null) {
				STC_EXCEP_OFFLINE_MSG msg = STC_EXCEP_OFFLINE_MSG.newBuilder()
						.setTips("速度异常,请重新登录...")
						.build();
				connect.sendMsg(msg);
				result = false;
			}
		}
		moveTimeList.clear();
		return result;
	}


	/** 检测坦克移动位置 */
	@Override
	public void checkMove(float x, float z){
		int range = (int)Util.range(x, z, this.x, this.z);
		if(range > MOVE_MAX_DISTANCE){
			this.errorMoveCount += 1;
		}
		if(this.errorMoveCount > MAX_ERROR_LOCATION){
			RoleConnect connect = GameContext.getOnlineCenter().getRoleConnectByRoleId(roleId);
			if(connect != null){
				STC_EXCEP_OFFLINE_MSG msg = STC_EXCEP_OFFLINE_MSG.newBuilder()
						.setTips("位置数据异常,请重新登录...")
						.build();
				connect.sendMsg(msg);
				//重置异常次数
				this.errorMoveCount = 0;
			}
		}
	}

	/** 清空移动错误记录 */
	@Override
	public void clearClearMoveCount(){
		this.errorMoveCount = 0;
	}


	/** 是否为精英坦克 */
	@Override
	public boolean isEliteTank(){
		return this.elitePart1 > 0 && this.elitePart2 > 0;
	}

	/** 清空统计数据 */
	@Override
	public void clearCount(){
		//攻击者列表
		this.killerMap.clear();
		//一生击杀数
		this.lifeKillCount = 0;
		//连杀数
		this.continueKillCount = 0;
		//击杀时间
		this.killTime = 0;
	}

	//放入道具
	@Override
	public void putGoods(int goodsId, int count){
		this.goodsMap.put(goodsId, count);
	}

	//删除道具
	@Override
	public void removeGoods(int goodsId){
		this.goodsMap.remove(goodsId);
	}

	@Override
	public int getGoods(int goodsId){
		Integer count = goodsMap.get(goodsId);
		if(count == null){
			return 0;
		}
		return count;
	}

	//返回所有道具
	@Override
	public Map<Integer, Integer> getAllGoods(){
		return this.goodsMap;
	}


	/** 判断目标是否在自己视野中 */
	@Override
	public boolean view(AbstractInstance target) {
		return AITankViewDeal.INSTANCE.view(target, this);
	}

	@Override
	public boolean viewSelf(AbstractInstance target) {
		return AITankViewDeal.INSTANCE.viewSelf(target, this);
	}

	//死亡不提供伤害
	@Override
	public boolean deathNotView(){
		return isDeath() && getBuff(SHARED_VIEW_BUFF) == null;
	}


	/** 判断射程 */
	@Override
	public boolean fireRange(AbstractInstance target){
		float range = Util.range(this.getX(), this.getZ(), target.getX(), target.getZ());
		return this.get(AttrType.range) >= range;
	}


	//判断俯仰角
	@Override
	public boolean isFireElevation(AbstractInstance target){
		float x = Util.range(this.getX(), this.getZ(), target.getX(), target.getZ());
		float y = Math.abs(this.getY() - target.getY());
		float elev = (float)(Math.atan2(y, x) / Math.PI * 180);
		return elev <= this.get(AttrType.elevation);
	}


	//判断是否有阻挡，有阻挡返回true
	@Override
	public boolean hadBarrier(AbstractInstance target){
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.mapInstanceId);
		return mapInstance.getGrid().hadBarrier(this.getX(), this.getZ(), target.getX(), target.getZ());
	}


	//初始化正面受损概率
	private void initFHitPeshe(){
		int damedRat = (int)(get(AttrType.cat_damed_rat) * allRat);
		int nullRat = allRat - damedRat;

		TankEffectPeshe damedPeshe = new TankEffectPeshe();
		damedPeshe.setEffect(EffectType.gun_hit);
		damedPeshe.setGon(damedRat);

		TankEffectPeshe nullPeshe = new TankEffectPeshe();
		nullPeshe.setEffect(null);
		nullPeshe.setGon(nullRat);

		fHitPeshe.add(damedPeshe);
		fHitPeshe.add(nullPeshe);
	}


	//初始化侧面受损概率
	private void initIHitPeshe(){
		int breRat = (int)(get(AttrType.cat_bre_rat) * allRat);		//履带受损
		int slowRat = (int)(get(AttrType.speed_slow_rat) * allRat);	//发动机
		int nullRat = allRat - (breRat + slowRat);			//不触发概率

		TankEffectPeshe brePeshe = new TankEffectPeshe();
		brePeshe.setEffect(EffectType.track_hit);
		brePeshe.setGon(breRat);

		TankEffectPeshe slowPeshe = new TankEffectPeshe();
		slowPeshe.setEffect(EffectType.engine_hit);
		slowPeshe.setGon(slowRat);

		TankEffectPeshe nullPeshe = new TankEffectPeshe();
		nullPeshe.setEffect(null);
		nullPeshe.setGon(nullRat);

		this.iHitPeshe.add(brePeshe);
		this.iHitPeshe.add(slowPeshe);
		this.iHitPeshe.add(nullPeshe);
	}



	//背部受损概率
	private void initBHitPeshe(){
		int fireRat = (int)(get(AttrType.fire_rat) * allRat);		//机身起火
		int cdSlowRat = (int)(get(AttrType.cd_slow_rat) * allRat); 	//弹药架
		int nullRat = allRat - (fireRat + cdSlowRat);		//不触发概率

		TankEffectPeshe firePeshe = new TankEffectPeshe();
		firePeshe.setEffect(EffectType.body_fire);
		firePeshe.setGon(fireRat);

		TankEffectPeshe cdSlowPeshe = new TankEffectPeshe();
		cdSlowPeshe.setEffect(EffectType.ammo_rack_hit);
		cdSlowPeshe.setGon(cdSlowRat);

		TankEffectPeshe nullPeshe = new TankEffectPeshe();
		nullPeshe.setEffect(null);
		nullPeshe.setGon(nullRat);

		this.bHitPesh.add(firePeshe);
		this.bHitPesh.add(cdSlowPeshe);
		this.bHitPesh.add(nullPeshe);
	}


	//命中部位 0前,1侧,2后
	@Override
	public TankEffectPeshe randomPeshe(int hitPart){
		HitPart part = HitPart.values()[hitPart];
		switch(part){
			case TurretFront : return RandomUtil.getPeshe(this.fHitPeshe);
			case TurretMiddle: return RandomUtil.getPeshe(this.iHitPeshe);
			case Turretbehind : return RandomUtil.getPeshe(this.bHitPesh);
			default:
				break;
		}
		return null;
	}

	//获取技能
	@Override
	public Skill getSkill(int skillId){
		return this.skillMap.get(skillId);
	}

	@Override
	public void putSkill(Skill skill){
		this.skillMap.put(skill.getId(), skill);
	}

	@Override
	public Collection<Skill> getAllSkill(){
		return this.skillMap.values();
	}

	@Override
	public int getRoleId() {
		return roleId;
	}

	@Override
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@Override
	public String getRoleName() {
		return roleName;
	}

	@Override
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public int getTemplateId() {
		return templateId;
	}

	@Override
	public void setTemplateId(int tankId) {
		this.templateId = tankId;
	}

	@Override
	public int getMapInstanceId() {
		return mapInstanceId;
	}

	@Override
	public void setMapInstanceId(int mapInstanceId) {
		this.mapInstanceId = mapInstanceId;
	}

	@Override
	public TEAM getTeam() {
		return team;
	}

	@Override
	public void setTeam(TEAM team) {
		this.team = team;
	}

	@Override
	public int getKillCount() {
		return killCount;
	}

	@Override
	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}

	@Override
	public int getDeathCount() {
		return deathCount;
	}

	@Override
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public float getZ() {
		return z;
	}

	@Override
	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public Map<AttrType, Float> getAttribute() {
		return attribute;
	}

	@Override
	public void setAttribute(Map<AttrType, Float> attribute) {
		this.attribute = attribute;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getAiMain() {
		return aiMain;
	}

	@Override
	public void setAiMain(int aiMain) {
		this.aiMain = aiMain;
	}

	@Override
	public int getHelpKill() {
		return helpKill;
	}

	@Override
	public void setHelpKill(int helpKill) {
		this.helpKill = helpKill;
	}

	@Override
	public void putKiller(int tankInstanceId, long time){
		this.killerMap.put(tankInstanceId, time);
	}

	@Override
	public Map<Integer, Long> getKillerMap(){
		return this.killerMap;
	}

	@Override
	public void removeKiller(int id){
		this.killerMap.remove(id);
	}

	@Override
	public int getLevel() {
		return level;
	}
	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public float getMvpScore() {
		return mvpScore;
	}

	@Override
	public void setMvpScore(float mvpScore) {
		this.mvpScore = mvpScore;
	}

	@Override
	public int getLifeKillCount() {
		return lifeKillCount;
	}
	@Override
	public void setLifeKillCount(int lifeKillCount) {
		this.lifeKillCount = lifeKillCount;
	}
	@Override
	public int getContinueKillCount() {
		return continueKillCount;
	}
	@Override
	public void setContinueKillCount(int continueKillCount) {
		this.continueKillCount = continueKillCount;
	}
	@Override
	public long getKillTime() {
		return killTime;
	}
	@Override
	public void setKillTime(long killTime) {
		this.killTime = killTime;
	}
	@Override
	public Collection<IBuff> getBuffers(){
		return this.buffMap.values();
	}
	@Override
	public int getMaxContinueKillCount() {
		return maxContinueKillCount;
	}
	@Override
	public void setMaxContinueKillCount(int maxContinueKillCount) {
		this.maxContinueKillCount = maxContinueKillCount;
	}
	@Override
	public int getMatchScore() {
		return matchScore;
	}
	@Override
	public void setMatchScore(int matchScore) {
		this.matchScore = matchScore;
	}
	@Override
	public BattleResultRecord getQuestRecord() {
		return questRecord;
	}
	@Override
	public int getReceiveAllDamage() {
		return receiveAllDamage;
	}
	@Override
	public void setReceiveAllDamage(int receiveAllDamage) {
		this.receiveAllDamage = receiveAllDamage;
	}
	@Override
	public int getHitAllDamage() {
		return hitAllDamage;
	}
	@Override
	public void setHitAllDamage(int hitAllDamage) {
		this.hitAllDamage = hitAllDamage;
	}
	public void setLastFireTime(){
		this.lastFireTime = System.currentTimeMillis();
	}
	//设置战斗力
	@Override
	public int getBattleScore() {
		return this.battleScore;
	}
	@Override
	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}
	//设置军衔
	@Override
	public int getTitleId() {
		return this.titleId;
	}
	@Override
	public void setTitleId(int titleId) {
		this.titleId = titleId;
	}
	@Override
	public int getAidKill() {
		return aidKill;
	}

	@Override
	public void setAidKill(int aidKill) {
		this.aidKill = aidKill;
	}

	@Override
	public int getAidpAllDamage() {
		return aidpAllDamage;
	}

	@Override
	public void setAidpAllDamage(int aidpAllDamage) {
		this.aidpAllDamage = aidpAllDamage;
	}

	@Override
	public int getLastDamage() {
		return lastDamage;
	}

	@Override
	public void setLastDamage(int lastDamage) {
		this.lastDamage = lastDamage;
	}

	@Override
	public int getHandup() {
		return handup;
	}

	@Override
	public void setHandup(int handup) {
		this.handup = handup;
	}


	/** 语音扬声器序号 */
	@Override
	public void setVoiceId(int voiceId){
		this.voiceId = voiceId;
	}

	@Override
	public int getVoiceId(){
		return this.voiceId;
	}

	/** 语音扬声器状态 */
	@Override
	public void setVoiceState(int voiceState){
		this.voiceState = voiceState;
	}

	@Override
	public int getVoiceState(){
		return this.voiceState;
	}

	@Override
	public int getDodgeCount() {
		return dodgeCount;
	}

	@Override
	public void setDodgeCount(int dodgeCount) {
		this.dodgeCount = dodgeCount;
	}

	@Override
	public int getMissCount() {
		return missCount;
	}

	@Override
	public void setMissCount(int missCount) {
		this.missCount = missCount;
	}

	@Override
	public int getFindCount() {
		return findCount;
	}

	@Override
	public void setFindCount(int findCount) {
		this.findCount = findCount;
	}

	@Override
	public Map<INCOME_TYPE, List<IncomeInfo>> getIncomeMap() {
		return incomeMap;
	}

	@Override
	public void setIncomeMap(Map<INCOME_TYPE, List<IncomeInfo>> incomeMap) {
		this.incomeMap = incomeMap;
	}

	/**
	 * 判断自身炮塔是否朝着目标
	 *
	 * @param  target 目标
	 * @return        自身炮塔是否朝着目标
	 */
	@Override
	public boolean turrentTowardsWith(AbstractInstance target) {

		// 必须可以转动炮塔，若无炮塔，默认已朝向目标
		if (get(AttrType.had_revolve) != 1) {
			return true;
		}

		// 获得自身和目标的向量的角度
		double targetAngle = Math.atan2(target.getZ() - z, target.getX() - x)
				* (-180) / Math.PI;
		if (targetAngle < 0) {
			targetAngle = targetAngle + 360;
		}

		int rangeOfAngle = getTankApp().getAiConfig().getFireAngleRange();

		// 只要炮塔的方向角和目标的向量角的夹角小于一个给定值即可
		boolean acute  = rangeOfAngle >= Math.abs(gunAngle - targetAngle);
		boolean obtuse = rangeOfAngle >= 360 - Math.abs(gunAngle - targetAngle);

		return acute || obtuse;
	}

	public int getActualBattleScore() {
		return actualBattleScore;
	}

	public void setActualBattleScore(int actualBattleScore) {
		this.actualBattleScore = actualBattleScore;
	}

}
