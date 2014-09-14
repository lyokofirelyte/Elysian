package com.github.lyokofirelyte.Elysian.Games.Blink;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Storage.DivStorageModule;

public enum BlinkData {

	GAME_LOCS("GAME_LOCS");
	
	BlinkData(String type){
		this.type = type;
	}
	
	String type;
	
	@DivStorageModule(types = { DivinityManager.gamesDir + "blink" })
	public String s(){
		return type;
	}
}