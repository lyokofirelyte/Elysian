package com.github.lyokofirelyte.Elysian.MMO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.SkillExpGainEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Divinity.Storage.ElySkill;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.HolyMackerel;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.LifeForce;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SkyBlade;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SuperBreaker;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.TreeFeller;

public class ElyMMO extends HashMap<Material, MXP> implements Listener {
	
	private static final long serialVersionUID = 1L;
	
	public Elysian main;
	public TreeFeller treeFeller;
	public SuperBreaker superBreaker;
	public SkyBlade skyBlade;
	public LifeForce life;
	public HolyMackerel holy;
	public ElyPatrol patrols;
	
	public Map<String, List<Item>> noPickup = new HashMap<>();
	
	public ElyMMO(Elysian i) {
		main = i;
		fillMap();
	}
	
	//Material -> Skill -> [XP, LevelRequirement]
	public void fillMap(){
		sm(Material.LOG, ElySkill.WOODCUTTING, 125, 0);
		sm(Material.LOG_2, ElySkill.WOODCUTTING, 138, 15);
		sm(Material.LEAVES, ElySkill.WOODCUTTING, 168, 30);
		sm(Material.LEAVES_2, ElySkill.WOODCUTTING, 200, 45);
		
		sm(Material.RAW_FISH, ElySkill.FISHING, 0, 200);
		
		sm(Material.STONE, ElySkill.MINING, 15, 0);
		sm(Material.NETHERRACK, ElySkill.MINING, 15, 0);
		sm(Material.HARD_CLAY, ElySkill.MINING, 15, 0);
		sm(Material.STAINED_CLAY, ElySkill.MINING, 15, 0);
		sm(Material.NETHER_BRICK, ElySkill.MINING, 17, 5);
		sm(Material.ENDER_STONE, ElySkill.MINING, 17, 5);
		sm(Material.ICE, ElySkill.MINING, 17, 5);
		sm(Material.PACKED_ICE, ElySkill.MINING, 18, 7);
		sm(Material.QUARTZ_ORE, ElySkill.MINING, 20, 10);
		sm(Material.IRON_ORE, ElySkill.MINING, 35, 15);
		sm(Material.COAL_ORE, ElySkill.MINING, 50, 30);
		sm(Material.REDSTONE_ORE, ElySkill.MINING, 65, 40);
		sm(Material.GOLD_ORE, ElySkill.MINING, 80, 55);
		sm(Material.OBSIDIAN, ElySkill.MINING, 85, 60);
		sm(Material.MYCEL, ElySkill.MINING, 90, 65);
		sm(Material.LAPIS_ORE, ElySkill.MINING, 95, 70);
		sm(Material.DIAMOND_ORE, ElySkill.MINING, 155, 85);
		sm(Material.EMERALD_ORE, ElySkill.MINING, 180, 90);
		
		sm(Material.DIRT, ElySkill.DIGGING, 12, 0);
		sm(Material.GRASS, ElySkill.DIGGING, 15, 5);
		sm(Material.SAND, ElySkill.DIGGING, 20, 10);
		sm(Material.SNOW_BLOCK, ElySkill.DIGGING, 25, 20);
		sm(Material.SOUL_SAND, ElySkill.DIGGING, 30, 25);
		sm(Material.CLAY, ElySkill.DIGGING, 35, 40);
		sm(Material.GRAVEL, ElySkill.DIGGING, 50, 55);
		sm(Material.GLOWSTONE, ElySkill.DIGGING, 70, 60);
		
		sm(Material.LONG_GRASS, ElySkill.FARMING, 15, 0);
		sm(Material.CROPS, ElySkill.FARMING, 17, 0);
		sm(Material.PUMPKIN, ElySkill.FARMING, 38, 10);
		sm(Material.MELON_BLOCK, ElySkill.FARMING, 50, 30);
		sm(Material.SUGAR_CANE_BLOCK, ElySkill.FARMING, 65, 40);
		sm(Material.COCOA, ElySkill.FARMING, 80, 55);
		sm(Material.NETHER_WARTS, ElySkill.FARMING, 85, 60);
		sm(Material.CARROT, ElySkill.FARMING, 95, 70);
		sm(Material.POTATO, ElySkill.FARMING, 125, 85);
		sm(Material.CACTUS, ElySkill.FARMING, 150, 90);
		sm(Material.RED_ROSE, ElySkill.FARMING, 200, 95);
		sm(Material.RED_MUSHROOM, ElySkill.FARMING, 250, 96);
		sm(Material.BROWN_MUSHROOM, ElySkill.FARMING, 250, 96);
		sm(Material.VINE, ElySkill.FARMING, 300, 97);
		sm(Material.WATER_LILY, ElySkill.FARMING, 325, 98);
		
		sm(Material.ARROW, ElySkill.CRAFTING, 5, 0);
		sm(Material.STICK, ElySkill.CRAFTING, 5, 0);
		sm(Material.WORKBENCH, ElySkill.CRAFTING, 10, 0);
		sm(Material.FURNACE, ElySkill.CRAFTING, 15, 5);
		sm(Material.WOOD, ElySkill.CRAFTING, 20, 10);
		sm(Material.COBBLESTONE_STAIRS, ElySkill.CRAFTING, 33, 20);
		sm(Material.MELON_BLOCK, ElySkill.CRAFTING, 50, 25);
		sm(Material.IRON_BLOCK, ElySkill.CRAFTING, 65, 27);
		sm(Material.COAL_BLOCK, ElySkill.CRAFTING, 90, 30);
		sm(Material.REDSTONE_BLOCK, ElySkill.CRAFTING, 93, 35);
		sm(Material.LAPIS_BLOCK, ElySkill.CRAFTING, 95, 45);
		sm(Material.IRON_SWORD, ElySkill.CRAFTING, 100, 50);
		sm(Material.DIAMOND_SWORD, ElySkill.CRAFTING, 116, 55);
		sm(Material.DIAMOND_BLOCK, ElySkill.CRAFTING, 150, 65);
		sm(Material.EMERALD_BLOCK, ElySkill.CRAFTING, 200, 70);
		sm(Material.CAKE, ElySkill.CRAFTING, 220, 75);
		sm(Material.ANVIL, ElySkill.CRAFTING, 350, 85);
		sm(Material.BEACON, ElySkill.CRAFTING, 375, 90);
		
		tool(Material.WOOD_SWORD, ElySkill.ATTACK, 0);
		tool(Material.STONE_SWORD, ElySkill.ATTACK, 15);
		tool(Material.IRON_SWORD, ElySkill.ATTACK, 25);
		tool(Material.GOLD_SWORD, ElySkill.ATTACK, 30);
		tool(Material.DIAMOND_SWORD, ElySkill.ATTACK, 45);
		
		tool(Material.WOOD_AXE, ElySkill.ATTACK, 50);
		tool(Material.STONE_AXE, ElySkill.ATTACK, 65);
		tool(Material.IRON_AXE, ElySkill.ATTACK, 75);
		tool(Material.GOLD_AXE, ElySkill.ATTACK, 80);
		tool(Material.DIAMOND_AXE, ElySkill.ATTACK, 92);
		
		tool(Material.WOOD_PICKAXE, ElySkill.ATTACK, 59);
		tool(Material.STONE_PICKAXE, ElySkill.ATTACK, 69);
		tool(Material.IRON_PICKAXE, ElySkill.ATTACK, 79);
		tool(Material.GOLD_PICKAXE, ElySkill.ATTACK, 89);
		tool(Material.DIAMOND_PICKAXE, ElySkill.ATTACK, 99);
		
		tool(Material.WOOD_SPADE, ElySkill.ATTACK, 0);
		tool(Material.STONE_SPADE, ElySkill.ATTACK, 5);
		tool(Material.IRON_SPADE, ElySkill.ATTACK, 10);
		tool(Material.GOLD_SPADE, ElySkill.ATTACK, 20);
		tool(Material.DIAMOND_SPADE, ElySkill.ATTACK, 25);
		
		tool(Material.WOOD_AXE, ElySkill.WOODCUTTING, 0);
		tool(Material.STONE_AXE, ElySkill.WOODCUTTING, 15);
		tool(Material.IRON_AXE, ElySkill.WOODCUTTING, 25);
		tool(Material.GOLD_AXE, ElySkill.WOODCUTTING, 30);
		tool(Material.DIAMOND_AXE, ElySkill.WOODCUTTING, 45);
		
		tool(Material.WOOD_PICKAXE, ElySkill.MINING, 0);
		tool(Material.STONE_PICKAXE, ElySkill.MINING, 15);
		tool(Material.IRON_PICKAXE, ElySkill.MINING, 25);
		tool(Material.GOLD_PICKAXE, ElySkill.MINING, 30);
		tool(Material.DIAMOND_PICKAXE, ElySkill.MINING, 45);
		
		tool(Material.WOOD_SPADE, ElySkill.DIGGING, 0);
		tool(Material.STONE_SPADE, ElySkill.DIGGING, 15);
		tool(Material.IRON_SPADE, ElySkill.DIGGING, 25);
		tool(Material.GOLD_SPADE, ElySkill.DIGGING, 30);
		tool(Material.DIAMOND_SPADE, ElySkill.DIGGING, 45);
		
		tool(Material.LEATHER_HELMET, ElySkill.RESISTANCE, 0);
		tool(Material.CHAINMAIL_HELMET, ElySkill.RESISTANCE, 15);
		tool(Material.IRON_HELMET, ElySkill.RESISTANCE, 25);
		tool(Material.GOLD_HELMET, ElySkill.RESISTANCE, 30);
		tool(Material.DIAMOND_HELMET, ElySkill.RESISTANCE, 45);
	}
	
	private void tool(Material tool, ElySkill skill, int level){
		if (containsKey(tool)){
			get(tool).addTool(skill, level, tool);
		} else {
			put(tool, new MXP(tool, skill, 0, 0));
			get(tool).addTool(skill, level, tool);
		}
	}
	
	private void sm(Material m, ElySkill s, int x, int l){
		if (containsKey(m)){
			get(m).addSkill(s, l, x);
		} else {
			put(m, new MXP(m, s, l, x));
		}
	}
	
	private Map<Material, Integer> getTools(ElySkill skill){
		
		Map<Material, Integer> map = new HashMap<>();
		
		for (MXP m : values()){
			for (Material mat : m.toolReqs.keySet()){
				for (ElySkill s : m.toolReqs.get(mat).keySet()){
					if (s.equals(skill)){
						map.put(mat, m.toolReqs.get(mat).get(s));
					}
				}
			}
		}
		
		return map;
	}
	
	private Boolean[] canGiveXp(Player p, Material m, ElySkill s, Material itemInHand, String neededItem){
		
		Boolean[] results = new Boolean[]{false, true};
		
		if (containsKey(m) && get(m).hasSkill(s)){
			if (get(m).hasLevel(s, main.api.getDivPlayer(p).getLevel(s))){
				if (neededItem.equals("none") || isHolding(p, neededItem)){
					results[0] = true;
				}
			}
		}
		
		if (containsKey(itemInHand)){
			if (!get(itemInHand).canUseTool(s, itemInHand, main.api.getDivPlayer(p).getLevel(s))){
				results[1] = false;
			}
		}

		return results;
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFall(EntityDamageEvent e){
		
		if (e.getCause() == DamageCause.FALL && e.getEntity() instanceof Player){
			if (((Player)e.getEntity()).getHealth() > 0){
				main.api.event(new SkillExpGainEvent((Player)e.getEntity(), ElySkill.ENDURANCE, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
				e.setDamage(e.getDamage() - (e.getDamage()*((main.api.getDivPlayer((Player)e.getEntity()).getLevel(ElySkill.ENDURANCE)*.4)/100)));
			}
		}
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onMob(EntityDamageByEntityEvent e){
		
		if (e.getEntity() instanceof Player == false && e.getDamager() instanceof Player){
			
			Player p = (Player) e.getDamager();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
				
				switch (p.getItemInHand().getType()){
				
					case STICK: 
						main.api.event(new SkillExpGainEvent(p, ElySkill.FENCING, Integer.parseInt(Math.round(e.getDamage()*7) + "")));
						e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.FENCING)*.8)/100)));
					break;
					
					default: 
						
						e.setDamage(e.getDamage() + (e.getDamage()*((dp.getLevel(ElySkill.ATTACK)*.4)/100)));
						main.api.event(new SkillExpGainEvent(p, ElySkill.ATTACK, Integer.parseInt(Math.round(e.getDamage()*5) + "")));
						
					break;
				}
				
			} else {
				main.api.event(new SkillExpGainEvent(p, ElySkill.ATTACK, Integer.parseInt(Math.round(e.getDamage()*3) + "")));
			}
			
		} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Player == false){
			
			DivinityPlayer dp = main.api.getDivPlayer((Player) e.getEntity());
			main.api.event(new SkillExpGainEvent((Player)e.getEntity(), ElySkill.RESISTANCE, Integer.parseInt(Math.round(e.getDamage()*7) + "")));
			e.setDamage(e.getDamage() - (e.getDamage()*((dp.getLevel(ElySkill.RESISTANCE)*.4)/100)));
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e){
		
		if (e.getWhoClicked() instanceof Player){
			
			Player p = (Player) e.getWhoClicked();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (canGiveXp(p, e.getCurrentItem().getType(), ElySkill.CRAFTING, Material.AIR, "none")[0]){
				main.api.event(new SkillExpGainEvent((Player) e.getWhoClicked(), ElySkill.CRAFTING, get(e.getCurrentItem().getType()).getXP(ElySkill.CRAFTING)*e.getCurrentItem().getAmount()));
				if (new Random().nextInt(100) <= main.api.getDivPlayer(p).getLevel(ElySkill.CRAFTING)*0.2){
					if (dp.getLong(DPI.CRAFT_COOLDOWN) <= System.currentTimeMillis()){
						p.getInventory().addItem(e.getCurrentItem());
						main.s(p, "Extra item crafted! Your current chance is &6" + main.api.getDivPlayer(p).getLevel(ElySkill.CRAFTING)*0.2 + "%&b.");
						dp.set(DPI.CRAFT_COOLDOWN, System.currentTimeMillis() + 7200000L);
					} else {
						dp.err("Extra item not crafted - cooldown of " + ((System.currentTimeMillis() - (dp.getLong(DPI.CRAFT_COOLDOWN))/1000)/60)*-1 + " minutes remain.");
					}
				}
			}
		}
	}
	
	private String getDesc(ElySkill skill){
		
		switch (skill){
		
			default: return "&c&oNo skill desc found.";
		
			case WOODCUTTING: return "&6Chop logs to increase your WC level!";
			case MINING: return "&6Mine various ores of your level to increase!";
			case ATTACK: return "&6Murder mobs with a weapon of your level!";
			case FENCING: return "&6Bash mob's faces in with a stick! It's fun I swear!";
			case DIGGING: return "&6It's like mining except with dirt and snow.";
			case ARCHERY: return "&6Shoot stuff with a bow.";
			case CRAFTING: return "&6Craft some items to increase this level.";
			case VAMPYRISM: return "&6I'm still coming up with ideas for this one.";
			case RESISTANCE: return "&6Take a lot of damage - it'll make you take less as you level!";
			case ENDURANCE: return "&6JUMP OFF OF CLIFFS, BUT DON'T DIE!\n&6This decreases fall damage as you level.";
			case BUILDING: return "&6You just place stuff. Pretty easy. What, you want a medal or something?";
			case FARMING: return "&6The best skill to get 99 in. Tear down crops.";
			case PATROL: return "&6Hunt or skill with a group of people and share the XP!";
			case FISHING: return "&6Just fish stuff! :)";
		}
	}
	
	private String getPerks(ElySkill skill){
		
		switch (skill){
		
			default: return "&c&oNo skill perk found.";
		
			case WOODCUTTING: return "&bLevel 10: &6TREE FELLER (right-click axe)\n&7&oInstantly break an entire tree.\n&7&oEvery level decreases cooldown by 1 second.";
			case MINING: return "&bLevel 10: &6SUPER BREAKER (right-click pick)\n&7&oObtain max mining speed.\n&7&oEvery level decreases cooldown by 1 second.";
			case ATTACK: return "&bLevel 10: &6SKY BLADE (right-click sword)\n&7&oAn AOE monster attack.\n&7&oEvery level decreases cooldown by 1 second.\n&bExtra damage increase (0.3%) per level.";
			case FENCING: return "&bExtra damage increase (0.8%) per level.";
			case DIGGING: return "&6Level 10: &6TURBO DRILL (right-click spade)\n&7&oEvery level decreases cooldown by 1 second.";
			case ARCHERY: return "&6---- PERK COMING SOON ----";
			case CRAFTING: return "&60.2% chance per level to craft an extra item.\n&7&o2 hour cooldown on success.";
			case VAMPYRISM: return "&6I'm still coming up with ideas for this one.\n&7&o2spooky4me";
			case RESISTANCE: return "&60.4% less damage taken per level.";
			case ENDURANCE: return "&60.4% less fall damage taken per level.";
			case BUILDING: return "&6You literally get nothing for leveling this skill. Nothing.";
			case FARMING: return "&bLevel 10: &6LIFE FORCE (right-click sapling)\n&7&oPlants a random tree.\n&7&oEvery level decreases cooldown by 1 second.";
			case PATROL: return "&6More Shop Options";
			case FISHING: return "&bLevel 10: &6HOLY MACKEREL! (right-click rod)\n&7&oWhip up a crazy fish-storm!\n&7&oThe cooldown for this does not change as you level.";
		}
	}
	
	@DivCommand(name = "Stats", aliases = {"stats", "gstats"}, desc = "MMO Stat Viewer", help = "/stats <player> or /gstats <skill/total>", player = true, min = 1)
	public void onStats(Player p, String[] args, String cmd){
		
		if (cmd.equals("gstats")){
			gStats(p, args[0]);
		} else {
		
			String snow = "&3\u2744 ";
			
			if (main.doesPartialPlayerExist(args[0])){
				
				DivinityPlayer dp = main.matchDivPlayer(args[0]);
				JSONChatMessage msg = new JSONChatMessage("", null, null);

				for (ElySkill skill : ElySkill.values()){
					
					msg = new JSONChatMessage("", null, null);
					int lvl = dp.getLevel(skill);
					int xp = dp.getXP(skill);
					int neededXp = dp.getNeededXP(skill);
					
					JSONChatExtra extra = new JSONChatExtra(main.AS(snow + " &b" + skill.s()));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&3" + skill.s() + "\n&f----- " + snow + "&f-----\n&bLVL: &6" + lvl + "&b/&699\n&bXP: &6" + xp + "\n&bNEXT LVL: &6" + neededXp));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS(" &3(&6" + lvl + "&3)" + StringUtils.repeat(".", (18-skill.s().length()))));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS("&3[&b Info &3] "));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(getDesc(skill)));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS("&3[&b XP &3] "));
					
					String message = "&3&oXP Generation Methods\n";
					
					for (MXP m : values()){
						if (m.hasSkill(skill)){
							message = m.getXP(skill) > 0 ? message + "&6" + m.getMat().name().toLowerCase() + "&f: " + (lvl >= m.getNeededLevel(skill) ? "&a" : "&c") + "Level " + m.getNeededLevel(skill) + ", &b" + m.getXP(skill) + " xp.\n" : message;
						}
					}
					
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(message));
					msg.addExtra(extra);
					
					Map<Material, Integer> map = getTools(skill);
					message = "";
					
					for (Material tool : map.keySet()){
						message = (message.equals("") ? "&3&oExtra Drops Tool Requirement" : message) + "\n" + "&b" + tool.toString().toLowerCase() + "&f: " + (lvl >= map.get(tool) ? "&a" : "&c") + map.get(tool);
					}
					
					extra = new JSONChatExtra(main.AS("&3[&b Tools &3] "));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(message + " "));
					msg.addExtra(extra);
					
					extra = new JSONChatExtra(main.AS("&3[&b Perks &3] "));
					extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(getPerks(skill) + "\n&7&oAll gathering skills have a chance to drop double.\n&7&oThis chance increases by 0.4% per level."));
					msg.addExtra(extra);
					main.s(p, msg);
				}
				
				p.sendMessage("");
				
				main.s(p, "&7&oHover over everything for more info...");
				
			} else {
				main.s(p, "&c&oThat player does not exist.");
			}
		}
	}
	
	private void gStats(Player p, String s){
		
		ElySkill checkSkill = null;
		String skillList = "";
		
		for (ElySkill skill : ElySkill.values()){
			if (skill.s().equalsIgnoreCase(s)){
				checkSkill = skill;
			}
			skillList = skillList.equals("") ? "&6" + skill.s() : skillList + "&7, &6" + skill.s();
		}
		
		if (!s.equalsIgnoreCase("total") && checkSkill == null){
			main.s(p, "&c&oInvalid skill. Choose from...");
			main.s(p, skillList + "&7, &6total");
			return;
		}
		
		Map<Integer, List<DivinityStorage>> players = new HashMap<>();
		
		for (DivinityStorage dp : main.api.divManager.getAllUsers()){
			
			int total = 0;
			
			for (ElySkill skill : ElySkill.values()){
				total = (checkSkill != null && checkSkill.equals(skill)) || s.equals("total") ? total + ((DivinityPlayer)dp).getLevel(skill) : total;
			}
			
			if (!players.containsKey(total)){
				players.put(total, new ArrayList<DivinityStorage>(Arrays.asList(dp)));
			} else {
				players.get(total).add(dp);
			}
		}
		
		List<Integer> values = new ArrayList<Integer>();
		
		for (Integer i : players.keySet()){
			values.add(i);
		}
		
		Collections.sort(values);
		Collections.reverse(values);
		
		main.s(p, "&3Top 20 Players - &6" + s.toUpperCase());
		
		for (int i = 0; i < (values.size() >= 20 ? 20 : values.size()); i++){

			JSONChatMessage msg = new JSONChatMessage(main.AS("&3\u2744 "), null, null);
			JSONChatExtra extra = null;
			int loops = 0;
			
			for (DivinityStorage ds : players.get(values.get(i))){

				String hoverText = "&3Skill Layout";
				int total = 0;
				
				for (ElySkill skill : ElySkill.values()){
					total = (checkSkill != null && checkSkill.equals(skill)) || s.equals("total") ? total + ((DivinityPlayer)ds).getLevel(skill) : total;
					hoverText = hoverText + "\n&b" + skill.s() + "&f: &6" + ((DivinityPlayer)ds).getLevel(skill);
				}
				
				extra = new JSONChatExtra(main.AS(ds.getStr(DPI.DISPLAY_NAME) + " &b(&6" + total + "&b) &3\u2744 "), null, null);
				extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS(hoverText));
				msg.addExtra(extra);
				
				loops++;
				
				if (loops >= 3){
					break;
				}
			}
			
			main.s(p, msg);
		}
		
		p.sendMessage(main.AS("&7&oHover for full skill layout."));
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onFish(PlayerFishEvent e){
		
		if (new Random().nextInt(101) < (main.api.getDivPlayer(e.getPlayer()).getLevel(ElySkill.FISHING)*0.3)){
			e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), new ItemStack(Material.RAW_FISH));
		}
		
		main.api.event(new SkillExpGainEvent(e.getPlayer(), ElySkill.FISHING, 200));
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent e){
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		Location l = e.getBlock().getLocation();
		Material itemInHand = p.getItemInHand() != null ? p.getItemInHand().getType() : Material.AIR;
		boolean cont = true;
		
		if (main.api.getSystem().getList(MMO.INVALID_BLOCKS).contains(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ())){
			main.api.getSystem().getList(MMO.INVALID_BLOCKS).remove(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
			cont = false;
		}
		
		List<ElySkill> skills = Arrays.asList(ElySkill.WOODCUTTING, ElySkill.MINING, ElySkill.DIGGING, ElySkill.FARMING);
		List<String> skillTools = Arrays.asList("_axe", "_pick", "_spade", "none");
		
		for (int i = 0; i < skills.size(); i++){
			
			Boolean[] results = canGiveXp(p, e.getBlock().getType(), skills.get(i), itemInHand, skillTools.get(i));
			
			if (results[0]){
				if (skills.get(i).equals(ElySkill.FARMING)){
					cont = true;
				}
			}
			
			if (cont){
			
				if (results[1]){
					if (new Random().nextInt(101) < (dp.getLevel(skills.get(i))*0.3)){
						p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(e.getBlock().getType()));
					}
				}
				
				if (results[0]){
					main.api.event(new SkillExpGainEvent(p, skills.get(i), get(e.getBlock().getType()).getXP(skills.get(i))));
				}
			}
		}
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlace(BlockPlaceEvent e){

		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (isHolding(p, "sapling") && dp.getBool(MMO.IS_LIFE_FORCING)){
			e.setCancelled(true);
			life.l(p, dp, p.getLocation());
		}
		
		Location l = e.getBlock().getLocation();
		main.api.event(new SkillExpGainEvent(e.getPlayer(), ElySkill.BUILDING, 100));
		main.api.getSystem().getList(MMO.INVALID_BLOCKS).add(l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
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
	
	//lvl xp xp_needed
	@EventHandler (priority = EventPriority.MONITOR)
	public void onXp(SkillExpGainEvent e){
		
		if (e.isCancelled() || (!e.getPlayer().getWorld().getName().equals("world") && !e.getPlayer().getWorld().getName().equals("world_nether") && !e.getPlayer().getWorld().getName().equals("world_the_end"))){
			return;
		}
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		e.setXp(dp.getBool(DPI.IGNORE_XP) ? e.getXp() : e.getXp()*2); // Added for balancing - current curve way too high.
		
		String[] results = dp.getStr(e.getSkill()).split(" ");
		int level = Integer.parseInt(results[0]);
		
		if (!dp.getBool(DPI.IGNORE_XP) && !dp.getBool(DPI.SHARE_XP)){
			if (patrols.doesPatrolExistWithPlayer(p)){
				for (String member : patrols.getPatrolWithPlayer(p).getMembers()){
					if (!member.equals(p.getName())){
						main.matchDivPlayer(member).set(DPI.IGNORE_XP, true);
						main.api.event(new SkillExpGainEvent(main.getPlayer(member), e.getSkill(), Math.round(e.getXp()/5)));
					}
				}
			}
		}
		
		dp.set(DPI.IGNORE_XP, false);
		
		if (level < 99){
			
			double needed = Double.parseDouble(results[2]);
			int xp = Integer.parseInt(results[1]) + (level >= 70 ? e.getXp() + Math.round(e.getXp()/4) : e.getXp()) + 20;
			dp.set(e.getSkill(), level + " " + xp + " " + needed);
			
			if (!main.logger.protectedMats.contains(p.getItemInHand().getType()) && !e.getSkill().equals(ElySkill.BUILDING) && p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR) && dp.getBool(DPI.XP_DISP_NAME_TOGGLE)){
				
				boolean cont = true;
				
				if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()){
					
					cont = false;
					
					for (ElySkill skill : ElySkill.values()){
						if (p.getItemInHand().getItemMeta().getDisplayName().toLowerCase().contains(skill.toString().toLowerCase())){
							cont = true;
							break;
						}
					}
				}
				
				if (cont){
					ItemMeta im = e.getPlayer().getItemInHand().getItemMeta();
					im.setDisplayName(main.AS("&b&o" + e.getSkill().s() + " &6&o" + xp + "&b&o/&6&o" + Math.round(needed)));
					p.getItemInHand().setItemMeta(im);
				}
			}
			
			if (xp >= needed){
				needed = needed + (needed*.10) + (500*level);
				dp.set(e.getSkill(), (level+1) + " " + xp + " " + needed);
				dp.s("Your &6" + e.getSkill().s() + " &blevel is now &6" + (level+1) + "&b!");
				main.fw(p.getWorld(), p.getLocation(), Type.BALL, main.api.divUtils.getRandomColor());
				
				if (level == 19 || level == 49 || level == 69 || level == 79 || level == 89 || level == 98){
					DivinityUtils.bc(p.getDisplayName() + " &bhas reached &6" + e.getSkill().s() + " &blevel &6" + (level+1) + "&b!");
				}
				
				if (level == 98){
					dp.s("WELL DONE! You've reached the max level in this skill!");
					main.effects.playCircleFw(p, main.api.divUtils.getRandomColor(), Type.BALL_LARGE, 5, 1, 0, true, false);
				}
			}
		}
	}
	
	@EventHandler
	public void onDeath(EntityDeathEvent e){
		
		if (e.getEntity() instanceof Player == false && e.getEntity().getKiller() != null && e.getEntity().getKiller() instanceof Player){
			
			Player p = e.getEntity().getKiller();
			DivinityPlayer dp = main.api.getDivPlayer(p);
			
			if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR) && containsKey(p.getItemInHand().getType()) && get(p.getItemInHand().getType()).canUseTool(ElySkill.ATTACK, p.getItemInHand().getType(), main.api.getDivPlayer(p).getLevel(ElySkill.ATTACK))){
				if (new Random().nextInt(100) <= dp.getLevel(ElySkill.ATTACK)*0.4){
					List<ItemStack> drops = new ArrayList<ItemStack>();
					for (ItemStack i : e.getDrops()){
						if (i != null){
							drops.add(i);
						}
					}
					for (ItemStack i : drops){
						p.getWorld().dropItemNaturally(p.getLocation(), i);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		
		for (String user : noPickup.keySet()){
			for (Item i : noPickup.get(user)){
				if (i.equals(e.getItem())){
					e.setCancelled(true);
					return;
				}
			}
		}
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
				
				if (isHolding(p, "_axe") && dp.getLevel(ElySkill.WOODCUTTING) >= 10){
					treeFeller.r(p, dp);
				}
				
				if (isHolding(p, "_pickaxe") && dp.getLevel(ElySkill.MINING) >= 10){
					superBreaker.r(p, dp, MMO.IS_SUPER_BREAKING);
				}
				
				if (isHolding(p, "_spade") && dp.getLevel(ElySkill.DIGGING) >= 10){
					superBreaker.r(p, dp, MMO.IS_TURBO_DRILLING);
				}
				
				if (isHolding(p, "_sword") && dp.getLevel(ElySkill.ATTACK) >= 10){
					skyBlade.r(p, dp);
				}
				
				if (isHolding(p, "sapling") && dp.getLevel(ElySkill.FARMING) >= 10){
					life.r(p, dp);
				}
				
				if (isHolding(p, "fishing") && dp.getLevel(ElySkill.FISHING) >= 10){
					holy.l(p, dp, p.getLocation());
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
					superBreaker.l(p, dp, b, MMO.IS_SUPER_BREAKING, MMO.IS_MINING, MMO.SUPER_BREAKER_CD, ElySkill.MINING);
				}
				
				if (isHolding(p, "_spade") && dp.getBool(MMO.IS_TURBO_DRILLING)){
					superBreaker.l(p, dp, b, MMO.IS_TURBO_DRILLING, MMO.IS_DIGGING, MMO.TURBO_DRILL_CD, ElySkill.DIGGING);
				}
				
			break;
		}
	}
}