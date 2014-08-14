package com.github.lyokofirelyte.Elysian.Commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Divinity.Storage.DAI;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
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
		"&6&o/ely help"
	};
	
	String[] elyLogo = new String[]{
		"&b. . . . .&f(  &3E  l  y  s  i  a  n &f  )&b. . . . .",	
		"",
		"&7&oA MC Operating System by Hugs",
		"&6&o/ely help"
	};
	
	Map<String, String[]> help = new HashMap<String, String[]>();
	
	private void fillMap(){
		help.put("/announcer", s("Mod2+", "Adjust the auto-announcer"));
		help.put("/a", s("Member+", "Alliance root command"));
		help.put("/bal", s("Member, Admin+", "Economy commands"));
		help.put("/pay", s("Member+", "Pay someone!"));
		help.put("/mail", s("Member+", "Mail system"));
		help.put("/mute", s("Mod+", "Silence someone"));
		help.put("/kick", s("Mod+", "Remove someone from the server"));
		help.put("/perms", s("Admin+", "Adjust permissions for players"));
		help.put("/staff", s("Member+", "View current staff - auto-updates"));
		help.put("/back", s("Mod2+", "Return to your previous location"));
		help.put("/gm", s("Mod2+", "Gamemode switcher"));
		help.put("/fly", s("Mod2+", "Toggle flight abilities"));
		help.put("/tp", s("Mod2+", "Teleport to a player or destination"));
		help.put("/tpa", s("Statesman+", "Request a TP to someone"));
		help.put("/tpahere", s("Emperor+", "Request that someone TP to you"));
		help.put("/tpaccept", s("Member+", "Accept a teleport request"));
		help.put("/tpdeny", s("Member+", "Deny a teleport request"));
		help.put("/tpall", s("Admin+", "Teleport everyone to your location"));
		help.put("/tpblock", s("Citizen+", "Prevent anyone from TPing within 5 blocks of you"));
		help.put("/home", s("Member+", "Return to a saved location"));
		help.put("/o", s("Intern+", "Staff chat"));
		help.put("/toggle", s("Member+", "Toggle menu"));
		help.put("/sm", s("Mod2+", "Spawn mob command"));
		help.put("/more", s("Mod2+", "Set the amount of items in your hand to 64"));
		help.put("/i", s("Mod2+", "Get a stack of an item by name or ID"));
		help.put("/nick", s("Member+", "Rename yourself for display purposes"));
		help.put("/skull", s("Mod2+", "Give yourself someone's skill head"));
		help.put("/top", s("Mod2+", "Teleport to the top-most block"));
		help.put("/ci", s("Townsman+", "Clear your inventory"));
		help.put("/tell, /pm, /t, /msg", s("Member+", "Private message someone"));
		help.put("/r", s("Member+", "Respond to your latest PM"));
		help.put("/log", s("Intern+", "Logger/rollback tool"));
		help.put("/chest", s("Member+", "Chest protection command"));
		help.put("/modify", s("Admin+", "Directly modify any stat for people or alliances\n&4&oDO NOT ABUSE!"));
		help.put("/disable", s("Mod+", "Prevent someone from executing commands and moving"));
		help.put("/firework", s("Statesman+", "Some fireworks will appear!"));
		help.put("/flare", s("National+", "A burst of fireworks will appear!"));
		help.put("/bio", s("Member+", "Modify your hover-over description"));
		help.put("/kill", s("Mod2+", "Kill someone instantly\n&7&o*moderation purposes only"));
		help.put("/filter", s("Mod2+", "Modify chat and command filters"));
		help.put("/qc", s("Member+", "Quick chat command for sharing stats"));
		help.put("/exp", s("Member+", "Store and take XP from your XP storage unit"));
		help.put("/warp", s("Mod2+", "Warp to a location"));
		help.put("/setwarp", s("Mod2+", "Add a location at your current position"));
		help.put("/remwarp", s("Mod2+", "Remove a warp"));
		help.put("/spawn, /s", s("Member+", "Teleport to spawn"));
		help.put("/sudo", s("Admin+", "Force someone to do something"));
		help.put("/suicide", s("Dweller+", "Goodbye, world!"));
		help.put("/protect", s("Mod2+", "Elysian region protection command"));
		help.put("/rings", s("Member+", "Elysian transport system"));
		help.put("/heal", s("Mod+", "Heals you to full health"));
		help.put("/feed", s("National+", "Fills your food bar to max"));
		help.put("/root", s("Member+", "Main Menu"));
		help.put("/vanish", s("Mod+", "Renders you invisible to all players"));
		help.put("/creative", s("Member+", "Teleport into or out of the creative world"));
		help.put("/spectate", s("Mod2+", "View someone in first person"));
		help.put("/seen", s("Dweller+", "View traffic stats for a player"));
		help.put("/newmember <player>", s("Intern+", "Gives a player member permissions"));
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
		dp.setDPI(DPI.PLAYER_DESC, "&7&o" + main.AS(main.api.divUtils.createString(args, 0)));
		main.s(p, "Updated!");
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
		StringBuilder text = new StringBuilder();
		for(String s : args){
			text.append(s + " ");
		}
		DivinityUtils.bc(text.toString());
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
							main.matchDivPlayer(args[0]).setDPI(i, main.api.divUtils.createString(args, 2));
							main.api.event(new DivinityChannelEvent("&6System", "wa.staff.intern", "&c&oOh! &4\u2744", dispName + " &cmodified " + i.s() + " for " + main.matchDivPlayer(args[0]).getDPI(DPI.DISPLAY_NAME) + "&c!", "&c"));
						} catch (Exception e){
							main.s(p, "&c&oModification failed. Try a different value or stat.");
						}
					}
				}
			} else if (main.api.divManager.getAllianceMap().containsKey(args[0])){
				for (DAI i : DAI.values()){
					if (i.s().equalsIgnoreCase(args[1])){
						try {
							String dispName = p instanceof Player ? ((Player) p).getDisplayName() : "&6Console";
							main.api.getDivAlliance(args[0]).setDAI(i,  main.api.divUtils.createString(args, 2));
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
		for (String s : divLogo){
			p.sendMessage(main.AS(s));
		}
	}
	
	@DivCommand(aliases = {"ely", "elysian"}, desc = "Elysian Main Command", help = "/ely help", player = false)
	public void onElysian(CommandSender p, String[] args){
		
		if (args.length == 0){
			
			for (String s : elyLogo){
				p.sendMessage(main.AS(s));
			}
			
		} else {
			
			switch (args[0].toLowerCase()){
			
				case "help": case "helpmepleaseidontknowwhatimdoing":
					
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
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				break;
				
				case "reload":
					
					if (main.perms(p, "wa.staff.admin")){
					
						try {
							main.api.divManager.load();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				break;
			}
		}
	}
}