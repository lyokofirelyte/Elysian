package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRing;
import com.github.lyokofirelyte.Elysian.Elysian;

public class ElyRings implements Listener {

	private Elysian main;
	
	public ElyRings(Elysian i){
		main = i;
	}
	
	@SuppressWarnings("deprecation")
	@DivCommand(aliases = {"rings"}, desc = "Elysian Ring Transport System Command", help = "/rings help", player = true, min = 1)
	public void onRings(Player p, String[] args){
		
		switch (args[0]){
		
			case "add":
				
				if (!main.doesRingExist(args[1])){
					
					ItemStack i = p.getItemInHand();
					DivinityRing ring = main.api.getDivRing(args[1]);
					Location l = p.getLocation();
					Vector v = l.toVector();
					
					ring.setCenter(l.getWorld().getName() + " " + v.getBlockX() + " " + (v.getBlockY()-1) + " " + v.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
					ring.setRingMaterial(i.getType().getId(), i.getData().getData());
					ring.setDest("none");
					
				} else {
					main.s(p, "&c&oThat ring already exists!");
				}
				
			break;
			
			case "remove":
				
				if (main.doesRingExist(args[1])){
					main.api.divManager.getRingMap().remove(args[1]);
					new File("./plugins/Divinity/rings/" + args[1].toLowerCase() + ".yml").delete();
					main.s(p, "&c&oDeleted!");
				} else {
					main.s(p, "&c&oThat ring does not exist!");
				}
				
			break;
		}
	}
}