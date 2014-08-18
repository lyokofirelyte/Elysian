package com.github.lyokofirelyte.Elysian;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.lyokofirelyte.Divinity.Storage.DPI;
import com.github.lyokofirelyte.Divinity.Storage.DivinityPlayer;



/**
 * 
 * @author msnijder30
 *
 */

public class ElyMarkkit implements Listener {

	private Elysian main;
	private HashMap<String, Inventory> inventory = new HashMap<String, Inventory>();
	private HashMap<String, String> invName = new HashMap<String, String>();
	private HashMap<String, Integer> totalPrice = new HashMap<String, Integer>();
	private HashMap<String, Integer> showPrice = new HashMap<String, Integer>();
	
	public ElyMarkkit(Elysian i){
		main = i;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
			
		Player p = e.getPlayer();
			
		if (main.silentPerms(p, "wa.staff.mod2") == true && e.getLine(0).equalsIgnoreCase("markkit") && e.getLine(1) != null && !e.getLine(1).equals("")){
			e.setLine(0, main.AS("&dWC &5Markkit"));
			e.setLine(1, main.AS("&f" + e.getLine(1)));
		} else if (e.getLine(0).equalsIgnoreCase("markkit")){
			e.setLine(0, main.AS("&4INVALID!"));
			e.setLine(1, main.AS("&cWE DIDN'T"));
			e.setLine(2, main.AS("&cLISTEN! D:"));
		} else {
			for (int x = 0; x < 4; x++){
				if (e.getLine(x) != null){
					e.setLine(x, main.AS(e.getLine(x)));
				}
			}
		}
	}
		
	@EventHandler
	public void onClick(final InventoryClickEvent e){
		
		Player p = (Player) e.getWhoClicked();
			
		List<Integer> sellCart = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38);
		List<Integer> buyCart = Arrays.asList(6, 7, 8, 15, 16, 17, 24, 25, 26, 33, 34, 35, 42, 43, 44);
		List<Integer> itemSlot = Arrays.asList(4, 13, 22, 31, 40, 49);

		if(e.getInventory().getName().contains("items stocked") || e.getInventory().getName().contains("Double price!")){
			e.setCancelled(true);
			String name = invName.get(e.getWhoClicked().getName());
			if(e.getCurrentItem() != null){
				if(!sellCart.contains(e.getRawSlot()) && !buyCart.contains(e.getRawSlot()) &&!itemSlot.contains(e.getRawSlot()) && e.getCurrentItem().getTypeId() == main.markkitYaml.getInt("Items." + name + ".ID") && e.getCurrentItem().getDurability() == main.markkitYaml.getInt("Items." + name + ".Damage")){
					ItemStack clicked = e.getCurrentItem();
					for (Integer i : sellCart){
						if(e.getInventory().getItem(i) == null){
							e.getInventory().setItem(i, clicked);
							p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
							p.updateInventory();
							break;
						} else if(e.getInventory().getItem(i).getAmount() <= 64 - clicked.getAmount()){
							ItemStack calculated = new ItemStack(clicked.getType(), e.getInventory().getItem(i).getAmount() + clicked.getAmount(), (short) clicked.getDurability());
							e.getInventory().setItem(i, calculated);
							p.getInventory().setItem(e.getSlot(), new ItemStack(Material.AIR));
							p.updateInventory();
							break;
						}
					}
				}
			}
				
			switch(e.getRawSlot()){
				
				//the buying area aka shoppig cart
			case 6: case 7: case 8: case 15: case 16: case 17: case 24: case 25: case 26: case 33: case 34: case 35: case 42: case 43: case 44:
		
				if(e.getInventory().getItem(e.getRawSlot()) != null){
					e.getInventory().setItem(e.getRawSlot(), new ItemStack(Material.AIR));
				}
				
			break;
				
			case 53:
					
				showPrice.put(e.getWhoClicked().getName(), 0);

				for (Integer i : buyCart){
					if(e.getInventory().getItem(i) != null){
						if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
							int fullPrice = main.markkitYaml.getInt("Items." + name + "." + 64 + ".buyprice");
							int currentPrice = fullPrice*e.getInventory().getItem(i).getAmount()/64;
							showPrice.put(e.getWhoClicked().getName(), showPrice.get(e.getWhoClicked().getName()) + (currentPrice*2));
						}else{
							int fullPrice = main.markkitYaml.getInt("Items." + name + "." + 64 + ".buyprice");
							int currentPrice = fullPrice*e.getInventory().getItem(i).getAmount()/64;
							showPrice.put(e.getWhoClicked().getName(), showPrice.get(e.getWhoClicked().getName()) + currentPrice);
						}
					}
				}
					
				ItemStack calculateRight = new ItemStack(Material.MUSHROOM_SOUP, 1);
				ItemMeta rightMeta = calculateRight.getItemMeta();
				rightMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
				rightMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: " + showPrice.get(e.getWhoClicked().getName())));
				calculateRight.setItemMeta(rightMeta);

				e.getInventory().setItem(53, calculateRight);
					
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
							
					ItemStack calculateRight = new ItemStack(Material.BOWL, 1);
					ItemMeta rightMeta = calculateRight.getItemMeta();
					rightMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
					rightMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
					calculateRight.setItemMeta(rightMeta);
					e.getInventory().setItem(53, calculateRight);
					
				}}, 100L);
				
			break;
				
				
			case 45:
				
				showPrice.put(e.getWhoClicked().getName(), 0);

				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						int fullPrice = main.markkitYaml.getInt("Items." + name + "." + 64 + ".buyprice");
						int currentPrice = fullPrice*e.getInventory().getItem(i).getAmount()/64;
						showPrice.put(e.getWhoClicked().getName(), showPrice.get(e.getWhoClicked().getName()) + currentPrice);
					}
				}
					
				ItemStack calculateLeft = new ItemStack(Material.MUSHROOM_SOUP, 1);
				ItemMeta leftMeta = calculateLeft.getItemMeta();
				leftMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
				leftMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: " + showPrice.get(e.getWhoClicked().getName())));
				calculateLeft.setItemMeta(leftMeta);
					
				e.getInventory().setItem(45, calculateLeft);
					
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable(){ public void run(){
							
					ItemStack calculateLeft = new ItemStack(Material.BOWL, 1);
					ItemMeta leftMeta = calculateLeft.getItemMeta();
					leftMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
					leftMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
					calculateLeft.setItemMeta(leftMeta);
					e.getInventory().setItem(45, calculateLeft);
					
				}}, 100L);
				
			break;
					
			case 46:
					//sell button
				int itemCount = 0;
				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						itemCount = itemCount + e.getInventory().getItem(i).getAmount();
						int fullPrice = main.markkitYaml.getInt("Items." + name + "." + 64 + ".sellprice");
						int currentPrice = fullPrice*e.getInventory().getItem(i).getAmount()/64;
						if(totalPrice.get(e.getWhoClicked().getName()) == null){
							totalPrice.put(e.getWhoClicked().getName(), 0);
						}
						totalPrice.put(e.getWhoClicked().getName(), totalPrice.get(e.getWhoClicked().getName()) + currentPrice);
						e.getInventory().setItem(i, new ItemStack(Material.AIR));
					}
				}
					
				DivinityPlayer dp = main.api.getDivPlayer(p);
				dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) + totalPrice.get(e.getWhoClicked().getName()));
				main.s((Player)e.getWhoClicked(), totalPrice.get(e.getWhoClicked().getName()) + " was added to your account!");
				totalPrice.put(e.getWhoClicked().getName(), 0);
				main.markkitYaml.set("Items." + name + ".inStock", main.markkitYaml.getInt("Items." + name + ".inStock") + itemCount);
				if(main.markkitYaml.getInt("Items." + name + ".inStock") < 0 || main.markkitYaml.getInt("Items." + name + ".inStock") == 0){
					main.markkitYaml.set("Items." + name + ".isSellDoubled", true);
				}else{
					main.markkitYaml.set("Items." + name + ".isSellDoubled", false);
				}
				loadInventory((Player)e.getWhoClicked(), name);
			break;
					
			case 47:
				
				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						p.getLocation().getWorld().dropItemNaturally(p.getLocation(), e.getInventory().getItem(i));
					}
				e.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
				
			break;
					
			case 51:
					
				for (int i = 0; i< 60; i++){
					if(i == 6 || i == 7 || i == 8 || i == 15 || i == 16 || i == 17 || i == 24 || i == 25 || i == 26 || i == 33 || i == 34 || i == 35 || i == 42 || i == 43 || i == 44){
						e.getInventory().setItem(i, new ItemStack(Material.AIR));
					}
				}
					
				break;
					
				case 52:
					//buy button
					int itemC = 0;
					for(int i = 0; i< 60; i++){
						if(i == 6 || i == 7 || i == 8 || i == 15 || i == 16 || i == 17 || i == 24 || i == 25 || i == 26 || i == 33 || i == 34 || i == 35 || i == 42 || i == 43 || i == 44){
							if(e.getInventory().getItem(i) != null){
								itemC = itemC + e.getInventory().getItem(i).getAmount();
								int price = main.markkitYaml.getInt("Items." + name + "." + e.getInventory().getItem(i).getAmount() + ".buyprice");
								if(totalPrice.get(e.getWhoClicked().getName()) == null){
									totalPrice.put(e.getWhoClicked().getName(), 0);
								}
								totalPrice.put(e.getWhoClicked().getName(), totalPrice.get(e.getWhoClicked().getName()) + price);
							}
							if(i == 44){
								dp = main.api.getDivPlayer(p);
								if (dp.getInt(DPI.BALANCE) >= totalPrice.get(e.getWhoClicked().getName())){
									if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled")){
										if(dp.getInt(DPI.BALANCE) >= totalPrice.get(e.getWhoClicked().getName())*2){
											dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - totalPrice.get(e.getWhoClicked().getName())*2);
											main.s((Player)e.getWhoClicked(), totalPrice.get(e.getWhoClicked().getName())*2 + " was taken from your account!");
										}else{
											main.s((Player)e.getWhoClicked(), "You do not have enough money");
										}

									}else{
										dp.set(DPI.BALANCE, dp.getInt(DPI.BALANCE) - totalPrice.get(e.getWhoClicked().getName()));
										main.s((Player)e.getWhoClicked(), totalPrice.get(e.getWhoClicked().getName()) + " was taken from your account!");
									}
									totalPrice.put(e.getWhoClicked().getName(), 0);
									for(int x = 0; x< 60; x++){
										if(x == 6 || x == 7 || x == 8 || x == 15 || x == 16 || x == 17 || x == 24 || x == 25 || x == 26 || x == 33 || x == 34 || x == 35 || x == 42 || x == 43 || x == 44){
											if(e.getInventory().getItem(x) != null){
												ItemStack item = new ItemStack(e.getInventory().getItem(x).getType());
												item.setAmount(e.getInventory().getItem(x).getAmount());
												item.setDurability(e.getInventory().getItem(x).getDurability());
												if(p.getInventory().firstEmpty() == -1){
													p.getWorld().dropItemNaturally(p.getLocation(), item);
												}else{
													p.getInventory().addItem(item);
												}	
											}
											e.getInventory().setItem(x, new ItemStack(Material.AIR));
										}
									}
									main.markkitYaml.set("Items." + name + ".inStock", main.markkitYaml.getInt("Items." + name + ".inStock") - itemC);
									if(main.markkitYaml.getInt("Items." + name + ".inStock") < 0 || main.markkitYaml.getInt("Items." + name + ".inStock") == 0){
										main.markkitYaml.set("Items." + name + ".isSellDoubled", true);
									}else{
										main.markkitYaml.set("Items." + name + ".isSellDoubled", false);
									}
									loadInventory((Player)e.getWhoClicked(), name);
								} else {
									main.s(p, "You do not have enough money!");
									totalPrice.put(e.getWhoClicked().getName(), 0);
								}
							}
						}
					}
				break;
					
				//the item you have to click
				case 4: case 13: case 22: case 31: case 40: case 49:
					ItemStack clicked = e.getInventory().getItem(e.getRawSlot());
					int count = 0;
					for (Integer i : buyCart){
						if(e.getInventory().getItem(i) != null){
							count = count + e.getInventory().getItem(i).getAmount();
						}
					}
					for (Integer i : buyCart){
						if (e.getInventory().getItem(i) == null){
							if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
								e.getInventory().setItem(i, clicked);
							}else{
								if(main.markkitYaml.getInt("Items." + name + ".inStock") > count + e.getCurrentItem().getAmount() - 1){
									e.getInventory().setItem(i, clicked);
								}else{
									main.s(p, "There is no more playerstock!, please buy this and re-open to buy moar.");
								}
							}
							p.updateInventory();
							break;
						}
					}
					break;
				}
			}
		}
		@EventHandler
		public void onClickyTheSign(PlayerInteractEvent e) {
			

			if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
				
				if (e.getClickedBlock().getState() instanceof Sign){
					Sign sign = (Sign) e.getClickedBlock().getState();
					if (sign.getLine(0).equals(main.AS("&dWC &5Markkit"))){
						
						if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() != Material.AIR){
							main.s(e.getPlayer(), "You must use your hand to activate the sign.");
							return;
						}
						String name = sign.getLine(1).replace("§f", "");
						invName.put(e.getPlayer().getName(), name);
						loadInventory(e.getPlayer(), name);
					}
				}
			}
		}
		
		@EventHandler
		public void onClose(InventoryCloseEvent e){
			Player p = (Player) e.getPlayer();
			List<Integer> sellCart = Arrays.asList(0, 1, 2, 9, 10, 11, 18, 19, 20, 27, 28, 29, 36, 37, 38);
			if(e.getInventory().getName().contains("Double price!") || e.getInventory().getName().contains("items stocked")){
				for(Integer i : sellCart){
					if(e.getInventory().getItem(i) != null){
						p.getLocation().getWorld().dropItemNaturally(p.getLocation(), e.getInventory().getItem(i));
					}
					e.getInventory().setItem(i, new ItemStack(Material.AIR));
				}
			}
		}
		
		public void loadInventory(Player p, String name){
			Material mat = Material.getMaterial(main.markkitYaml.getInt("Items." + name + ".ID"));
			short damage = (short) main.markkitYaml.getInt("Items." + name + ".Damage");
			
			if(main.markkitYaml.get("Items." + name) == null){
				main.s(p, "Cannot find this markkit, please contact staff.");
				return;
			}
			
			if(main.markkitYaml.get("Items." + name + ".inStock") == null){
				main.markkitYaml.set("Items." + name + ".inStock", 192);
			}
			if(main.markkitYaml.getInt("Items." + name + ".inStock") < 0){
				main.markkitYaml.set("Items." + name + ".inStock", 0);
			}
			Inventory inv;
			if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
				inv = Bukkit.createInventory(null, 54, main.AS("&40 stocked. Double price!"));
			}else{
				inv = Bukkit.createInventory(null, 54, main.AS("&6" + main.markkitYaml.getInt("Items." + name + ".inStock") + "&b items stocked."));
			}
			
			
			for(int i = 0; i<51; i++){
				if(i == 3 || i == 12 || i == 21 || i == 30 || i == 39 || i == 48 || i == 5 | i == 14 || i == 23 || i == 32 || i == 41 | i == 50){
					ItemStack divider = new ItemStack(Material.THIN_GLASS);
					ItemMeta divide = divider.getItemMeta();
					divide.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Separator");
					divider.setItemMeta(divide);
					inv.setItem(i, divider);
				}
			}
			
			ItemStack redCancel = new ItemStack(Material.WOOL, 1, (short) 14);
			ItemStack greenAccept = new ItemStack(Material.WOOL, 1, (short) 5);
			ItemMeta cancel = redCancel.getItemMeta();
			ItemMeta accept = greenAccept.getItemMeta();
			cancel.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Cancel!");
			redCancel.setItemMeta(cancel);
			accept.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Accept!");
			greenAccept.setItemMeta(accept);
			
			ItemStack calculateRight = new ItemStack(Material.BOWL, 1);
			ItemMeta rightMeta = calculateRight.getItemMeta();
			rightMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
			rightMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
			calculateRight.setItemMeta(rightMeta);
			
			ItemStack calculateLeft = new ItemStack(Material.BOWL, 1);
			ItemMeta leftMeta = calculateLeft.getItemMeta();
			leftMeta.setDisplayName(ChatColor.RED + "Click here to calculate the price!");
			leftMeta.setLore(Arrays.asList(ChatColor.GREEN + "Price: "));
			calculateLeft.setItemMeta(leftMeta);
			
			inv.setItem(47, redCancel);
			inv.setItem(51, redCancel);
			inv.setItem(46, greenAccept);
			inv.setItem(52, greenAccept);
			inv.setItem(53, calculateRight);
			inv.setItem(45, calculateLeft);


			if(main.markkitYaml.contains("Items." + name + "." + 64)){
				if(main.markkitYaml.getInt("Items." + name + ".64.buyprice") > 0){
					ItemStack item = new ItemStack(mat, 64, damage);
					ItemMeta itemMeta = item.getItemMeta();
					if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", (Integer.parseInt(main.markkitYaml.getString("Items." + name + ".64.buyprice"))*2) + "", ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".64.sellprice")));
					}else{
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", main.markkitYaml.getString("Items." + name + ".64.buyprice"), ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".64.sellprice")));
					}
					item.setItemMeta(itemMeta);
					inv.setItem(4, item);
				}
			}
			
			if(main.markkitYaml.contains("Items." + name + "." + 32)){
				if(main.markkitYaml.getInt("Items." + name + ".32.buyprice") > 0){
					ItemStack item = new ItemStack(mat, 32, damage);
					ItemMeta itemMeta = item.getItemMeta();
					if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", (Integer.parseInt(main.markkitYaml.getString("Items." + name + ".32.buyprice"))*2) + "", ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".32.sellprice")));
					}else{
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", main.markkitYaml.getString("Items." + name + ".32.buyprice"), ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".32.sellprice")));
					}								item.setItemMeta(itemMeta);
					inv.setItem(13, item);
				}
			}
			
			if(main.markkitYaml.contains("Items." + name + "." + 16)){
				if(main.markkitYaml.getInt("Items." + name + ".16.buyprice") > 0){
					ItemStack item = new ItemStack(mat, 16, damage);
					ItemMeta itemMeta = item.getItemMeta();
					if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", (Integer.parseInt(main.markkitYaml.getString("Items." + name + ".16.buyprice"))*2) + "", ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".16.sellprice")));
					}else{
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", main.markkitYaml.getString("Items." + name + ".16.buyprice"), ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".16.sellprice")));
					}								item.setItemMeta(itemMeta);
					inv.setItem(22, item);
				}
			}
			
			if(main.markkitYaml.contains("Items." + name + "." + 8)){
				if(main.markkitYaml.getInt("Items." + name + ".8.buyprice") > 0){
					ItemStack item = new ItemStack(mat, 8, damage);
					ItemMeta itemMeta = item.getItemMeta();
					if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", (Integer.parseInt(main.markkitYaml.getString("Items." + name + ".8.buyprice"))*2) + "", ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".8.sellprice")));
					}else{
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", main.markkitYaml.getString("Items." + name + ".8.buyprice"), ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".8.sellprice")));
					}								item.setItemMeta(itemMeta);
					inv.setItem(31, item);
				}
			}
			
			if(main.markkitYaml.contains("Items." + name + "." + 1) && main.markkitYaml.get("Items." + name + "." + 64) != null){
				if(main.markkitYaml.getInt("Items." + name + ".1.buyprice") > 0){
					ItemStack item = new ItemStack(mat, 1, damage);
					ItemMeta itemMeta = item.getItemMeta();
					if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", (Integer.parseInt(main.markkitYaml.getString("Items." + name + ".1.buyprice"))*2) + "", ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".1.sellprice")));
					}else{
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", main.markkitYaml.getString("Items." + name + ".1.buyprice"), ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".1.sellprice")));
					}								item.setItemMeta(itemMeta);
					inv.setItem(40, item);
				}
			}else if(main.markkitYaml.contains("Items." + name + "." + 1) && main.markkitYaml.get("Items." + name + "." + 64) == null){
				if(main.markkitYaml.getInt("Items." + name + ".1.buyprice") > 0){
					ItemStack item = new ItemStack(mat, 1, damage);
					ItemMeta itemMeta = item.getItemMeta();
					if(main.markkitYaml.getBoolean("Items." + name + ".isSellDoubled") == true){
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", (Integer.parseInt(main.markkitYaml.getString("Items." + name + ".1.buyprice"))*2) + "", ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".1.sellprice")));
					}else{
						itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Buy", main.markkitYaml.getString("Items." + name + ".1.buyprice"), ChatColor.RED + "Sell", main.markkitYaml.getString("Items." + name + ".1.sellprice")));
					}								item.setItemMeta(itemMeta);
					inv.setItem(4, item);
				}
			}
			inventory.put(name, inv);
			p.openInventory(inventory.get(name));
		}
}