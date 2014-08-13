package com.github.lyokofirelyte.Elysian;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;

public class ElyAnnouncer implements Runnable {
	
	private Elysian main;
	
	public ElyAnnouncer(Elysian i){
		main = i;
	}

	@Override
	public void run(){
		
		DivinityPlayer dp = main.api.getSystem();
		List<String> messages = dp.getListDPI(DPI.ANNOUNCER);
		int index = dp.getIntDPI(DPI.ANNOUNCER_INDEX);
		
		if (messages.size() > index){
			DivinityUtils.bc(dp.getListDPI(DPI.ANNOUNCER).get(index));
			dp.setDPI(DPI.ANNOUNCER_INDEX, index + 1);
		} else if (messages.size() > 0){
			DivinityUtils.bc(messages.get(0));
			dp.setDPI(DPI.ANNOUNCER_INDEX, 1);
		} else {
			dp.setDPI(DPI.ANNOUNCER_INDEX, 0);
		}
	}
	
	public void stop(){
		main.cancelTask(ElyTask.ANNOUNCER);
	}
	
	@DivCommand(aliases= {"announcer"}, perm = "wa.staff.mod2", help = "/announcer <add, remove, view> <message>", player = false, min = 1)
	public void onAnnounceCommand(CommandSender cs, String[] args){
		
		List<String> messages = main.api.getSystem().getListDPI(DPI.ANNOUNCER);
		String msg = args.length > 1 ? new String(args[1]) : "";
		
		for (int x = 2; x < args.length; x++){
			msg = msg + " " + args[x];
		}
		
		switch (args[0]){
		
			case "add":
				
				if (!messages.contains(msg)){
					messages.add(msg);
					main.s(cs, "none", "Message added!");
				} else {
					main.s(cs, "none", "&c&oMessage already present.");
				}
				
			break;
			
			case "remove":
				
				if (messages.contains(msg)){
					messages.remove(msg);
					main.s(cs, "none", "Message removed!");
				} else {
					main.s(cs, "none", "&c&oMessage not present.");
				}
				
			break;
			
			case "view":
				
				for (String s : messages){
					main.s(cs, "none", s);
				}
				
			break;
		}
	}
}