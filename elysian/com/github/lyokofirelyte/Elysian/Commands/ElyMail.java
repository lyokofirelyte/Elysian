package com.github.lyokofirelyte.Elysian.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyMail {

	 Elysian main;
	 
	 public ElyMail(Elysian i){
		 main = i;
	 }
	 
	 private String[] messages = new String[]{
		"/mail send <player> <message>",
		"/mail send staff <message>",
		"/mail send alliance <message>",
		"/mail send all <messsage>",
		"/mail read",
		"/mail clear"
	 };
	 
	 @DivCommand(aliases = {"mail"}, desc = "Elysian Mail Command", help = "/mail help", player = false, min = 1)
	 public void onMail(CommandSender p, String[] args){
		 
		 
		 String msg = args.length >= 3 ? args[2] : "";
		 String perm = "wa.member";
		 boolean send = true;
		 
		 for (int i = 3; i < args.length; i++){
			 msg = msg + " " + args[i];
		 }
		 
		  switch (args[0].toLowerCase()){
		  
		  	  case "read":
		  		  
		  		  checkMail(p);
		  		  
		  	  break;
		  	  
		  	  case "clear":
		  		  
		  		  DivinityPlayer clearing = null;
		  		  
		  		  if (p instanceof Player){
		  			clearing = main.api.getDivPlayer((Player)p);
		  		  } else {
		  			clearing = (DivinityPlayer) main.api.getSystem();
		  		  }
		  		  
		  		  if (clearing.getList(DPI.MAIL).size() > 0){
		  			  clearing.set(DPI.MAIL, new ArrayList<String>());
		  			  main.s(p, "none", "Cleared!");
		  		  } else {
		  			  main.s(p, "none", "&c&oYou have no mail.");
		  		  }
		  		  
		  	  break;

			  case "help":
				  
				  for (String s : messages){
					  main.s(p, "none", s);
				  }
				  
			  break;
			  
			  case "send":
				  
				  if (args.length < 3){
					  main.help("mail", this);
				  }
				  
				  switch (args[1]){
				  
					  case "staff":
						  
						  if (main.perms(p, "wa.staff.intern")){
							  perm = "wa.staff.intern";
						  } else {
							  send = false;
						  }
						  
					  break;
					  
					  case "alliance":
						  
						  if (p instanceof Player){
							  DivinityPlayer dp = main.api.getDivPlayer((Player)p);
							  if (!dp.getStr(DPI.ALLIANCE_NAME).equals("none")){
								  perm = "wa.alliance." + dp.getStr(DPI.ALLIANCE_NAME);
							  } else {
								  main.s(p, "none", "&c&oYou are not in an alliance.");
								  send = false;
							  }
						  } else {
							  main.s(p, "none", "&c&oConsole can not send mail to alliances.");
							  send = false;
						  }
						  
					  break;
					  
					  case "all":
						  
						  if (!main.perms(p, "wa.staff.intern")){
							  send = false;
						  }
						  
					  break;
					  
					  default:
						  
						  if (main.doesPartialPlayerExist(args[1])){
							  send = false;
							  main.matchDivPlayer(args[1]).getList(DPI.MAIL).add("personal" + "%SPLIT%" + p.getName() + "%SPLIT%" + msg);
							  if (Bukkit.getPlayer(main.matchDivPlayer(args[1]).uuid()) != null){
								  main.s(Bukkit.getPlayer(main.matchDivPlayer(args[1]).uuid()), "none", "You've recieved a mail! /mail read");
							  }
							  main.s(p, "Mail sent!");
						  } else {
							  main.s(p, "playerNotFound");
							  send = false;
						  }
						  
					  break;
				  
				  }
				  
				  if (send){
					  for (DivinityStorage dp : main.api.divManager.getAllUsers()){
						  if (dp.getList(DPI.PERMS).contains(perm)){
							  dp.getList(DPI.MAIL).add(perm + "%SPLIT%" + p.getName() + "%SPLIT%" + msg);
							  if (Bukkit.getPlayer(dp.uuid()) != null){
								  main.s(Bukkit.getPlayer(dp.uuid()), "none", "You've recieved a mail! /mail read");
							  }
						  }
					  }
					  main.s(p, "Mail sent!");
				  }
				  
			  break;
		  }
	 }
	 
	 public void checkMail(CommandSender p){
		 
		 DivinityPlayer reading = null;
 		  
 		  if (p instanceof Player){
 			  reading = main.api.getDivPlayer((Player)p);
 		  } else {
 			  reading = (DivinityPlayer) main.api.getSystem();
 		  }
 		  
 		  if (reading.getList(DPI.MAIL).size() > 0){
 			  
	  		  main.s(p, "none", "Reading mail &6(" + reading.getList(DPI.MAIL).size() + ")");
	  		  
 			  for (String s : reading.getList(DPI.MAIL)){
 				  
 				  String[] split = s.split("%SPLIT%");
 				  
 				  switch (split[0]){
 				  
	  				  case "personal":
	  					  
	  					  main.s(p, "none", "&6" + split[1] + " &7-> &6you&f: &7" + split[2]);
	  					  
	  				  break;
	  				  
	  				  case "wa.staff.intern":
	  					  
	  					  main.s(p, "none", "&6" + split[1] + " &7-> &cstaff&f: &7" + split[2]);
	  					  
	  				  break;
	  				  
	  				  case "wa.member":
	  					  
	  					  main.s(p, "none", "&6" + split[1] + " &7-> &2global&f: &7" + split[2]);
	  					  
	  				  break;
	  				  
	  				  default:
	  					  
	  					  if (s.startsWith("wa.alliance")){
	  						  main.s(p, "none", "&6" + split[1] + " &7-> &3alliance&f: &7" + split[2]);
	  					  }
	  					  
	  				  break;
 				  }
 			  }
 		  } else {
 			  main.s(p, "none", "&c&oYou have no mail.");
 		  }
	 }
}	