package com.github.lyokofirelyte.Elysian.Events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyTP implements Listener {
	
	private Elysian main;
	
	public ElyTP(Elysian i){
		main = i;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onTP(DivinityTeleportEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		List<String> prevLocs = dp.getList(DPI.PREVIOUS_LOCATIONS);
		Vector tv = e.getTo().toVector();
		Player p = e.getPlayer();
		
		if (dp.getBool(DPI.IN_COMBAT) && !main.silentPerms(p, "wa.rank.citizen")){
			main.s(p, "&c&oYou can't escape during combat until you reach Citizen!");
			p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
			return;
		}
		
		if (!main.silentPerms(p, "wa.staff.mod2")){
			for (Player player : Bukkit.getOnlinePlayers()){
				if (main.api.getDivPlayer(player).getBool(DPI.TP_BLOCK)){
					Location vv = player.getLocation();
					if (tv.getBlockX() <= vv.getBlockX()+7 && tv.getBlockX() >= vv.getBlockX()-7){
						if (tv.getBlockZ() <= vv.getBlockZ()+7 && tv.getBlockZ() >= vv.getBlockZ()-7){
							if (tv.getBlockY() <= vv.getBlockY()+7 && tv.getBlockY() >= vv.getBlockY()-7){
								main.s(p, "&c&oTeleblock in place at that area. Teleport cancelled.");
								p.getWorld().playEffect(p.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
								return;
							}
						}
					}
				}
			}
		}
		
		while (prevLocs.size() > 10){
			prevLocs.remove(0);
		}
		
		Location f = e.getFrom();
		Vector v = f.toVector();
		
		prevLocs.add(f.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ() + " " + f.getYaw() + " " + f.getPitch());
		
		effects(p);
		p.teleport(e.getTo());
		effects(p);
		
		Vector toVector = e.getTo().toVector();
		main.s(p, "&oArrived at &6" + toVector.getBlockX() + ", " + toVector.getBlockY() + ", " + toVector.getBlockZ() + "&b&o.");
	}
	
	private void effects(Player p){
		if (!main.api.getDivPlayer(p).getBool(DPI.VANISHED)){
			for (Location l : main.api.divUtils.circle(p.getLocation(), 4, 4, true, true, 0)){
				p.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 0);
			}
		}
	}
}