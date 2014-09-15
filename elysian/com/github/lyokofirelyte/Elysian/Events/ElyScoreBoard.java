package com.github.lyokofirelyte.Elysian.Events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystem;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class ElyScoreBoard implements Listener {
	
	private Elysian main;
	
	public ElyScoreBoard(Elysian i){
		main = i;
	}
	
	@EventHandler
	public void onScoreBoard(ScoreboardUpdateEvent e){
		
		Player p = e.getPlayer();
		DivinityPlayer dp = main.api.getDivPlayer(p);
		DivinitySystem system = main.api.getSystem();
		boolean a = false;
		
		if (e.isCancelled() || (!dp.getBool(DPI.SCOREBOARD_TOGGLE) && !e.getReason().equals("required"))){
			if (!dp.getBool(DPI.SCOREBOARD_TOGGLE)){
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			return;
		}
		
		String[] scoreNames = new String[]{
			"§3Shinies: ",
			"§9Online: ",
			"§3Exp: ",
			"§4Blood: ",
			"§5/root"
		};
		
		Integer[] scoreValues = new Integer[]{
			dp.getInt(DPI.BALANCE),
			Bukkit.getOnlinePlayers().length,
			dp.getInt(DPI.EXP),
			dp.getInt(MMO.VAMP_BAR),
			0
		};
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective o = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		
		if (o == null){
			o = board.registerNewObjective("wa", "dummy");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			a = !a;
		}
		
		if (system.getList(DPI.AFK_PLAYERS).contains(p.getName())){
			
			o.setDisplayName("§7[ afk " + getMinutes(Long.parseLong(dp.getStr(DPI.AFK_TIME))) + " ]");
			
		} else {
			
			String loc = Math.round(p.getLocation().toVector().getX()) + " " + Math.round(p.getLocation().toVector().getY()) + " " + Math.round(p.getLocation().toVector().getZ());
			
			if (loc.length() > 16){
				loc = loc.substring(0, 16);
			}
			
			o.setDisplayName("§3" + loc);
		}
		
		for (int x = 0; x < scoreNames.length; x++){
			Score s = o.getScore(scoreNames[x]);
			s.setScore(scoreValues[x]);
		}
		
		if (a){
			p.setScoreboard(board);
		}
	}
	
	private String getMinutes(Long l){
		Long time = System.currentTimeMillis() - l;
		Long seconds = time/1000;
		return (seconds/60) + "";
	}
}