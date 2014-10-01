package com.github.lyokofirelyte.Elysian.Games.Cranked;

import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
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
		
		if(args.length == 0){
			for(String s : new String[]{
					"help",
					"addarena",
					"remarena",
					"addspawn <arena>",
					"remspawn <arena> <spawnid>",
					"arenalist",
					"spawnlist <arena>"
					
			}){
				main.s(p, "/cranked " + s);
			}
		}
		
		
	}
	
}
