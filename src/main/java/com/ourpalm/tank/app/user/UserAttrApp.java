package com.ourpalm.tank.app.user;

import java.util.List;

import com.ourpalm.tank.app.log.OutputType;
import com.ourpalm.tank.domain.UserAttr;
import com.ourpalm.tank.message.ROLE_MSG.RoleAttr;
import com.ourpalm.tank.vo.AttrUnit;
import com.ourpalm.tank.vo.result.Result;

public interface UserAttrApp {
	
	boolean changeAttribute(Integer roleId, AttrUnit unit, int originId, String origin);
	
	boolean changeAttribute(Integer roleId, AttrUnit unit, boolean check, int originId, String origin);
	
	boolean changeAttribute(Integer roleId, List<AttrUnit> list, int originId, String origin);
	
	boolean changeAttribute(Integer roleId, List<AttrUnit> list, boolean check, int originId, String origin);
	
	boolean changeAttribute(Integer roleId, int originId, String origin, AttrUnit... unit);
	
	boolean changeAttribute(Integer roleId, AttrUnit unit, OutputType origin);
	
	boolean changeAttribute(Integer roleId, List<AttrUnit> list, OutputType origin);
	
	boolean changeAttribute(Integer roleId, OutputType origin, AttrUnit... unit);
	/**
	 * 当银币不够减时，通过金币与银币转换比，所金币转换成银币，再减
	 * @param roleId
	 * @param needIron	需要的银币
	 * @return	是否成功  失败时表明金币不足
	 */
	boolean consumIron(int roleId, int needIron, int originId, String origin);
	
	
	int get(int roleId, RoleAttr attr);
	
	UserAttr getUserAttr(int roleId);
	
	Result goldTranslateToIron(int roleId, int gold, int rate);

	Result diamondTranslateToGold(int roleId, int diamonds);
}
