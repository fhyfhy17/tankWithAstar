package com.ourpalm.core.service;

public class ServerHandler {
	public static Main main;



	public static Main getMain() {
		return main;
	}

	public static void setMain(Main main) {
		ServerHandler.main = main;
	}

	public void init() throws Exception {
		System.err.println("execute init method！");
	}

	public void init(String[] args) throws Exception {
		System.err.println("execute init(args) method");
		Main main = new Main();
		main.startServer();
		setMain(main);
	}

	public void start() throws Exception {
	}

	public void stop() throws Exception {
		System.err.println("execute stop method！");
		if (ServerHandler.getMain() != null) {
			ServerHandler.getMain().stop2(ServerHandler.getMain());
		}

	}

	public void destroy() throws Exception {
		if (ServerHandler.getMain() != null) {
			ServerHandler.getMain().destroy2();
		}
	}

}
