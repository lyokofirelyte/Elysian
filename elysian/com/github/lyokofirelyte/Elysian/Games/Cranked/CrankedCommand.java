package com.github.lyokofirelyte.Elysian.Games.Cranked;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class CrankedCommand {

	Cranked root;
	Elysian main;

	CrankedCommand(Cranked i){
		root = i;
		main = root.main;
	}
	
	@DivCommand(aliases = {"cranked"}, desc = "Main Cranked Command", help = "/cranked help", player = true, min = 0)
	public void onCranked(Player p, String[] args){
		DivinityGame dg = root.toDivGame();
		DivinityPlayer dp = main.getDivPlayer(p);
		
		if(args.length == 0 || args[0].equalsIgnoreCase("help")){
			for(String s : new String[]{
					"help",
					"addarena <name>",
					"remarena <name>",
					"addspawn <arena>",
					"remspawn <arena> <spawnid>",
					"arenalist",
					"spawnlist <arena>",
					"start <arena>",
					"join <arena>"
					
			}){
				main.s(p, "/cranked " + s);
			}
			return;
		}
		
		switch(args[0]){
			
			case "addarena":
				if(dg.contains("Arenas." + args[0])){
					dg.set("Arenas." + args[1] + ".Name", args[1]);
					dp.s("Arena set!");
				}else{
					dp.s("&cThat arena has already been set!");
				}
				break;
				
				
			case "remarena":
				if (args.length == 2 && dg.contains("Arenas." + args[1])){
					dg.set("Arenas." + args[1], null);
					dp.s("Removed the arena &6" + args[1] + "&b!");
				} else {
					dp.err("Invalid args or that arena does not exist.");
				}				break;
			
				
			case "addspawn":
				if(root.doesArenaExist(args[1])){
					if(root.toDivGame().getConfigurationSection("Arenas." + args[1] + ".locations") == null){
						root.toDivGame().createSection("Arenas." + args[1] + ".locations");
						System.out.println("is null");
					}
					root.toDivGame().getStringList("Arenas." + args[1] + ".locations").add(p.getWorld().getName() + " " + p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ());
					dp.s("Spawnpoint added!");
					System.out.println(root.toDivGame().getStringList("Arenas." + args[1] + ".locations"));
				}else{
					dp.s("Arena does not exist!");
				}
				break;
			
			
			case "remspawn":
				break;
				
			case "arenalist":
				
				break;
				
			case "join":
				
				root.setPlaying(p);
	
				break;
				
			case "spawnlist":
				
				break;
			
			case "start":
				root.setStarted(true);
				break;
		}
	}
	
}
