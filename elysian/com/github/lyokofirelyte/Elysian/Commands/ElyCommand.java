package com.github.lyokofirelyte.Elysian.Commands;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
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
	
	Map<String, String[]> help = new HashMap<String, String[]>();
	
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
	
	@DivCommand(aliases = {"root", "menu"}, desc = "Open the main menu", help = "/root", player = true)
	public void onRoot(Player p, String[] args){
		main.invManager.displayGui(p, new GuiRoot(main));
	}
	
	@DivCommand(aliases = {"bio"}, desc = "Modify your hover-over description", help = "/bio <message>", player = true, min = 1)
	public void onBio(Player p, String[] args){
		
		DivinityPlayer dp = main.api.getDivPlayer(p);
		dp.set(DPI.PLAYER_DESC, "&7&o" + main.AS(main.api.divUtils.createString(args, 0)));
		main.s(p, "Updated!");
	}
	
	@DivCommand(aliases = {"enderdragon"}, desc = "Spawn the enderdragon in the end", help = "/enderdragon", player = true)
	public void onEnderDragon(Player p, String[] args){
		
		DivinitySystem system = main.api.getSystem();
		
		if (system.getLong(DPI.ENDERDRAGON_CD) <= System.currentTimeMillis()){
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
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				break;
				
				case "reload":
					
					if (main.perms(p, "wa.staff.admin")){
					
						try {
							main.api.divManager.load();
							main.onRegister();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				break;
			}
		}
	}
}