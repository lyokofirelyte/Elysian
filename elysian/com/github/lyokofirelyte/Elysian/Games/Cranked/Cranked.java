package com.github.lyokofirelyte.Elysian.Games.Cranked;

import java.util.ArrayList;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Storage.DivGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGame;
import com.github.lyokofirelyte.Elysian.ElySave;
import com.github.lyokofirelyte.Elysian.Elysian;

public class Cranked implements DivGame, ElySave{
	
	protected Elysian main;
	public CrankedCommand command;
	public CrankedActive active;
	public boolean isStarted = false;
	public ArrayList<String> players = new ArrayList<String>();
	public THashMap<String, Integer> kills = new THashMap<String, Integer>();
	public String currentGame;
	
	public Cranked(Elysian i){
		main = i;
		command = new CrankedCommand(this);
		active = new CrankedActive(this);
	}

	@Override
	public DivinityGame toDivGame() {
		return main.api.getDivGame("cranked", "cranked");
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(){
		// TODO Auto-generated method stub
		
	}
	
	public boolean doesArenaExist(String s){	
		if(this.toDivGame().contains("Arenas." + s)){
			return true;
		}else{
			return false;
		}	
	}
	
	public boolean isPlaying(Player p){
		if(players.contains(p.getName())){
			return true;
		}
		return false;
	}
	
	public void setStarted(boolean bool){
		isStarted = bool;
	}
	
	public void setPlaying(Player p){
		if(!players.contains(p.getName())){
			players.add(p.getName());
		}
	}
	
	public Location getRandomLocation(){
		for(String s : this.toDivGame().getStringList("Arenas." + currentGame + ".locations")){
			String[] data = s.split(" ");
			return new Location(Bukkit.getWorld(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
		}
		return null;
	}
}
