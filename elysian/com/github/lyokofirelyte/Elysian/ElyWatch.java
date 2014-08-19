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
import com.github.lyokofirelyte.Divinity.Storage.DivinitySystem;

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
		DivinitySystem system = main.api.getSystem();
		for (Player p : Bukkit.getOnlinePlayers()){
			DivinityPlayer dp = main.api.getDivPlayer(p);
			dp.set(DPI.IN_COMBAT, false);
			afkCheck(p, dp, system);
			nameCheck(p, dp);
			unMuteCheck(p, dp);
			creativeCheck(p);
			rankCheck(dp);
			moneyCheck(p, dp);
			main.api.event(new ScoreboardUpdateEvent(p));
		}
		system.set(DPI.ROLLBACK_IN_PROGRESS, false);
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
		
		if (dp.getBool(DPI.MUTED)){
			if (System.currentTimeMillis() >= dp.getLong(DPI.MUTE_TIME)){
				dp.set(DPI.MUTED, false);
				DivinityUtils.bc("&4&oThe mute placed on " + p.getDisplayName() + " &4&ohas expired.");
			}
		}
		
		if (dp.getBool(DPI.DISABLED)){
			if (System.currentTimeMillis() >= dp.getLong(DPI.DISABLE_TIME)){
				dp.set(DPI.DISABLED, false);
				DivinityUtils.bc("&4&oThe disable placed on " + p.getDisplayName() + " &4&ohas expired.");
			}
		}
	}
	
	private void nameCheck(Player p, DivinityPlayer dp){
		
		if (!ChatColor.stripColor(main.AS(p.getDisplayName())).startsWith(ChatColor.stripColor(main.AS(dp.getStr(DPI.DISPLAY_NAME).substring(0, 3))))){
			main.s(p, "none", "Invalid nickname detected, resetting...");
			p.setDisplayName("&7" + p.getName());
		}
		
		if (dp.getStr(DPI.ALLIANCE_NAME).equals("none") && !p.getDisplayName().startsWith("&7")){
			p.setDisplayName("&7" + dp.getStr(DPI.DISPLAY_NAME));
		}
	}
	
	private void afkCheck(Player p, DivinityPlayer dp, DivinitySystem system){
		
		if (dp.getLong(DPI.AFK_TIME_INIT) <= 0){
			dp.set(DPI.AFK_TIME_INIT, System.currentTimeMillis());
		}
		
		if (!system.getList(DPI.AFK_PLAYERS).contains(p.getName()) && System.currentTimeMillis() >= dp.getLong(DPI.AFK_TIME_INIT) + 180000L){
			dp.set(DPI.AFK_TIME, System.currentTimeMillis());
			system.getList(DPI.AFK_PLAYERS).add(p.getName());
			DivinityUtils.bc(p.getDisplayName() + " &b&ois now away.");
			if (main.AS("&7[afk] " + p.getDisplayName()).length() > 16){
				p.setPlayerListName(main.AS("&7[afk] " + p.getDisplayName()).substring(0, 15));
			} else {
				p.setPlayerListName(main.AS("&7[afk] " + p.getDisplayName()));
			}
		}
	}
	
	private void rankCheck(DivinityPlayer dp){
		
		if (dp.getList(DPI.PERMS).contains("wa.staff.intern")){
			for (String rank : main.perms.staffGroups){
				if (dp.getList(DPI.PERMS).contains("wa.staff." + rank) && !dp.getStr(DPI.RANK_NAME).contains("WCN")){
					switch (rank){
						case "admin":
							dp.set(DPI.RANK_NAME, "&4WCN"); 
							dp.set(DPI.STAFF_DESC, "&7&oAn administrator of the server.\n&7&oResponsible for server management.\n&7&oPlugin Devs: Hugs, Winneon, Msnijder");
						break;
						case "mod2": case "mod+":
							dp.set(DPI.RANK_NAME, "&9WCN");
							dp.set(DPI.STAFF_DESC, "&7&oAn expirenced moderator.\n&7&oResponsible for all moderation actions and community well-being.\n&7&oAccess to most commands.");
						break;
						case "mod":
							dp.set(DPI.RANK_NAME, "&2WCN");
							dp.set(DPI.STAFF_DESC, "&7&oA moderator of the server.\n&7&oResponsible for chat, helping people, grief checks, and general server support.");
						break;
						case "intern":
							dp.set(DPI.RANK_NAME, "&aWCN");
							dp.set(DPI.STAFF_DESC, "&7&oNew staff of the server.\n&7&oResponsible for improving their focus on the server.\n&7&oCan check griefs and provide general help to members.");
						break;
					}
				}
			}
		}
	}
	
	private void moneyCheck(Player p, DivinityPlayer dp){
		
		if (dp.getInt(DPI.MOB_MONEY) > 0){
			dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + dp.getInt(DPI.MOB_MONEY));
			main.s(p, dp.getInt(DPI.MOB_MONEY) + " &oshinies earned from various mobs.");
			dp.set(DPI.MOB_MONEY, 0);
		}
	}
}