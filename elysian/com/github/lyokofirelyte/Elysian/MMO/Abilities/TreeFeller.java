package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Storage.DRF;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.ElySkill;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class TreeFeller extends ElyMMO implements Listener {

	public TreeFeller(Elysian i) {
		super(i);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.isCancelled() && e.getAction() != Action.RIGHT_CLICK_AIR){
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		switch (e.getAction()){
		
			default: break;
			
			case RIGHT_CLICK_AIR:

				if (p.getItemInHand() != null && p.getItemInHand().getType().toString().toLowerCase().contains("_axe")){
					dp.set(MMO.IS_TREE_FELLING, !dp.getBool(MMO.IS_TREE_FELLING));
					dp.s("Tree feller " + (dp.getBool(MMO.IS_TREE_FELLING) + "").replace("true", "&aactive - left click a tree!").replace("false", "&cinactive."));
				}
				
			break;
			
			case LEFT_CLICK_BLOCK:
				
				if (e.getClickedBlock().getType().toString().toLowerCase().contains("log") && p.getItemInHand() != null && p.getItemInHand().getType().toString().toLowerCase().contains("_axe") && dp.getBool(MMO.IS_TREE_FELLING)){
					String result = main.pro.isInAnyRegion(e.getClickedBlock().getLocation());
					if (main.pro.hasFlag(result, DRF.BLOCK_BREAK)){
						if (!main.pro.hasRegionPerms(e.getPlayer(), result)){
							dp.err("No permissions for this area!");
							return;
						}
					}
					if (dp.getLong(MMO.TREE_FELLER_CD) <= System.currentTimeMillis()){
						chop(p, dp, e.getClickedBlock().getLocation());
						dp.set(MMO.TREE_FELLER_CD, System.currentTimeMillis() + (180000 - (dp.getInt(ElySkill.WOODCUTTING)*1000)));
					} else {
						dp.err("Tree feller on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.TREE_FELLER_CD))/1000)*-1 + " &c&oseconds remain.");
					}
				}
				
			break;
		}
	}
	
	private void chop(Player p, DivinityPlayer dp, Location l){
		
		boolean skyOpen = true;
		int bottom = l.getBlockY()-1;
		int top = 0;
		
		Map<Integer, List<Block>> blocks = new HashMap<Integer, List<Block>>();
		
		for (int i = l.getBlockY(); i < 256; i++){
			Location testLoc = new Location(l.getWorld(), l.getX(), i, l.getZ());
			if (!testLoc.getBlock().getType().toString().toLowerCase().contains("log") && !testLoc.getBlock().getType().toString().toLowerCase().contains("leaves")){
				top = top == 0 ? i : top;
				if (!testLoc.getBlock().getType().equals(Material.AIR)){
					skyOpen = false;
					break;
				}
			} else if (testLoc.getBlock().getType().toString().toLowerCase().contains("log") || testLoc.getBlock().getType().toString().toLowerCase().contains("leaves")){
				List<Block> b = new ArrayList<Block>();
				b.add(testLoc.getBlock());
				blocks.put(i, b);
				for (Location radiusLoc : main.api.divUtils.circle(testLoc, 7, 1, false, false, 0)){
					if (radiusLoc.getBlock().getType().toString().toLowerCase().contains("log") || radiusLoc.getBlock().getType().toString().toLowerCase().contains("leaves")){
						blocks.get(i).add(radiusLoc.getBlock());
					}
				}
			}
		}
		
		p.getWorld().playSound(p.getLocation(), Sound.EXPLODE, 5F, 5F);
		
		if (skyOpen){
			dp.s("The sky is clear! Wooosssh!");
			p.teleport(new Location(l.getWorld(), l.getX(), top+1, l.getZ(), p.getLocation().getYaw(), 90));
			p.setVelocity(new Vector(0, 3, 0));
			main.api.schedule(this, "tpPlayer", 30L, "tpPlayer", p, new Location(l.getWorld(), l.getX(), top + 10, l.getZ(), p.getLocation().getYaw(), 90));
			main.api.repeat(this, "checkPlayer", 35L, 1L, "treeCheck" + p.getName(), p, top, bottom, blocks);
			main.api.schedule(this, "checkPlayerSaftey", 400L, "treeCheck2", p);
		} else {
			dp.s("No room for flight attack! Arming explosives!");
			for (int i : blocks.keySet()){
				for (Block b : blocks.get(i)){
					b.breakNaturally();
				}
			}
			dp.set(MMO.IS_TREE_FELLING, false);
		}
	}
	
	public void tpPlayer(Player p, Location l){
		p.teleport(l);
		p.setVelocity(new Vector(0, -1, 0));
	}
	
	public void checkPlayerSaftey(Player p){
		try {
			main.api.cancelTask("treeCheck" + p.getName());
		} catch (Exception e){}
	}
	
	@SuppressWarnings("deprecation")
	public void checkPlayer(Player p, int top, int bottom, Map<Integer, List<Block>> blocks){
		
		int y = p.getLocation().getBlockY();

		if (blocks.containsKey(y-1)){
			for (Block b : blocks.get(y-1)){
				if (b.getType().toString().toLowerCase().contains("log")){
					p.playEffect(b.getLocation(), Effect.STEP_SOUND, b.getTypeId());
				}
				b.breakNaturally();
			}
		}
		
		if (y <= bottom+2){
			main.api.cancelTask("treeCheck" + p.getName());
			main.s(p, "You showed that tree!");
			main.api.getDivPlayer(p).set(MMO.IS_TREE_FELLING, false);
			for (Location l : main.api.divUtils.circle(p.getLocation(), 3, 1, true, false, 0)){
				l.getWorld().playEffect(l, Effect.ENDER_SIGNAL, 2);
			}
		}
	}
}