package com.github.lyokofirelyte.Elysian.MMO;

import org.bukkit.event.Listener;

import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.Abilities.TreeFeller;

public class ElyMMO implements Listener {
	
	public Elysian main;
	public TreeFeller treeFeller;
	
	public ElyMMO(Elysian i) {
		main = i;
	}
}