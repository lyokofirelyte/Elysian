package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.Storage.DAI;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityAlliance;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
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
			 dp.setDPI(DPI.DISPLAY_NAME, nick(dp, args[0]));
			 p.setPlayerListName(main.AS(dp.getDPI(DPI.DISPLAY_NAME)));
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
		 String inv = dp.getDPI(DPI.ALLIANCE_INVITE);
		 
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
		 				if (alliance.getIntDAI(DAI.TIER) < 10){
		 					alliance.setDAI(DAI.TIER, alliance.getIntDAI(DAI.TIER) + 1);
		 				} else {
		 					main.s(p, "&c&oThat alliance is already at max tier.");
		 				}
		 			} else {
		 				main.s(p, "&c&oThat alliance does not exist.");
		 			}
		 		}
		 		
		 	break;
		 	
		 	case "colors":
		 		
		 		if (dp.getBoolDPI(DPI.ALLIANCE_LEADER) || main.perms(p, "wa.staff.admin")){
		 			if (args.length == 4 && doesAllianceExist(args[1])){
		 				if (dp.getIntDPI(DPI.BALANCE) >= 50000){
		 					if (args[2].length() == 2 && args[2].startsWith("&") && args[3].length() == 2 && args[3].startsWith("&")){
		 						
		 						DivinityAlliance alliance = main.api.getDivAlliance(dp.getDPI(DPI.ALLIANCE_NAME));
		 						dp.setDPI(DPI.BALANCE, dp.getIntDPI(DPI.BALANCE) - 50000);
		 						
		 						for (String member : alliance.getListDAI(DAI.MEMBERS)){
		 							main.matchDivPlayer(UUID.fromString(member)).setDPI(DPI.ALLIANCE_COLOR_1, args[2]);
		 							main.matchDivPlayer(UUID.fromString(member)).setDPI(DPI.ALLIANCE_COLOR_2, args[3]);
		 						}
		 						
		 						for (Player pl : Bukkit.getOnlinePlayers()){
		 							if (main.api.getDivPlayer(pl).getDPI(DPI.ALLIANCE_NAME).equalsIgnoreCase(dp.getDPI(DPI.ALLIANCE_NAME))){
		 								pl.performCommand("nick " + ChatColor.stripColor(main.AS(main.api.getDivPlayer(pl).getDPI(DPI.DISPLAY_NAME))));
		 							}
		 						}
		 						
	 							alliance.setDAI(DAI.COLOR_1, args[2]);
	 							alliance.setDAI(DAI.COLOR_2, args[3]);
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
		 				if (p1.getDPI(DPI.ALLIANCE_NAME).equals(p2.getDPI(DPI.ALLIANCE_NAME))){
		 					if (p1.getBoolDPI(DPI.ALLIANCE_LEADER) && (dp.getDPI(DPI.ALLIANCE_NAME).equals(p1.getDPI(DPI.ALLIANCE_NAME)) || main.silentPerms(p, "wa.staff.admin"))){
		 						p1.setDPI(DPI.ALLIANCE_LEADER, false);
		 						p2.setDPI(DPI.ALLIANCE_LEADER, true);
		 						main.api.getDivAlliance(p1.getDPI(DPI.ALLIANCE_NAME)).setDAI(DAI.LEADER, p2.uuid().toString());
		 						DivinityUtils.bc(p2.getDPI(DPI.DISPLAY_NAME) + " &bis now the leader of " + main.coloredAllianceName(p1.getDPI(DPI.ALLIANCE_NAME)) + "&b!");
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
		 				
		 				leader.setDPI(DPI.ALLIANCE_NAME, args[1].toLowerCase());
		 				leader.setDPI(DPI.ALLIANCE_COLOR_1, args[2]);
		 				leader.setDPI(DPI.ALLIANCE_COLOR_2, args[3]);
		 				leader.setDPI(DPI.ALLIANCE_LEADER, true);
		 				leader.getListDPI(DPI.PERMS).add("wa.alliance." + args[1].toLowerCase());
		 				
		 				alliance.setDAI(DAI.BALANCE, 0);
		 				alliance.setDAI(DAI.TIER, 0);
		 				alliance.setDAI(DAI.COLOR_1, args[2]);
		 				alliance.setDAI(DAI.COLOR_2, args[3]);
		 				alliance.setDAI(DAI.NAME, args[1]);
		 				alliance.setDAI(DAI.LEADER, leader.uuid().toString());
		 				alliance.setDAI(DAI.CENTER, v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ());
		 				alliance.getListDAI(DAI.MEMBERS).add(leader.uuid().toString());

		 				p.performCommand("nick " + ChatColor.stripColor(main.AS(p.getDisplayName())));
		 				leader.setDPI(DPI.DISPLAY_NAME, p.getDisplayName());
		 				
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
		 			if (doesAllianceExist(args[1]) && (main.api.getDivAlliance(args[1]).name().equalsIgnoreCase(dp.getDPI(DPI.ALLIANCE_NAME)) && dp.getBoolDPI(DPI.ALLIANCE_LEADER))|| main.silentPerms(p, "wa.staff.admin")){
		 				
		 				DivinityUtils.bc(main.coloredAllianceName(args[1]) + " &bhas been disbanded.");
		 				DivinityAlliance alliance = main.api.getDivAlliance(args[1]);
		 				
		 				main.s(p, "Alliance funds transferred to you.");
		 				dp.setDPI(DPI.BALANCE, dp.getIntDPI(DPI.BALANCE) + alliance.getIntDAI(DAI.BALANCE));
		 				
		 				for (DivinityPlayer gone : main.api.divManager.getAllUsers()){
		 					if (gone.getDPI(DPI.ALLIANCE_NAME).equalsIgnoreCase(args[1])){
		 						removeFromAlliance(gone, gone.getDPI(DPI.ALLIANCE_NAME));
		 					}
		 				}
		 				
		 				main.api.divManager.getAllianceMap().remove(args[1].toLowerCase());
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
		 					
		 					if (dp.getIntDPI(DPI.BALANCE) >= Integer.parseInt(args[2])){
		 						
		 						dp.setDPI(DPI.BALANCE, dp.getIntDPI(DPI.BALANCE) - Integer.parseInt(args[2]));
		 						alliance.setDAI(DAI.BALANCE, alliance.getIntDAI(DAI.BALANCE) + Integer.parseInt(args[2]));
		 						main.s(p, "Donation successful!");
		 						
		 						for (String player : alliance.getListDAI(DAI.MEMBERS)){
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
		 			String theirAlliance = new String(them.getDPI(DPI.ALLIANCE_NAME));
		 			
		 			if (dp.getBoolDPI(DPI.ALLIANCE_LEADER) || main.perms(p, "wa.staff.admin")){
		 				
			 			if (!them.getBoolDPI(DPI.ALLIANCE_LEADER)){
			 				
			 				if (theirAlliance.equalsIgnoreCase(args[1]) || main.silentPerms(p, "wa.staff.admin")){
			 					removeFromAlliance(them, theirAlliance);
					 			DivinityUtils.bc(them.getDPI(DPI.DISPLAY_NAME) + " has been kicked from " + main.coloredAllianceName(theirAlliance) + "&b!");
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
		 		
		 		for (DivinityPlayer player : main.api.divManager.getAllUsers()){
		 			if (player.getBoolDPI(DPI.ALLIANCE_LEADER)){
		 				alliances.add(main.coloredAllianceName(player.getDPI(DPI.ALLIANCE_NAME)));
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
		 				List<String> members = alliance.getListDAI(DAI.MEMBERS);
		 				String players = main.matchDivPlayer(UUID.fromString(members.get(0))).getDPI(DPI.DISPLAY_NAME);
		 				
		 				for (int i = 1; i < members.size(); i++){
		 					players = players + "&7, " + main.matchDivPlayer(UUID.fromString(members.get(i))).getDPI(DPI.DISPLAY_NAME);
		 				}
		 				
		 				String[] messages = new String[]{
		 					"Alliance Name: " + main.coloredAllianceName(args[1]),
		 					"Leader: " + main.matchDivPlayer(UUID.fromString(alliance.getDAI(DAI.LEADER))).getDPI(DPI.DISPLAY_NAME),
		 					"Member Count: &6" + members.size(),
		 					"Tier: &6" + alliance.getDAI(DAI.TIER),
		 					"Balance: &6" + alliance.getDAI(DAI.BALANCE),
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
		 			if (dp.getBoolDPI(DPI.ALLIANCE_LEADER)){
		 				main.matchDivPlayer(args[1]).setDPI(DPI.ALLIANCE_INVITE, dp.getDPI(DPI.ALLIANCE_NAME) + " " + p.getName());
		 				main.s(p, "Invite sent!");
		 				if (main.isOnline(args[1])){
		 					main.s(main.getPlayer(args[1]), p.getDisplayName() + " &bhas invited you to join " + main.coloredAllianceName(dp.getDPI(DPI.ALLIANCE_NAME)).toUpperCase() + "&b.");
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
		 			if (dp.getDPI(DPI.ALLIANCE_NAME).equals("none")){
		 				
		 				DivinityAlliance alliance = main.api.getDivAlliance(inv.split(" ")[0]);
		 				alliance.getListDAI(DAI.MEMBERS).add(dp.uuid().toString());
		 				
		 				dp.setDPI(DPI.ALLIANCE_INVITE, "none");
		 				dp.setDPI(DPI.ALLIANCE_NAME, alliance.name());
		 				dp.setDPI(DPI.ALLIANCE_COLOR_1, alliance.getDAI(DAI.COLOR_1));
		 				dp.setDPI(DPI.ALLIANCE_COLOR_2, alliance.getDAI(DAI.COLOR_2));
		 				dp.getListDPI(DPI.PERMS).add("wa.alliance." + alliance.name());

		 				p.performCommand("nick " + ChatColor.stripColor(main.AS(p.getDisplayName())));
		 				dp.setDPI(DPI.DISPLAY_NAME, p.getDisplayName());
		 				DivinityUtils.bc(p.getDisplayName() + " &bhas joined " + main.coloredAllianceName(dp.getDPI(DPI.ALLIANCE_NAME)) + "!");
		 			} else {
		 				main.s(p, "&c&oYou're already in an alliance.");
		 			}
		 		} else {
		 			main.s(p, "&c&oYou don't have any invites.");
		 		}
		 		
		 	break;
		 	
		 	case "leave":

		 		if (!dp.getDPI(DPI.ALLIANCE_NAME).equals("none") && !dp.getBoolDPI(DPI.ALLIANCE_LEADER)){
		 			removeFromAlliance(dp, dp.getDPI(DPI.ALLIANCE_NAME));
		 			DivinityUtils.bc(p.getDisplayName() + " has left " + main.coloredAllianceName(dp.getDPI(DPI.ALLIANCE_NAME)) + "!");
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
		 			dp.setDPI(DPI.ALLIANCE_INVITE, "none");
		 		} else {
		 			main.s(p, "&c&oYou don't have any invites.");
		 		}
		 		
		 	break;
			 
			default:
				 
				if (!dp.getDPI(DPI.ALLIANCE_NAME).equals("none")){
					main.api.event(new DivinityChannelEvent(p, "wa.alliance." + dp.getDPI(DPI.ALLIANCE_NAME), "&aAlliance &2\u2744", args, "&a", DPI.ALLIANCE_TOGGLE));
				} else {
					main.s(p, "none", "You are not in an alliance :(");
				}
				
			break;
		 }
	 }

	 private void removeFromAlliance(DivinityPlayer player, String alliance){
		 
		 if (main.api.divManager.getAllianceMap().containsKey(alliance)){
			 main.api.getDivAlliance(alliance).getListDAI(DAI.MEMBERS).remove(player.uuid().toString());
		 }
		 
		 player.getListDPI(DPI.PERMS).remove("wa.alliance." + alliance);
		 player.setDPI(DPI.ALLIANCE_NAME, "none");
		 player.setDPI(DPI.ALLIANCE_LEADER, false);
		 player.setDPI(DPI.ALLIANCE_COLOR_1,  "&7");
		 player.setDPI(DPI.ALLIANCE_COLOR_2, "&7");
		 player.setDPI(DPI.DISPLAY_NAME, ChatColor.stripColor(main.AS(player.getDPI(DPI.DISPLAY_NAME))));
		 
		 if (main.isOnline(player.name())){
			 main.getPlayer(player.name()).setDisplayName("&7" + player.getDPI(DPI.DISPLAY_NAME));
			 main.getPlayer(player.name()).setPlayerListName(main.AS("&7" + player.getDPI(DPI.DISPLAY_NAME)));
		 }
	 }
	 
	 public String nick(DivinityPlayer dp, String arg){
		 String p1 = arg.substring(0, arg.length()/2);
		 String p2 = arg.substring((arg.length()/2));
		 return dp.getDPI(DPI.ALLIANCE_COLOR_1) + p1 + dp.getDPI(DPI.ALLIANCE_COLOR_2) + p2;
	 }
	 
	 private boolean doesAllianceExist(String alliance){
		 return main.api.divManager.getAllianceMap().containsKey(alliance.toLowerCase());
	 }
}