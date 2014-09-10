package com.github.lyokofirelyte.Elysian;

public enum ElyTask {

	LOGGER("LOGGER"),
	WATCHER("WATCHER"),
	SPECTATE("SPECTATE"),
	MMO_BLOCKS("MMO_BLOCKS"),
	ANNOUNCER("ANNOUNCER"),
	WEBSITE("WEBSITE");
	
	ElyTask(String type){
		taskType = type;
	}
	
	String taskType;
}