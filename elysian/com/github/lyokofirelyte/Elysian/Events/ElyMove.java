package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyMove implements Listener {
	
	private Elysian main;
	
	public ElyMove(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		
		if (main.api.getDivPlayer(e.getPlayer()).getBoolDPI(DPI.DISABLED) || borderCheck(e.getPlayer(), e.getTo().toVector())){
			e.getPlayer().teleport(e.getFrom());
		}
		
		main.afkCheck(e.getPlayer());
		main.api.event(new ScoreboardUpdateEvent(e.getPlayer(), "move"));
		
		if (!main.api.getDivPlayer(e.getPlayer()).getDPI(DPI.SPECTATE_TARGET).equals("none")){
			main.getPlayer(main.api.getDivPlayer(e.getPlayer()).getDPI(DPI.SPECTATE_TARGET)).teleport(e.getPlayer().getLocation());
		}
	}
	
	private boolean borderCheck(Player p, Vector v){
		
		if (p.getWorld().getName().equals("world") && (v.getBlockX() > 5000 || v.getBlockX() < -5000 || v.getBlockZ() > 5000 || v.getBlockZ() < -5000)){
			main.s(p, "&c&oBorder reached!");
			return true;
		}
		
		return false;
	}
}