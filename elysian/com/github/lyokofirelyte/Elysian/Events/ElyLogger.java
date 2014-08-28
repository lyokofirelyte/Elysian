package com.github.lyokofirelyte.Elysian.Events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.apache.commons.math3.util.Precision;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystem;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyLogger implements Listener, Runnable {
	
	private Elysian main;
	
	public ElyLogger(Elysian i){
		main = i;
	}
	
	List<Material> protectedMats = Arrays.asList(
		Material.CHEST,
		Material.FURNACE,
		Material.BREWING_STAND,
		Material.ENCHANTMENT_TABLE,
		Material.BURNING_FURNACE,
		Material.ENDER_CHEST,
		Material.HOPPER,
		Material.JUKEBOX,
		Material.DISPENSER,
		Material.DROPPER,
		Material.BEACON,
		Material.TRAPPED_CHEST
	);
	
	private Map<String, List<String>> recent = new HashMap<String, List<String>>();
	private Map<Player, Map<String, Integer>> warnings = new HashMap<Player, Map<String, Integer>>();
	
	@Override
	public void run(){
		if (main.queue.size() > 0){
			initLog(new HashMap<Location, List<List<String>>>(main.queue));
			main.queue = new HashMap<Location, List<List<String>>>();
			recent = new HashMap<String, List<String>>();
		}
		if (warnings.size() > 0){
			Map<Player, Map<String, Integer>> warningsCurrent = new HashMap<Player, Map<String, Integer>>(warnings);
			for (Player p : warningsCurrent.keySet()){
				for (String mat : warningsCurrent.get(p).keySet()){
					main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", p.getDisplayName() + " &c&ofound " + warningsCurrent.get(p).get(mat) + " &6&o" + mat, "&c"));
				}
			}
			warnings = new HashMap<Player, Map<String, Integer>>();
		}
	}
	
	public void addToQue(Location l, String player, String action, String eventName, String whatItWas, String whatItIs){
		if (!main.queue.containsKey(l)){
			List<List<String>> s = new ArrayList<List<String>>();
			main.queue.put(l, s);
		}
		main.queue.get(l).add(Arrays.asList(player, action, eventName, whatItWas, whatItIs));
	}
	
	public void removeFromQue(Location l){
		if (main.queue.containsKey(l)){
			main.queue.remove(l);
		}
	}
	
	private void addToRecent(String p, String thing){
		if (!recent.containsKey(p)){
			recent.put(p, new ArrayList<String>());
		}
		recent.get(p).add(thing);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && (e.getClickedBlock().getType().equals(Material.BED_BLOCK) || e.getClickedBlock().getType().equals(Material.BED))){
			int sleeping = 0;
			for (Player p : Bukkit.getOnlinePlayers()){
				if (p.isSleeping()){
					sleeping++;
				}
			}
			if (sleeping > (Bukkit.getOnlinePlayers().length/2)){
				e.getPlayer().getWorld().setTime(0);
			}
		}
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getClickedBlock() != null && main.api.getDivPlayer(e.getPlayer()).getBool(DPI.LOGGER) && e.getPlayer().getItemInHand().getType().equals(Material.ENDER_PORTAL_FRAME)){
			
			lookup(e.getPlayer(), e.getClickedBlock().getLocation());
			e.setCancelled(true);
			
		} else if (e.getClickedBlock() != null && !main.api.getDivPlayer(e.getPlayer()).getStr(DPI.CHEST_MODE).equals("none") && protectedMats.contains(e.getClickedBlock().getType())){
		
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			Location l = e.getClickedBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			
			List<String> names = dp.getList(DPI.CHEST_NAMES);
			List<String> failedNames = new ArrayList<String>();
			String failLine = "";
			
			if (!dp.getList(DPI.OWNED_CHESTS).contains(loc) && !main.silentPerms(e.getPlayer(), "wa.staff.mod2") && !names.get(0).equals("view")){
				main.s(e.getPlayer(), "none", "&c&oThat is not yours to modify!");
				return;
			}
			
			for (String s : names){
				if (!main.doesPartialPlayerExist(s) && !s.equals("view")){
					failedNames.add(s);
				} else if (dp.getStr(DPI.CHEST_MODE).equals("add") && main.matchDivPlayer(s).getList(DPI.OWNED_CHESTS).contains(loc)){
					failedNames.add(s);
				} else if (dp.getStr(DPI.CHEST_MODE).equals("remove") && !main.matchDivPlayer(s).getList(DPI.OWNED_CHESTS).contains(loc)){
					failedNames.add(s);
				} else if (s.equals("view")){
					String users = "";
					for (DivinityStorage d : main.api.divManager.getAllUsers()){
						if (d.getList(DPI.OWNED_CHESTS).contains(loc)){
							users = users + "&3" + d.name() + " ";
						}
					}
					users = users.trim();
					main.s(e.getPlayer(), " ", users.replaceAll(" ", "&b, &3"));
					dp.set(DPI.CHEST_MODE, "none");
					dp.set(DPI.CHEST_NAMES, new ArrayList<String>());
					return;
				}
			}
			
			for (String s : failedNames){
				names.remove(s);
				failLine = failLine + "&c" + s + " ";
			}
			
			failLine = failLine.trim();
			failLine = failLine.replaceAll(" ", "&6, &c");
			
			for (String s : names){
				DivinityPlayer toModify = main.matchDivPlayer(s);
				if (dp.getStr(DPI.CHEST_MODE).equals("add")){
					toModify.getList(DPI.OWNED_CHESTS).add(loc);
				} else {
					toModify.getList(DPI.OWNED_CHESTS).remove(loc);
				}
			}
			
			if (!failLine.equals("")){
				main.s(e.getPlayer(), "none", "&c&oFailed on adding some people! (Are they already added?)");
				main.s(e.getPlayer(), "none", failLine);
			} else {
				main.s(e.getPlayer(), "none", "All users modified successfully.");
			}
			
			dp.set(DPI.CHEST_MODE, "none");
			e.getPlayer().getWorld().playEffect(e.getClickedBlock().getLocation(), Effect.ENDER_SIGNAL, 3);
			
		} else if (e.getClickedBlock() != null && protectedMats.contains(e.getClickedBlock().getType())){
			
			DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
			Location l = e.getClickedBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			
			if (dp.getStr(DPI.DEATH_CHEST_LOC).equals(loc)){
				
				for (ItemStack i : dp.getStack(DPI.DEATH_CHEST_INV)){
					if (i != null && !i.getType().equals(Material.AIR) && e.getPlayer().getInventory().firstEmpty() != -1){
						e.getPlayer().getInventory().addItem(i);
					} else if (i != null && !i.getType().equals(Material.AIR)){
						e.getPlayer().getWorld().dropItemNaturally(l, i);
					}
				}
				
				e.getPlayer().updateInventory();
				e.getClickedBlock().setType(Material.AIR);
				
				dp.set(DPI.DEATH_CHEST_INV, "none");
				dp.set(DPI.DEATH_CHEST_LOC, "none");
				
			} else {
				
				if (!dp.getList(DPI.OWNED_CHESTS).contains(loc) && !main.silentPerms(e.getPlayer(), "wa.staff.mod2")){
					for (DivinityStorage DP : main.api.divManager.getAllUsers()){
						if (DP.getList(DPI.OWNED_CHESTS).contains(loc)){
							e.setCancelled(true);
							main.s(e.getPlayer(), "none", "&c&oThat is not your storage unit!");
							return;
						}
					}
				}
			}
			
		} else if (e.getClickedBlock() != null && !e.getClickedBlock().getType().equals(Material.AIR)){
			
			String player = e.getPlayer().getName();
			
			switch (e.getMaterial()){
			
				default: break;
			
				case WOOD_DOOR: case FENCE_GATE: case TRAP_DOOR:
				
					if (recent.containsKey(player)){
						if (recent.get(e.getPlayer().getName()).contains("door")){
							break;
						}
					}
					
					addToRecent(player, "door");
					addToQue(e.getClickedBlock().getLocation(), "&b" + player, "&3used &b" + e.getClickedBlock().getType().toString().toLowerCase(), "interact", "DOOR", "DOOR");
					
				break;
				
			}
		}
	}
	
	@DivCommand(aliases = {"chest"}, desc = "Elysian Chest Protection Command", help = "/chest help", min = 1, player = true)
	public void onChestCommand(final Player p, final String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		List<String> names = new ArrayList<String>();
		
		switch (args[0]){
		
			case "add": case "remove":
				dp.set(DPI.CHEST_MODE, args[0]);
				for (int i = 1; i < args.length; i++){
					names.add(args[i]);
				}
				dp.set(DPI.CHEST_NAMES, names);
				main.s(p, "none", "Left-click on a storage unit to " + args[0] + " the names.");
				p.setGameMode(GameMode.SURVIVAL);
			break;
			
			case "help":
				main.s(p, "none", "/chest add/remove player1 player2 player3 etc");
				main.s(p, "none", "/chest view");
				main.s(p, "none", "/chest cancel");
			break;
			
			case "view":
				dp.set(DPI.CHEST_MODE, args[0]);
				dp.getList(DPI.CHEST_NAMES).add("view");
				main.s(p, "none", "Left-click on a storage unit to view the owners.");
				p.setGameMode(GameMode.SURVIVAL);
			break;
			
			case "cancel":
				dp.set(DPI.CHEST_MODE, "none");
				main.s(p, "none", "Action cancelled.");
			break;
		}
	}
	
	//location, player, message, event, what it was, what it now is
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e){
		
		if (e.isCancelled()){
			return;
		}

		Material mat = e.getBlock().getType();
		String matName = mat.name().toLowerCase();
		DivinityPlayer dp = main.api.getDivPlayer(e.getPlayer());
		
		if (!dp.getList(DPI.PERMS).contains("wa.member")){
			e.setCancelled(true);
			return;
		}
		
		if (protectedMats.contains(mat) || e.getBlock() instanceof DoubleChest || e.getBlock() instanceof Chest){
			Location l = e.getBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			if (!dp.getList(DPI.OWNED_CHESTS).contains(loc) && !main.silentPerms(e.getPlayer(), "wa.staff.mod2")){
				for (DivinityStorage DP : main.api.divManager.getAllUsers()){
					if (DP.getList(DPI.OWNED_CHESTS).contains(loc)){
						e.setCancelled(true);
						main.s(e.getPlayer(), "none", "&c&oThat is not your storage unit!");
						main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", e.getPlayer().getDisplayName() + " &cattempted to destroy a storage unit!", "&c"));
						return;
					}
				}
			} else {
				dp.getList(DPI.OWNED_CHESTS).remove(loc);
			}
		}
		
		if (!e.getBlock().getWorld().getName().equals("WACP")){
			addToQue(e.getBlock().getLocation(), "&b" + e.getPlayer().getName(), "&cdestroyed &b" + matName, "break", matName + "split" + e.getBlock().getData(), "AIRsplit0");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		
		if (!main.api.getDivPlayer(e.getPlayer()).getList(DPI.PERMS).contains("wa.member")){
			e.setCancelled(true);
			return;
		}
		
		String matName = e.getBlock().getType().toString().toLowerCase();
		
		if (e.getBlock() != null && main.api.getDivPlayer(e.getPlayer()).getBool(DPI.LOGGER) && e.getPlayer().getItemInHand().getType().equals(Material.ENDER_PORTAL_FRAME)){
			lookup(e.getPlayer(), e.getBlock().getLocation());
			e.setCancelled(true);
		} else if (!e.getBlock().getWorld().getName().equals("WACP")){
			addToQue(e.getBlock().getLocation(), "&b" + e.getPlayer().getName(), "&aplaced &b" + e.getBlock().getType().toString().toLowerCase(), "place", "AIRsplit0", matName + "split" + e.getBlock().getData());
		}
		
		if (!e.getPlayer().getWorld().getName().equals("WACP") && protectedMats.contains(e.getBlock().getType())){
			main.s(e.getPlayer(), "none", "This storage unit is now protected. Allow friend access with /chest add <player>.");
			Location l = e.getBlock().getLocation();
			String loc = l.getWorld().getName() + " " + l.toVector().getBlockX() + " " + l.toVector().getBlockY() + " " + l.toVector().getBlockZ();
			main.api.getDivPlayer(e.getPlayer()).getList(DPI.OWNED_CHESTS).add(loc);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		Entity e = event.getEntity();
	    
	    if (e.getType().equals(EntityType.ENDER_CRYSTAL) && e.getWorld().equals(Bukkit.getWorld("world"))){
	    	event.setCancelled(true);
	    }

	    for (Entity entity : e.getNearbyEntities(7.0D, 7.0D, 7.0D)) {
	    	if ((entity.getType().equals(EntityType.MINECART_TNT)) || (entity.getType().equals(EntityType.PRIMED_TNT))){
	    		event.setCancelled(true);
	    		break;
	    	}
	    }

	    for (Block block : event.blockList()) {
	    	if (block.getType().equals(Material.TNT) || protectedMats.contains(block.getType())){
	    		event.setCancelled(true);
	    		break;
	    	}
			addToQue(block.getLocation(), "&benvironment-explosion", "&cblew up &b" + block.getType().name().toLowerCase(), "break", block.getType().name().toLowerCase() + "split" + block.getData(), "AIRsplit0");
	    }
	}
	
	@DivCommand(aliases = {"log", "logger"}, perm = "wa.staff.intern", desc = "Elysian Logging Command", help = "/log help", player = true)
	public void onLogCommand(final Player p, final String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinitySystem system = main.api.getSystem();
		
		if (system.getBool(DPI.ROLLBACK_IN_PROGRESS)){
			main.s(p, "&c&oRollback in progress - command blocked to prevent file corruption.");
			return;
		}
		
		if (args.length == 0){
			
			dp.set(DPI.LOGGER, !dp.getBool(DPI.LOGGER));
			
			if (dp.getBool(DPI.LOGGER)){
				main.s(p, "none", "&oLogger activated!");
				p.setItemInHand(new ItemStack(Material.ENDER_PORTAL_FRAME, 1));
			} else {
				main.s(p, "none", "&oLogger deactivated!");
				p.setItemInHand(new ItemStack(Material.AIR));
			}
			
		} else if (main.api.divUtils.isInteger(args[0])){
			
			try {
				
				int base = Integer.parseInt(args[0])-1;
				List<String> results = dp.getList(DPI.LOGGER_RESULTS);
				main.s(p, "none", "Viewing page &6" + (base+1) + "&b. SysTime: &7" + main.api.divUtils.getTime(System.currentTimeMillis()));

				for (int x = 5*base; x < (5*base)+5; x++){
					if (results.size()-1 >= x){
						main.s(p, "none", results.get(x));
					}
				}
				
			} catch (Exception e){
				main.s(p, "none", "&4No results found!");
			}
			
		} else {
			
			switch (args[0]){
			
				case "help":
					
					main.s(p, "none", "/log <page number>");
					main.s(p, "none", "/log rollback <radius> <time> [player]");
					main.s(p, "none", "Time examples: 40s, 10m, 2h, 1d, 2w. Only use one time measurement at a time.");
					
				break;
				
				case "rollback": ///log rollback <radius> 5m/5h [player]
					
					main.api.getSystem().set(DPI.ROLLBACK_IN_PROGRESS, true);
					
					new Thread(new Runnable(){ public void run(){
					
						String timeType = "";
						int radius = 0;
						long time = 0;
						
						try {
							
							if (main.api.divUtils.isInteger(args[1])){
								
								main.s(p, "none", "&oRollback started...");
								
								radius = Integer.parseInt(args[1]);
								timeType = args[2].substring(args[2].length()-1);
								time = Long.parseLong(args[2].replace(timeType, ""));
								
								switch (timeType){
									case "s": time = time*1000; break;
									case "m": time = time*60*1000; break;
									case "h": time = time*60*60*1000; break;
									case "d": time = time*24*60*60*1000; break;
									case "w": time = time*7*24*60*60*1000; break;
									default: main.s(p, "none", "s, m, h, d, or w"); return;
								}
								
								time = System.currentTimeMillis() - time;
								Location pLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()-radius, p.getLocation().getZ());
								
								final List<String> locs = main.api.divUtils.strCircle(pLoc, radius, radius*2, false, false, 0);
								final Map<Location, Material> newBlocks = new HashMap<Location, Material>();
								final Map<Location, Byte> newBlockIds = new HashMap<Location, Byte>();
								
								Map<String, YamlConfiguration> filesToCheck = new HashMap<String, YamlConfiguration>();
								Map<String, Map<Integer, List<String>>> results = new HashMap<String, Map<Integer, List<String>>>();
								List<String> allowedEvents = Arrays.asList("break", "place");
								
								for (String l : locs){
									String loc = l.split(" ")[1] + "," + l.split(" ")[3];
									float x = Precision.round(Integer.parseInt(l.split(" ")[1]), -3);
									float z = Precision.round(Integer.parseInt(l.split(" ")[3]), -3);
									File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
									
									if (file.exists()){
										filesToCheck.put(loc, YamlConfiguration.loadConfiguration(file));
										results.put(loc, new HashMap<Integer, List<String>>());
									}
								}
								
								if (results.size() <= 0){
									main.s(p, "none", "&oThere was nothing to roll back.");
									return;
								}
								
								for (String yaml : filesToCheck.keySet()){
									for (int x = 0; x < 257; x++){
										if (x > p.getLocation().getY() - radius && x < p.getLocation().getY() + radius){
											results.get(yaml).put(x, new ArrayList<String>());
											for (String s : filesToCheck.get(yaml).getStringList("History." + p.getWorld().getName() + "." + x)){
												if (allowedEvents.contains(s.split("%")[3].toLowerCase())){
													if (args.length == 4){
														if (s.split("%")[0].toLowerCase().contains(args[3].toLowerCase())){
															results.get(yaml).get(x).add(s);
														}
													} else {
														results.get(yaml).get(x).add(s);
													}
												}
											}
										}
									}
								}
								
								//player, message, time, event, what it was, what it now is, y coord
								
								for (String loc : results.keySet()){
									for (int yCoord : results.get(loc).keySet()){
										Map<Long, String> resultTimes = new HashMap<Long, String>();
										Map<Long, String> finalTimes = new HashMap<Long, String>();
										List<Long> times = new ArrayList<Long>();
										
										for (String result : results.get(loc).get(yCoord)){
											resultTimes.put(Long.parseLong(result.split("%")[2]), result);
										}
										
										for (Long l : resultTimes.keySet()){
											times.add(l);
										}
										
										Collections.sort(times);
										Collections.reverse(times);
										
										int loops = 1;
										
										for (Long daTime : times){
											finalTimes.put(daTime, resultTimes.get(daTime));
										}
										
										for (String result : finalTimes.values()){
											String[] ss = result.split("%");
											if (Long.parseLong(ss[2]) <= time){
												newBlocks.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Material.valueOf((ss[5].split("split")[0]).toUpperCase()));
												newBlockIds.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Byte.parseByte(ss[5].split("split")[1]));
												break;
											} else if (loops == finalTimes.size()){
												newBlocks.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Material.valueOf((ss[4].split("split")[0]).toUpperCase()));
												newBlockIds.put(new Location(p.getWorld(), Double.parseDouble(loc.split(",")[0]), yCoord, Double.parseDouble(loc.split(",")[1])), Byte.parseByte(ss[4].split("split")[1]));
											}
											loops++;
										}
									}
								}

								final List<Location> finalLocs = new ArrayList<Location>();
								final DivinitySystem system = main.api.getSystem();
								system.set(DPI.EXP, 0);
								
								for (Location l : newBlocks.keySet()){
									finalLocs.add(l);
								}
								
								if (finalLocs.size() <= 0){
									main.s(p, "none", "Nothing could be found to rollback.");
									return;
								}
								
								system.set(DPI.HOME, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){ @SuppressWarnings("deprecation")
								public void run(){
									
									for (int x = 0; x < 5; x++){
										
										if (finalLocs.size() > x){
										
											Material oldBlock = finalLocs.get(system.getInt(DPI.EXP)).getBlock().getType();
											Material newBlock = newBlocks.get(finalLocs.get(system.getInt(DPI.EXP)));
											byte newBlockId = newBlockIds.get(finalLocs.get(system.getInt(DPI.EXP)));
	
											if (!oldBlock.equals(newBlock)){
												addToQue(finalLocs.get(system.getInt(DPI.EXP)), "&b" + p.getName(), "&erolled back &b" + oldBlock.name().toLowerCase() + " -> " + newBlock.name().toLowerCase(), "rollback", oldBlock.name().toLowerCase(), newBlock.name().toLowerCase());
											}
											
											finalLocs.get(system.getInt(DPI.EXP)).getBlock().setTypeIdAndData(newBlock.getId(), newBlockId, true);
											system.set(DPI.EXP, system.getInt(DPI.EXP) + 1);
											
											if (system.getInt(DPI.EXP) >= finalLocs.size()){
												Bukkit.getScheduler().cancelTask(system.getInt(DPI.HOME));
												main.s(p, "none", "&oRollback completed.");
												main.api.getSystem().set(DPI.ROLLBACK_IN_PROGRESS, false);
												break;
											}
										}
									}

								}}, 1L, 1L));
								
							} else {
								main.s(p, "invalidNumber");
							}
							
						} catch (Exception e){
							e.printStackTrace();
							main.s(p, "none", "&cRollback failed! Invalid inputs.");
						}
						
					}}).start();
					
				break;
			}
		}
	}
	
	private void initLog(final Map<Location, List<List<String>>> map){
		new Thread(new Runnable(){ public void run(){
			for (Location l : map.keySet()){
				for (List<String> list : map.get(l)){
					try {
						log(l, list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
					} catch (Exception e){}
				}
			}
		}}).start();
	}
	
	private void lookup(final Player p, final Location l){
		
		if (main.api.getSystem().getBool(DPI.ROLLBACK_IN_PROGRESS)){
			main.s(p, "&c&oRollback in progress - command blocked to prevent file corruption.");
			return;
		}
		
		new Thread(new Runnable(){ public void run(){
		
			String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
			float x = Precision.round(l.toVector().getBlockX(), -3);
			float z = Precision.round(l.toVector().getBlockZ(), -3);
			int y = l.toVector().getBlockY();
				
			File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
			
			if (!file.exists()){
				
				main.s(p, "none", "&4No data found!");
				
			} else {
				
				YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
				List<String> results = new ArrayList<String>(yaml.getStringList("History." + p.getWorld().getName() + "." + y));
				Collections.reverse(results);
				
				main.api.getDivPlayer(p).set(DPI.LOGGER_RESULTS, new ArrayList<String>());
				main.s(p, "none", "Viewing page &61&b/&6" + (Math.round(results.size()/5)+1) + "&b. SysTime: &7" + main.api.divUtils.getTime(System.currentTimeMillis()));
				
				for (String s : results){
					String[] ss = s.split("%");
					String time = main.api.divUtils.getTime(Long.parseLong(ss[2]));
					main.api.getDivPlayer(p).getList(DPI.LOGGER_RESULTS).add("&7" + time + " " + ss[0] + " " + ss[1]);
				}
				
				for (int g = 0; g < 5; g++){
					if (results.size() > g){
						String[] ss = results.get(g).split("%");
						main.s(p, "none", "&7" + main.api.divUtils.getTime(Long.parseLong(ss[2])) + " " + ss[0] + " " + ss[1]);
					}
				}
				
				if (results.size() >= 5){
					main.s(p, "none", "Type /log <number> to view a different page!");
				}
			}
				
		}}).start();
	}
	
	private void log(final Location l, final String player, final String action, final String eventName, final String whatWasIt, final String whatIsIt){
		
		String loc = l.toVector().getBlockX() + "," + l.toVector().getBlockZ();
		int y = l.toVector().getBlockY();
		float x = Precision.round(l.toVector().getBlockX(), -3);
		float z = Precision.round(l.toVector().getBlockZ(), -3);
			
		File file = new File("./plugins/Divinity/logger/" + x + "," + z + "/" + loc + ".yml");
			
		if (!file.exists()){
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (Exception ee){
				ee.printStackTrace();
			}
		}
			
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		List<String> history = new ArrayList<String>();	
		
		if (yaml.getStringList("History." + l.getWorld().getName() + "." + y) == null || yaml.getStringList("History." + l.getWorld().getName() + "." + y).equals("") || yaml.getStringList("History." + l.getWorld().getName() + "." + y).equals(new ArrayList<String>())){
			yaml.set("History." + l.getWorld().getName() + "." + y, history);
		}
		
		history = yaml.getStringList("History." + l.getWorld().getName() + "." + y);
		history.add(player + "%" + action + "%" + System.currentTimeMillis() + "%" + eventName + "%" + whatWasIt + "%" + whatIsIt);
		yaml.set("History." + l.getWorld().getName() + "." + y, history);

		if (history.size() <= 1){
			
			String[] wut = whatWasIt.split("split");
			
			switch (wut[0].toLowerCase()){
			
				case "diamond_ore": case "lapis_ore": case "redstone_ore": case "emerald_ore": case "gold_ore":
					
					Player p = main.getPlayer(player.substring(2));
					
					if (!warnings.containsKey(p)){
						warnings.put(p, new HashMap<String, Integer>());
					}
					
					String what = wut[0].toLowerCase();
					
					if (!warnings.get(p).containsKey(what)){
						warnings.get(p).put(what, 1);
					} else {
						warnings.get(p).put(what, (warnings.get(p).get(what)+1));
					}
					
				break;
			}
		}

		try {
			yaml.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}