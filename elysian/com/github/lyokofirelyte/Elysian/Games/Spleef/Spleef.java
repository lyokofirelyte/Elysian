package com.github.lyokofirelyte.Elysian.Games.Spleef;

import java.util.HashMap;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Games.ElyGame;

public class Spleef implements ElyGame {

	public Elysian main;
	public SpleefModule module;
	public SpleefCommandMain commandMain;
	public SpleefActive active;
	
	public Spleef(Elysian i){
		main = i;
		module = new SpleefModule(this);
		commandMain = new SpleefCommandMain(this);
		active = new SpleefActive(this);
		main.api.repeat(this, "update", 20L, 400L, "spleefUpdater", null);
	}
	
	public void update(){
		for (SpleefStorage s : module.data.values()){
			if (s.toGame() != null){
				if (!main.api.divManager.data.containsKey(DivinityManager.gamesDir + "spleef/")){
					main.api.divManager.data.put(DivinityManager.gamesDir + "spleef/", new HashMap<String, DivinityStorage>());
				}
				main.api.divManager.data.get(DivinityManager.gamesDir + "spleef/").put(s.name(), s.toDivStorage());
			}
		}
	}
}