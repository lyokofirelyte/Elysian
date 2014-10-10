package com.github.lyokofirelyte.Elysian.Commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.PublicUtils.Direction;
import com.github.lyokofirelyte.Divinity.PublicUtils.ParticleEffect;
import com.github.lyokofirelyte.Divinity.Storage.DAI;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystem;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiRoot;

public class ElyCommand {

	private Elysian main;
	
	public ElyCommand(Elysian i){
		main = i;
		fillMap();
	}
	
	String[] divLogo = new String[]{
		"&b. . . . .&f(  &3D  i  v  i  n  i  t  y &f  )&b. . . . .",
		"",
		"&7&oAn API by Hugs, for Elysian & Connecting Plugins",	
		"&6&oRegistered Modules: "
	};
	
	String[] elyLogo = new String[]{
		"&b. . . . .&f(  &3E  l  y  s  i  a  n &f  )&b. . . . .",	
		"",
		"&7&oA MC Operating System by Hugs",
		"&6&o/ely help"
	};
	
	Map<String, String[]> help = new THashMap<String, String[]>();
	
	private void fillMap(){
		for (Object o : main.api.commandMap.values()){
			for (Method m : o.getClass().getMethods()){
				if (m.getAnnotation(DivCommand.class) != null){
					DivCommand anno = m.getAnnotation(DivCommand.class);
					String name = anno.aliases()[0];
					for (int i = 1; i < anno.aliases().length; i++){
						name = anno.aliases().length > i ? name + "&7, &3" + anno.aliases()[i] : name;
					}
					String[] perm = anno.perm().split("\\.");
					String p = perm[perm.length-1];
					help.put("/" + name, s(p.substring(0, 1).toUpperCase() + p.substring(1) + "+", anno.desc() + "\n&6" + anno.help()));
				}
			}
		}
	}
	
	private String[] s(String arg, String arg1){
		return new String[]{arg, arg1};
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"effects"}, desc = "Effects Command", help = "/effects help", player = true)
	public void onEffects(Player p, String[] args){
		
		DivinitySystem ds = main.api.getSystem();
		
		if (args.length == 0){
			
			for (String s : new String[]{
				"/effects add <name> <effName> <OSX> <OSY> <OSZ> <speed> <amount> <range> <cycleTime> [x,y,z]",
				"/effects rem <name>",
				"/effects stop <name>",
				"/effects list",
				"/effects playonce <effName> <OSX> <OSY> <OSZ> <speed> <amount> <range> [x,y,z]",
				"/effects effectlist",
				"/effects locktoplayer <player> <name> <effName> <OSX> <OSY> <OSZ> <speed> <amount> <range> <cycleTime>",
				"/effects draw <word> <effName> <direction> <cycleTime>",
				"/effects clearplayer <name>"
			}){
				main.s(p, s);
			}
			
		} else {
			
			switch (args[0]){
				
				case "draw":
					
					try {
						ds.addLetterEffect(args[1], ParticleEffect.fromName(args[2]), DivinityUtils.getCardinalMove(p), Direction.getDirection(args[3]), Long.parseLong(args[4]));
						main.s(p, "Added!");
					} catch (Exception e){
						main.s(p, "Invalid inputs!");
					}
					
				break;
			
				case "locktoplayer":
					
					if (main.doesPartialPlayerExist(args[1]) && main.isOnline(args[1])){
						
						try {
							DivinityPlayer dp = main.matchDivPlayer(args[1]);
							dp.lockEffect(args[2], ParticleEffect.fromName(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]),
								Integer.parseInt(args[7]), Integer.parseInt(args[8]), Integer.parseInt(args[9]), Long.parseLong(args[10]));
							main.s(p, "Added!");
						} catch (Exception e){
							main.s(p, "Invalid args!");
						}
						
					} else {
						main.s(p, "Player not found.");
					}
					
				break;
				
				case "clearplayer":
					
					if (main.isOnline(args[1])){
						main.matchDivPlayer(args[1]).clearEffects();
					}
					
				break;
			
				case "playonce":
					
					try {
						ds.playEffect(ParticleEffect.fromName(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), 
							Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]),
							args.length == 9 ? new Location(p.getWorld(), Integer.parseInt(args[10].split(",")[0]), 
								Integer.parseInt(args[8].split(",")[1]),
								Integer.parseInt(args[8].split(",")[2])) : 
								p.getLocation(),
							Integer.parseInt(args[7]));
					} catch (Exception e){
						main.s(p, "&c&oInvalid inputs!");
					}
					
				break;
				
				case "add":
					
					try {
						ds.addEffect(args[1], ParticleEffect.fromName(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), 
							Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]),
							args.length == 11 ? new Location(p.getWorld(), Integer.parseInt(args[10].split(",")[0]), 
								Integer.parseInt(args[10].split(",")[1]),
								Integer.parseInt(args[10].split(",")[2])) : 
								p.getLocation(),
							Integer.parseInt(args[8]), Long.parseLong(args[9]));
						main.s(p, "Added! :D");
					} catch (Exception e){
						main.s(p, "&c&oInvalid inputs!");
					}
					
				break;
				
				case "rem":
					
					if (args.length == 2 && (ds.contains("Effects." + args[1])) || (ds.contains("LetterEffects." + args[1]))){
						ds.remEffect(args[1]);
						main.s(p, "Removed.");
					} else {
						main.s(p, "Not found!");
					}
					
				break;
				
				case "stop":
					
					if (args.length == 2){
						ds.cancelEffect(args[1]);
						main.s(p, "Cancelled!");
					}
					
				break;
				
				case "list":
					
					for (String s : ds.getConfigurationSection("Effects").getKeys(false)){
						main.s(p, s);
					}
					
					for (String s : ds.getConfigurationSection("LetterEffects").getKeys(false)){
						main.s(p, s);
					}
					
				break;
				
				case "effectlist":
					
					for (ParticleEffect e : ParticleEffect.values()){
						main.s(p, e.toString());
					}
					
				break;
			}
		}
	}
	
	@DivCommand(perm = "wa.rank.dweller", aliases = {"notepad"}, desc = "Notepad Management System", help = "/notepad", player = true)
	public void onNotepad(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		JSONChatMessage msg = new JSONChatMessage("");
		JSONChatExtra addButton = new JSONChatExtra(main.AS("&bElysian Note System &a{+}"));
		addButton.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/notepad #add");
		addButton.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&aAdd a new note!"));
		msg.addExtra(addButton);
		
		int counter = 0;
		
		if (args.length == 0){
			
			p.sendMessage("");
			main.s(p, msg);
			
			for (String message : dp.getList(DPI.NOTEPAD)){
				JSONChatMessage m = new JSONChatMessage(main.AS("&7" + main.numerals.get(counter) + "&f: &3" + message + " "));
				JSONChatExtra editButton = new JSONChatExtra(main.AS("&7[&e*&7] "));
				JSONChatExtra deleteButton = new JSONChatExtra(main.AS("&7[&c-&7]"));
				editButton.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&eEdit this note."));
				editButton.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/notepad #edit <" + counter + "> " + message);
				deleteButton.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&cDelete this note."));
				deleteButton.setClickEvent(JSONChatClickEventType.RUN_COMMAND, "/notepad #delete " + message);
				m.addExtra(editButton);
				m.addExtra(deleteButton);
				main.s(p, m);
				counter++;
				if (counter == 101){
					break;
				}
			}
			
			p.sendMessage("");
			
		} else {
			
			switch (args[0]){
			
				case "#add":
					
					dp.s("Please type in a new note to add.");
					dp.s("%c will be replaced by your current coords.");
					dp.getList(DPI.NOTEPAD_SETTING).add("add");
					
				break;
				
				case "#edit":
					
					String message = "";
					
					if (args.length >= 2){
						boolean set = args[1].contains("<");
						int num = set ? Integer.parseInt(args[1].replace("<", "").replace(">", "")) : 0;
						num = num < dp.getList(DPI.NOTEPAD).size() ? num : dp.getList(DPI.NOTEPAD).size()-1;
						Location l = p.getLocation();
						if (set){
							message = main.api.divUtils.createString(args, 2);
							dp.getList(DPI.NOTEPAD).set(num, message.replace("%c", l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ()));
						} else {
							message = main.api.divUtils.createString(args, 1);
							dp.getList(DPI.NOTEPAD).add(message.replace("%c", l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ()));
						}
						onNotepad(p, new String[]{});
					}
					
				break;
				
				case "#delete":
					
					dp.getList(DPI.NOTEPAD).remove(main.api.divUtils.createString(args, 1));
					onNotepad(p, new String[]{});
					
				break;
			}
		}
	}
	
	@DivCommand(aliases = {"root", "menu"}, desc = "Open the main menu", help = "/root", player = true)
	public void onRoot(Player p, String[] args){
		main.invManager.displayGui(p, new GuiRoot(main));
	}
	
	 @DivCommand(perm = "wa.member", aliases = {"poll"}, desc = "Polls!", help = "/poll help", player = false, min = 1)
	 public void onPoll(CommandSender p, String[] args){
		 
		 /*DivinitySystem system = main.api.getSystem();
		 
		 switch (args[0]){
		 
			 case "help":
				 
				 for (String s : new String[]{
					"/poll vote [yes/no]",
					"/poll view",
					"/poll set <what to vote for>"
				 }){
					main.s(p, s);
				 }
				 
			 break;
			 
			 case "yes": case "no":
				 
				 for (String s : system.getList(DPI.VOTED)){
					 if (s.contains(p.getName())){
						 main.s(p, "You already voted.");
						 return;
					 }
				 }
				 
				 system.getList(DPI.VOTED).add(args[0].replace("yes", "A").replace("no", "B") + " " + p.getName());
				 main.s(p, "Thanks for voting.");
				 
		     break;
		     
			 case "view":
				 
				 String msg = "";
				 List<String> votes = new ArrayList(system.getList(DPI.VOTED));
				 Collections.sort(votes);
				 int yes = 0;
				 
				 for (String vote : votes){
					 msg = msg + " " + vote.replace("A ", "&a").replace("B ", "&c");
					 yes = vote.startsWith("A") ? yes++ : yes;
				 }
				 
				 main.s(p, msg);
				 p.sendMessage("");
				 main.s(p, "&a" + Math.round((yes/votes.size())*100) + "% yes, &c" + Math.round(((votes.size()-yes)/votes.size())*100) + "% no.");
				 
			 break;
			 
			 case "set":
				 
				 if (main.perms(p, "wa.staff.admin")){
					 system.set(DPI.VOTE_MESSAGE, main.api.divUtils.createString(args, 0));
					 main.s(p, "Set!");
				 }
				 
			 break;
		 }*/
		 
		 
		 if(args[0].equalsIgnoreCase("help")){
		 for (String s : new String[]{
					"/poll vote [yes/no]",
					"/poll view",
					"/poll set <what to vote for>"
				}){
					main.s(p, s);
				}
		 }else if(args[0].equalsIgnoreCase("vote") && args.length == 2){
			 if(args[1].equalsIgnoreCase("yes")){
				 if(main.api.getSystem().getList(DPI.YES_VOTE).contains(p.getName())){
					 main.s(p, "You already voted yes!");
					 return;
				 }else if(main.api.getSystem().getList(DPI.NO_VOTE).contains(p.getName())){
					 main.api.getSystem().getList(DPI.NO_VOTE).remove(p.getName());
					 main.api.getSystem().getList(DPI.YES_VOTE).add(p.getName());
				 }else{
					 main.api.getSystem().getList(DPI.YES_VOTE).add(p.getName());
				 }
				 main.s(p, "Thanks for your vote, you voted yes.");
			 }else if(args[1].equalsIgnoreCase("no")){
				 if(main.api.getSystem().getList(DPI.NO_VOTE).contains(p.getName())){
					 main.s(p, "You already voted no!");
					 return;
				 }else if(main.api.getSystem().getList(DPI.YES_VOTE).contains(p.getName())){
					 main.api.getSystem().getList(DPI.YES_VOTE).remove(p.getName());
					 main.api.getSystem().getList(DPI.NO_VOTE).add(p.getName());
				 }else{
					 main.api.getSystem().getList(DPI.NO_VOTE).add(p.getName());
				 }
				 main.s(p, "Thanks for your vote, you voted no.");
			 }else{
				 main.s(p, "/poll help");
			 }
		 }else if(args[0].equalsIgnoreCase("set")){
			 if(main.perms(p, "wa.staff.admin")){
				 StringBuilder message = new StringBuilder();
				 for(int i = 0; i < args.length; i++){
					 if(i >=1){
						 message.append(args[i] + " ");
					 }
				 }
				 main.api.getSystem().set(DPI.VOTE_MESSAGE, message.toString());
				 main.api.getSystem().set(DPI.YES_VOTE, null);
				 main.api.getSystem().set(DPI.NO_VOTE, null);
				 main.s(p, "Created vote!");
			 }
		 }else if(args[0].equalsIgnoreCase("view")){
			 main.s(p, "Current voting for: " + main.api.getSystem().getStr(DPI.VOTE_MESSAGE));
			 main.s(p, "Players who voted yes: ");
			 for(String s : main.api.getSystem().getList(DPI.YES_VOTE)){
				 main.s(p, s);
			 }
			 main.s(p, "Players who voted no: ");
			 for(String s : main.api.getSystem().getList(DPI.NO_VOTE)){
				 main.s(p, s);
			 }
			 if(main.api.getSystem().getList(DPI.YES_VOTE).size() + main.api.getSystem().getList(DPI.NO_VOTE).size() != 0){
				 main.s(p, "Yes: " + main.api.getSystem().getList(DPI.YES_VOTE).size() / (main.api.getSystem().getList(DPI.YES_VOTE).size() + main.api.getSystem().getList(DPI.NO_VOTE).size()) * 100 + "%");
				 main.s(p, "No: " + main.api.getSystem().getList(DPI.NO_VOTE).size() / (main.api.getSystem().getList(DPI.YES_VOTE).size() + main.api.getSystem().getList(DPI.NO_VOTE).size()) * 100 + "%");
			 }
		 }
	 }
 
	@DivCommand(perm = "wa.rank.regional", aliases = {"rainoff"}, desc = "Turn off that rain!", help = "/rainoff", player = true)
	public void onRainoff(Player p, String[] args){
		DivinityPlayer player = main.matchDivPlayer(p.getName());
		World w = p.getWorld();
		if(!w.hasStorm()){
			player.s("There is no rain!");
			return;
		}
		if(player.getLong(DPI.RAIN_TOGGLE) == 0 || player.getLong(DPI.RAIN_TOGGLE) <= System.currentTimeMillis() - 3 * 60 * 60 * 1000){
			player.set(DPI.RAIN_TOGGLE, System.currentTimeMillis());
			w.setStorm(false);
			main.api.divUtils.bc(player.getStr(DPI.DISPLAY_NAME) + " &bhas turned off the rain!");
		}else{
			player.s("You have to wait " + ((player.getLong(DPI.RAIN_TOGGLE) + 1000 * 60 * 60 * 3) - System.currentTimeMillis()) / 1000 / 60 + " more minutes");
		}
		
	}
		
	
	@DivCommand(perm = "wa.rank.districtman", aliases = {"near"}, desc = "See nearby players!", help = "/near", player = true, min = 0)
	public void onNear(Player p, String[] args){
		StringBuilder players = new StringBuilder();
		int count = 0;
		for(Entity e : p.getNearbyEntities(100, 100, 100)){
			if(e instanceof Player){
				Player found = (Player) e;
				DivinityPlayer pl = main.matchDivPlayer(found.getName());
				if(found.getName() != p.getName()){
				count = count + 1;
				players.append(pl.getStr(DPI.DISPLAY_NAME) + ", &b");
				}
			}
		}
		
		if(count == 0){
			main.s(p, "No nearby players found");
		}else{
			main.s(p, count + " player(s) found: " + players);
		}
		
	}
	
	
	@DivCommand(aliases = {"bio"}, desc = "Modify your hover-over description", help = "/bio <message>", player = true, min = 1)
	public void onBio(Player p, String[] args){
	
		DivinityPlayer dp = main.api.getDivPlayer(p);
		dp.set(DPI.PLAYER_DESC, "&7&o" + main.AS(main.api.divUtils.createString(args, 0)));
		main.s(p, "Updated!");
	}
	
	@DivCommand(aliases = {"me"}, desc = "This is you", help = "/me <action>", player = true, min = 1)
	public void onMe(Player p, String[] args){
		for(Player player : Bukkit.getOnlinePlayers()){
			player.sendMessage(main.AS("&f* " + p.getName() + " " + main.api.divUtils.createString(args, 0)));
		}
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"motd"}, desc = "Change the MOTD", help = "/motd <message>", player = false, min = 1)
	public void onMOTD(CommandSender p, String[] args){
		main.api.getSystem().set(DPI.MOTD, main.api.divUtils.createString(args, 0));
		main.s(p, "&bUpdated!");
	}
	
	@DivCommand(aliases = {"enderdragon"}, desc = "Spawn the enderdragon in the end", help = "/enderdragon", player = true)
	public void onEnderDragon(Player p, String[] args){
		
		DivinitySystem system = main.api.getSystem();
		
		if (system.getLong(DPI.ENDERDRAGON_CD) <= System.currentTimeMillis() && !system.getBool(DPI.ENDERDRAGON_DEAD)){
			system.set(DPI.ENDERDRAGON_DEAD, true);
			system.set(DPI.ENDERDRAGON_CD, System.currentTimeMillis() + 7200000L);
			Location temp = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
			p.teleport(new Location(Bukkit.getWorld("world_the_end"), 0, 10, 0));
			Bukkit.getWorld("world_the_end").spawnEntity(new Location(Bukkit.getWorld("world_the_end"), 0, 10, 0), EntityType.ENDER_DRAGON);
			p.teleport(temp);
			DivinityUtils.bc(p.getDisplayName() + " has spawned an enderdragon in the end!");
		} else {
			main.s(p, "&c&oActive cooldown. &6" + ((system.getLong(DPI.ENDERDRAGON_CD) - System.currentTimeMillis())/1000)/60 + " &c&ominutes remain.");
		}
	}
	
	@DivCommand(aliases = {"colors"}, desc = "View the colors", help = "/colors", player = false)
	public void onColors(CommandSender cs, String[] args){
		main.s(cs, "&aa &bb &cc &dd &ee &ff &00 &11 &22 &33 &44 &55 &66 &77 &88 &99 &7&ll &7&mm &7&nn &7&oo &7&rr");
	}
	
	@DivCommand(aliases = {"calendar"}, desc = "View our calendar!", help = "/calendar", player = true, min = 0)
	public void onCalendar(Player p, String[] args){

		DivinitySystem player = main.api.getSystem();
		if(args.length == 1){
			if(main.silentPerms(p, "wa.staff.admin")){
				player.set(DPI.CALENDAR_LINK, args[0]);
			}
		}
		
		 JSONChatMessage msg = new JSONChatMessage("", null, null);
		 JSONChatExtra extra = new JSONChatExtra(main.AS("&aClick here to for our calendar!"), null, null);
		 extra.setClickEvent(JSONChatClickEventType.OPEN_URL, player.getStr(DPI.CALENDAR_LINK));
		 msg.addExtra(extra);
		 msg.sendToAllPlayers(); 
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"sudo"}, desc = "Force someone to run a command", help = "/sudo <player> <command>", player = false, min = 2)
	public void onSudo(CommandSender cs, String[] args){
		
		if (main.doesPartialPlayerExist(args[0]) && main.isOnline(args[0])){
			main.getPlayer(args[0]).performCommand(main.api.divUtils.createString(args, 1));
			main.s(cs, "Forced " + main.getPlayer(args[0]).getDisplayName() + " &bto run " + main.api.divUtils.createString(args, 1) + ".");
		} else {
			main.s(cs, "playerNotFound");
		}
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"bc", "broadcast"}, desc = "Broadcasts a message", help = "/broadcast", player = false, min = 1)
	public void onBroadcast(CommandSender cs, String[] args){
		DivinityUtils.bc(main.api.divUtils.createString(args, 0));
	}
	
	@DivCommand(aliases = {"calc"}, desc = "Calculator Command", help = "/calc <query>", player = false)
	public void onCalc(CommandSender cs, String[] args){
		try {
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");  
			main.s(cs, (double)engine.eval(main.api.divUtils.createString(args, 0)) + "");
		} catch (Exception e){
			main.s(cs, "&c&oInvalid equation.");
		}
	}
	
	@DivCommand(perm = "wa.rank.villager", aliases = {"hat"}, desc = "Wear a hat!", help = "/hat", player = true)
	public void onHat(Player p, String[] args){
		if (p.getItemInHand() != null && (p.getInventory().getHelmet() == null || p.getInventory().getHelmet().getType().equals(Material.AIR))){
			p.getInventory().setHelmet(p.getItemInHand());
			p.setItemInHand(new ItemStack(Material.AIR));
		} else {
			main.s(p, "&c&oHand must have something in it and helmet be open.");
		}
	}
	
	@DivCommand(perm = "wa.staff.admin", aliases = {"modify"}, desc = "Divinity Modification Command", help = "/modify list, /modify <player/alliance> <stat> <value>", player = false, min = 1)
	public void onModify(CommandSender p, String[] args){
		
		if (args[0].equals("list")){
			
			main.s(p, "&3Player Values");
			
			for (DPI i : DPI.values()){
				main.s(p, i.s());
			}
			
			main.s(p, "&3Alliance Values");
			
			for (DAI i : DAI.values()){
				main.s(p, i.s());
			}
			
		} else if (args.length >= 3){
			if (main.doesPartialPlayerExist(args[0])){
				for (DPI i : DPI.values()){
					if (i.s().equalsIgnoreCase(args[1])){
						try {
							String dispName = p instanceof Player ? ((Player) p).getDisplayName() : "&6Console";
							main.matchDivPlayer(args[0]).set(i, main.api.divUtils.createString(args, 2));
							main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", dispName + " &cmodified " + i.s() + " for " + main.matchDivPlayer(args[0]).getStr(DPI.DISPLAY_NAME) + "&c!", "&c"));
						} catch (Exception e){
							main.s(p, "&c&oModification failed. Try a different value or stat.");
						}
					}
				}
			} else if (main.api.divManager.getMap(DivinityManager.allianceDir).containsKey(args[0])){
				for (DAI i : DAI.values()){
					if (i.s().equalsIgnoreCase(args[1])){
						try {
							String dispName = p instanceof Player ? ((Player) p).getDisplayName() : "&6Console";
							main.api.getDivAlliance(args[0]).set(i,  main.api.divUtils.createString(args, 2));
							main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", dispName + " &cmodified " + i.s() + " for " + args[0] + "&c!", "&c"));
						} catch (Exception e){
							main.s(p, "&c&oModification failed. Try a different value or stat.");
						}
					}
				}
			} else {
				main.s(p, "playerNotFound");
			}
			
		} else {
			main.s(p, main.help("modify", this));
		}
	}
	
	@DivCommand(aliases = {"list"}, desc = "List everyone online!", help = "/list", player = false)
	public void onList(CommandSender cs, String[] args){
		
		JSONChatMessage msg = new JSONChatMessage("");
		JSONChatExtra extra = new JSONChatExtra("");
		String m = "";
		boolean color = true;
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (cs instanceof Player){
				extra = new JSONChatExtra(main.AS((color ? "&3" : "&9") + ChatColor.stripColor(main.AS(p.getDisplayName())) + " "));
				extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&o" + p.getName()));
				extra.setClickEvent(JSONChatClickEventType.SUGGEST_COMMAND, "/tell " + p.getName() + " ");
				msg.addExtra(extra);
				color = !color;
			} else {
				m = m.equals("") ? p.getDisplayName() : m + "&8, " + p.getDisplayName();
			}
		}
		
		if (cs instanceof Player){
			msg.sendToPlayer((Player)cs);
		} else {
			main.s(cs, m);
		}
	}
	
	@DivCommand(aliases = {"div", "divinity"}, desc = "Divinity Main Command", help = "/ely help", player = false)
	public void onDivinity(CommandSender p, String[] args){
		
		String[] m1 = main.api.getAllModules().size() > 0 ? main.api.getAllModules().get(0).getClass().getName().split("\\.") : new String[]{"none"};
		String moduleList = "&6" + m1[m1.length-1];
		
		for (int x = 1; x < main.api.getAllModules().size(); x++){
			String[] m2 = main.api.getAllModules().get(x).getClass().getName().split("\\.");
			moduleList = moduleList + "&7, &6" + m2[m2.length-1];
		}
		
		for (String s : divLogo){
			p.sendMessage(main.AS(s));
		}
		
		p.sendMessage(main.AS(moduleList));
	}
	
	@DivCommand(aliases = {"ely", "elysian", "?"}, desc = "Elysian Main Command", help = "/ely help", player = false)
	public void onElysian(CommandSender p, String[] args){
		
		if (args.length == 0){
			
			for (String s : elyLogo){
				p.sendMessage(main.AS(s));
			}
			
		} else {
			
			switch (args[0].toLowerCase()){
			
				case "help": case "helpmepleaseidontknowwhatimdoing":
					
					fillMap();
					
					List<String> sortedHelp = new ArrayList<String>();
					
					for (String s : help.keySet()){
						sortedHelp.add(s);
					}
					
					Collections.sort(sortedHelp);
					
					if (p instanceof Player){
						for (String s : sortedHelp){
							JSONChatMessage message = new JSONChatMessage("", null, null);
							JSONChatExtra extra = new JSONChatExtra(main.AS("&3" + s + " &7\u2744 &6" + help.get(s)[0]), null, null);
							extra.setHoverEvent(JSONChatHoverEventType.SHOW_TEXT, main.AS("&7&o" + help.get(s)[1]));
							message.addExtra(extra);
							message.sendToPlayer(((Player)p));
						}
						p.sendMessage(main.AS("&7&oHover to display the description of each command"));
					} else {
						main.s(p, "&c&oConsole can't run this!");
					}
					
				break;
			
				case "save":
					
					if (main.perms(p, "wa.staff.admin")){
						try {
							main.api.divManager.save();
							main.onUnRegister();
							DivinityUtils.bc("Divinity has saved.");
							DivinityUtils.bc("&7&o" + main.api.divManager.getAllUsers().size() + " users, " + main.api.divManager.getMap(DivinityManager.allianceDir).size() + " alliances, and " + main.api.divManager.getMap(DivinityManager.regionsDir).size() + " regions.");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				break;
				
				case "reload":
					
					if (main.perms(p, "wa.staff.admin")){
					
						try {
							main.api.divManager.load(true);
							main.api.divManager.load(false);
							main.onRegister();
							DivinityUtils.bc("Divinity has reloaded.");
							DivinityUtils.bc("&7&o" + main.api.divManager.getAllUsers().size() + " users, " + main.api.divManager.getMap(DivinityManager.allianceDir).size() + " alliances, and " + main.api.divManager.getMap(DivinityManager.regionsDir).size() + " regions.");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				break;
				
				case "backup":
					
					if (main.perms(p, "wa.staff.admin")){
						 main.api.divManager.backup();
						 main.s(p, "Backup Complete!");
					}
					
				break;
			}
		}
	}
}