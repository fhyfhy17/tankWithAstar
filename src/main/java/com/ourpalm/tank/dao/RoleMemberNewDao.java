package com.ourpalm.tank.dao;

import java.util.Collection;
import java.util.Map;

import com.ourpalm.tank.domain.RoleMemberNew;

public interface RoleMemberNewDao {

	Map<String, RoleMemberNew> getAll(int roleId);

	RoleMemberNew get(int roleId, String uniqueId);

	void save(RoleMemberNew member);

	void delete(int roleId, Collection<String> memberList);
}
