package com.ourpalm.tank.app.prompt;

import com.ourpalm.core.service.Service;
import com.ourpalm.tank.message.ROLE_MSG.PROMPT;

public interface PromptApp extends Service{

	void prompt(int roleId, PROMPT promptType);
}
