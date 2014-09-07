package com.github.lyokofirelyte.Elysian.MMO.Abilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;
import com.github.lyokofirelyte.Elysian.MMO.ElyMMO;
import com.github.lyokofirelyte.Elysian.MMO.MMO;

public class HolyMackerel extends ElyMMO {

	public HolyMackerel(Elysian i) {
		super(i);
	}

	public void l(Player p, DivinityPlayer dp, Location l){
		
		if (dp.getLong(MMO.HOLY_COOLDOWN) <= System.currentTimeMillis()){
			holy(p, l);
			dp.set(MMO.HOLY_COOLDOWN, System.currentTimeMillis() + 600000L);
		} else {
			dp.err("Holy Mackerel is on cooldown! &6" + ((System.currentTimeMillis() - dp.getLong(MMO.HOLY_COOLDOWN))/1000)*-1 + " &c&oseconds remain.");
		}
	}
	
	private void holy(final Player p, final Location l){	
		
		new Thread(new Runnable(){ public void run(){
			
			List<Location> dropLocs = main.api.divUtils.circle(l, 10, 1, false, false, 10);
			
			for (Location l : dropLocs){
				
				ItemStack i = new ItemStack(Material.RAW_FISH);
				
				if (!noPickup.containsKey(p.getName())){
					noPickup.put(p.getName(), new ArrayList<Item>());
				}
				
				noPickup.get(p.getName()).add(l.getWorld().dropItem(l, i));
				
				try { Thread.sleep(1000); } catch (InterruptedException e) {}
			}
			
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			
			for (Item i : noPickup.get(p.getName())){
				i.setVelocity(new Vector(0, 0.5, 0));
			}
			
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			
			for (Item i : noPickup.get(p.getName())){
				i.remove();
			}
			
			ItemStack fancyFish = new ItemStack(Material.COOKED_FISH);
			ItemMeta im = fancyFish.getItemMeta();
			im.setDisplayName(main.AS("&b&oIt's a whopper!"));
			im.setLore(Arrays.asList(main.AS("&7&oFills your food bar to full!")));
			fancyFish.setItemMeta(im);
			p.getInventory().addItem(fancyFish);
			
			main.s(p, "You caught a fancy fish in the storm!");
			
		}}).start();
	}
}