package com.github.lyokofirelyte.Elysian.Games.Cranked;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.lyokofirelyte.Divinity.Events.DivinityTeleportEvent;
import com.github.lyokofirelyte.Elysian.Elysian;

public class CrankedActive implements Listener{

	Cranked root;
	Elysian main;

	CrankedActive(Cranked i){
		root = i;
		main = root.main;
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if(root.isStarted && root.isPlaying(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		if(root.isStarted && root.isPlaying(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e){
		if(e.getEntity() instanceof Player && root.isPlaying((Player)e.getEntity()) && root.isStarted){
			Player killed = (Player) e.getEntity();
			Player killer = killed.getKiller();
			
			
			if(!root.kills.contains(killer)){
				root.kills.put(killer.getName(), 1);
			}else{
				root.kills.put(killer.getName(), root.kills.get(killer.getName()) + 1);
			}
			
			e.getDrops().clear();
		}		
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player damager = (Player) e.getDamager();
			Player player = (Player) e.getEntity();

			if(root.isPlaying(damager) && root.isPlaying(player) && root.isStarted){
				if(damager.getName() == player.getName()){
					e.setCancelled(true);

					return;
				}
				
				e.setCancelled(false);

			}
		}
		
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onRespawn(PlayerRespawnEvent e){
		if(root.isPlaying(e.getPlayer()) && root.isStarted){
			System.out.println("teleporting " + e.getPlayer().getName());
			main.api.event(new DivinityTeleportEvent(e.getPlayer(), root.getRandomLocation()));
		}
	}
}
