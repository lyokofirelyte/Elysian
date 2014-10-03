package com.github.lyokofirelyte.Elysian;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.github.lyokofirelyte.Divinity.Divinity;
import com.github.lyokofirelyte.Divinity.Manager.DivInvManager;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Manager.RecipeHandler;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
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
import com.github.lyokofirelyte.Elysian.Commands.ElyWealth;
import com.github.lyokofirelyte.Elysian.Events.ElyChannel;
import com.github.lyokofirelyte.Elysian.Events.ElyChat;
import com.github.lyokofirelyte.Elysian.Events.ElyGameMode;
import com.github.lyokofirelyte.Elysian.Events.ElyJoinQuit;
import com.github.lyokofirelyte.Elysian.Events.ElyLogger;
import com.github.lyokofirelyte.Elysian.Events.ElyMessages;
import com.github.lyokofirelyte.Elysian.Events.ElyMobs;
import com.github.lyokofirelyte.Elysian.Events.ElyMove;
import com.github.lyokofirelyte.Elysian.Events.ElyScoreBoard;
import com.github.lyokofirelyte.Elysian.Events.ElyTP;
import com.github.lyokofirelyte.Elysian.Events.FriendlyReminder;
import com.github.lyokofirelyte.Elysian.Games.Blink.Blink;
import com.github.lyokofirelyte.Elysian.Games.Cranked.Cranked;
import com.github.lyokofirelyte.Elysian.Games.Gotcha.Gotcha;
import com.github.lyokofirelyte.Elysian.Games.Spleef.Spleef;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Elysian.Games.Spleef.SpleefStorage;
import com.github.lyokofirelyte.Elysian.Games.TeamPVP.TeamPVP;
import com.github.lyokofirelyte.Elysian.Gui.GuiCloset;
import com.github.lyokofirelyte.Elysian.Gui.GuiRoot;
import com.github.lyokofirelyte.Elysian.MMO.ElyAutoRepair;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.ElyPatrol;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.HolyMackerel;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.LifeForce;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SkyBlade;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SoulSplit;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.SuperBreaker;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.TreeFeller;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellCommand;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellEvents;
import com.github.lyokofirelyte.Elysian.MMO.Magics.SpellTasks;
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
		main.mmo.holy = new HolyMackerel(main);
		main.mmo.patrols = new ElyPatrol(main);
		main.mmo.soulSplit = new SoulSplit(main);
		main.mmo.spellEvents = new SpellEvents(main);
		main.mmo.spellTasks = new SpellTasks(main);
		main.mmo.repair = new ElyAutoRepair(main);
		main.autoSave = new ElyAutoSave(main);
		main.spleef = new Spleef(main);
		main.friendlyReminder = new FriendlyReminder(main);
		main.wealth = new ElyWealth(main);
		main.saveClasses.put("main.blink", new Blink(main));
		main.saveClasses.put("main.teampvp", new TeamPVP(main));
		main.saveClasses.put("main.gotcha", new Gotcha(main));
		main.saveClasses.put("main.cranked", new Cranked(main));
		
		main.blink = (Blink) main.saveClasses.get("main.blink");
		main.teamPVP = (TeamPVP) main.saveClasses.get("main.teampvp");
		main.gotcha = (Gotcha) main.saveClasses.get("main.gotcha");
		main.cranked = (Cranked) main.saveClasses.get("main.cranked");
		
		closet();
		listener();
		commands();
		tasks();
		rec();
		
		main.numerals = new ArrayList<String>(YamlConfiguration.loadConfiguration(main.getResource("numerals.yml")).getStringList("Numerals"));
		
		games(
			main.blink,
			main.teamPVP,
			main.gotcha,
			main.cranked
		);

		try {
			main.api.divManager.load(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (DivinityStorage game : main.api.divManager.getMap(DivinityManager.gamesDir + "spleef/").values()){
			SpleefStorage s = new SpleefStorage(main, SpleefDataType.GAME, game.name());
			for (SpleefGameData data : SpleefGameData.values()){
				s.put(data, game.getStr(data));
			}
			main.spleef.module.data.put(game.name(), s);
		}
		
		loads();
	}

	
	private void loads(){
		for (ElySave s : main.saveClasses.values()){
			s.load();
		}
	}
	
	private void games(DivGame... games){
		for (DivGame g : games){
			if (!main.api.divManager.data.containsKey(g.toDivGame().getFullPath())){
				main.api.divManager.data.put(g.toDivGame().getFullPath(), new THashMap<String, DivinityStorage>());
			}
			main.api.divManager.data.get(g.toDivGame().getFullPath()).put(g.toDivGame().name(), g.toDivGame());
		}
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
			new ElyGameMode(main),
			main.mobs,
			main.staff,
			main.logger,
			main.chat,
			main.tp,
			main.ss,
			main.pro,
			main.rings,
			main.invManager,
			main.mmo,
			main.spleef.active,
			main.friendlyReminder,
			main.mmo.spellEvents,
			main.mmo.repair,
			main.spleef.active,
			main.blink.blinkCommand,
			main.teamPVP.active,
			main.gotcha.active
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
			new SpellCommand(main),
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
			main.mmo.repair,
			main.closets.get(0),
			main.spleef.commandMain,
			main.mmo.patrols,
			main.blink.blinkCommand,
			main.teamPVP.command,
			main.gotcha.command,
			main.wealth,
			main.cranked.command
		);
	}
	
	private void tasks(){
		main.tasks.put(ElyTask.ANNOUNCER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.announcer, 0L, 24000L));
		main.tasks.put(ElyTask.MMO_BLOCKS, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.cleanup, 0L, 432000L));
		main.tasks.put(ElyTask.LOGGER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.logger, 300L, 300L));
		main.tasks.put(ElyTask.WATCHER, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.watcher, 500L, 500L));
		main.tasks.put(ElyTask.WEBSITE, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.api.web, 100L, 100L));
		main.tasks.put(ElyTask.AUTO_SAVE, Bukkit.getScheduler().scheduleSyncRepeatingTask(main, main.autoSave, 24000L, 24000L));
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
	
	private void rec(){
		
		Potion splash = new Potion(PotionType.INSTANT_HEAL, 1);//poison 1
		splash.setSplash(true);
		 
		ItemStack i = splash.toItemStack(1);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(main.AS("&4&oVAMPYRE VIAL"));
		meta.setLore(Arrays.asList(main.AS("&c&oDrink up!")));
		i.setItemMeta(meta);

		ShapedRecipe r = new ShapedRecipe(i).shape(
			"000",
			"yay",
			"zzz"
		);
		
		RecipeHandler rh = new RecipeHandler(r);
		rh.setIngredient('0', new ItemStack(Material.ROTTEN_FLESH));
		rh.setIngredient('y', new ItemStack(Material.REDSTONE));
		rh.setIngredient('a', new ItemStack(Material.APPLE));
		rh.setIngredient('z', new ItemStack(Material.SPIDER_EYE));
		main.getServer().addRecipe(rh.getShapedRecipe());
		
		
		splash = new Potion(PotionType.POISON, 1);//poison 1
		splash.setSplash(true);
		 
		i = splash.toItemStack(1);
		meta = i.getItemMeta();
		meta.setDisplayName(main.AS("&b&oFLASH!"));
		meta.setLore(Arrays.asList(main.AS("&9&oOh I wonder where you'll go...")));
		i.setItemMeta(meta);

		r = new ShapedRecipe(i).shape(
			"fff",
			"fff",
			"fff"
		);
		
		rh = new RecipeHandler(r);
		rh.setIngredient('f', new ItemStack(Material.FEATHER));
		main.getServer().addRecipe(rh.getShapedRecipe());
		
		
		i = DivInvManager.createItem("SUPERCOBBLE", new String[] {"&6&oConsumed by magic spells"}, Enchantment.DURABILITY, 10, Material.COBBLESTONE, 9);

		r = new ShapedRecipe(i).shape(
			"fff",
			"fff",
			"fff"
		);
		
		rh = new RecipeHandler(r);
		rh.setIngredient('f', new ItemStack(Material.COBBLESTONE));
		main.getServer().addRecipe(rh.getShapedRecipe());
	}
}