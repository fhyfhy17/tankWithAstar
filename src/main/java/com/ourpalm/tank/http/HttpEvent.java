package com.ourpalm.tank.http;

public abstract class HttpEvent {
	private boolean get = true;
	private String url;
	private String params;

	public boolean isGet() {
		return get;
	}

	public void setGet(boolean get) {
		this.get = get;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public abstract void call();
}
