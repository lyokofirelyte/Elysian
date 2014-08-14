package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyNewMember {

	 Elysian main;
	 
	 public ElyNewMember(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"newmember"}, desc = "Add a new member", help = "/newmember <user>", player = false, min = 1)
	 public void onMail(CommandSender p, String[] args){
		 if(p instanceof Player){
			 Player player = (Player)p;
			 player.performCommand("perms add " + args[0] + " wa.member");
			 main.s(p, "You gave " + args[0] + " member, yay");
		 }else{
			 main.getServer().dispatchCommand(main.getServer().getConsoleSender(), "perms add " + args[0] + " wa.member");
			 main.s(p, "You gave " + args[0] + " member, yay");
		 }
		 
		 if(main.isOnline(args[0])){
			 Player target = main.getPlayer(args[0]);
			 target.performCommand("rankup");
		 }
	 }
}