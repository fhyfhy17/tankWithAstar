package com.ourpalm.tank.app.battle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.ourpalm.tank.message.BATTLE_MSG.AttrType;
import com.ourpalm.tank.vo.AbstractInstance;

public class HitParamAttrRecover implements HitParamCallBack {

	public Map<AttrType, Float> oldSelfAttrMap = new HashMap<>();
	public Map<AttrType, Float> oldTargetAttrMap = new HashMap<>();

	@Override
	public void call(AbstractInstance self, AbstractInstance target) {
		if (!oldSelfAttrMap.isEmpty()) {
			for (Entry<AttrType, Float> entry : oldSelfAttrMap.entrySet()) {
				self.changeAttr(entry.getKey(), entry.getValue());
			}
		}
		if (!oldTargetAttrMap.isEmpty()) {
			for (Entry<AttrType, Float> entry : oldTargetAttrMap.entrySet()) {
				target.changeAttr(entry.getKey(), entry.getValue());
			}
		}
	}

	public Map<AttrType, Float> getOldSelfAttrMap() {
		return oldSelfAttrMap;
	}

	public void setOldSelfAttrMap(Map<AttrType, Float> oldSelfAttrMap) {
		this.oldSelfAttrMap = oldSelfAttrMap;
	}

	public Map<AttrType, Float> getOldTargetAttrMap() {
		return oldTargetAttrMap;
	}

	public void setOldTargetAttrMap(Map<AttrType, Float> oldTargetAttrMap) {
		this.oldTargetAttrMap = oldTargetAttrMap;
	}

}
