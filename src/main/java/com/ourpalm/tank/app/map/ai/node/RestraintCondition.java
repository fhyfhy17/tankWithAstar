package com.ourpalm.tank.app.map.ai.node;

import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.Cate;
import com.ourpalm.tank.vo.Restraint;
import com.ourpalm.tank.vo.behaviortree.Node;

/**
 * 行为树节点：克制条件判断
 */
public class RestraintCondition extends Node<AbstractInstance> {

	/** 约束结果 */
	Restraint expectedRestraint;

	RestraintCondition(Restraint expectedRestraint) {
		this.expectedRestraint = expectedRestraint;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {

		if (receiver.getFireTarget() == null) {
			return false;
		}

		Cate iCate = receiver.getCate();
		Cate tCate = receiver.getFireTarget().getCate();

		if (iCate == null || tCate == null) {
			return false;
		}

		return iCate.eval(tCate) == expectedRestraint;
	}
}
