package com.github.lyokofirelyte.Elysian.Events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyMobs implements Listener {
	
	private Elysian main;
	
	public ElyMobs(Elysian i){
		main = i;
	}
	
	private String[] s(String s, String t){
		return new String[]{s, t};
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e){
	
		if (e.getEntity() instanceof LivingEntity && e.getEntity() instanceof Player == false){
			
			LivingEntity entity =  ((LivingEntity)e.getEntity());
			double hp = entity.getHealth();
			double percent = (hp/entity.getMaxHealth())*100;
			String[] mult = percent >= 80 ? s("&a", "5") : percent >= 60 ? s("&a", "4") : percent >= 40 ? s("&e", "3") : percent >= 20 ? s("&c", "2") : s("&4", "1");
			entity.setCustomName(main.AS(mult[0] + StringUtils.repeat("\u2744", Integer.parseInt(mult[1]))));
			entity.setCustomNameVisible(true);
			
		} else if (e.getEntity() instanceof Player){
			
			Player p = (Player)e.getEntity();
			
			if (e.getDamager() instanceof Player){
				
				Player damager = (Player)e.getDamager();
				
				if (!main.api.getDivPlayer(damager).getDPI(DPI.DUEL_PARTNER).equals(p.getName())){
					e.setCancelled(true);
				}
				
			} else {
				main.api.getDivPlayer((Player)e.getEntity()).setDPI(DPI.IN_COMBAT, true);
			}
		}
	}

	@EventHandler
	public void onMobDeath(final EntityDeathEvent e){
		
		if (e.getEntity().getWorld().getName().equals("world")){
			
			if (e.getEntity() instanceof Monster && e.getEntity().getKiller() instanceof Player){
				
				new Thread(new Runnable(){ public void run(){
					for (String loc : main.api.divUtils.strCircle(e.getEntity().getLocation(), 15, 10, false, false, 0)){
						String[] l = loc.split(" ");
						Location location = new Location(Bukkit.getWorld(l[0]), Double.parseDouble(l[1]), Double.parseDouble(l[2]), Double.parseDouble(l[3]));
						if (location.getBlock() != null && location.getBlock().getType().equals(Material.MOB_SPAWNER)){
							return;
						}
					}
					payOut((Player)e.getEntity().getKiller());
				}}).start();
				
			} else if (e.getEntity() instanceof Player && e.getEntity().getKiller() instanceof Player){
				
				Player dead = (Player)e.getEntity();
				Map<String, String> replacements = new HashMap<String, String>();
				
				DivinityPlayer killer = main.api.getDivPlayer((Player)e.getEntity().getKiller());
				DivinityPlayer deadDP = main.api.getDivPlayer((Player)e.getEntity());
				
				List<String> wins = killer.getListDPI(DPI.DUEL_WINS);
				
				for (String win : wins){
					if (win.split(" ")[0].equals(dead.getName())){
						replacements.put(win, win.replace(win.split(" ")[1], (Integer.parseInt(win.split(" ")[1])+1) + ""));
					}
				}
				
				if (replacements.size() > 0){
					for (String replace : replacements.keySet()){
						wins.remove(replace);
						wins.add(replacements.get(replace));
					}
				} else {
					wins.add(dead.getName() + " " + 1);
				}
				
				main.s(main.getPlayer(killer.name()), "Well done! You've defeated " + dead.getDisplayName() + "&b.");
				main.s(dead, "&c&oBetter luck next time...");
				
				killer.setDPI(DPI.DUEL_PARTNER, "none");
				deadDP.setDPI(DPI.DUEL_PARTNER, "killed");
				deadDP.setDPI(DPI.BACKUP_INVENTORY, dead.getInventory().getContents());
				deadDP.setDPI(DPI.DEATH_ARMOR, dead.getInventory().getArmorContents());
				
				if (killer.getBoolDPI(DPI.IS_DUEL_SAFE)){
					e.setDroppedExp(0);
					e.getDrops().clear();
				}
				
				DivinityUtils.bc(dead.getDisplayName() + " &e&owas brutally murdered in a duel with " + killer.getDPI(DPI.DISPLAY_NAME));
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		
		e.setDeathMessage(null);
		
		if (e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player){
			return;
		}

		Vector v = e.getEntity().getLocation().toVector();
		Player p = e.getEntity();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		
		dp.getListDPI(DPI.PREVIOUS_LOCATIONS).add(p.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ());

		if (e.getEntity().getLastDamageCause() != null){
			DivinityUtils.bc(dp.getDPI(DPI.DISPLAY_NAME) + " &e&odied due to &6&o" + p.getLastDamageCause().getCause().name().toLowerCase());
		} else {
			DivinityUtils.bc(dp.getDPI(DPI.DISPLAY_NAME) + " &e&odied due to unknown causes.");
		}
		
		if (dp.getDPI(DPI.DEATH_CHEST_INV).equals("none")){
			dp.setDPI(DPI.DEATH_CHEST_INV, p.getInventory().getContents());
			dp.setDPI(DPI.DEATH_ARMOR, p.getInventory().getArmorContents());
			dp.setDPI(DPI.DEATH_CHEST_LOC, p.getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ());
			p.getLocation().getBlock().setType(Material.CHEST);
			e.getDrops().clear();
			main.s(e.getEntity(), "&7&oYour items are in a chest at your death location.");
		}
	}
	
	@EventHandler
	public void onExp(PlayerExpChangeEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (!dp.getBoolDPI(DPI.EXP_DEPOSIT)){
			if (e.getPlayer().getWorld().getName().equals("world")){
				dp.setDPI(DPI.EXP, dp.getIntDPI(DPI.EXP) + e.getAmount());
				e.setAmount(0);
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (dp.getDPI(DPI.DUEL_PARTNER).equals("killed")){
			dp.setDPI(DPI.DUEL_PARTNER, "none");
			if (dp.getBoolDPI(DPI.IS_DUEL_SAFE)){
				e.getPlayer().getInventory().setContents(dp.getStackDPI(DPI.BACKUP_INVENTORY));
				e.getPlayer().getInventory().setArmorContents(dp.getStackDPI(DPI.DEATH_ARMOR));
				main.s(e.getPlayer(), "This duel was safe. Inventory restored.");
			}
		}
		
		if (dp.getBoolDPI(DPI.DEATHLOCS_TOGGLE)){
			main.s(e.getPlayer(), "&7&oYou died at: &6&o" + dp.getDPI(DPI.DEATH_CHEST_LOC).replace(" ", "&7, "));
		}
	}
	
	@EventHandler
	public void onPortal(EntityCreatePortalEvent e){

		if (e.getEntity().getType() == EntityType.ENDER_DRAGON){		
			e.setCancelled(true);
		}
	}
	
	private void payOut(Player p){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		Random rand = new Random();
		List<String> perms = dp.getListDPI(DPI.PERMS);
		
		int mult = perms.contains("wa.rank.emperor") ? 3 : perms.contains("wa.rank.disctrictman") ? 2 : 1;
		int randomMoneyAmount = rand.nextInt(120) + 1;
		int randomNumber = rand.nextInt(4) + 1;
		
		dp.setDPI(DPI.MOB_MONEY, dp.getIntDPI(DPI.MOB_MONEY) + (randomNumber == 4 ? randomMoneyAmount*mult : 5));
	}
	
	@DivCommand(aliases = {"exp", "xp"}, help = "/exp <take> <amount>", desc = "Elysian EXP Storing System", player = true)
	public void onExp(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		int amt = args.length >= 2 && main.api.divUtils.isInteger(args[1]) ? Integer.parseInt(args[1]) : 0;
		
		if (args.length == 0){
			main.s(p, "Stored XP: &6" + dp.getIntDPI(DPI.EXP) + "&b.");
			main.s(p, "&7&o825 = level 30. Only take out what you need, as you can't put it back!");
		} else if (args.length == 2){
		
			if (args[0].equals("take")){
				
				if (dp.getIntDPI(DPI.EXP) >= amt){
					dp.setDPI(DPI.EXP, dp.getIntDPI(DPI.EXP)-amt);
					dp.setDPI(DPI.EXP_DEPOSIT, true);
					p.giveExp(amt);
					dp.setDPI(DPI.EXP_DEPOSIT, false);
				} else {
					main.s(p, "&c&oNot enough stored xp!");
				}
				
			} else {
				main.s(p, main.help("exp", this));
			}
			
		} else {
			main.s(p, main.help("exp", this));
		}
	}
	
	@DivCommand(aliases = {"duel"}, help = "/duel <player> [safe?]", desc = "Elysian Duel System", player = true)
	public void onDuel(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		String safe = args.length == 2 ? "&asafe" : "&cdangerous";
		
		if (args.length == 0){
			if (!dp.getDPI(DPI.DUEL_INVITE).equals("none")){
				if (main.isOnline(dp.getDPI(DPI.DUEL_INVITE))){
					dp.setDPI(DPI.DUEL_PARTNER, dp.getDPI(DPI.DUEL_INVITE));
					main.s(p, "ACCEPTED DUEL! BEGIN!");
					main.s(main.getPlayer(dp.getDPI(DPI.DUEL_INVITE)), "ACCEPTED DUEL! BEGIN!");
					main.matchDivPlayer(dp.getDPI(DPI.DUEL_INVITE)).setDPI(DPI.DUEL_PARTNER, p.getName());
					main.matchDivPlayer(dp.getDPI(DPI.DUEL_INVITE)).setDPI(DPI.DUEL_INVITE, "none");
					dp.setDPI(DPI.DUEL_INVITE, "none");
				} else {
					main.s(p, "&c&oThey've logged off.");
				}
			}
		} else if (main.doesPartialPlayerExist(args[0])){
			if (main.isOnline(args[0])){
				main.s(main.getPlayer(args[0]), "You are invited to a duel from " + p.getDisplayName() + "&b.");
				main.s(main.getPlayer(args[0]), "This is a " +  safe + " &bduel. Type /duel to accept.");
				main.matchDivPlayer(args[0]).setDPI(DPI.DUEL_INVITE, p.getName());
				main.matchDivPlayer(args[0]).setDPI(DPI.IS_DUEL_SAFE, args.length == 2 ? true : false);
				dp.setDPI(DPI.IS_DUEL_SAFE, args.length == 2 ? true : false);
				main.s(p, "Sent!");
			} else {
				main.s(p, "playerNotFound");
			}
		} else {
			main.s(p, "playerNotFound");
		}
	}
}