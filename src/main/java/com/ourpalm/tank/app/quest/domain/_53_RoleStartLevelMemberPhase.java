package com.ourpalm.tank.app.quest.domain;

import java.util.Map;

import com.ourpalm.core.util.Util;
import com.ourpalm.tank.app.GameContext;
import com.ourpalm.tank.domain.RoleMember;
import com.ourpalm.tank.template.MemberTemplate;

/**
 * 拥有N级以上的N星乘员
 * @author Administrator
 *
 */
public class _53_RoleStartLevelMemberPhase extends QuestPhase {
	private int level;
	private int quality;

	public _53_RoleStartLevelMemberPhase(int limit, String param1, String param2) {
		super(limit);
		
		if(isInvalid(param1) || isInvalid(param2)) {
			throw new IllegalArgumentException("创建任务 [拥有N级以上的N星乘员 ] 失败，参数错误");
		}
		
		level = Integer.parseInt(param1);
		quality = Integer.parseInt(param2);
		
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
			
			if(member.getLevel() != this.level)
				continue;
			
			t = true;
			this.progress += 1;
		}
		return t;
	}


	@Override
	public boolean roleStartLevelMember(int quality, int level) {
		if(this.quality != quality)
			return false;
		
		if(this.level != level)
			return false;
		
		this.progress += 1;
		return true;
	}


}
