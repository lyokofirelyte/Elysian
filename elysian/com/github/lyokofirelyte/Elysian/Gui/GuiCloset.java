package com.github.lyokofirelyte.Elysian.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import com.github.lyokofirelyte.Divinity.DivGui;
import com.github.lyokofirelyte.Divinity.Commands.DivCommand;
import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;
import com.github.lyokofirelyte.Elysian.Elysian;
import static com.github.lyokofirelyte.Divinity.Manager.DivInvManager.*;

public class GuiCloset extends DivGui {
	
	private Elysian main;
	private DivGui parent;
	int closets = 0;
	
	public GuiCloset(Elysian main, DivGui parent){
		
		super(54, "&3t r a d e &f][&3 c e n t r e");
		this.main = main;
		this.parent = parent;
	}
	
	@Override
	public void create(){
		
		addButton(45, createItem("&b( e l y s i a n )", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.BEACON));
		
		for (int i = 0; i < 5; i++){
			if (parent.equals(main.closets.get(i))){
				closets = i+1;
				break;
			}
		}
		
		if (main.closets.containsKey(closets-1)){
			addButton(52, createItem("&3PAGE " + (closets-1), new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		} else {
			addButton(52, createItem("&b( e l y s i a n )", new String[] { "&b< < <" }, Enchantment.DURABILITY, 10, Material.FLINT));
		}
		
		if (main.closets.containsKey(closets+1)){
			addButton(53, createItem("&3PAGE " + (closets+1), new String[] { "&b> > >" }, Enchantment.DURABILITY, 10, Material.FLINT));
		} else {
			addButton(53, createItem("&c&oLast Page Reached.", new String[] { "&b> > >" }, Enchantment.DURABILITY, 10, Material.FLINT));
		}
	}
	
	@Override
	public void actionPerformed(final Player p){

		switch (slot){
		
			case 45: main.invManager.displayGui(p, parent); break;
			
			case 52:
				
				if (main.closets.containsKey(closets-1)){
					main.invManager.displayGui(p, main.closets.get(closets-1));
				} else {
					main.invManager.displayGui(p, new GuiRoot(main));
				}
				
			break;
			
			case 53: 
				
				if (main.closets.containsKey(closets+1)){
					main.invManager.displayGui(p, main.closets.get(closets+1));
				}
				
			break;
			
			default:
				
				if (item != null && !item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().hasLore()){
					
					DivinityPlayer seller = main.matchDivPlayer(ChatColor.stripColor(main.AS(item.getItemMeta().getLore().get(1))));
					DivinityPlayer buyer = main.api.getDivPlayer(p);
					int price = Integer.parseInt(item.getItemMeta().getLore().get(0).substring(2));
					
					if (buyer.getInt(DPI.BALANCE) >= price){
						
						if (!seller.uuid().equals(buyer.uuid())){
							buyer.set(DPI.BALANCE, buyer.getInt(DPI.BALANCE) - price);
							seller.set(DPI.BALANCE, seller.getInt(DPI.BALANCE) + price);
						}
						
						buyer.s("Purchased for &6" + price + "&b!");
						seller.getList(DPI.MAIL).add("personal" + "%SPLIT%" + "&6System" + "%SPLIT%" + p.getDisplayName() + " &bpurchased your &6" + item.getType().toString() + "&b!");
						
						main.api.getSystem().getStack(DPI.CLOSET_ITEMS).remove(item);
						p.getInventory().addItem(modItem(item));
						getInv().remove(item);
						
					} else {
						buyer.err("You don't have enough money.");
					}
				}
				
			break;
		}
	}
	
	private ItemStack modItem(ItemStack i){
		ItemMeta im = i.getItemMeta();
		im.setLore(new ArrayList<String>());
		i.setItemMeta(im);
		return i;
	}
	
	private boolean isRoom(ItemStack i, boolean add){
		
		for (int x = 0; x < 5; x++){
			if (main.closets.get(x).getInv().firstEmpty() != -1){
				if (add){
					main.closets.get(x).getInv().addItem(i);
				}
				return true;
			}
		}
		
		return false;
	}
	
	@DivCommand(aliases = {"sell"}, desc = "Add an item to the trading house in /root!", help = "/sell <price>", player = true, min = 1)
	public void onSell(Player p, String[] args){
		
		int amt = 0;
		
		for (int i = 0; i < 5; i++){
			if (main.closets.get(i).getInv().getContents().length > 0){
				for (ItemStack item : main.closets.get(i).getInv().getContents()){
					if (item != null && item.getItemMeta().getLore().contains(p.getName())){
						amt++;
						if (amt >= 3){
							main.s(p, "&c&oYou can only sell 3 items. Try opening up a player-owned shop?");
							return;
						}
					}
				}
			}
		}
		
		if (isRoom(null, false)){
			if (p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR)){
				if (main.api.divUtils.isInteger(args[0])){
					ItemStack i = p.getItemInHand();
					ItemMeta im = i.getItemMeta();
					List<String> lore = Arrays.asList(main.AS("&6" + args[0]), main.AS(p.getName()));
					im.setLore(lore);
					i.setItemMeta(im);
					isRoom(i, true);
					main.api.getSystem().getStack(DPI.CLOSET_ITEMS).add(i);
					p.getInventory().remove(i);
					main.invManager.displayGui(p, main.closets.get(0));
				} else {
					main.s(p, "&c&oThat is not a valid price.");
				}
			} else {
				main.s(p, "&c&oYou must hold an item in your hand!");
			}
		} else {
			main.s(p, "&c&oThe trading hub is full.");
		}
	}
}