package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Elysian.Elysian;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

public class GuiRings extends DivGui {
	
	private Elysian main;
	private DivGui parent;
	
	public GuiRings(Elysian main, DivGui parent){
		
		super(18, "&3Rings Destination");
		this.main = main;
		this.parent = parent;
	}
	
	@Override
	public void create(){
		this.addButton(0, createItem("&eSIDEBOARD", new String[] { "&6The scoreboard" }, Material.GLOWSTONE));
	}
	
	@Override
	public void actionPerformed(Player p){
		
		switch (this.slot){
		
			case 0:
				//set location
			break;
		}
	}
}