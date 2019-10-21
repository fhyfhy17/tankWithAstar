package com.ourpalm.tank.app.quest.domain;

import java.util.Map;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.template.MemberTemplate;

/**
 * 获得N星乘员
 * @author Administrator
 *
 */
public class _52_RoleStartMemberPhase extends QuestPhase {
	private int quality;

	public _52_RoleStartMemberPhase(int limit, String param1) {
		super(limit);
		
		if(isInvalid(param1)) {
			throw new IllegalArgumentException("创建任务 [获得N星乘员 ] 失败，参数错误");
		}
		
		quality = Integer.parseInt(param1);
	}

	@Override
	public boolean initProgress() {
		Map<String, RoleMember> memberMap = GameContext.getMemberApp().getRoleMembers(roleId);
		if(Util.isEmpty(memberMap))
			return false;
		
		boolean t = false;
		for(RoleMember member : memberMap.values()) {
			MemberTemplate template = GameContext.getMemberApp().getMemberTemplate(member.getTemplateId());
			if(template == null)
				continue;
			
			if(template.getQuality() != this.quality)
				continue;
			
			t = true;
			this.progress += 1;
		}
		return t;
	}


	@Override
	public boolean roleStartMember(int quality) {
		if(this.quality != quality) {
			return false;
		}
		
		this.progress += 1;
		return true;
	}


}
