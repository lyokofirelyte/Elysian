package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Manager.DivinityManager;
import com.github.lyokofirelyte.Divinity.Storage.DivinityStorage;
import com.github.lyokofirelyte.Elysian.Elysian;

import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.createItem;

public class GuiRings extends DivGui {
	
	private Elysian main;
	private Vector v;
	private int i = 0;
	private String name;
	
	public GuiRings(Elysian main, Vector v, String name){
		
		super(18, "&3Rings Destination");
		this.main = main;
		this.v = v;
		this.name = name;
	}
	
	@Override
	public void create(){
		
		for (DivinityStorage ring : main.api.divManager.getMap(DivinityManager.ringsDir).values()){
			if (!ring.name().equals(name)){
				addButton(i, createItem("&e" + ring.name(), new String[] { "&6&oTeleport here..."}, Material.GLOWSTONE));
				i++;
			}
		}
	}
	
	@Override
	public void actionPerformed(Player p){
		
		if (slot <= i){
			if (main.api.getDivRing(item.getItemMeta().getDisplayName().substring(2)).isInOperation()){
				return;
			}
			if (main.api.getDivRing(name).isInOperation()){
				return;
			}
			main.rings.calculate(p, v, this.item.getItemMeta().getDisplayName().substring(2), name, true);
		}
	}
}