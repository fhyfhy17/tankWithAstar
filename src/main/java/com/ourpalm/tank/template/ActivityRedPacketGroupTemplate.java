package com.ourpalm.tank.template;

import java.util.ArrayList;
import java.util.List;

import com.ourpalm.core.util.KeySupport;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.app.activity.logic.ActivityLogicEnum;
import com.ourpalm.tank.app.activity.logic._201_RedPacketLogic;

public class ActivityRedPacketGroupTemplate implements KeySupport<Integer>{
	private int group;
	private int _1;
	private int _2;
	private int _3;
	private int _4;
	private int _5;
	private int _6;
	private int _7;
	private int _8;
	private int _9;
	private int _10;
	private int _11;
	private int _12;
	private int _13;
	private int _14;
	private int _15;
	private int _16;
	private int _17;
	private int _18;
	private int _19;
	private int _20;
	
	private List<Integer> rewardList = new ArrayList<>();
	
	public void init() {
		addReward(_1);
		addReward(_2);
		addReward(_3);
		addReward(_4);
		addReward(_5);
		addReward(_6);
		addReward(_7);
		addReward(_8);
		addReward(_9);
		addReward(_10);
		addReward(_11);
		addReward(_12);
		addReward(_13);
		addReward(_14);
		addReward(_15);
		addReward(_16);
	}
	
	private void addReward(int id) {
		_201_RedPacketLogic logic = GameContext.getActivityApp().getActivityLogic(ActivityLogicEnum.RedPacket);
		ActivityRedPacketDrawTemplate t = logic.getDrawTemplate(id);
		if(t == null) {
			throw new IllegalArgumentException("红包group表奖励不存在，groupid: "+ group + " id:" + id);
		}
		rewardList.add(id);
	}
	
	public List<Integer> getRewardList() {
		return rewardList;
	}

	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int get_1() {
		return _1;
	}
	public void set_1(int _1) {
		this._1 = _1;
	}
	public int get_2() {
		return _2;
	}
	public void set_2(int _2) {
		this._2 = _2;
	}
	public int get_3() {
		return _3;
	}
	public void set_3(int _3) {
		this._3 = _3;
	}
	public int get_4() {
		return _4;
	}
	public void set_4(int _4) {
		this._4 = _4;
	}
	public int get_5() {
		return _5;
	}
	public void set_5(int _5) {
		this._5 = _5;
	}
	public int get_6() {
		return _6;
	}
	public void set_6(int _6) {
		this._6 = _6;
	}
	public int get_7() {
		return _7;
	}
	public void set_7(int _7) {
		this._7 = _7;
	}
	public int get_8() {
		return _8;
	}
	public void set_8(int _8) {
		this._8 = _8;
	}
	public int get_9() {
		return _9;
	}
	public void set_9(int _9) {
		this._9 = _9;
	}
	public int get_10() {
		return _10;
	}
	public void set_10(int _10) {
		this._10 = _10;
	}
	public int get_11() {
		return _11;
	}
	public void set_11(int _11) {
		this._11 = _11;
	}
	public int get_12() {
		return _12;
	}
	public void set_12(int _12) {
		this._12 = _12;
	}
	public int get_13() {
		return _13;
	}
	public void set_13(int _13) {
		this._13 = _13;
	}
	public int get_14() {
		return _14;
	}
	public void set_14(int _14) {
		this._14 = _14;
	}
	public int get_15() {
		return _15;
	}
	public void set_15(int _15) {
		this._15 = _15;
	}
	public int get_16() {
		return _16;
	}
	public void set_16(int _16) {
		this._16 = _16;
	}
	public int get_17() {
		return _17;
	}
	public void set_17(int _17) {
		this._17 = _17;
	}
	public int get_18() {
		return _18;
	}
	public void set_18(int _18) {
		this._18 = _18;
	}
	public int get_19() {
		return _19;
	}
	public void set_19(int _19) {
		this._19 = _19;
	}
	public int get_20() {
		return _20;
	}
	public void set_20(int _20) {
		this._20 = _20;
	}

	@Override
	public Integer getKey() {
		return group;
	}
	
}
