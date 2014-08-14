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
		
		if (dp.getBoolDPI(DPI.SPECTATING)){
			
			dp.setDPI(DPI.SPECTATING, false);
			end(p, main.getPlayer(dp.getDPI(DPI.SPECTATE_TARGET)));
			
		} else if (args.length == 1 && main.doesPartialPlayerExist(args[0]) && main.isOnline(args[0]) && !main.matchDivPlayer(args[0]).getBoolDPI(DPI.SPECTATING) && main.matchDivPlayer(args[0]) != dp){
			
			Vector v = p.getLocation().toVector();
			
			main.getPlayer(args[0]).hidePlayer(p);
			p.hidePlayer(main.getPlayer(args[0]));
			
			main.matchDivPlayer(args[0]).setDPI(DPI.SPECTATE_TARGET, p.getName());
			dp.setDPI(DPI.SPECTATE_TARGET, args[0]);
			dp.getListDPI(DPI.PREVIOUS_LOCATIONS).add(p.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + p.getLocation().getYaw() + " " + p.getLocation().getPitch());
			
		} else {
			main.s(p, "&c&oThat player is not online or is spectating someone.");
		}
	}
	
	private void end(Player p, Player target){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> lastLocs = dp.getListDPI(DPI.PREVIOUS_LOCATIONS);
		String[] lastLoc = lastLocs.get(lastLocs.size()-1).split(" ");
		
		main.api.getDivPlayer(target).setDPI(DPI.SPECTATE_TARGET, "none");
		
		dp.setDPI(DPI.SPECTATE_TARGET, "none");
		dp.setDPI(DPI.SPECTATING, false);
		p.teleport(new Location(Bukkit.getWorld(lastLoc[0]), Double.parseDouble(lastLoc[1]), Double.parseDouble(lastLoc[2]), Double.parseDouble(lastLoc[3]), Float.parseFloat(lastLoc[4]), Float.parseFloat(lastLoc[5])));
		p.showPlayer(target);
		target.showPlayer(p);
		
		main.s(p, "&oYou have stopped spectating " + main.matchDivPlayer(main.api.getDivPlayer(p).getDPI(DPI.SPECTATE_TARGET)).getDPI(DPI.DISPLAY_NAME) + "&b.");
	}
}