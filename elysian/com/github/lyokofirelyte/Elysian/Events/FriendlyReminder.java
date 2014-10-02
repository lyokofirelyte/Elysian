package com.github.lyokofirelyte.Elysian.Events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.apache.commons.math3.util.Precision;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class FriendlyReminder implements Listener{
	
	private Elysian main;
	private ArrayList<EntityType> friendlies;
	
	public FriendlyReminder(Elysian i) {
		main = i;
		
		friendlies = new ArrayList<EntityType>();
		friendlies.add(EntityType.SHEEP);
		friendlies.add(EntityType.COW);
		friendlies.add(EntityType.CHICKEN);
		friendlies.add(EntityType.WOLF);
		friendlies.add(EntityType.OCELOT);
		friendlies.add(EntityType.HORSE);
		friendlies.add(EntityType.PIG);
	}
	
	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		main.getDivPlayer(e.getPlayer()).set(DPI.FR_FK_COOLDOWN, System.currentTimeMillis());
		main.getDivPlayer(e.getPlayer()).set(DPI.FR_CH_COOLDOWN, System.currentTimeMillis());
		main.getDivPlayer(e.getPlayer()).set(DPI.FR_CR_COOLDOWN, System.currentTimeMillis());
		main.getDivPlayer(e.getPlayer()).set(DPI.FR_TR_COOLDOWN, System.currentTimeMillis());
	}
	
	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		//nothing for now I think? O.o
	}
	
	/**
	 * Called every time an entity dies. Hoping to find a faster
	 * way to do this as I think it would get laggy during the
	 * mornings where there are a lot of mob deaths.
	 * @param e
	 */
	@EventHandler(ignoreCancelled = true)
	public void onFriendlyKill (EntityDeathEvent e) {
		if(friendlies.contains(e.getEntityType())) {
			if(e.getEntity().getKiller() instanceof Player) {
				DivinityPlayer dp = main.getDivPlayer(e.getEntity().getKiller());
				checkAndMessage(dp, "friendlyKill");
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onCreeperExplosion(EntityExplodeEvent e) {
		List<Entity> entities = e.getEntity().getNearbyEntities(10, 4, 10);
		for( Entity E: entities) {
			if(E.getType() == EntityType.PLAYER) {
				checkAndMessage(main.getDivPlayer((Player) E), "CreeperHole");
			}
		}
	}
	
	
	/**
	 * Holds both the crop replanting messages and the tree replanting messages
	 * @param e
	 */
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak (BlockBreakEvent e) {
		if(e.getBlock().getType().equals(Material.CROPS)) {
			checkAndMessage(main.getDivPlayer(e.getPlayer()), "CropReplanting");
		}
		
		if(e.getBlock().getType().equals(Material.LOG)) {
			if(check(main.getDivPlayer(e.getPlayer()), "TreeReplanting")) {
				Player p = e.getPlayer();
				Location l = e.getBlock().getLocation();
				String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
				float x = Precision.round(l.toVector().getBlockX(), -3);
				float z = Precision.round(l.toVector().getBlockZ(), -3);
				int y = l.toVector().getBlockY();
				File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
				List<String> results = new ArrayList<String>();
				
				if (file.exists()){
					YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
					results = new ArrayList<String>(yaml.getStringList("History." + p.getWorld().getName() + "." + y));
				}

				if (results.size() == 0){
					main.getDivPlayer(e.getPlayer()).s(msg("TreeReplanting"));
				}
			}
		}
	}
	
	private boolean check(DivinityPlayer dp, String EventType) {
		switch(EventType) {
		case "friendlyKill" : {
			if(dp.getBool(DPI.FR_FK_TOGGLE) && (dp.getLong(DPI.FR_FK_COOLDOWN) <= System.currentTimeMillis())) {
				return true;
			}
		}
		case "CreeperHole" : {
			if(dp.getBool(DPI.FR_CH_TOGGLE) && (dp.getLong(DPI.FR_CH_COOLDOWN) <= System.currentTimeMillis())) {
				return true;
			}
		}
		case "CropReplanting" : {
			if(dp.getBool(DPI.FR_CR_TOGGLE) && (dp.getLong(DPI.FR_CR_COOLDOWN) <= System.currentTimeMillis())) {
				return true;
			}
		}
		case "TreeReplanting" : {
			if(dp.getBool(DPI.FR_TR_TOGGLE) && (dp.getLong(DPI.FR_TR_COOLDOWN) <= System.currentTimeMillis())) {
				return true;
			}
		}
		default: return false;
		}
	}
	
	private void checkAndMessage(DivinityPlayer dp, String EventType) {
		main.getLogger().log(Level.INFO, "This is a test for the firing system of Friendly Reminder");
		switch(EventType) {
		case "friendlyKill" : {
			if(dp.getBool(DPI.FR_FK_TOGGLE) && (dp.getLong(DPI.FR_FK_COOLDOWN) <= System.currentTimeMillis())) {
				dp.s(msg(EventType));
				dp.set(DPI.FR_FK_COOLDOWN, System.currentTimeMillis() + 5 * 60000L);
			}
			break;
		}
		case "CreeperHole" : {
			if(dp.getBool(DPI.FR_CH_TOGGLE) && (dp.getLong(DPI.FR_CH_COOLDOWN) <= System.currentTimeMillis())) {
				dp.s(msg(EventType));
				dp.set(DPI.FR_CH_COOLDOWN, System.currentTimeMillis() + 5 * 60000L);
			}
			break;
		}
		case "CropReplanting" : {
			if(dp.getBool(DPI.FR_CR_TOGGLE) && (dp.getLong(DPI.FR_CR_COOLDOWN) <= System.currentTimeMillis())) {
				dp.s(msg(EventType));
				dp.set(DPI.FR_CR_COOLDOWN, System.currentTimeMillis() + 5 * 60000L);
			}
			break;
		}
		case "TreeReplanting" : {
			if(dp.getBool(DPI.FR_TR_TOGGLE) && (dp.getLong(DPI.FR_TR_COOLDOWN) <= System.currentTimeMillis())) {
				dp.s(msg(EventType));
				dp.set(DPI.FR_TR_COOLDOWN, System.currentTimeMillis() + 5 * 60000L);
			}
			break;
		}
		default: break;
		}
	}
	
	private String msg(String type) {
		int c = new Random().nextInt(4);
		String s;
		System.out.println("Switch: " + type+"|" + c);
		switch(type + "|" + c) {
		case "friendlyKill|0": s = "If you kill an animal please erm, 'Repopulate' them!";
		break;
		case "friendlyKill|1": s = "Please make sure to breed more if you kill animals!";
		break;
		case "friendlyKill|2": s = "Food is good, breeding is better! Remember to repopulate!";
		break;
		case "friendlyKill|3": s = "Don't forget to repopulate!";
		break;
		case "CreeperHole|0" : s = "Creeper problems? Make sure to fill that crater!";
		break;
		case "CreeperHole|1" : s = "Help the server stay gorgeous! Fill creeper holes!";
		break;
		case "CreeperHole|2" : s = "Explosive! Make sure to fill those holes!";
		break;
		case "CreeperHole|3" : s = "Did you know that creeper holes are refillable?";
		break;
		case "CropReplanting|0": s = "Have fun! But make sure to re-plant!";
		break;
		case "CropReplanting|1": s = "Make sure to put those seeds in the ground!";
		break;
		case "CropReplanting|2": s = "Those seeds go in the ground ya know!";
		break;
		case "CropReplanting|3": s = "Sharing is caring! But make sure to Re-plant!";
		break;
		case "TreeReplanting|0": s = "Nobody likes deforestation! Replant!";
		break;
		case "TreeReplanting|1": s = "Everyone likes trees! Replace those saplings!";
		break;
		case "TreeReplanting|2": s = "Saplings can grow trees! Shocking right!?";
		break;
		case "TreeReplanting|3": s = "Tree: I'll be back! Make it happen by replanting!";
		break;
		default: s = "Error! Please tell Staff this occured!";
		break;
		}
		return ChatColor.GREEN + s;
	}

	public void toggleFK(DivinityPlayer p) {
		p.set(DPI.FR_FK_TOGGLE, !(p.getBool(DPI.FR_FK_TOGGLE)));
	}
	
	public void toggleCH(DivinityPlayer p) {
		p.set(DPI.FR_CH_TOGGLE, !(p.getBool(DPI.FR_CH_TOGGLE)));
	}
	
	public void toggleCR(DivinityPlayer p) {
		p.set(DPI.FR_CR_TOGGLE, !(p.getBool(DPI.FR_CR_TOGGLE)));
	}
	
	public void toggleTR(DivinityPlayer p) {
		p.set(DPI.FR_TR_TOGGLE, !(p.getBool(DPI.FR_TR_TOGGLE)));
	}
	
	public String getStatus(DivinityPlayer p, String toggle) {
		String E = "&2Enabled", D = "&4Disabled";
		switch(toggle.toLowerCase()) {
		case "fk" : if(p.getBool(DPI.FR_FK_TOGGLE))
			return E;
		return D;
		case "ch" : if(p.getBool(DPI.FR_CH_TOGGLE))
			return E;
		return D;
		case "cr" : if(p.getBool(DPI.FR_CR_TOGGLE))
			return E;
		return D;
		case "tr" : if(p.getBool(DPI.FR_TR_TOGGLE))
			return E;
		return D;
		}
		return null;
	}

}
