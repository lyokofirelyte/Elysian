package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRegion;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class ElySomeCommand implements Listener {

	private Elysian main;
	
	public ElySomeCommand(Elysian i){
		main = i;
	}
	
	@DivCommand(aliases = {"tp", "teleport", "tele"}, help = "/tp <player>", desc = "A teleport command!", min = 3, player = true)
	public void onSomeTeleportCommand(Player p, String[] args){
		
	}
	
	private void example(){
		
		if (!main.api.getSystem().getList(MMO.INVALID_BLOCKS).contains("iron")){
			// continue
		}
		
		DivinityPlayer dp = main.matchDivPlayer("hug");
		DivinityRegion rg = main.getDivRegion("spawn");
		
		if (dp.getLong(DPI.SOME_COOLDOWN)>= System.currentTimeMillis()){
			dp.s("Some message");
			dp.set(DPI.SOME_COOLDOWN, System.currentTimeMillis() + 60*1000L);
		} else {
			dp.err("Cooldown!");
		}
		
		main.api.schedule(this, "messageLater", 20L, "someTaskName", "hug");
	}
	
	public void messageLater(String p){
		if (main.isOnline(p)){
			main.s(main.getPlayer(p), "messaged 1 second later!");
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void someEvent(PlayerCommandPreprocessEvent e){
		
		Location l = e.getPlayer().getLocation();
		
		String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
		float x = Precision.round(l.toVector().getBlockX(), -3);
		float z = Precision.round(l.toVector().getBlockZ(), -3);
		int y = l.toVector().getBlockY();
		File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
		List<String> results = new ArrayList<String>();
		
		if (file.exists()){
			YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
			results = new ArrayList<String>(yaml.getStringList("History." + e.getPlayer().getWorld().getName() + "." + y));
		}
		
		if (results.size() > 0){
			// not natural
		}
	}
}