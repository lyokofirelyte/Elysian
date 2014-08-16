package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyNewMember {

	 Elysian main;
	 
	 public ElyNewMember(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"newmember"}, desc = "Add a new member", help = "/newmember <user>", player = false, min = 1)
	 public void onRankUp(CommandSender p, String[] args){
		 
		 if (main.doesPartialPlayerExist(args[0])){
			 main.matchDivPlayer(args[0]).getListDPI(DPI.PERMS).add("wa.member");
			 main.s(p, "Added permissions!");
			 
			 if (main.isOnline(args[0])){
				 main.getPlayer(args[0]).performCommand("rankup");
			 }
			 
		 } else {
			 main.s(p, "playerNotFound");
		 }
	 }
}