package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyModeration {

	 private Elysian main;
	 
	 public ElyModeration(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.rank.dweller", aliases = {"suicide"}, desc = "Goodbye, World!", help = "/suicide", player = true)
	 public void onSuicide(Player p, String[] args){
		 p.setHealth(0);
	 }
	 
	 @DivCommand(name = "Mute", perm = "wa.staff.mod", aliases = {"mute", "kik", "disable"}, desc = "Mute someone!", help = "/mute <player> <duration in minutes, default = 5>", player = false, min = 1, max = 2)
	 public void onMute(CommandSender cs, String[] args, String cmd){
		 
		 DivinityPlayer who = main.doesPartialPlayerExist(args[0]) ? main.matchDivPlayer(args[0]) : null;
		 Long time = args.length == 2 && main.api.divUtils.isInteger(args[1]) ? Integer.parseInt(args[1])*60L*1000L : (5L*60L) * 1000L;
		 String muter = cs instanceof Player ? ((Player)cs).getDisplayName() : "Console";
		 DPI effectType = cmd.equals("mute") ? DPI.MUTED : DPI.DISABLED;
		 DPI effectDelay = cmd.equals("mute") ? DPI.MUTE_TIME: DPI.DISABLE_TIME;
		 String type = effectType.equals(DPI.MUTED) ? "mute" : "disable";
		 
		 if (cmd.equals("kik") && who != null){
			 kick(cs, args, muter, who);
		 } else {
			 
			 if (who != null){
				 
				 if (who.getBool(effectType)){
					 who.set(effectType, false);
					 DivinityUtils.bc(muter + " &4&ohas released the " + type + " from " + who.getStr(DPI.DISPLAY_NAME) + "&4.");
				 } else {
					 who.set(effectDelay, System.currentTimeMillis() + time);
					 who.set(effectType, true);
					 DivinityUtils.bc(muter + " &4&ohas placed a " + type + " on " + who.getStr(DPI.DISPLAY_NAME) + " &4&ofor &6&o" + (time/1000)/60 + " &4&ominutes.");
				 }
				 
			 } else {
				 main.s(cs, "playerNotFound");
			 }
		 }
	 }
	 
	 private void kick(CommandSender cd, String[] args, String kicker, DivinityPlayer who){
		 String message = args.length > 1 ? "" : "no reason";
		 for (String s : args){
			 message = message + " " + s;
		 }
		 DivinityUtils.bc(kicker + " &4&ohas kicked " + who.getStr(DPI.DISPLAY_NAME) + " &e&o(&6&o" + message.replace(args[0], "").trim() + "&e&o)");
		 Bukkit.getPlayer(who.uuid()).kickPlayer(main.AS("&e&o(&6&o" + message.replace(args[0], "").trim() + "&e&o)"));
	 }
	 
	 @DivCommand(perm = "wa.staff.mod2", aliases = {"kill"}, desc = "Kill someone!", help = "/kill <player>", player = false, min = 1)
	 public void onKill(CommandSender cs, String[] args){
		 
		 String display = cs instanceof Player ? ((Player)cs).getDisplayName() : "&6Console";
		 
		 if (main.doesPartialPlayerExist(args[0]) && main.isOnline(args[0])){
			 main.getPlayer(args[0]).setHealth(0);
			 DivinityUtils.bc(display + " &4has REKT " + main.matchDivPlayer(args[0]).getStr(DPI.DISPLAY_NAME));
		 } else {
			 main.s(cs, "playerNotFound");
		 }
	 }
}