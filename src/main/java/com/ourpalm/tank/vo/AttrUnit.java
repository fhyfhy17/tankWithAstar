package com.ourpalm.tank.vo;

import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.type.Operation;

/**
 * 角色的属性变化
 */
public class AttrUnit {
	private RoleAttr type;
	private Operation operation;
	private Integer value;
	
	public static AttrUnit build(RoleAttr type, Operation operation, Integer value){
		AttrUnit unit = new AttrUnit();
		unit.setType(type);
		unit.setOperation(operation);
		unit.setValue(value);
		
		return unit;
	}
	
	public RoleAttr getType() {
		return type;
	}
	public void setType(RoleAttr type) {
		this.type = type;
	}
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
}
