package com.github.lyokofirelyte.Elysian;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;

public class ElyWatch implements Runnable {
	
	private Elysian main;
	
	public ElyWatch(Elysian i){
		main = i;
	}
	
	List<String> worlds = Arrays.asList(
		"WACP",
		"Keopi",
		"Tripolis",
		"Syracuse",
		"not_cylum",
		"WCN_Builds",
		"creative"
	);

	@Override
	public void run(){
		DivinityPlayer system = main.api.getSystem();
		for (Player p : Bukkit.getOnlinePlayers()){
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.setDPI(DPI.IN_COMBAT, false);
			afkCheck(p, dp, system);
			nameCheck(p, dp);
			unMuteCheck(p, dp);
			creativeCheck(p);
			rankCheck(dp);
			moneyCheck(p, dp);
			main.api.event(new ScoreboardUpdateEvent(p));
		}
		system.setDPI(DPI.ROLLBACK_IN_PROGRESS, false);
	}
	
	private void creativeCheck(Player p){
		
		if (worlds.contains(p.getWorld().getName())){
			if (!p.getGameMode().equals(GameMode.CREATIVE)){
				p.setGameMode(GameMode.CREATIVE);
			}
			if (!p.getAllowFlight()){
				p.setAllowFlight(true);
			}
			if (p.getWalkSpeed() < 0.2F){
				p.setWalkSpeed(0.2F);
			}
			if (p.getFlySpeed() < 0.2F){
				p.setFlySpeed(0.2F);
			}
		}
	}
	
	private void unMuteCheck(Player p, DivinityPlayer dp){
		
		if (dp.getBoolDPI(DPI.MUTED)){
			if (System.currentTimeMillis() >= dp.getLongDPI(DPI.MUTE_TIME)){
				dp.setDPI(DPI.MUTED, false);
				DivinityUtils.bc("&4&oThe mute placed on " + p.getDisplayName() + " &4&ohas expired.");
			}
		}
		
		if (dp.getBoolDPI(DPI.DISABLED)){
			if (System.currentTimeMillis() >= dp.getLongDPI(DPI.DISABLE_TIME)){
				dp.setDPI(DPI.DISABLED, false);
				DivinityUtils.bc("&4&oThe disable placed on " + p.getDisplayName() + " &4&ohas expired.");
			}
		}
	}
	
	private void nameCheck(Player p, DivinityPlayer dp){
		
		if (!ChatColor.stripColor(main.AS(p.getDisplayName())).startsWith(ChatColor.stripColor(main.AS(dp.getDPI(DPI.DISPLAY_NAME).substring(0, 3))))){
			main.s(p, "none", "Invalid nickname detected, resetting...");
			p.setDisplayName("&7" + p.getName());
		}
		
		if (dp.getDPI(DPI.ALLIANCE_NAME).equals("none") && !p.getDisplayName().startsWith("&7")){
			p.setDisplayName("&7" + dp.getDPI(DPI.DISPLAY_NAME));
		}
	}
	
	private void afkCheck(Player p, DivinityPlayer dp, DivinityPlayer system){
		
		if (dp.getLongDPI(DPI.AFK_TIME_INIT) <= 0){
			dp.setDPI(DPI.AFK_TIME_INIT, System.currentTimeMillis());
		}
		
		if (!system.getListDPI(DPI.AFK_PLAYERS).contains(p.getName()) && System.currentTimeMillis() >= dp.getLongDPI(DPI.AFK_TIME_INIT) + 180000L){
			dp.setDPI(DPI.AFK_TIME, System.currentTimeMillis());
			system.getListDPI(DPI.AFK_PLAYERS).add(p.getName());
			DivinityUtils.bc(p.getDisplayName() + " &b&ois now away.");
			if (main.AS("&7[afk] " + p.getDisplayName()).length() > 16){
				p.setPlayerListName(main.AS("&7[afk] " + p.getDisplayName()).substring(0, 15));
			} else {
				p.setPlayerListName(main.AS("&7[afk] " + p.getDisplayName()));
			}
		}
	}
	
	private void rankCheck(DivinityPlayer dp){
		
		if (dp.getListDPI(DPI.PERMS).contains("wa.staff.intern")){
			for (String rank : main.perms.staffGroups){
				if (dp.getListDPI(DPI.PERMS).contains("wa.staff." + rank) && !dp.getDPI(DPI.RANK_NAME).contains("WCN")){
					switch (rank){
						case "admin":
							dp.setDPI(DPI.RANK_NAME, "&4WCN"); 
							dp.setDPI(DPI.STAFF_DESC, "&7&oAn administrator of the server.\n&7&oResponsible for server management.\n&7&oPlugin Devs: Hugs, Winneon, Msnijder");
						break;
						case "mod2": case "mod+":
							dp.setDPI(DPI.RANK_NAME, "&9WCN");
							dp.setDPI(DPI.STAFF_DESC, "&7&oAn expirenced moderator.\n&7&oResponsible for all moderation actions and community well-being.\n&7&oAccess to most commands.");
						break;
						case "mod":
							dp.setDPI(DPI.RANK_NAME, "&2WCN");
							dp.setDPI(DPI.STAFF_DESC, "&7&oA moderator of the server.\n&7&oResponsible for chat, helping people, grief checks, and general server support.");
						break;
						case "intern":
							dp.setDPI(DPI.RANK_NAME, "&aWCN");
							dp.setDPI(DPI.STAFF_DESC, "&7&oNew staff of the server.\n&7&oResponsible for improving their focus on the server.\n&7&oCan check griefs and provide general help to members.");
						break;
					}
				}
			}
		}
	}
	
	private void moneyCheck(Player p, DivinityPlayer dp){
		
		if (dp.getIntDPI(DPI.MOB_MONEY) > 0){
			dp.setDPI(DPI.BALANCE, dp.getIntDPI(DPI.BALANCE) + dp.getIntDPI(DPI.MOB_MONEY));
			main.s(p, dp.getIntDPI(DPI.MOB_MONEY) + " &oshinies earned.");
			dp.setDPI(DPI.MOB_MONEY, 0);
		}
	}
}