package com.github.lyokofirelyte.Elysian.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatClickEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatExtra;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatHoverEventType;
import com.github.lyokofirelyte.Divinity.JSON.JSONChatMessage;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyNewMember {

	 Elysian main;
	 
	 public ElyNewMember(Elysian i){
		 main = i;
	 }
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"newmember"}, desc = "Add a new member", help = "/newmember <user>", player = false, min = 1)
	 public void onNewMember(CommandSender p, String[] args){
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
	 
	 @DivCommand(perm = "wa.staff.intern", aliases = {"member"}, desc = "Send the forum link", help = "/member", player = false, min = 0)
	 public void onMember(CommandSender p, String[] args){
		 DivinityUtils.bc("--------------------------------------------");
		 
		 JSONChatMessage msg = new JSONChatMessage("", null, null);
		 JSONChatExtra extra = new JSONChatExtra(main.AS("&aClick here to sign up!"), null, null);
		 extra.setClickEvent(JSONChatClickEventType.OPEN_URL, "http://www.minecraftforum.net/forums/servers/pc-servers/hybrid-servers/773300-worlds-apart-1-7-survival-vanilla-creative-ranks");
		 msg.addExtra(extra);
		 msg.sendToAllPlayers();
		 
		 DivinityUtils.bc("--------------------------------------------");
	 }
	 
	 
	 
	 
	 
	 
	 
}