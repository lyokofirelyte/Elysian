package com.github.lyokofirelyte.Elysian.Gui;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;

public class GuiFriendlyReminder extends DivGui{
	
	Elysian main;
	private DivGui parent;
	private DivinityPlayer player;

	public GuiFriendlyReminder(Elysian i, DivGui parent, DivinityPlayer dp) {
		super(9, "&5FRIENDLY REMINDERS");
		this.main = i;
		this.parent = parent;
		this.player = dp;
		
	}

	@Override
	public void create() {
		
		addButton(0, createItem("&aAnimal Killing", new String[] { "&3Toggle animal", "&3killing msgs" , main.friendlyReminder.getStatus(player, "FK")}, Material.LEATHER, 1));
		addButton(1, createItem("&aCreeper Holes", new String[] { "&3Toggle creeper", "&3hole msgs" , main.friendlyReminder.getStatus(player, "CH")}, Material.SKULL_ITEM, 1, 4));
		addButton(2, createItem("&aCrop Replanting", new String[] { "&3Toggle crop re-", "&3planting msgs" , main.friendlyReminder.getStatus(player, "CR")}, Material.DIAMOND_HOE, 1));
		addButton(3, createItem("&aTree Replanting", new String[] { "&3Toggle tree re-", "&3planting msgs" , main.friendlyReminder.getStatus(player, "TR")}, Material.SAPLING, 0));
		addButton(8, createItem("&bRETURN", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
	}

	@Override
	public void actionPerformed(Player p) {
		switch(slot) {
		case 0: main.friendlyReminder.toggleFK(main.getDivPlayer(p));
		create();
		break;
		case 1: main.friendlyReminder.toggleCH(main.getDivPlayer(p));
		create();
		break;
		case 2: main.friendlyReminder.toggleCR(main.getDivPlayer(p));
		create();
		break;
		case 3: main.friendlyReminder.toggleTR(main.getDivPlayer(p));
		create();
		break;
		case 8: main.invManager.displayGui(p, this.parent);
		break;
		}
		
	}

}
