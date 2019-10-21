package com.ourpalm.tank.app.map.ai.node;

import static com.ourpalm.tank.app.GameContext.getTankApp;

import com.ourpalm.tank.template.TankTemplate;
import com.ourpalm.tank.vo.AbstractInstance;
import com.ourpalm.tank.vo.behaviortree.Node;

public class TankCategoryCondition extends Node<AbstractInstance> {

	int type;

	public TankCategoryCondition(int type) {
		this.type = type;
	}

	@Override
	public boolean eval(AbstractInstance receiver) {
		int templateId = receiver.getTemplateId();
		TankTemplate template = getTankApp().getTankTemplate(templateId);
		if (template != null) {
			return template.getTankType_i() == type;
		}
		return false;
	}

	public static Node<AbstractInstance> isLT() {
		return new TankCategoryCondition(1);
	}

	public static Node<AbstractInstance> isMT() {
		return new TankCategoryCondition(2);
	}

	public static Node<AbstractInstance> isHT() {
		return new TankCategoryCondition(3);
	}

	public static Node<AbstractInstance> isTD() {
		return new TankCategoryCondition(4);
	}
}
