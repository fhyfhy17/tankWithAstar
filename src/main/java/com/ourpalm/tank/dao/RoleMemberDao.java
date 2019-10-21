package com.ourpalm.tank.dao;

import java.util.Collection;
import java.util.Map;

import com.ourpalm.tank.domain.RoleMember;

public interface RoleMemberDao {

	Map<String, RoleMember> getAll(int roleId);
	
	Map<String, RoleMember> get(int roleId, Collection<String> ids);
	
	RoleMember get(int roleId, int templateId);
	
	void save(RoleMember member);
	
	void delete(int roleId, Collection<RoleMember> memberList);
}
