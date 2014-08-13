package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Elysian.Elysian;
import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.*;

public class GuiRoot extends DivGui {
	
	private Elysian main;
	
	public GuiRoot(Elysian main){
		
		super(27, "&b( e l y s i a n )");
		this.main = main;
	}
	
	@Override
	public void create(){
		
		addButton(0, createItem("&aCHAT", new String[] { "&3Chat Options" }, Material.INK_SACK, 1, 9));
		addButton(2, createItem("&4TOGGLES", new String[] { "&cToggle Options" }, Material.INK_SACK, 1, 1));
		addButton(4, createItem("&cSTATS", new String[] { "&eStat Viewer", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 5));
		addButton(6, createItem("&aTHE CLOSET", new String[] { "&3General Store Trading", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 12));
		addButton(8, createItem("&3ALLIANCES", new String[] { "&aAlliance controls" }, Material.INK_SACK, 1, 2));
		addButton(10, createItem("&4INCINERATOR", new String[] { "&cThrow away items" }, Material.INK_SACK, 1, 10));
		addButton(12, createItem("&5STAFF SECTION", new String[] { "&dStaff only", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 8));
		addButton(14, createItem("&dPARAGON SHOPPE", new String[] { "&5Paragon rewards", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 6));
		addButton(16, createItem("&bPATROLS", new String[] { "&dPatrol Menu", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 11));
		addButton(18, createItem("&1QUICK COMMANDS", new String[] { "&5Command Menu", "&4[ OFFLINE ]" }, Material.INK_SACK, 1, 14));
		addButton(20, createItem("&2CREATIVE WORLD", new String[] { "&aWarp to creative" }, Material.INK_SACK, 1, 13));
		addButton(22, createItem("&eLOGOFF", new String[] { "&6Leave the game" }, Material.INK_SACK));
		addButton(24, createItem("&bCLOSE", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		
	}
	
	@Override
	public void actionPerformed(final Player p){
	
		switch (slot){
			
			case 0:
				main.invManager.displayGui(p, new GuiChat(main, this));
			break;
				
			case 2:
				main.invManager.displayGui(p, new GuiToggles(main, this));
			break;
			
			case 24:
				p.closeInventory();
			break;
			
		}
	}
}