package com.github.lyokofirelyte.Elysian.Commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElySpectate {

	private Elysian main;
	
	public ElySpectate(Elysian i){
		main = i;
	}
	
	@DivCommand(aliases = {"spectate", "spec"}, desc = "Elysian Spectate Command", help = "/spec <player>, /spec", player = true, perm = "wa.staff.mod2")
	public void onSpectate(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (dp.getBool(DPI.SPECTATING)){
			
			dp.set(DPI.SPECTATING, false);
			end(p, main.getPlayer(dp.getStr(DPI.SPECTATE_TARGET)));
			
		} else if (args.length == 1 && main.doesPartialPlayerExist(args[0]) && main.isOnline(args[0]) && !main.matchDivPlayer(args[0]).getBool(DPI.SPECTATING) && main.matchDivPlayer(args[0]) != dp){
			
			Vector v = p.getLocation().toVector();
			
			main.getPlayer(args[0]).hidePlayer(p);
			p.hidePlayer(main.getPlayer(args[0]));
			
			main.matchDivPlayer(args[0]).set(DPI.SPECTATE_TARGET, p.getName());
			dp.set(DPI.SPECTATE_TARGET, args[0]);
			dp.set(DPI.SPECTATING, true);
			dp.getList(DPI.PREVIOUS_LOCATIONS).add(p.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + p.getLocation().getYaw() + " " + p.getLocation().getPitch());
			p.teleport(main.getPlayer(args[0]));
			
		} else {
			main.s(p, "&c&oThat player is not online or is spectating someone.");
		}
	}
	
	private void end(Player p, Player target){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> lastLocs = dp.getList(DPI.PREVIOUS_LOCATIONS);
		String[] lastLoc = lastLocs.get(lastLocs.size()-1).split(" ");
		
		main.s(p, "&oYou have stopped spectating " + main.matchDivPlayer(main.api.getDivPlayer(p).getStr(DPI.SPECTATE_TARGET)).getStr(DPI.DISPLAY_NAME) + "&b.");
		main.api.getDivPlayer(target).set(DPI.SPECTATE_TARGET, "none");
		
		dp.set(DPI.SPECTATE_TARGET, "none");
		dp.set(DPI.SPECTATING, false);
		p.setFlying(false); p.setAllowFlight(false); 
		p.teleport(new Location(Bukkit.getWorld(lastLoc[0]), Double.parseDouble(lastLoc[1]), Double.parseDouble(lastLoc[2]), Double.parseDouble(lastLoc[3]), Float.parseFloat(lastLoc[4]), Float.parseFloat(lastLoc[5])));
		p.showPlayer(target);
		target.showPlayer(p);
	}
}