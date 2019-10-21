package com.ourpalm.tank.dao.impl;

import com.ourpalm.core.dao.redis.Client;

public abstract class AbstractJedisDao {
	
	protected Client client;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	
	
	
}
