package com.github.lyokofirelyte.Elysian.Games.Blink;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Sign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.github.lyokofirelyte.Divinity.Storage.DivGame;
import com.github.lyokofirelyte.Divinity.Storage.DivinityGame;
import com.github.lyokofirelyte.Elysian.ElySave;
import com.github.lyokofirelyte.Elysian.Elysian;

public class Blink implements DivGame, ElySave {

	protected Elysian main;
	public Map<String, BlinkGame> games = new HashMap<String, BlinkGame>();
	public BlinkCommand blinkCommand;
	
	public Blink(Elysian i){
		main = i;
		blinkCommand = new BlinkCommand(this);
	}
	
	public DivinityGame toDivGame(){
		return main.api.getDivGame("blink", "blink");
	}
	
	public BlinkGame getGame(String game){
		return games.get(game);
	}
	
	public void createGame(String name){
		
		if (!games.containsKey(name)){
			
			games.put(name, new BlinkGame(){
				
				private Map<Integer, BlinkSlot> slots = new HashMap<Integer, BlinkSlot>();
				private Map<Integer, String[]> locations = new HashMap<Integer, String[]>();
				
				public BlinkSlot getSlot(int slot){
					return slots.get(slot);
				}
				
				public Sign getSign(int slot){
					String[] l = locations.get(slot);
					return (Sign) new Location(Bukkit.getWorld(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3])).getBlock().getState();
				}
				
				public Map<Integer, BlinkSlot> getSlots(){
					return slots;
				}
				
				public boolean hasSlot(int slot){
					return slots.containsKey(slot);
				}
				
				public boolean isPlayerOnSlot(int slot){
					return getSlot(slot).isInUse();
				}

				public void setSign(int slot, Location l){
					locations.put(slot, new String[]{ l.getWorld().getName(), l.getBlockX() + "", l.getBlockY() + "", l.getBlockZ() + "" });
				}
				
				public void addSlot(int number, final int purchase, final int payout, Location l){
					
					locations.put(number, new String[]{ l.getWorld().getName(), l.getBlockX() + "", l.getBlockY() + "", l.getBlockZ() + "" });
					l.getBlock().setType(Material.WALL_SIGN);
					
					slots.put(number, new BlinkSlot(){
						
						private String player = "none";
						private int cashOut = payout;
						private int purchasePrice = purchase;
						private boolean isInUse = false;
						
						public String getPlayer(){
							return player;
						}
						
						public int getCashOut(){
							return cashOut;
						}
						
						public int getPurchasePrice(){
							return purchasePrice;
						}
						
						public boolean isInUse(){
							return isInUse;
						}
						
						public void setPlayer(String player){
							this.player = player;
						}
						
						public void setCashOut(int cashOut){
							this.cashOut = cashOut;
						}
						
						public void setPurchasePrice(int price){
							purchasePrice = price;
						}
						
						public void setInUse(boolean inUse){
							isInUse = inUse;
						}
					});
				}
				
				public void remSlot(int slot){
					slots.remove(slot);
				}
			});
		}
	}
	
	public interface BlinkGame {
		public void addSlot(int slot, int purchase, int payout, Location l);
		public void remSlot(int slot);
		public void setSign(int slot, Location l);
		public boolean isPlayerOnSlot(int slot);
		public boolean hasSlot(int slot);
		public Sign getSign(int slot);
		public BlinkSlot getSlot(int slot);
		public Map<Integer, BlinkSlot> getSlots();
	}
	
	public interface BlinkSlot {
		public String getPlayer();
		public int getCashOut();
		public int getPurchasePrice();
		public boolean isInUse();
		public void setPlayer(String player);
		public void setCashOut(int cashOut);
		public void setPurchasePrice(int price);
		public void setInUse(boolean inUse);
	}
	
	@Override
	public void load(){
		
		DivinityGame dg = main.api.getDivGame("blink", "blink");
		
		if (dg.contains("Games")){
			for (String game : dg.getConfigurationSection("Games").getKeys(false)){
				createGame(game);
				for (String slot : dg.getConfigurationSection("Games." + game).getKeys(false)){
					String path = "Games." + game + "." + slot + ".";
					getGame(game).addSlot(dg.getInt(slot), dg.getInt(path + "Purchase"), dg.getInt(path + "Payout"), new Location(Bukkit.getWorld(dg.getString(path + "World")), dg.getInt(path + "X"), dg.getInt(path + "Y"), dg.getInt(path + "Z")));
					getGame(game).getSlot(dg.getInt(slot)).setPlayer(dg.getString(path + "Player"));
					getGame(game).getSlot(dg.getInt(slot)).setInUse(dg.getBoolean(path + "IsInUse"));
				}
			}
		}
	}

	@Override
	public void save(){
		
		DivinityGame dg = toDivGame();
		
		for (String game : games.keySet()){
			BlinkGame bg = games.get(game);
			for (int slot : bg.getSlots().keySet()){
				BlinkSlot s = bg.getSlot(slot);
				Location l = bg.getSign(slot).getBlock().getLocation();
				String path = "Games." + game + "." + slot + ".";
				dg.set(path + "Player", s.getPlayer());
				dg.set(path + "Purchase", s.getPurchasePrice());
				dg.set(path + "Payout", s.getCashOut());
				dg.set(path + "IsInUse", s.isInUse());
				dg.set(path + "World", l.getWorld().getName());
				dg.set(path + "X", l.getBlockX());
				dg.set(path + "Y", l.getBlockY());
				dg.set(path + "Z", l.getBlockZ());
				System.out.println("Added..");
			}
		}
	}
}