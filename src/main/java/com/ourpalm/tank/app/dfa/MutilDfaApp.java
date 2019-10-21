package com.ourpalm.tank.app.dfa;

import java.util.Set;

import com.ourpalm.core.service.Service;

public interface MutilDfaApp extends Service {

	public Set<String> matchSensitiveWord(String txt);
}
