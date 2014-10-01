package com.github.lyokofirelyte.Elysian;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class ElyMarkkitItem {
	Material material;
	int damage;
	public Elysian main;


	public ElyMarkkitItem(Elysian i, Material mat, int data){
		material = mat;
		damage = data;
		main = i;
	}
	
	public Material getMaterial(){
		return material;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public int getStackSellPrice(){
		return main.markkitYaml.getInt("Items." + getSignName() + "." + 64 + ".sellprice");
	}
	
	public int getStackBuyPrice(){
		return main.markkitYaml.getInt("Items." + getSignName() + "." + 64 + ".buyprice");
	}
	
	public int getSellPrice(int i){
		return main.markkitYaml.getInt("Items." + getSignName() + "." + 64 + ".sellprice") * i / 64;
	}
	
	public int getBuyPrice(int i){
		return main.markkitYaml.getInt("Items." + getSignName() + "." + 64 + ".buyprice") * i / 64;
	}
	
	public int getInStock(){
		return main.markkitYaml.getInt("Items." + getSignName() + ".inStock");
	}
	
	public boolean isSellDoubled(){
		return main.markkitYaml.getBoolean("Items." + getSignName() + ".isSellDoubled");
	}
	
	public String getSignName(){
		 ConfigurationSection configSection = main.markkitYaml.getConfigurationSection("Items");
		 String text = "§fNot Found";
		for (String path : configSection.getKeys(false)){
			 if((Integer.parseInt(main.markkitYaml.getString("Items." + path + ".ID")) == material.getId()) && (Integer.parseInt(main.markkitYaml.getString("Items." + path + ".Damage")) == damage)){
				 text = path;
				 return text;
			 }
		 }
		return text;
	}
}
