package com.github.lyokofirelyte.Elysian.Gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;
import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.*;

public class GuiChest extends DivGui {
	
	private Elysian main;
	private DivinityPlayer p;
	int closets = 0;
	
	public GuiChest(Elysian main, DivinityPlayer p){
		
		super(54, "&2Chest Viewer");
		this.main = main;
		this.p = p;
	}
	
	@Override
	public void create(){
		 
		 for (String chest : p.getList(DPI.OWNED_CHESTS)){
			 if (inv.firstEmpty() != -1){
				 String[] loc = chest.split(" ");
				 if (getInv().firstEmpty() != -1){
					 addButton(getInv().firstEmpty(), createItem("&b" + loc[0] + " " + loc[1] + " " + loc[2] + " " + loc[3], new String[] { "&7&oOwned Chest" }, Enchantment.DURABILITY, 10, Material.CHEST));
				 }
			 }
		 }
	}
	
	@Override
	public void actionPerformed(final Player p){

		if (item != null && item.getType().equals(Material.CHEST)){
			String[] loc = ChatColor.stripColor(main.AS(item.getItemMeta().getDisplayName())).split(" ");
			World world = Bukkit.getWorld(loc[0]);
			Block block = new Location(world, Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3])).getBlock();
			
			if (block.getState() instanceof Chest){
				Chest chest = (Chest) block.getState();
				p.openInventory(chest.getInventory());
			} else if (block.getState() instanceof DoubleChest){
				DoubleChest chest = (DoubleChest) block.getState();
				p.openInventory(chest.getInventory());
			}
		}
	}
}