package com.github.lyokofirelyte.Elysian;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.github.lyokofirelyte.Divinity.Storage.DivinitySystem;

public class ElyMarkkitItem {
	Material material;
	int damage;
	public Elysian main;
	private DivinitySystem system;

	public ElyMarkkitItem(Elysian i, Material mat, int data){
		material = mat;
		damage = data;
		main = i;
		system = main.api.getSystem();
	}
	
	public ElyMarkkitItem(Elysian i, String signname){
		main = i;
		system = main.api.getSystem();
		material = Material.getMaterial(system.getMarkkit().getInt("Items." + signname + ".ID"));
		damage = system.getMarkkit().getInt("Items." + signname + ".Damage");
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public int getDurability(){
		return damage;
	}
	
	public int getMaterialID(){
		return material.getId();
	}
	
	public String getInStockSignName(){
		String name = main.api.getSystem().getMarkkit().getInt("Items." + getSignName() + ".inStock") + "";
		if(name.length() >= 4){
			return name.substring(0, name.length() - 3) + "K";
		}else{
			return name;
		}
	}
	
	public int getStackSellPrice(){
		int price = main.api.getSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".sellprice");
		int inStock = getInStock();
		if(inStock >= 10000){
			return (int) Math.ceil(price * 0.5);
		}else if(inStock >= 1000){
			return (int) Math.ceil(price * ((100-inStock/1000*10.0/2)/100));
		}else{
			return price;
		}
	}
	
	public int getStackBuyPrice(){
		return main.api.getSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".buyprice");
	}
	
	public int getSellPrice(int i){
		int price = main.api.getSystem().getMarkkit().getInt("Items." + getSignName() + "." + 64 + ".sellprice") * i / 64;
		int inStock = getInStock();
		if(inStock >= 10000){
			return (int) Math.ceil(price * 0.5);
		}else if(inStock >= 1000){
			return (int) Math.ceil(price * ((100-inStock/1000*10.0/2)/100));
		}else{
			return price;
		}	
	}
	
	public int getBuyPrice(int i){
		return main.api.getSystem().getMarkkit().getInt("Items." + getSignName() + "." + 1 + ".buyprice") * i;
	}
	
	public int getInStock(){
		return main.api.getSystem().getMarkkit().getInt("Items." + getSignName() + ".inStock");
	}
	
	public void setInStock(int i){
		main.api.getSystem().getMarkkit().set("Items." + getSignName() + ".inStock", i);
	}
	public boolean isSellDoubled(){
		return main.api.getSystem().getMarkkit().getBoolean("Items." + getSignName() + ".isSellDoubled");
	}
	
	public void setSellDoubled(boolean doubled){
		main.api.getSystem().getMarkkit().set("Items." + getSignName() + ".isSellDoubled", doubled);
	}
	public String getSignName(){
		 ConfigurationSection configSection = main.api.getSystem().getMarkkit().getConfigurationSection("Items");
		 String text = "§fNot Found";
		for (String path : configSection.getKeys(false)){
			 if((Integer.parseInt(main.api.getSystem().getMarkkit().getString("Items." + path + ".ID")) == material.getId()) && (Integer.parseInt(main.api.getSystem().getMarkkit().getString("Items." + path + ".Damage")) == damage)){
				 text = path;
				 return text;
			 }
		 }
		return text;
	}
}

class ElyPlayerItem{
	public Elysian main;
	Player p;
	Material m;
	int damage;
	
	ElyPlayerItem(Elysian main, Player p, Material m, int damage){
		this.main = main;
		this.p = p;
		this.m = m;
		this.damage = damage;
	}
}