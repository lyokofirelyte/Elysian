package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyJoinQuit implements Listener {
	
	private Elysian main;
	
	public ElyJoinQuit(Elysian i){
		main = i;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		e.setJoinMessage(null);
		
		Player pl = e.getPlayer();
		DivinityPlayer p = main.api.getDivPlayer(pl);
		p.setDPI(DPI.AFK_TIME_INIT, 0);
		
		if (main.api.getSystem().getListDPI(DPI.AFK_PLAYERS).contains(pl.getName())){
			main.api.getSystem().getListDPI(DPI.AFK_PLAYERS).remove(pl.getName());
		}
		
		p.setDPI(DPI.LAST_LOGIN, main.api.divUtils.getTimeFull());
		pl.setPlayerListName(main.AS(p.getDPI(DPI.DISPLAY_NAME)));
		pl.setDisplayName(p.getDPI(DPI.DISPLAY_NAME));
		
		DivinityUtils.customBC("&2+ " + pl.getDisplayName() + " &e&o(" + p.getDPI(DPI.JOIN_MESSAGE) + "&e&o)");
		main.mail.checkMail(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		e.setQuitMessage(null);
		
		Player pl = e.getPlayer();
		DivinityPlayer p = main.api.getDivPlayer(e.getPlayer());
		p.setDPI(DPI.LAST_LOGOUT, main.api.divUtils.getTimeFull());
		p.setDPI(DPI.LOGOUT_LOCATION, pl.getLocation());
		p.setDPI(DPI.DISPLAY_NAME,  pl.getDisplayName());
		
		DivinityUtils.customBC("&4- " + pl.getDisplayName() + " &e&o(" + p.getDPI(DPI.QUIT_MESSAGE) + "&e&o)");
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e){
		e.setLeaveMessage(null);
	}
}