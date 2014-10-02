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
import com.github.lyokofirelyte.Divinity.Storage.ElySkill;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

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
		p.set(DPI.AFK_TIME_INIT, 0);
		
		if (main.api.getSystem().getList(DPI.AFK_PLAYERS).contains(pl.getName())){
			main.api.getSystem().getList(DPI.AFK_PLAYERS).remove(pl.getName());
		}
		
		p.set(DPI.LAST_LOGIN, main.api.divUtils.getTimeFull());
		pl.setPlayerListName(main.AS(p.getStr(DPI.DISPLAY_NAME)));
		pl.setDisplayName(p.getStr(DPI.DISPLAY_NAME));
		
		DivinityUtils.customBC("&2<+> " + pl.getDisplayName() + " &e&o(" + p.getStr(DPI.JOIN_MESSAGE) + "&e&o)");
		pl.sendMessage("");
		String[] msg = new String[2];
		msg[0] = "&3Welcome back! We're running Elysian & Divinity v1.1.";
		
		if (p.getList(DPI.MAIL).size() > 0){
			msg[1] = "You've got mail! /mail read or /mail clear.";
		} else {
			msg[1] = "&7&oNo new messages.";
		}
		
		defaultCheck(p);
		
		if (p.getBool(MMO.IS_SOUL_SPLITTING)){
			main.mmo.soulSplit.stop(pl, p);
		}
		
		p.s(msg[0]);
		p.s(msg[1]);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		e.setQuitMessage(null);
		
		Player pl = e.getPlayer();
		DivinityPlayer p = main.api.getDivPlayer(e.getPlayer());
		p.set(DPI.LAST_LOGOUT, main.api.divUtils.getTimeFull());
		p.set(DPI.LOGOUT_LOCATION, pl.getLocation());
		p.set(DPI.DISPLAY_NAME,  pl.getDisplayName());
		p.set(DPI.SPECTATING, false);
		
		if (!p.getStr(DPI.SPECTATE_TARGET).equals("none")){
			main.matchDivPlayer(p.getStr(DPI.SPECTATE_TARGET)).set(DPI.SPECTATE_TARGET, "none");
			main.matchDivPlayer(p.getStr(DPI.SPECTATE_TARGET)).set(DPI.SPECTATING, false);
			p.set(DPI.SPECTATE_TARGET, "none");
		}
		
		if (main.mmo.patrols.doesPatrolExistWithPlayer(pl)){
			main.mmo.patrols.getPatrolWithPlayer(pl).getMembers().remove(pl.getName());
		}
		
		DivinityUtils.customBC("&4<-> " + pl.getDisplayName() + " &e&o(" + p.getStr(DPI.QUIT_MESSAGE) + "&e&o)");
		p.remHologram();
		p.clearEffects();
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e){
		e.setLeaveMessage(null);
	}
	
	private void defaultCheck(DivinityPlayer p){
		
		for (ElySkill s : ElySkill.values()){
			if (p.getStr(s).equals("none")){
				p.set(s, "0 0 100");
			}
		}
		
		if (p.getStr(DPI.XP_DISP_NAME_TOGGLE).equals("none")){
			p.set(DPI.XP_DISP_NAME_TOGGLE, true);
		}
	}
}