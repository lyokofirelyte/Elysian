package com.github.lyokofirelyte.Elysian;

public enum ElyTask {

	LOGGER("LOGGER"),
	WATCHER("WATCHER"),
	SPECTATE("SPECTATE"),
	ANNOUNCER("ANNOUNCER");
	
	ElyTask(String type){
		taskType = type;
	}
	
	String taskType;
}