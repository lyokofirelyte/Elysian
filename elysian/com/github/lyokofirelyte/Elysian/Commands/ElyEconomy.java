package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;


public class ElyEconomy {

	 Elysian main;
	 
	 public ElyEconomy(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(aliases = {"pay"}, desc = "Pay someone money!", help = "/pay <player> <amount>", player = true, min = 2)
	 public void onPay(Player p, String[] args){
		 
		 DivinityPlayer dp = main.api.getDivPlayer(p);
		 int bal = 0;
		 DivinityPlayer who = null;
		 
		 if (main.doesPartialPlayerExist(args[0])){
			 
			 if (main.api.divUtils.isInteger(args[1]) && dp.getIntDPI(DPI.BALANCE) > (bal = Integer.parseInt(args[1]))){
				 
				 who = main.matchDivPlayer(args[0]);
				 who.setDPI(DPI.BALANCE, who.getIntDPI(DPI.BALANCE)+bal);
				 dp.setDPI(DPI.BALANCE, dp.getIntDPI(DPI.BALANCE)-bal);
				 
				 main.s(p, "none", "You sent &6" + bal + " &bto " + who.getDPI(DPI.DISPLAY_NAME) + "&b.");
				 
				 if (Bukkit.getPlayer(who.uuid()) != null){
		 			main.s(Bukkit.getPlayer(who.uuid()), "none", "You were paid &6" + bal + " &bby " + p.getDisplayName() + "&b.");
				 }
				 
			 } else {
				 main.s(p, "invalidNumber");
			 }
			 
		 } else {
			 main.s(p, "playerNotFound");
		 }
	 }
	 
	 @DivCommand(aliases = {"balance", "bal"}, desc = "Ely Bal Command", help = "/balance <set, take, give, top> [player] [amount]", player = true, min = 0, max = 3)
	 public void onBal(Player p, String[] args){
		 
		 if (args.length == 0){
			 main.s(p, "balance");
		 } else {
			
			 switch (args[0].toLowerCase()){
			 
			 	default:
			 		
			 		main.s(p, "none", main.help("balance", this));
			 		
			 	break;
			 
			 	case "top":
			 		balTop(p);
				break;
				
			 	case "set": case "take": case "give":
						
			 		if (main.perms(p, "wa.staff.admin")){
			 			
				 		DivinityPlayer who = null;
				 		int bal = 0;
				 		
			 			if (main.doesPartialPlayerExist(args[1])){
			 				
			 				if (main.api.divUtils.isInteger(args[2])){
			 						
			 					bal = Integer.parseInt(args[2]);
			 						
			 					if (bal > 1 && bal < 5000000){
			 						who = main.matchDivPlayer(args[1]);
			 						
						 			if (args[0].toLowerCase().equals("take")){
						 				
						 				who.setDPI(DPI.BALANCE, who.getIntDPI(DPI.BALANCE)-bal);
						 				
						 				if (who.getIntDPI(DPI.BALANCE) < 0){
						 					who.setDPI(DPI.BALANCE, 0);
						 				}
						 				
						 			} else if (args[0].toLowerCase().equals("give")){
						 				who.setDPI(DPI.BALANCE, who.getIntDPI(DPI.BALANCE)+bal);
						 			} else {
						 				who.setDPI(DPI.BALANCE, bal);
						 			}
						 			
						 			main.s(p, "none", "Set the balance of &6" + who.getDPI(DPI.DISPLAY_NAME) + " &bto &6" + who.getDPI(DPI.BALANCE) + "&b.");
						 			
						 			if (Bukkit.getPlayer(who.uuid()) != null){
						 				main.s(Bukkit.getPlayer(who.uuid()), "none", "Your balance was set to &6" + who.getDPI(DPI.BALANCE) + " &bby " + p.getDisplayName() + "&b.");
						 			}
						 			
			 					} else {
			 						main.s(p, "invalidNumber");
			 					}
			 				} else {
			 					main.s(p, "invalidNumber");
			 				}
			 			} else {
				 			main.s(p, "playerNotFound");
				 		}
			 		}
			 	break;
			 }
		 }
	 }
	 
	 private void balTop(Player sendTo){
		 
		 List<Integer> balances = new ArrayList<>();
		 Map<Integer, DivinityPlayer> players = new HashMap<Integer, DivinityPlayer>();
		 int serverTotal = 0;
		 
		 for (DivinityPlayer p : main.api.divManager.getAllUsers()){
			 if (p.getIntDPI(DPI.BALANCE) > 2000){
				 balances.add(p.getIntDPI(DPI.BALANCE));
				 players.put(p.getIntDPI(DPI.BALANCE), p);
				 serverTotal = serverTotal + p.getIntDPI(DPI.BALANCE);
			 }
		 }
		 
		 Collections.sort(balances);
		 Collections.reverse(balances);
		 
		 main.s(sendTo, "none", "Top Balances");
		 main.s(sendTo, "none", "&6Server Total: &b" + serverTotal);
		 
		 for (int i = 0; i < 10; i++){
			 main.s(sendTo, players.get(balances.get(i)).getDPI(DPI.DISPLAY_NAME) + "&f: &6" + balances.get(i));
		 }
	 }
}