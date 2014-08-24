package com.github.lyokofirelyte.Elysian.MMO;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SkyBlade;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SuperBreaker;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.TreeFeller;

public class ElyMMO implements Listener {
	
	public Elysian main;
	public TreeFeller treeFeller;
	public SuperBreaker superBreaker;
	public SkyBlade skyBlade;
	
	public ElyMMO(Elysian i) {
		main = i;
	}
	
	public boolean isHolding(Player p, String item){
		return p.getItemInHand() != null && p.getItemInHand().getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	public boolean isType(Location l, String item){
		return l.getBlock() != null && l.getBlock().getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	public boolean isType(Block b, String item){
		return b != null && b.getType().toString().toLowerCase().contains(item.toLowerCase());
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEntityEvent e){
		
		if (e.isCancelled()){
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		if (isHolding(p, "_sword") && dp.getBool(MMO.IS_SKY_BLADING)){
			skyBlade.l(p, dp);
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR){
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		Block b = e.getClickedBlock() != null ? e.getClickedBlock() : null;
		
		switch (e.getAction()){
		
			default: break;
			
			case RIGHT_CLICK_AIR:
				
				if (isHolding(p, "_axe")){
					treeFeller.r(p, dp);
				}
				
				if (isHolding(p, "_pickaxe")){
					superBreaker.r(p, dp);
				}
				
				if (isHolding(p, "_sword")){
					skyBlade.r(p, dp);
				}
				
				// nothing suspicious move along
				if (p.getItemInHand().getType().equals(Material.CAKE) || p.getItemInHand().getType().equals(Material.CAKE_BLOCK)){
					dp.err("This cake is a lie, you should ask for your money back.");
					p.getWorld().dropItem(p.getLocation(), p.getItemInHand());
					p.setItemInHand(new ItemStack(Material.AIR));
					p.playSound(p.getLocation(), Sound.CLICK, 5F, 5F);
				}
				
			break;
			
			case LEFT_CLICK_BLOCK:
				
				if (isType(e.getClickedBlock(), "log") && isHolding(p, "_axe") && dp.getBool(MMO.IS_TREE_FELLING)){
					treeFeller.l(p, dp, b);
				}
				
				if (isHolding(p, "_pickaxe") && dp.getBool(MMO.IS_SUPER_BREAKING)){
					superBreaker.l(p, dp, b);
				}
				
			break;
		}
	}
}