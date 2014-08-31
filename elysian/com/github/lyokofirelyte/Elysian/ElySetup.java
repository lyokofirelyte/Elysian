package com.github.lyokofirelyte.Elysian;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Elysian.Commands.ElyAlliance;
import com.github.lyokofirelyte.Elysian.Commands.ElyCommand;
import com.github.lyokofirelyte.Elysian.Commands.ElyEconomy;
import com.github.lyokofirelyte.Elysian.Commands.ElyEffects;
import com.github.lyokofirelyte.Elysian.Commands.ElyHome;
import com.github.lyokofirelyte.Elysian.Commands.ElyMail;
import com.github.lyokofirelyte.Elysian.Commands.ElyModeration;
import com.github.lyokofirelyte.Elysian.Commands.ElyNewMember;
import com.github.lyokofirelyte.Elysian.Commands.ElyPerms;
import com.github.lyokofirelyte.Elysian.Commands.ElyProtect;
import com.github.lyokofirelyte.Elysian.Commands.ElyRanks;
import com.github.lyokofirelyte.Elysian.Commands.ElyRings;
import com.github.lyokofirelyte.Elysian.Commands.ElySpaceship;
import com.github.lyokofirelyte.Elysian.Commands.ElySpectate;
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
import com.github.lyokofirelyte.Elysian.Gui.GuiCloset;
import com.github.lyokofirelyte.Elysian.Gui.GuiRoot;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.LifeForce;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SkyBlade;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SuperBreaker;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.TreeFeller;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class ElySetup {

	private Elysian main;
	
	public ElySetup(Elysian i){
		main = i;
	}
	
	public void start(){
		main.api = (Divinity) Bukkit.getPluginManager().getPlugin("Divinity");
		main.we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		main.logger = new ElyLogger(main);
		main.mmo = new ElyMMO(main);
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
		main.cleanup = new ElyMMOCleanup(main);
		main.mmo.treeFeller = new TreeFeller(main);
		main.mmo.superBreaker = new SuperBreaker(main);
		main.mmo.skyBlade = new SkyBlade(main);
		main.mmo.life = new LifeForce(main);
		closet();
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
			main.invManager,
			main.mmo
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
			new ElySpectate(main),
			new ElyNewMember(main),
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
			main.rings,
			main.mmo,
			main.closets.get(0)
		);
	}
	
	private void tasks(){
		main.tasks.put(ElyTask.ANNOUNCER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.announcer, 0L, 1200000L));
		main.tasks.put(ElyTask.MMO_BLOCKS, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.cleanup, 0L, 21600000L));
		main.tasks.put(ElyTask.LOGGER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.logger, 300L, 300L));
		main.tasks.put(ElyTask.WATCHER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.watcher, 500L, 500L));
	}
	
	private void closet(){
		
		for (int i = 0; i < 5; i++){
			main.closets.put(i, new GuiCloset(main, i == 0 ? new GuiRoot(main) : main.closets.get(i-1)));
		}
		
		for (int i = 0; i < 5; i++){
			main.closets.get(i).create();
		}
		
		for (ItemStack i : main.api.getSystem().getStack(DPI.CLOSET_ITEMS)){
			for (int x = 0; x < 5; x++){
				if (main.closets.get(x).getInv().firstEmpty() != -1){
					main.closets.get(x).getInv().addItem(i);
					break;
				}
			}
		}
	}
}