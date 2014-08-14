package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRing;
import com.github.lyokofirelyte.Elysian.Elysian;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

public class GuiRings extends DivGui {
	
	private Elysian main;
	private Vector v;
	private int i = 0;
	private String name;
	
	public GuiRings(Elysian main, Vector v, String name){
		
		super(9, "&3Rings Destination");
		this.main = main;
		this.v = v;
		this.name = name;
	}
	
	@Override
	public void create(){
		
		for (String ring : main.api.divManager.getRingMap().keySet()){
			if (!ring.equals(name)){
				addButton(i, createItem("&e" + ring, new String[] { "&6&oTeleport here..."}, Material.GLOWSTONE));
				i++;
			}
		}
	}
	
	@Override
	public void actionPerformed(Player p){
		
		if (slot <= i){
			main.rings.calculate(p, v, this.item.getItemMeta().getDisplayName().substring(2), name, true);
		}
	}
}