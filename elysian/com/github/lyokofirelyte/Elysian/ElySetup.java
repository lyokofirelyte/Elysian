package com.github.lyokofirelyte.Elysian;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Elysian.Commands.ElyAlliance;
import com.github.lyokofirelyte.Elysian.Commands.ElyCommand;
import com.github.lyokofirelyte.Elysian.Commands.ElyEconomy;
import com.github.lyokofirelyte.Elysian.Commands.ElyEffects;
import com.github.lyokofirelyte.Elysian.Commands.ElyHome;
import com.github.lyokofirelyte.Elysian.Commands.ElyMail;
import com.github.lyokofirelyte.Elysian.Commands.ElyModeration;
import com.github.lyokofirelyte.Elysian.Commands.ElyPerms;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.Commands.ElyRanks;
import com.github.lyokofirelyte.Elysian.Commands.ElyRings;
import com.github.lyokofirelyte.Elysian.Commands.ElySpaceship;
import com.github.lyokofirelyte.Elysian.Commands.ElyStaff;
import com.github.lyokofirelyte.Elysian.Commands.ElyToggle;
import com.github.lyokofirelyte.Elysian.Commands.ElyWarps;
import com.github.lyokofirelyte.Elysian.Events.ElyChannel;
import com.github.lyokofirelyte.Elysian.Events.ElyChat;
import com.github.lyokofirelyte.Elysian.Events.ElyJoinQuit;
import com.github.lyokofirelyte.Elysian.Events.ElyLogger;
import com.github.lyokofirelyte.Elysian.Events.ElyMessages;
import com.github.lyokofirelyte.Elysian.Events.ElyMobs;
import com.github.lyokofirelyte.Elysian.Events.ElyMove;
import com.github.lyokofirelyte.Elysian.Events.ElyScoreBoard;
import com.github.lyokofirelyte.Elysian.Events.ElyTP;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class ElySetup {

	private Elysian main;
	
	public ElySetup(Elysian i){
		main = i;
	}
	
	public void start(){
		main.api = (Divinity) Bukkit.getPluginManager().getPlugin("Divinity");
		main.we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		main.markkitYaml = YamlConfiguration.loadConfiguration(new File("./plugins/Divinity/markkit.yml"));
		main.logger = new ElyLogger(main);
		main.watcher = new ElyWatch(main);
		main.staff = new ElyStaff(main);
		main.announcer = new ElyAnnouncer(main);
		main.tp = new ElyTP(main);
		main.mail = new ElyMail(main);
		main.chat = new ElyChat(main);
		main.ss = new ElySpaceship(main);
		main.effects = new ElyEffects(main);
		main.perms = new ElyPerms(main);
		main.mobs = new ElyMobs(main);
		main.pro = new ElyProtect(main);
		main.rings = new ElyRings(main);
		main.invManager = new DivInvManager(main.api);
		listener();
		commands();
		tasks();
	}

	private void regList(Listener... classes){
		for (Listener o : classes){
			Bukkit.getPluginManager().registerEvents(o, main);
		}
	}
	
	private void listener(){
		regList(
			new ElyJoinQuit(main),
			new ElyMessages(main),
			new ElyMove(main),
			new ElyScoreBoard(main),
			new ElyChannel(main),
			new ElyMarkkit(main),
			main.mobs,
			main.staff,
			main.logger,
			main.chat,
			main.tp,
			main.ss,
			main.pro,
			main.rings,
			main.invManager
		);
	}
	
	private void commands(){
		main.api.divReg.registerCommands(
			new ElyEconomy(main), 
			new ElyAlliance(main),
			new ElyToggle(main),
			new ElyModeration(main),
			new ElyCommand(main),
			new ElyRanks(main),
			new ElyHome(main),
			new ElyWarps(main),
			main.mail,
			main.logger,
			main.staff,
			main.announcer,
			main.chat,
			main.tp,
			main.perms,
			main.ss,
			main.effects,
			main.mobs,
			main.pro,
			main.rings
		);
	}
	
	private void tasks(){
		main.tasks.put(ElyTask.ANNOUNCER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.announcer, 0L, 1200000L));
		main.tasks.put(ElyTask.LOGGER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.logger, 300L, 300L));
		main.tasks.put(ElyTask.WATCHER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.watcher, 500L, 500L));
	}
}