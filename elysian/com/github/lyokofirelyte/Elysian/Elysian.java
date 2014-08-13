package com.github.lyokofirelyte.Elysian;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.DivinityAPI;
import com.github.lyokofirelyte.Divinity.DivinityUtils;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Events.DivinityPluginMessageEvent;
import com.github.lyokofirelyte.Divinity.Events.ScoreboardUpdateEvent;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Storage.DAI;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRing;
import com.github.lyokofirelyte.Elysian.Commands.ElyEffects;
import com.github.lyokofirelyte.Elysian.Commands.ElyMail;
import com.github.lyokofirelyte.Elysian.Commands.ElyPerms;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.Commands.ElyRings;
import com.github.lyokofirelyte.Elysian.Commands.ElySpaceship;
import com.github.lyokofirelyte.Elysian.Commands.ElyStaff;
import com.github.lyokofirelyte.Elysian.Events.ElyChat;
import com.github.lyokofirelyte.Elysian.Events.ElyLogger;
import com.github.lyokofirelyte.Elysian.Events.ElyMobs;
import com.github.lyokofirelyte.Elysian.Events.ElyTP;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Elysian extends DivinityAPI {
	
	public YamlConfiguration markkitYaml;
	
	public Divinity api;
	public WorldEditPlugin we;
	
	public ElyEffects effects;
	public ElyPerms perms;
	public ElyMail mail;
	public ElyLogger logger;
	public ElyWatch watcher;
	public ElyStaff staff;
	public ElyAnnouncer announcer;
	public ElyChat chat;
	public ElySpaceship ss;
	public ElyTP tp;
	public ElyMobs mobs;
	public ElyProtect pro;
	public ElyRings rings;
	
	public DivInvManager invManager;
	
	public Map<ElyTask, Integer> tasks = new HashMap<ElyTask, Integer>();
	public Map<Location, List<List<String>>> queue = new HashMap<Location, List<List<String>>>();

	@Override
	public void onEnable(){
		new ElySetup(this).start();
	}
	
	@Override
	public void onDisable(){
		Bukkit.getScheduler().cancelTasks(this);
	}

	@Override
	public Divinity getApi(){
		return api;
	}
	
	public String coloredAllianceName(String alliance){
		String name = api.getDivAlliance(alliance).getDAI(DAI.NAME);
		String p1 = api.getDivAlliance(alliance).getDAI(DAI.COLOR_1);
		String p2 = api.getDivAlliance(alliance).getDAI(DAI.COLOR_2);
		return p1 + name.substring(0, name.length()/2) + p2 + name.substring(name.length()/2);
	}
	
	public void cancelTask(ElyTask task){
		if (tasks.containsKey(task)){
			Bukkit.getScheduler().cancelTask(tasks.get(task));
			tasks.remove(task);
		}
	}
	
	public boolean isOnline(String p){
		return Bukkit.getPlayer(matchDivPlayer(p).uuid()) != null;
	}
	
	public Player getPlayer(String p){
		return Bukkit.getPlayer(matchDivPlayer(p).uuid());
	}
	
	public boolean doesRegionExist(String region){
		return api.divManager.getRegionMap().containsKey(region);
	}

	public boolean doesPartialPlayerExist(String player){
		return api.divManager.searchForPlayer(player).containsKey(true);
	}
	
	public boolean doesRingExist(String ring){
		return api.divManager.getRingMap().containsKey(ring);
	}
	
	public DivinityRing getDivRing(String ring){
		return api.getDivRing(ring);
	}
	
	public DivinityPlayer matchDivPlayer(UUID uuid){
		for (DivinityPlayer dp : api.divManager.getAllUsers()){
			if (dp.uuid().equals(uuid)){
				return dp;
			}
		}
		return null;
	}
	
	public DivinityPlayer matchDivPlayer(String player){
		return api.divManager.searchForPlayer(player).get(true);
	}
	
	public String AS(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public void s(CommandSender s, String type){
		Bukkit.getPluginManager().callEvent(new DivinityPluginMessageEvent(s, type));
	}
	
	public void s(CommandSender s, String type, String message){
		Bukkit.getPluginManager().callEvent(new DivinityPluginMessageEvent(s, type, new String[]{message}));
	}
	
	public void s(CommandSender s, String type, String[] message){
		Bukkit.getPluginManager().callEvent(new DivinityPluginMessageEvent(s, type, message));
	}
	
	public boolean perms(CommandSender cs, String perm){
		if (cs instanceof Player){
			return perms((Player)cs, perm);
		} else if (cs.isOp()){
			return true;
		}
		return false;
	}
	
	public boolean silentPerms(CommandSender cs, String perm){
		if (cs instanceof Player){
			return silentPerms((Player)cs, perm);
		} else if (cs.isOp()){
			return true;
		}
		return false;
	}
	
	public boolean perms(Player p, String perm){
		if (api.getDivPlayer(p).getListDPI(DPI.PERMS).contains(perm) || p.isOp()){
			return true;
		}
		s(p, "noPerms");
		return false;
	}
	
	public boolean silentPerms(Player p, String perm){
		if (api.getDivPlayer(p).getListDPI(DPI.PERMS).contains(perm) || p.isOp()){
			return true;
		}
		return false;
	}
	
	public String help(String alias, Object o){
		for (Method method : o.getClass().getMethods()) {
			if (method.getAnnotation(DivCommand.class) != null){
				DivCommand anno = method.getAnnotation(DivCommand.class);
				if (anno.aliases()[0].equals(alias)){
					return anno.help();
				}
			}
		}
		return "No help found for this command";
	}
	
	public void afkCheck(Player p){

		DivinityPlayer system = api.getSystem();
		api.getDivPlayer(p).setDPI(DPI.AFK_TIME_INIT, 0);
		
		if (system.getListDPI(DPI.AFK_PLAYERS).contains(p.getName())){
			system.getListDPI(DPI.AFK_PLAYERS).remove(p.getName());
			DivinityUtils.bc(p.getDisplayName() + " &b&ois no longer away. (" + Math.round(((System.currentTimeMillis() - api.getDivPlayer(p).getLongDPI(DPI.AFK_TIME)) / 1000) / 60) + " minutes)");
			api.event(new ScoreboardUpdateEvent(p));
			p.setPlayerListName(AS(p.getDisplayName()));
		}
	}
	
	public void fw(World w, Location l, Type type, Color color){
		try {
			api.fw.playFirework(w, l, FireworkEffect.builder().with(type).withColor(color).build());
		} catch (Exception e){}
	}
}