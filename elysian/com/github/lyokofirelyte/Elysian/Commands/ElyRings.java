package com.github.lyokofirelyte.Elysian.Commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DivinityRing;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.Gui.GuiRings;

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
	
	@SuppressWarnings("deprecation")
	public void calculate(Player p, Vector v, String destination, String ring, boolean tp){
		
		DivinityRing currentRing = main.api.getDivRing(ring);
		DivinityRing dest = main.api.getDivRing(destination);
		String[] destString = dest.getCenter();
		String startWorld = p.getWorld().getName();
		Location destLoc = new Location(Bukkit.getWorld(destString[0]), d(destString[1]), d(destString[2]), d(destString[3]), f(destString[4]), f(destString[5]));
		
		int startX = v.getBlockX();
		int startY = v.getBlockY();
		int startZ = v.getBlockZ();
	
		Map<Integer, List<Location>> locs = new HashMap<Integer, List<Location>>();
		
		dest.setInOperation(true);
		
		for (int i = 0; i < 3; i++){
			locs.put(i, new ArrayList<Location>());
			
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ));
			locs.get(i).add(l(startWorld, startX, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX+2, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX+2, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX-2, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX-2, startY-(i*2), startZ-3));
			
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ-3));
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ+4));
			locs.get(i).add(l(startWorld, startX+3, startY-(i*2), startZ-4));
			
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ+3));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ-3));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ+4));
			locs.get(i).add(l(startWorld, startX-3, startY-(i*2), startZ-4));
		}
		
		for (List<Location> loc : locs.values()){
			for (Location l : loc){
				l.getBlock().setType(Material.AIR);
				l(l.getWorld(), l.getX(), l.getY()-1, l.getZ()).getBlock().setType(Material.AIR);
			}
		}
		
		for (Location l : locs.get(0)){
			FallingBlock b = l.getWorld().spawnFallingBlock(l, currentRing.getMatId(), currentRing.getMatByte());
			b.setVelocity(new Vector(0, 1.3, 0));
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new checkLocs(), 5L);
	}
	
	private class checkLocs implements Runnable {
		
		public void run(){
			
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e){
		
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null){
			
			Vector v = e.getClickedBlock().getLocation().toVector();
			String[] clickedLoc = (e.getClickedBlock().getWorld().getName() + " " + v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ()).split(" ");
			
			for (DivinityRing ring : main.api.divManager.getRingMap().values()){
				if (ring.getCenter().equals(clickedLoc)){
					if (!ring.isInOperation()){
						ring.setInOperation(true);
						main.invManager.displayGui(e.getPlayer(), new GuiRings(main, v, ring.name()));
					} else {
						main.s(e.getPlayer(), "&c&oRing already in operation!");
					}
					return;
				}
			}
		}
	}
	
	private double d(String s){
		return Double.parseDouble(s);
	}
	
	private float f(String s){
		return Float.parseFloat(s);
	}
	
	private Location l(String w, int x, int y, int z){
		return new Location(Bukkit.getWorld(w), x, y, z);
	}
	
	private Location l(World w, double x, double y, double z){
		return new Location(w, x, y, z);
	}
}