package com.github.lyokofirelyte.Elysian.Games.Spleef;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Storage.DivGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Elysian.Elysian;

public class Spleef implements DivGame {

	public Elysian main;
	public SpleefModule module;
	public SpleefCommandMain commandMain;
	public SpleefActive active;
	
	public Spleef(Elysian i){
		main = i;
		module = new SpleefModule(this);
		commandMain = new SpleefCommandMain(this);
		active = new SpleefActive(this);
	}
	
	public void update(){
		for (SpleefStorage s : module.data.values()){
			if (s.toGame() != null){
				if (!main.api.divManager.data.containsKey(DivinityManager.gamesDir + "spleef/")){
					main.api.divManager.data.put(DivinityManager.gamesDir + "spleef/", new THashMap<String, DivinityStorage>());
				}
				main.api.divManager.data.get(DivinityManager.gamesDir + "spleef/").put(s.name(), s.toDivStorage());
			}
		}
	}
	
	public DivinityGame toDivGame(String dataName){
		return main.api.getDivGame("spleef", dataName);
	}

	@Override
	public DivinityGame toDivGame() {
		return null;
	}
}