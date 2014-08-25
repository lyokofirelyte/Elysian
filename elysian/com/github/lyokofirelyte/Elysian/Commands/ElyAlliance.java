package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Storage.DAI;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityAlliance;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyAlliance {

	 Elysian main;
	 
	 public ElyAlliance(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(aliases = {"nick"}, desc = "Change your nickname!", help = "/nick <name>", player = true, min = 1)
	 public void onNick(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 args[0] = ChatColor.stripColor(args[0]);
		 
		 if (!args[0].toLowerCase().startsWith(dp.name().substring(0, 3).toLowerCase())){
			 main.s(p, "none", "You must at least use the first 3 letters of your name.");
		 } else {
			 if (args[0].length() > 11){
				 args[0] = args[0].substring(0, 11);
			 }
			 p.setDisplayName(nick(dp, args[0]));
			 dp.set(DPI.DISPLAY_NAME, nick(dp, args[0]));
			 p.setPlayerListName(main.AS(dp.getStr(DPI.DISPLAY_NAME)));
			 main.s(p, "none", "Display name changed to " + main.AS(p.getDisplayName()) + "&b.");
		 }
	 }
	 
	 @DivCommand(aliases = {"rn", "realname"}, desc = "Check for someone's real name", help = "/rn <name>", player = false, min = 1)
	 public void onRealName(CommandSender p, String[] args){
		 
		 for (Player pp : Bukkit.getOnlinePlayers()){
			 if (ChatColor.stripColor(main.AS(pp.getDisplayName())).toLowerCase().contains(args[0].toLowerCase())){
				 main.s(p, "none", main.AS(pp.getDisplayName() + " &6-> &b" + pp.getName()));
			 }
		 }
	 }
	 
	 @DivCommand(aliases = {"a", "alliance"}, desc = "Elysian Alliance Command", help = "/a help", player = true, min = 1)
	 public void onAllianceCommand(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 String inv = dp.getStr(DPI.ALLIANCE_INVITE);
		 
		 switch (args[0]){
		 	
		 	case "help":
				 
		 		String[] help = new String[] {
		 			"/a list",
		 			"/a info <name>",
		 			"/a pay <name> <monies>",
					"/a invite <player>",
					"/a accept",
					"/a decline",
					"/a kick <player>",
					"/a leave",
					"/a colors <alliance> <color1> <color2>",
					"/a create <name> <color1> <color2> <leader>",
					"/a upgrade <name>",
					"/a disband <name>",
					"/a transfer <name> <name>",
					"/a <message for chat>",
					"/toggle alliance (in/out of chat)"
		 		};
				   
		 		for (String s : help){
		 			main.s(p, "none", s);
		 		}

		 	break;
		 	
		 	case "upgrade":
		 		
		 		if (main.perms(p, "wa.staff.mod")){
		 			if (args.length == 2 && doesAllianceExist(args[1])){
		 				DivinityAlliance alliance = main.api.getDivAlliance(args[1]);
		 				if (alliance.getInt(DAI.TIER) < 10){
		 					alliance.set(DAI.TIER, alliance.getInt(DAI.TIER) + 1);
		 				} else {
		 					main.s(p, "&c&oThat alliance is already at max tier.");
		 				}
		 			} else {
		 				main.s(p, "&c&oThat alliance does not exist.");
		 			}
		 		}
		 		
		 	break;
		 	
		 	case "colors":
		 		
		 		if (dp.getBool(DPI.ALLIANCE_LEADER) || main.perms(p, "wa.staff.admin")){
		 			if (args.length == 4 && doesAllianceExist(args[1])){
		 				if (dp.getInt(DPI.BALANCE) >= 50000){
		 					if (args[2].length() == 2 && args[2].startsWith("&") && args[3].length() == 2 && args[3].startsWith("&")){
		 						
		 						DivinityAlliance alliance = main.api.getDivAlliance(dp.getStr(DPI.ALLIANCE_NAME));
		 						dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - 50000);
		 						
		 						for (String member : alliance.getList(DAI.MEMBERS)){
		 							main.matchDivPlayer(UUID.fromString(member)).set(DPI.ALLIANCE_COLOR_1, args[2]);
		 							main.matchDivPlayer(UUID.fromString(member)).set(DPI.ALLIANCE_COLOR_2, args[3]);
		 						}
		 						
		 						for (Player pl : Bukkit.getOnlinePlayers()){
		 							if (main.api.getDivPlayer(pl).getStr(DPI.ALLIANCE_NAME).equalsIgnoreCase(dp.getStr(DPI.ALLIANCE_NAME))){
		 								pl.performCommand("nick " + ChatColor.stripColor(main.AS(main.api.getDivPlayer(pl).getStr(DPI.DISPLAY_NAME))));
		 							}
		 						}
		 						
	 							alliance.set(DAI.COLOR_1, args[2]);
	 							alliance.set(DAI.COLOR_2, args[3]);
		 					} else {
		 						main.s(p, "&c&o/colors <alliance> <color 1> <color 2>.");
		 					}
		 				} else {
		 					main.s(p, "&c&oYou need 50k for this!");
		 				}
		 			} else {
		 				main.s(p, "&c&oThat alliance does not exist!");
		 			}
		 		}
		 		
		 	break;
		 	
		 	case "transfer":
		 		
		 		if (args.length == 3){
		 			if (main.doesPartialPlayerExist(args[1]) && main.doesPartialPlayerExist(args[2])){
		 				DivinityPlayer p1 = main.matchDivPlayer(args[1]);
		 				DivinityPlayer p2 = main.matchDivPlayer(args[2]);
		 				if (p1.getStr(DPI.ALLIANCE_NAME).equals(p2.getStr(DPI.ALLIANCE_NAME))){
		 					if (p1.getBool(DPI.ALLIANCE_LEADER) && (dp.getStr(DPI.ALLIANCE_NAME).equals(p1.getStr(DPI.ALLIANCE_NAME)) || main.silentPerms(p, "wa.staff.admin"))){
		 						p1.set(DPI.ALLIANCE_LEADER, false);
		 						p2.set(DPI.ALLIANCE_LEADER, true);
		 						main.api.getDivAlliance(p1.getStr(DPI.ALLIANCE_NAME)).set(DAI.LEADER, p2.uuid().toString());
		 						DivinityUtils.bc(p2.getStr(DPI.DISPLAY_NAME) + " &bis now the leader of " + main.coloredAllianceName(p1.getStr(DPI.ALLIANCE_NAME)) + "&b!");
		 					} else {
		 						main.s(p, "&c&oThe first player must be the leader.");
		 					}
		 				} else {
		 					main.s(p, "&c&oBoth players must be in the same alliance.");
		 				}
		 			} else {
		 				main.s(p, "playerNotFound");
		 			}
		 		} else {
		 			main.s(p, "/a transfer <player> <player>");
		 		}
		 		
		 	break;

		 	case "create":
		 		
		 		if (args.length == 5){
		 			if (!doesAllianceExist(args[1])){
		 				
		 				DivinityAlliance alliance = main.api.getDivAlliance(args[1].toLowerCase());
		 				DivinityPlayer leader = main.matchDivPlayer(args[4]);
		 				Vector v = p.getLocation().toVector();
		 				
		 				leader.set(DPI.ALLIANCE_NAME, args[1].toLowerCase());
		 				leader.set(DPI.ALLIANCE_COLOR_1, args[2]);
		 				leader.set(DPI.ALLIANCE_COLOR_2, args[3]);
		 				leader.set(DPI.ALLIANCE_LEADER, true);
		 				leader.getList(DPI.PERMS).add("wa.alliance." + args[1].toLowerCase());
		 				
		 				alliance.set(DAI.BALANCE, 0);
		 				alliance.set(DAI.TIER, 0);
		 				alliance.set(DAI.COLOR_1, args[2]);
		 				alliance.set(DAI.COLOR_2, args[3]);
		 				alliance.set(DAI.NAME, args[1]);
		 				alliance.set(DAI.LEADER, leader.uuid().toString());
		 				alliance.set(DAI.CENTER, v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ());
		 				alliance.getList(DAI.MEMBERS).add(leader.uuid().toString());

		 				p.performCommand("nick " + ChatColor.stripColor(main.AS(p.getDisplayName())));
		 				leader.set(DPI.DISPLAY_NAME, p.getDisplayName());
		 				
		 				DivinityUtils.bc(main.coloredAllianceName(args[1]) + " &bhas been formed!");
		 			} else {
		 				main.s(p, "&c&oAlliance already exists.");
		 			}
		 		} else {
		 			main.s(p, "/a create <name> <color 1> <color2> <leader>");
		 		}
		 		
		 	break;
		 	
		 	case "disband":
		 		
		 		if (args.length == 2){
		 			if (doesAllianceExist(args[1]) && (main.api.getDivAlliance(args[1]).name().equalsIgnoreCase(dp.getStr(DPI.ALLIANCE_NAME)) && dp.getBool(DPI.ALLIANCE_LEADER))|| main.silentPerms(p, "wa.staff.admin")){
		 				
		 				DivinityUtils.bc(main.coloredAllianceName(args[1]) + " &bhas been disbanded.");
		 				DivinityAlliance alliance = main.api.getDivAlliance(args[1]);
		 				
		 				main.s(p, "Alliance funds transferred to you.");
		 				dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + alliance.getInt(DAI.BALANCE));
		 				
		 				for (DivinityStorage gone : main.api.divManager.getAllUsers()){
		 					if (gone.getStr(DPI.ALLIANCE_NAME).equalsIgnoreCase(args[1])){
		 						removeFromAlliance((DivinityPlayer)gone, gone.getStr(DPI.ALLIANCE_NAME));
		 					}
		 				}
		 				
		 				main.api.divManager.getMap(DivinityManager.allianceDir).remove(args[1].toLowerCase());
		 				new File("./plugins/Divinity/alliances/" + args[1].toLowerCase() + ".yml").delete();
		 				
		 			} else {
		 				main.s(p, "&c&oNo permissions, or that alliance does not exist.");
		 			}
		 		} else {
		 			main.s(p, "/a disband <name>");
		 		}
		 		
		 	break;
		 	
		 	case "pay": case "donate":
		 		
		 		if (args.length == 3){
		 			
		 			if (doesAllianceExist(args[1])){
		 				
		 				DivinityAlliance alliance = main.api.getDivAlliance(args[1]);
		 				
		 				if (main.api.divUtils.isInteger(args[2]) && Integer.parseInt(args[2]) > 0){
		 					
		 					if (dp.getInt(DPI.BALANCE) >= Integer.parseInt(args[2])){
		 						
		 						dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - Integer.parseInt(args[2]));
		 						alliance.set(DAI.BALANCE, alliance.getInt(DAI.BALANCE) + Integer.parseInt(args[2]));
		 						main.s(p, "Donation successful!");
		 						
		 						for (String player : alliance.getList(DAI.MEMBERS)){
		 							if (Bukkit.getPlayer(UUID.fromString(player)) != null){
		 								main.s(Bukkit.getPlayer(UUID.fromString(player)), p.getDisplayName() + " &bhas donated &6" + args[2] + " &bto your alliance!");
		 							}
		 						}
		 						
		 					} else {
		 						main.s(p, "&c&oNot enough money! :(");
		 					}
		 				} else {
		 					main.s(p, "invalidNumber");
		 				}
		 			} else {
		 				main.s(p, "&c&oThat alliance does not exist.");
		 			}
		 		} else {
		 			main.s(p, "/a pay <name> <monies>");
		 		}
		 		
		 	break;
		 	
		 	case "kick":
		 		
		 		if (args.length == 2 && main.doesPartialPlayerExist(args[1])){
		 			
		 			DivinityPlayer them = main.matchDivPlayer(args[1]);
		 			String theirAlliance = new String(them.getStr(DPI.ALLIANCE_NAME));
		 			
		 			if (dp.getBool(DPI.ALLIANCE_LEADER) || main.perms(p, "wa.staff.admin")){
		 				
			 			if (!them.getBool(DPI.ALLIANCE_LEADER)){
			 				
			 				if (theirAlliance.equalsIgnoreCase(args[1]) || main.silentPerms(p, "wa.staff.admin")){
			 					removeFromAlliance(them, theirAlliance);
					 			DivinityUtils.bc(them.getStr(DPI.DISPLAY_NAME) + " has been kicked from " + main.coloredAllianceName(theirAlliance) + "&b!");
			 				} else {
			 					main.s(p, "&c&oThey are not in an alliance, or at least not yours.");
			 				}
			 				
			 			} else {
			 				main.s(p, "&c&oYou can't kick leaders. Make someone else leader first.");
			 			}
		 			}
		 		}
		 		
		 	break;
		 	
		 	case "list":
		 		
		 		List<String> alliances = new ArrayList<String>();
		 		String msg = "&6";
		 		
		 		for (DivinityStorage player : main.api.divManager.getAllUsers()){
		 			if (player.getBool(DPI.ALLIANCE_LEADER)){
		 				alliances.add(main.coloredAllianceName(player.getStr(DPI.ALLIANCE_NAME)));
		 			}
		 		}
		 		
		 		if (alliances != null && alliances.size() > 0){
		 			msg = msg + alliances.get(0);
		 			for (int i = 1; i < alliances.size(); i++){
		 				msg = msg + "&7, &6" + alliances.get(i);
		 			}
		 			main.s(p, msg);
		 		} else {
		 			main.s(p, "&c&oThere are no alliances.");
		 		}
		 		
		 	break;
		 	
		 	case "info":
		 		
		 		if (args.length == 2){
		 			if (doesAllianceExist(args[1])){
		 				
		 				DivinityAlliance alliance = main.api.getDivAlliance(args[1]);
		 				List<String> members = alliance.getList(DAI.MEMBERS);
		 				String players = main.matchDivPlayer(UUID.fromString(members.get(0))).getStr(DPI.DISPLAY_NAME);
		 				
		 				for (int i = 1; i < members.size(); i++){
		 					players = players + "&7, " + main.matchDivPlayer(UUID.fromString(members.get(i))).getStr(DPI.DISPLAY_NAME);
		 				}
		 				
		 				String[] messages = new String[]{
		 					"Alliance Name: " + main.coloredAllianceName(args[1]),
		 					"Leader: " + main.matchDivPlayer(UUID.fromString(alliance.getStr(DAI.LEADER))).getStr(DPI.DISPLAY_NAME),
		 					"Member Count: &6" + members.size(),
		 					"Tier: &6" + alliance.getStr(DAI.TIER),
		 					"Balance: &6" + alliance.getStr(DAI.BALANCE),
		 					"Members: " + players,
		 				};
		 				
		 				for (String m : messages){
		 					main.s(p, m);
		 				}
		 				
		 			} else {
		 				main.s(p, "&c&oThat alliance does not exist.");
		 			}
		 			
		 		} else {
		 			main.s(p, "/a info <name>");
		 		}
		 		
		 	break;
		 	
		 	case "invite":
		 		
		 		if (args.length == 2 && main.doesPartialPlayerExist(args[1])){
		 			if (dp.getBool(DPI.ALLIANCE_LEADER)){
		 				main.matchDivPlayer(args[1]).set(DPI.ALLIANCE_INVITE, dp.getStr(DPI.ALLIANCE_NAME) + " " + p.getName());
		 				main.s(p, "Invite sent!");
		 				if (main.isOnline(args[1])){
		 					main.s(main.getPlayer(args[1]), p.getDisplayName() + " &bhas invited you to join " + main.coloredAllianceName(dp.getStr(DPI.ALLIANCE_NAME)).toUpperCase() + "&b.");
		 					main.s(main.getPlayer(args[1]), "Type &6/a accept &bor &6/a deny&b.");
		 				}
		 			} else {
		 				main.s(p, "&c&oOnly the leader can do this.");
		 			}
		 		} else {
		 			main.s(p, "playerNotFound");
		 		}
		 		
		 	break;
		 	
		 	case "accept":
		 		
		 		if (!inv.equals("none")){
		 			if (dp.getStr(DPI.ALLIANCE_NAME).equals("none")){
		 				
		 				DivinityAlliance alliance = main.api.getDivAlliance(inv.split(" ")[0]);
		 				alliance.getList(DAI.MEMBERS).add(dp.uuid().toString());
		 				
		 				dp.set(DPI.ALLIANCE_INVITE, "none");
		 				dp.set(DPI.ALLIANCE_NAME, alliance.name());
		 				dp.set(DPI.ALLIANCE_COLOR_1, alliance.getStr(DAI.COLOR_1));
		 				dp.set(DPI.ALLIANCE_COLOR_2, alliance.getStr(DAI.COLOR_2));
		 				dp.getList(DPI.PERMS).add("wa.alliance." + alliance.name());

		 				p.performCommand("nick " + ChatColor.stripColor(main.AS(p.getDisplayName())));
		 				dp.set(DPI.DISPLAY_NAME, p.getDisplayName());
		 				DivinityUtils.bc(p.getDisplayName() + " &bhas joined " + main.coloredAllianceName(dp.getStr(DPI.ALLIANCE_NAME)) + "!");
		 			} else {
		 				main.s(p, "&c&oYou're already in an alliance.");
		 			}
		 		} else {
		 			main.s(p, "&c&oYou don't have any invites.");
		 		}
		 		
		 	break;
		 	
		 	case "leave":

		 		if (!dp.getStr(DPI.ALLIANCE_NAME).equals("none") && !dp.getBool(DPI.ALLIANCE_LEADER)){
		 			removeFromAlliance(dp, dp.getStr(DPI.ALLIANCE_NAME));
		 			DivinityUtils.bc(p.getDisplayName() + " has left " + main.coloredAllianceName(dp.getStr(DPI.ALLIANCE_NAME)) + "!");
		 		} else {
		 			main.s(p, "&c&oYou're not in an alliance or you are the leader.");
		 		}
		 		
		 	break;
		 	
		 	case "decline": case "deny":
		 		
		 		if (!inv.equals("none")){
		 			if (main.isOnline(inv.split(" ")[0])){
		 				main.s(main.getPlayer(inv.split(" ")[0]), "&c&oInvite declined.");
		 			}
		 			main.s(p, "&c&oDeclined.");
		 			dp.set(DPI.ALLIANCE_INVITE, "none");
		 		} else {
		 			main.s(p, "&c&oYou don't have any invites.");
		 		}
		 		
		 	break;
			 
			default:
				 
				if (!dp.getStr(DPI.ALLIANCE_NAME).equals("none")){
					main.api.event(new DivinityChannelEvent(p, "wa.alliance." + dp.getStr(DPI.ALLIANCE_NAME), "&aAlliance &2\u2744", args, "&a", DPI.ALLIANCE_TOGGLE));
				} else {
					main.s(p, "none", "You are not in an alliance :(");
				}
				
			break;
		 }
	 }

	 private void removeFromAlliance(DivinityPlayer player, String alliance){
		 
		 if (main.api.divManager.getMap(DivinityManager.allianceDir).containsKey(alliance)){
			 main.api.getDivAlliance(alliance).getList(DAI.MEMBERS).remove(player.uuid().toString());
		 }
		 
		 player.getList(DPI.PERMS).remove("wa.alliance." + alliance);
		 player.set(DPI.ALLIANCE_NAME, "none");
		 player.set(DPI.ALLIANCE_LEADER, false);
		 player.set(DPI.ALLIANCE_COLOR_1,  "&7");
		 player.set(DPI.ALLIANCE_COLOR_2, "&7");
		 player.set(DPI.DISPLAY_NAME, ChatColor.stripColor(main.AS(player.getStr(DPI.DISPLAY_NAME))));
		 
		 if (main.isOnline(player.name())){
			 main.getPlayer(player.name()).setDisplayName("&7" + player.getStr(DPI.DISPLAY_NAME));
			 main.getPlayer(player.name()).setPlayerListName(main.AS("&7" + player.getStr(DPI.DISPLAY_NAME)));
		 }
	 }
	 
	 public String nick(DivinityPlayer dp, String arg){
		 String p1 = arg.substring(0, arg.length()/2);
		 String p2 = arg.substring((arg.length()/2));
		 return dp.getStr(DPI.ALLIANCE_COLOR_1) + p1 + dp.getStr(DPI.ALLIANCE_COLOR_2) + p2;
	 }
	 
	 private boolean doesAllianceExist(String alliance){
		 return main.api.divManager.getMap(DivinityManager.allianceDir).containsKey(alliance.toLowerCase());
	 }
}