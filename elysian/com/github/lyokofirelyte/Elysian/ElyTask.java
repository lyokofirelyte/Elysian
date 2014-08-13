package com.github.lyokofirelyte.Elysian;

public enum ElyTask {

	LOGGER("LOGGER"),
	WATCHER("WATCHER"),
	ANNOUNCER("ANNOUNCER");
	
	ElyTask(String type){
		taskType = type;
	}
	
	String taskType;
}