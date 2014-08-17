package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Divinity.Events.DivinityChannelEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyChannel implements Listener {
	
	private Elysian main;
	
	public ElyChannel(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onChannelMessage(DivinityChannelEvent e){
		
		if (e.isCancelled()){
			return;
		}

		String header = e.getHeader();
		String message = e.getMessage();
		String perm = e.getPerm();
		String color = e.getColor();
		String sender = "";
		DPI toggle = e.getToggle();
		
		if (e.getSender() != null){
			sender = e.getSender().getDisplayName();
		} else {
			sender = e.getSenderName();
		}
		
		for (Player player : Bukkit.getOnlinePlayers()){
			DivinityPlayer to = main.api.getDivPlayer(player);
			if (!toggle.equals(DPI.ELY)){
				if (to.getList(DPI.PERMS).contains(perm) && to.getBool(toggle)){
					player.sendMessage(main.AS(header + " " + sender + "&f: " + color + message));
				}
			} else {
				if (to.getList(DPI.PERMS).contains(perm)){
					player.sendMessage(main.AS(header + " " + sender + "&f: " + color + message));
				}
			}
		}
	}
}